def get_oemake_config(a, d):
    import re

    if   re.match('armeb$', a):               return 'generic-v7-defconfig'
    elif re.match('aarch64$', a):             return 'generic-v8-defconfig'
    elif re.match('aarch64_be$', a):          return 'generic-v8-defconfig'
    elif re.match('aarch64_ilp32$', a):       return 'generic-v8-defconfig'
    elif re.match('aarch64_be_ilp32$', a):    return 'generic-v8-defconfig'
    elif re.match('riscv32(eb|)$', a):        return 'generic-32b-defconfig'
    elif re.match('riscv64(eb|)$', a):        return 'generic-64b-defconfig'
    else:
        bb.note("cannot map '%s' to a Xvisor defconfig" % a)

def map_xvisor_arch(a, d):
    import re

    if   re.match('(i.86|x86.64)$', a):  return 'x86'
    elif re.match('armeb$', a):                 return 'arm'
    elif re.match('aarch64$', a):               return 'arm'
    elif re.match('aarch64_be$', a):            return 'arm'
    elif re.match('aarch64_ilp32$', a):         return 'arm'
    elif re.match('aarch64_be_ilp32$', a):      return 'arm'
    elif re.match('riscv(32|64|)(eb|)$', a):    return 'riscv'
    else:
        bb.note("cannot map '%s' to a Xvisor architecture" % a)
