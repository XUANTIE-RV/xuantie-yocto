#!/usr/bin/env python3
"""Mirror xl300 PMU + CBQRI node statuses from a source golden into a target, in place.

Usage: mirror_xl300_status.py <target.dts> <source.dts>

The pinned legacy master predates c950 being an xl300 target, so it emits the
c9501 xl300_pmu / xl300_cbqri nodes all-disabled. The new generator treats c950
exactly like c930 (same xiaohui_v2 platform + --bus stlk3), so the c9501 golden's
node statuses are copied from the same-layout c9301 legacy golden. Only the
`status` field is touched; addresses, cache phandles and rcid/mcid stay from the
target's own (c9501) output. This keeps the baseline legacy-sourced (c930's real
output) while letting c950 fully participate in semantic comparison.
"""

import re
import sys
from pathlib import Path

NODES = ["xl300_pmu_0", "xl300_pmu_1", "xl300_cbqri_cc_0", "xl300_cbqri_cc_1"]


def get_status(text, node):
    m = re.search(rf'{re.escape(node)}@[0-9a-fA-F]+\s*\{{[^}}]*?status = "([^"]*)"',
                  text, re.S)
    return m.group(1) if m else None


def set_status(text, node, value):
    return re.sub(
        rf'({re.escape(node)}@[0-9a-fA-F]+\s*\{{[^}}]*?status = ")[^"]*"',
        rf'\g<1>{value}"', text, count=1, flags=re.S)


def main():
    if len(sys.argv) != 3:
        sys.exit("usage: mirror_xl300_status.py <target.dts> <source.dts>")
    target, source = Path(sys.argv[1]), Path(sys.argv[2])
    src_text = source.read_text()
    tgt_text = target.read_text()
    for node in NODES:
        status = get_status(src_text, node)
        if status is None:
            continue
        tgt_text = set_status(tgt_text, node, status)
    target.write_text(tgt_text)


if __name__ == "__main__":
    main()
