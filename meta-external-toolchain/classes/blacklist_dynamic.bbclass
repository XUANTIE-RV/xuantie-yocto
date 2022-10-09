# Blacklist recipe names which include variable references, handling
# multilibs.
#
# Ex.
#     PNBLACKLIST_DYNAMIC += "${MLPREFIX}gcc-cross-${TARGET_ARCH}"
#     PNBLACKLIST_DYNAMIC += "gcc-source-${@'${GCCVERSION}'.replace('%', '')}"

PNBLACKLIST_DYNAMIC ?= ""

inherit blacklist

python pnblacklist_dynamic_setup () {
    d = e.data

    blacklisted = d.getVar('PNBLACKLIST_DYNAMIC', False)
    if not blacklisted.strip():
        return

    multilibs = d.getVar('MULTILIBS', True) or ''

    # this block has been copied from base.bbclass so keep it in sync
    prefixes = []
    for ext in multilibs.split():
        eext = ext.split(':')
        if len(eext) > 1 and eext[0] == 'multilib':
            prefixes.append(eext[1])

    to_blacklist = set()
    for prefix in [''] + prefixes:
        localdata = bb.data.createCopy(d)
        if prefix:
            localdata.setVar('MLPREFIX', prefix + '-')
            override = ':virtclass-multilib-' + prefix
            localdata.setVar('OVERRIDES', localdata.getVar('OVERRIDES', False) + override)
            bb.data.update_data(localdata)

        to_blacklist |= set(filter(None, localdata.getVar('PNBLACKLIST_DYNAMIC').split()))

    for blrecipe in to_blacklist:
        d.setVarFlag('PNBLACKLIST', blrecipe, 'blacklisted by PNBLACKLIST_DYNAMIC')
}
pnblacklist_dynamic_setup[eventmask] = "bb.event.ConfigParsed"
addhandler pnblacklist_dynamic_setup
