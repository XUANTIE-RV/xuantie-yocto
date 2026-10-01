# oe-core langdale pins PyYAML 6.0,
# whose setup.py uses build_ext.cython_sources() removed in Cython 3
# (main tree has cython 3.0.12) and whose pre-cythonized _yaml.c does not
# support Python 3.13 (main tree pins python3-native 3.13.4). Force the
# pure-python wheel; target 构建同样撞 cython_sources（checkin 会构建 target
# 配方），故不限定 class-native。ceph 构建依赖 python3-pyyaml-native。
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " file://0001-setup.py-build-pure-python-wheel-with-cython3.patch"
