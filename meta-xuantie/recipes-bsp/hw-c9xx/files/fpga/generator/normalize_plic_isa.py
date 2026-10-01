#!/usr/bin/env python3
"""Strip AIA ISA extensions from a PLIC golden DTS, in place.

The legacy generator passes the CPU's raw ISA straight through, so a PLIC
baseline can end up advertising the AIA extensions (smaia/smcsrind/ssaia/
sscsrind) even though AIA only makes sense with an aplic/imsics interrupt tree.
The template-driven generate_dts.py removes AIA on the plic path
(_apply_aia_policy); this normalizer brings a regenerated legacy PLIC golden in
line, so the baseline stays self-consistent and stable across
generate_golden_refs.sh rebuilds. Svvptc is unrelated to AIA and is left intact.

Only apply this to PLIC cases — APLIC goldens must keep (and auto-fill) AIA.
"""

import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import generate_dts  # noqa: E402  (needs the sys.path insert above)

AIA = generate_dts.AIA_EXTS


def strip_aia(text):
    aset = set(AIA)

    def fix_isa(m):
        toks = [t for t in m.group(1).split("_") if t not in aset]
        return 'riscv,isa = "' + "_".join(toks) + '"'

    # riscv,isa string: drop the AIA tokens from the underscore-joined list.
    text = re.sub(r'riscv,isa = "([^"]*)"', fix_isa, text)
    # riscv,isa-extensions list: drop each ', "ext"' entry (AIA is never the
    # first entry here, so removing the leading comma leaves no dangling comma).
    for e in AIA:
        text = re.sub(rf',\s*"{e}"', "", text)
    return text


def main():
    for path in sys.argv[1:]:
        p = Path(path)
        p.write_text(strip_aia(p.read_text()))


if __name__ == "__main__":
    main()
