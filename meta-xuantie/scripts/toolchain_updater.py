#!/usr/bin/env python3
"""
Yocto Toolchain Updater
Usage:
  Normal: meta-xuantie/scripts/toolchain_updater.py
  Force:  meta-xuantie/scripts/toolchain_updater.py -f
          meta-xuantie/scripts/toolchain_updater.py --force
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
DOWNLOAD_DIR = SCRIPT_DIR / "../../xuantie-build/toolchains-download"
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


def resolve_recipe_path(filepath):
    """Locate a bb recipe even when the JSON path predates a previous rename.

    JSON entries reference a fixed PV-suffixed bb name (e.g. clang_18.1.8.bb).
    After a base-version bump the file may already be `clang_18.1.18.bb` on
    disk. Fall back to a glob `<pn>_*.bb` in the same directory; return the
    sole match. If nothing or more than one matches, return the original path
    unchanged so the caller's existence check produces a clear error.
    """
    p = Path(filepath)
    if p.exists():
        return filepath
    pn = p.stem.split('_', 1)[0]
    matches = sorted(p.parent.glob(f'{pn}_*.bb'))
    if len(matches) == 1:
        return str(matches[0])
    return filepath


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

    if not FORCE_UPDATE and filepath.exists() and done_file.exists():
        marker = done_file.read_text().strip()
        prefix = f"{algo}:"
        if marker.startswith(prefix):
            cached = marker[len(prefix):]
            data = filepath.read_bytes()
            actual = hashlib.md5(data).hexdigest() if algo == 'md5' else hashlib.sha256(data).hexdigest()
            if actual == cached:
                print(f"   Cache hit: {filepath} ({algo.upper()}: {cached[:16]}...)")
                download_cache[url] = cached
                return cached
            print(f"   Cache stale: {filepath} ({algo.upper()} mismatch, re-downloading)")

    # Re-download (cache miss / forced / hash algo mismatch)
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


def extract_suite_from_url(url):
    """Suite token = path segment immediately before the filename, e.g. V3.5.0-beta-rc0."""
    parts = url.rstrip('/').split('/')
    return parts[-2] if len(parts) >= 2 else None


def extract_pn_base_from_url(url):
    """Return (pn, base) extracted from a deb-style URL filename, or (None, None).

    Pattern only covers gcc / clang today (those are the only PNs whose deb
    filename embeds the compiler version). Extend the alternation if new
    base-versioned families are added (e.g. binutils once it ships its own
    deb instead of sharing gcc's).
    """
    filename = url.split('/')[-1]
    m = re.match(r'^(?P<pn>gcc|clang)_(?P<base>[0-9]+\.[0-9]+\.[0-9]+)_riscv', filename)
    if m:
        return m.group('pn'), m.group('base')
    return None, None


def update_bb_file(filepaths, files):
    """Update FILE_RV* / sha256 / PV in each bb file; rename when base changes.

    `files` is expected to contain at least an `rv64_66` entry — that URL is
    used to derive the suite (V3.5.0-...) and the canonical PN/base. Any other
    rv* entries also get URL+sha256 substituted.
    """
    var_map = {
        'rv64_510': 'FILE_RV64_510',
        'rv64_66':  'FILE_RV64_66',
        'rv32_510': 'FILE_RV32_510',
        'rv32_66':  'FILE_RV32_66'
    }

    canonical_url = files.get('rv64_66') or next(iter(files.values()))
    new_suite = extract_suite_from_url(canonical_url)
    url_pn, url_base = extract_pn_base_from_url(canonical_url)

    for filepath in filepaths:
        print(f"  ➤ Updating: {filepath}")
        path_obj = Path(filepath)
        original_content = path_obj.read_text()
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
                continue

            new_content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
            if new_content != content:
                content = new_content
                file_changed = True

        # Determine recipe PN from filename and apply PV/base update.
        recipe_pn = path_obj.stem.split('_', 1)[0]
        new_base = None
        pv_match = re.search(r'^PV\s*=\s*"([^"]+)"', content, flags=re.MULTILINE)
        if pv_match and new_suite:
            current_pv = pv_match.group(1)
            current_base = current_pv.split('-', 1)[0] if '-' in current_pv else current_pv
            new_base = url_base if (url_pn and url_pn == recipe_pn and url_base) else current_base
            new_pv = f'{new_base}-{new_suite}'
            if new_pv != current_pv:
                content = re.sub(r'^PV\s*=\s*"[^"]+"', f'PV = "{new_pv}"', content,
                                 count=1, flags=re.MULTILINE)
                file_changed = True
        else:
            current_base = None

        if file_changed or FORCE_UPDATE:
            path_obj.write_text(content)

            # Rename when base version changed.
            if new_base and current_base and new_base != current_base:
                renamed = path_obj.with_name(f'{recipe_pn}_{new_base}.bb')
                if renamed != path_obj:
                    if renamed.exists():
                        print(f"  WARN: rename target exists, leaving {path_obj.name} in place"
                              f" alongside {renamed.name}", file=sys.stderr)
                    else:
                        print(f"  ➤ Renaming: {path_obj.name} -> {renamed.name}")
                        path_obj.rename(renamed)

def main():
    global FORCE_UPDATE

    parser = argparse.ArgumentParser(description="Yocto Toolchain Updater")
    parser.add_argument(
        '-f', '--force',
        action='store_true',
        help="Force update all files and increment PR even if content hasn't changed."
    )
    parser.add_argument(
        '-t', '--type',
        choices=['conf', 'bb'],
        default=None,
        help="Only process entries of this type (default: all types)"
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
        config_file = SCRIPT_DIR / "xuantie_toolchain_url.json"

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
    if args.type:
        updates = [u for u in updates if u['type'] == args.type]
    total_entries = len(updates)

    all_filepaths = []
    for item in updates:
        #  Convert relative paths to absolute (based on SCRIPT_DIR)
        filepaths = [str(SCRIPT_DIR / fp) for fp in normalize_files(item['file'])]
        resolved = [resolve_recipe_path(fp) if item['type'] == 'bb' else fp
                    for fp in filepaths]
        for orig, real in zip(filepaths, resolved):
            if not os.path.exists(real):
                sys.exit(f" File not found: {orig}")
        all_filepaths.extend(resolved)

    total_files = len(all_filepaths)
    if total_files == 0:
        print("  No files to update.")
        return

    print(f" Starting toolchain update ({total_entries} entries, {total_files} files)...\n")

    for idx, item in enumerate(updates, 1):
        #  Also convert here for processing
        filepaths = [str(SCRIPT_DIR / fp) for fp in normalize_files(item['file'])]
        if item['type'] == 'bb':
            filepaths = [resolve_recipe_path(fp) for fp in filepaths]
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
