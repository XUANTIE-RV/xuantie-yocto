#!/usr/bin/env python3
"""Mirror the core pmu node's event maps from a source golden into a target, in place.

Usage: mirror_pmu_table.py <target.dts> <source.dts>

The pinned legacy master predates c950 joining the c930 family, so its pmu
selection falls back to the v1-style table (branches=0x36, misses=0x38) for
xt-c9501*. The new generator routes c950 to pmu_v2 exactly like c930 (same
xiaohui_v2 platform), so the c9501 golden's three riscv,* event-map properties
are copied from the same-layout c9301 legacy golden. Only those properties are
touched; everything else stays from the target's own (c9501) output. Same
deliberate SoT divergence as mirror_xl300_status.py, handled at generation
time.
"""

import re
import sys
from pathlib import Path

PROPS = [
    "riscv,event-to-mhpmevent",
    "riscv,event-to-mhpmcounters",
    "riscv,raw-event-to-mhpmcounters",
]


def _assignment(text, prop):
    """Return the full `prop = ...;` statement (may span lines), or None."""
    m = re.search(rf'^\s*{re.escape(prop)}\s*=\s*.+?;', text, re.M | re.S)
    return m.group(0) if m else None


def main():
    if len(sys.argv) != 3:
        sys.exit("usage: mirror_pmu_table.py <target.dts> <source.dts>")
    target, source = Path(sys.argv[1]), Path(sys.argv[2])
    src_text = source.read_text()
    tgt_text = target.read_text()
    for prop in PROPS:
        if len(re.findall(rf'^\s*{re.escape(prop)}\s*=', tgt_text, re.M)) != 1:
            sys.exit(f"{target}: expected exactly one {prop} assignment")
        stmt = _assignment(src_text, prop)
        if stmt is None:
            sys.exit(f"{source}: no {prop} assignment found")
        tgt_text = re.sub(rf'^\s*{re.escape(prop)}\s*=\s*.+?;',
                          lambda m: stmt, tgt_text, count=1, flags=re.M | re.S)
    target.write_text(tgt_text)


if __name__ == "__main__":
    main()
