#!/usr/bin/env python3
"""
dtsi_engine.py — the lightweight template engine shared by the DTS generator.

Templates are the master dtsi files kept verbatim, with only the varying
literals replaced by directives. No external template engine, no cpp — just
three constructs:

  ${MACRO}                       substituted with defines[MACRO]

  ##IF MACRO## ... ##ENDIF##     block emitted only when defines[MACRO] is
                                 truthy (non-empty and not "0"); no nesting

  ##REPEAT NAME## ... ##ENDREPEAT##
                                 block emitted once per entry in repeats[NAME]
                                 (a list of dicts). Each iteration's dict is
                                 overlaid on the global defines, so ${...}
                                 inside the block can reference per-iteration
                                 values (e.g. ${CORE_ID}, ${CORE_STATUS}).

Everything else — including DTS // and /* */ comments — is preserved verbatim.
Macro references inside a /* ... */ block comment are left untouched so
template header documentation renders literally.
"""

import re
import sys

_IF_RE       = re.compile(r"^(\s*)##IF\s+([A-Za-z_][A-Za-z_0-9]*)##\s*$")
_ENDIF_RE    = re.compile(r"^(\s*)##ENDIF##\s*$")
_REPEAT_RE   = re.compile(r"^(\s*)##REPEAT\s+([A-Za-z_][A-Za-z_0-9]*)##\s*$")
_ENDREP_RE   = re.compile(r"^(\s*)##ENDREPEAT##\s*$")
_MACRO_RE    = re.compile(r"\$\{([A-Za-z_][A-Za-z_0-9]*)\}")


def _substitute_line(line, defines):
    out = []
    cursor = 0
    for m in _MACRO_RE.finditer(line):
        name = m.group(1)
        if name not in defines:
            sys.exit(f"dtsi_engine: undefined macro '${{{name}}}' "
                     f"(line: {line!r})")
        out.append(line[cursor:m.start()])
        out.append(str(defines[name]))
        cursor = m.end()
    out.append(line[cursor:])
    return "".join(out)


def _scan_comment_state(raw, in_block_comment):
    """Return (line_is_doc, new_in_block_comment) tracking /* */ regions."""
    opened = closed = False
    scan = raw
    state = in_block_comment
    while True:
        if state:
            end = scan.find("*/")
            if end == -1:
                break
            scan = scan[end + 2:]
            state = False
            closed = True
        else:
            start = scan.find("/*")
            if start == -1:
                break
            scan = scan[start + 2:]
            state = True
            opened = True
    line_is_doc = in_block_comment or opened or closed
    return line_is_doc, state


def render(template_text, defines, repeats=None):
    """Render a template. defines: {MACRO: value}. repeats: {NAME: [dict,...]}.

    REPEAT blocks are expanded first (each iteration overlays its dict on
    defines), then ${MACRO}/##IF## are processed on the expanded lines.
    """
    repeats = repeats or {}
    raw_lines = template_text.splitlines()

    # Expand REPEAT blocks into a flat (line, per_iter_overlay) list so a
    # subsequent single pass handles ${}/##IF## with the right scope.
    expanded = []            # list of (line, overlay_dict_or_None)
    i = 0
    n = len(raw_lines)
    in_comment = False
    while i < n:
        raw = raw_lines[i]
        line_is_doc, in_comment = _scan_comment_state(raw, in_comment)
        m_rep = _REPEAT_RE.match(raw)
        if m_rep and not line_is_doc:
            name = m_rep.group(2)
            # collect block body until matching ##ENDREPEAT##
            body = []
            i += 1
            while i < n and not _ENDREP_RE.match(raw_lines[i]):
                body.append(raw_lines[i])
                i += 1
            if i >= n:
                sys.exit(f"dtsi_engine: unterminated ##REPEAT {name}##")
            if name not in repeats:
                sys.exit(f"dtsi_engine: no repeat data for ##REPEAT {name}##")
            for overlay in repeats[name]:
                for b in body:
                    expanded.append((b, overlay))
            i += 1  # skip ##ENDREPEAT##
            continue
        expanded.append((raw, None))
        i += 1

    # Now substitute per line, applying the per-iteration overlay when present.
    out = []
    in_if = False
    if_emit = False
    in_comment = False
    for raw, overlay in expanded:
        line_is_doc, in_comment = _scan_comment_state(raw, in_comment)
        scope = defines if overlay is None else {**defines, **overlay}

        m_if = _IF_RE.match(raw)
        m_en = _ENDIF_RE.match(raw)
        if m_if and not line_is_doc:
            if in_if:
                sys.exit(f"dtsi_engine: nested ##IF## not supported ({raw!r})")
            in_if = True
            v = scope.get(m_if.group(2), "")
            if_emit = bool(v) and v != "0"
            continue
        if m_en and not line_is_doc:
            if not in_if:
                sys.exit(f"dtsi_engine: stray ##ENDIF## ({raw!r})")
            in_if = False
            continue
        if in_if and not if_emit:
            continue

        out.append(raw if line_is_doc else _substitute_line(raw, scope))
    if in_if:
        sys.exit("dtsi_engine: unterminated ##IF## block")
    return "\n".join(out) + "\n"
