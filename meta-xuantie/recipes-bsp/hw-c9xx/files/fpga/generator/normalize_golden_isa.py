#!/usr/bin/env python3
"""Rebase a legacy golden DTS's riscv,isa strings onto the cpu-isa-configs SoT.

The pinned legacy ref (bef11d40f5) predates two deliberate SoT changes:
  1. smrnmi was dropped from the xt-c9251/xt-c9301/xt-c9501 conf entries;
  2. every conf default_isa was re-emitted in toolchain canonical order
     (Tag_RISCV_arch slot order), while the legacy string still carries the
     old ordering.
Both are content-free against the new generator, whose ISA comes straight
from the conf. This normalizer rewrites the golden so the master baseline
stays comparable instead of permanently red:

  * riscv,isa string: tokens not declared by the CPU's conf are removed
    (smrnmi today), the rest are sorted into conf order (base token kept
    leading); generate_dts._apply_aia_policy is then replayed for the case's
    interrupt kind, so an aplic golden keeps (and tail-appends) AIA exactly
    like the generator does and a plic golden loses it;
  * riscv,isa-extensions list: the dropped tokens are removed the same way
    the legacy xt-c9501 sed did; the list is emitted by dtc from the legacy
    string and is not part of the semantic compare, so its order is left
    alone.

Idempotent: a golden already in conf order is rewritten to itself. Unknown
CPU (no conf entry) -> no-op with a note, so the harness never destroys a
baseline it cannot map.

Usage: normalize_golden_isa.py <golden.dts> <cpu> <aplic|plic>
"""

import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import generate_dts  # noqa: E402  (needs the sys.path insert above)


def canonical_isa(isa: str, conf_isa: str):
    """Return isa rebased onto conf order + the set of dropped tokens."""
    order = {tok: i for i, tok in enumerate(conf_isa.split("_"))}
    toks = isa.split("_")
    base, rest = toks[0], toks[1:]
    kept = [t for t in rest if t in order]
    dropped = sorted(set(rest) - set(kept))
    kept.sort(key=lambda t: order[t])
    return "_".join([base] + kept), dropped


def intc_kind_for(path, cpu):
    """The case's interrupt kind: an explicit _intc_aplic golden filename wins
    (that is the -intc override the harness passed), otherwise the CSV default
    select_intc_kind() reads — the same source the generator uses."""
    if "_intc_aplic" in path.name:
        return "aplic"
    return generate_dts.select_intc_kind(cpu)


def main():
    path, cpu = Path(sys.argv[1]), sys.argv[2]
    conf = generate_dts.load_cpu_isa_configs().get(cpu)
    if not conf:
        print(f"normalize_golden_isa: no cpu-isa-configs entry for '{cpu}', "
              f"left {path.name} untouched")
        return 0
    kind = intc_kind_for(path, cpu)
    text = path.read_text()
    before = {t for m in re.finditer(r'riscv,isa = "([^"]*)"', text)
              for t in m.group(1).split("_")[1:]}
    after = set()

    def fix(m):
        # conf rebase first, then replay the generator's AIA policy on top: an
        # aplic golden must end up holding AIA (tail-appended, exactly like
        # _apply_aia_policy), a plic golden must not.
        norm, _ = canonical_isa(m.group(1), conf)
        norm, _ = generate_dts._apply_aia_policy(norm, [], kind)
        after.update(norm.split("_")[1:])
        return 'riscv,isa = "' + norm + '"'

    text = re.sub(r'riscv,isa = "([^"]*)"', fix, text)
    # Only tokens the rewrite actually removed from the isa string are pruned
    # from the quoted extension list; a token dropped by the conf rebase but
    # re-added by the AIA policy must stay in both.
    for e in sorted(before - after):
        # same two shapes the legacy per-CPU sed handled: mid-list and last
        text = re.sub(rf',\s*"{re.escape(e)}"', "", text)
        text = re.sub(rf'"{re.escape(e)}",\s*', "", text)
    path.write_text(text)
    if before - after:
        print(f"normalize_golden_isa: {path.name}: dropped "
              f"{sorted(before - after)}, reordered onto conf ({kind})")
    return 0


if __name__ == "__main__":
    sys.exit(main())
