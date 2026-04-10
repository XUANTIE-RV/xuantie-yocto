#!/usr/bin/env python3
"""
Yocto Toolchain Updater
Usage:
  Normal: meta-xuantie/conf/distro/toolchain/toolchain_updater.py
  Force:  meta-xuantie/conf/distro/toolchain/toolchain_updater.py -f
          meta-xuantie/conf/distro/toolchain/toolchain_updater.py --force
"""

import os
import sys
import json
import re
import hashlib
import urllib.request
import argparse
from pathlib import Path


# Compute script directory ONCE at module level
SCRIPT_DIR = Path(__file__).parent.resolve()
DOWNLOAD_DIR = SCRIPT_DIR / "../../../../downloads"
DOWNLOAD_DIR.mkdir(exist_ok=True)

# Global cache: url -> sha256
download_cache = {}

FORCE_UPDATE = False

def normalize_files(file_field):
    if isinstance(file_field, str):
        return [file_field]
    elif isinstance(file_field, list):
        if not all(isinstance(f, str) for f in file_field):
            raise ValueError("All items in 'file' list must be strings")
        return file_field
    else:
        raise ValueError(f"Invalid 'file' field type: {type(file_field)}")


def download_and_hash(url, algo='sha256'):
    if url in download_cache:
        print(f"   Using cached: {url}")
        return download_cache[url]

    filename = url.split('/')[-1]
    if not filename:
        raise ValueError(f"Invalid URL: {url}")

    # Use SCRIPT_DIR-relative path
    filepath = DOWNLOAD_DIR / filename
    done_file = filepath.with_suffix(filepath.suffix + '.done')

    for f in [filepath, done_file]:
        if f.exists():
            f.unlink()

    print(f"   Downloading: {url}")
    try:
        with urllib.request.urlopen(url) as response:
            data = response.read()

        filepath.write_bytes(data)

        result = hashlib.md5(data).hexdigest() if algo == 'md5' else hashlib.sha256(data).hexdigest()
        done_file.write_text(f"{algo}:{result}")
        print(f"   Saved to {filepath}, {algo.upper()}: {result[:16]}...")

        download_cache[url] = result
        return result

    except Exception as e:
        print(f"   Failed to download: {url}", file=sys.stderr)
        print(f"      Error: {e}", file=sys.stderr)
        sys.exit(1)


def update_conf_file(filepaths, new_url):
    md5 = download_and_hash(new_url, 'md5')
    content = f'TOOLCHAIN_URL={new_url}\nTOOLCHAIN_MD5={md5}\n'
    for filepath in filepaths:
        current_content = Path(filepath).read_text() if Path(filepath).exists() else ""
        # If content is identical and not forced, skip
        if not FORCE_UPDATE and current_content == content:
            print(f"  ➤ Skipping: {filepath}")
            continue
        print(f"  ➤ Updating: {filepath}")
        Path(filepath).write_text(content)


def get_current_bb_config_md5(content, var_name):
    """
    Extract the current URL and sha256 from the .bb file for a specific variable
    and return an MD5 hash of that configuration string.
    This allows us to detect if the actual toolchain definition has changed.
    """
    pattern = rf'^{re.escape(var_name)}\s*=\s*"([^"]*)"'
    match = re.search(pattern, content, flags=re.MULTILINE)
    if match:
        current_value = match.group(1)
        return hashlib.md5(current_value.encode('utf-8')).hexdigest()
    return None


def update_bb_file(filepaths, files):
    var_map = {
        'rv64_510': 'FILE_RV64_510',
        'rv64_66':  'FILE_RV64_66',
        'rv32_510': 'FILE_RV32_510',
        'rv32_66':  'FILE_RV32_66'
    }

    for filepath in filepaths:
        print(f"  ➤ Updating: {filepath}")
        original_content = Path(filepath).read_text()
        content = original_content
        file_changed = False

        for key, full_url in files.items():
            var_name = var_map[key]
            sha256 = download_and_hash(full_url, 'sha256')
            # Use full URL in variable value
            new_value = f'{full_url};sha256sum={sha256}'
            pattern = rf'^({re.escape(var_name)}\s*=\s*").*("$)'
            replacement = f'{var_name} = "{new_value}"'
            current_val_md5 = get_current_bb_config_md5(content, var_name)
            new_val_md5 = hashlib.md5(new_value.encode('utf-8')).hexdigest()

            if not FORCE_UPDATE and current_val_md5 == new_val_md5:
                # print(f"     - {var_name}: No change detected (Skipping)")
                # Still apply regex to ensure format consistency if desired,
                # but logically no content change implies no PR bump needed for this reason.
                # However, to be safe and clean, we only mark file_changed if values differ.
                continue

            # Apply the substitution
            new_content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
            if new_content != content:
                content = new_content
                file_changed = True

        if file_changed or FORCE_UPDATE:
            if file_changed:
                # Increment PR only if content actually changed OR force update is on
                def inc_pr(m):
                    return f'{m.group(1)}{int(m.group(2)) + 1}{m.group(3)}'

                content = re.sub(r'(PR = "r)(\d+)(")', inc_pr, content)

            Path(filepath).write_text(content)

def main():
    global FORCE_UPDATE

    parser = argparse.ArgumentParser(description="Yocto Toolchain Updater")
    parser.add_argument(
        '-f', '--force',
        action='store_true',
        help="Force update all files and increment PR even if content hasn't changed."
    )
    parser.add_argument(
        'config_file',
        nargs='?',
        default=None,
        help="Path to config JSON file (default: toolchain_url.json in script dir)"
    )

    args = parser.parse_args()

    if args.force:
        FORCE_UPDATE = True
        print("!!! FORCE UPDATE MODE ENABLED (via -f flag) !!!")

    # Determine config file path
    if args.config_file:
        config_file = Path(args.config_file)
    else:
        config_file = SCRIPT_DIR / "toolchain_url.json"

    if not config_file.exists():
        sys.exit(f" Config file not found: {config_file}")

    backup_file = SCRIPT_DIR / ".last_config"

    with open(config_file) as f:
        current_config = json.load(f)

    should_skip_config_check = False
    if FORCE_UPDATE:
        should_skip_config_check = True
    elif backup_file.exists():
        with open(backup_file) as f:
            last_config = json.load(f)
        if current_config == last_config:
            #print("  Configuration unchanged. Skipping update.")
            return

    updates = current_config.get('updates', [])
    total_entries = len(updates)

    all_filepaths = []
    for item in updates:
        #  Convert relative paths to absolute (based on SCRIPT_DIR)
        filepaths = [str(SCRIPT_DIR / fp) for fp in normalize_files(item['file'])]
        all_filepaths.extend(filepaths)
        for fp in filepaths:
            if not os.path.exists(fp):
                sys.exit(f" File not found: {fp}")

    total_files = len(all_filepaths)
    if total_files == 0:
        print("  No files to update.")
        return

    print(f" Starting toolchain update ({total_entries} entries, {total_files} files)...\n")

    for idx, item in enumerate(updates, 1):
        #  Also convert here for processing
        filepaths = [str(SCRIPT_DIR / fp) for fp in normalize_files(item['file'])]
        file_list_str = ", ".join(filepaths) if len(filepaths) <= 2 else f"{filepaths[0]}, ... (+{len(filepaths)-1})"
        print(f"[{idx}/{total_entries}] Processing {item['type']}: {file_list_str}")

        if item['type'] == 'conf':
            update_conf_file(filepaths, item['new_url'])
        elif item['type'] == 'bb':
            update_bb_file(filepaths, item['files'])
        else:
            sys.exit(f"[{idx}/{total_entries}]  Unsupported type: {item['type']}")

        print()

    backup_file.write_text(json.dumps(current_config, indent=2))
    print(" All updates completed successfully.")


if __name__ == '__main__':
    main()
