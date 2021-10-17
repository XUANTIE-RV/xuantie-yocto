# conf: ice-base

## 配置

1. Build Configuration

```bash
Build Configuration:
BB_VERSION           = "1.48.0"
BUILD_SYS            = "x86_64-linux"
NATIVELSBSTRING      = "universal"
TARGET_SYS           = "riscv64-oe-linux"
MACHINE              = "ice"
DISTRO               = "nodistro"
DISTRO_VERSION       = "nodistro.0"
TUNE_FEATURES        = "riscv64"
EXTERNAL_TOOLCHAIN   = "/home/public/host"
EXTERNAL_TARGET_SYS  = "riscv64-linux"
GCC_VERSION          = "8.1.0"
meta                 = "HEAD:8579c5a69cabc3452dc220573d22a7d92a950757"
meta-oe
meta-python
meta-multimedia
meta-networking      = "HEAD:4453ac5cc0d8412e8f7ece5f79987809a8c4ddf3"
meta-external-toolchain = "HEAD:f9d1b7a1ec75241c0d61bd9c29a28048e6376ef8"
meta-riscv           = "HEAD:58bcdf7f8e3f116d0fbd406cde991fe09ffac71a"
``` 

2. bitbake-layers show-layers

```bash
layer                    path                                     priority
==========================================================================
meta                     ${YOCTOROOT}/openembedded-core/meta             5
meta-oe                  ${YOCTOROOT}/meta-openembedded/meta-oe          6
meta-python              ${YOCTOROOT}/meta-openembedded/meta-python      7
meta-multimedia          ${YOCTOROOT}/meta-openembedded/meta-multimedia  6
meta-networking          ${YOCTOROOT}/meta-openembedded/meta-networking  5
meta-external-toolchain  ${YOCTOROOT}/meta-external-toolchain            1
meta-riscv               ${YOCTOROOT}/meta-riscv                         6
```

## 镜像

| meta layer                                           | images                      | build | check | memo |
|:-----------------------------------------------------|:----------------------------|:-----:|:-----:|:-----|
| openembedded-core/meta/recipes-core                  | core-image-minimal          |  OK   |       |      |
|                                                      | core-image-base             |  OK   |       |      |
|                                                      | core-image-minimal-dev      |  OK   |       |      |
|                                                      | core-image-minimal-mtdutils |  OK   |       |      |
|                                                      | core-image-tiny-initramfs   |  OK   |       |      |
| openembedded-core/meta/recipes-sato                  | core-image-sato             |  OK   |       |      |
|                                                      | core-image-sato-dev         |  OK   |       |      |
|                                                      | core-image-sato-ptest-fast  | ERROR |       |      |
|                                                      | core-image-sato-sdk         | ERROR |       |      |
|                                                      | core-image-sato-sdk-ptest   | ERROR |       |      |
| openembedded-core/meta/recipes-graphics              | core-image-clutter          |  OK   |       |      |
|                                                      | core-image-weston           |  OK   |       |      |
|                                                      | core-image-x11              |  OK   |       |      |
| openembedded-core/meta/recipes-extended              | core-image-full-cmdline     |  OK   |       |      |
|                                                      | core-image-kernel-dev       | ERROR |       |      |
|                                                      | core-image-testmaster       |  OK   |       |      |
| meta-openembedded/meta-oe/recipes-core               | meta-oe-image-base          |  OK   |       |      |
| meta-openembedded/meta-python/recipes-core           | meta-python-image-base      |  OK   |       |      |
| meta-openembedded/meta-multimedia/recipes-multimedia | meta-multimedia-image-base  |  OK   |       |      |
| meta-openembedded/meta-networking/recipes-core       | meta-networking-image-base  |  OK   |       |      |
