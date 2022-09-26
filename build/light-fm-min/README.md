# conf: ice-gnome

## 配置

1. Build Configuration

```bash

```

2. bitbake-layers show-layers

```bash
layer                    path                                      priority
===========================================================================
meta                     ${YOCTOROOT}/openembedded-core/meta              5
meta-oe                  ${YOCTOROOT}/meta-openembedded/meta-oe           6
meta-python              ${YOCTOROOT}/meta-openembedded/meta-python       7
meta-multimedia          ${YOCTOROOT}/meta-openembedded/meta-multimedia   6
meta-networking          ${YOCTOROOT}/meta-openembedded/meta-networking   5
meta-external-toolchain  ${YOCTOROOT}/meta-external-toolchain             1
meta-riscv               ${YOCTOROOT}/meta-riscv                          6
meta-gnome               ${YOCTOROOT}/meta-openembedded/meta-gnome        7
meta-filesystems         ${YOCTOROOT}/meta-openembedded/meta-filesystems  6
meta-webserver           ${YOCTOROOT}/meta-openembedded/meta-webserver    6
```

## 镜像

| meta layer                                             | images                      | build | check | memo |
| :----------------------------------------------------- | :-------------------------- | :---: | :---: | :--- |
| openembedded-core/meta/recipes-core                    | core-image-base             |  OK   |       |      |
| openembedded-core/meta/recipes-core                    | core-image-minimal          |  OK   |       |      |
| openembedded-core/meta/recipes-core                    | core-image-minimal-dev      |       |       |      |
| openembedded-core/meta/recipes-core                    | core-image-minimal-mtdutils |       |       |      |
| openembedded-core/meta/recipes-sato                    | core-image-sato             |  OK   |       |      |
| openembedded-core/meta/recipes-sato                    | core-image-sato-dev         |       |       |      |
| openembedded-core/meta/recipes-sato                    | core-image-sato-ptest-fast  |       |       |      |
| openembedded-core/meta/recipes-sato                    | core-image-sato-sdk-ptest   |       |       |      |
| openembedded-core/meta/recipes-sato                    | core-image-sato-sdk         |       |       |      |
| openembedded-core/meta/recipes-graphics                | core-image-clutter          |  OK   |       |      |
| openembedded-core/meta/recipes-graphics                | core-image-weston           |  OK   |       |      |
| openembedded-core/meta/recipes-graphics                | core-image-x11              |  OK   |       |      |
| openembedded-core/meta/recipes-extended                | core-image-testmaster       |       |       |      |
| openembedded-core/meta/recipes-extended                | core-image-full-cmdline     |  OK   |       |      |
| openembedded-core/meta/recipes-extended                | core-image-kernel-dev       |       |       |      |
| meta-openembedded/meta-gnome/recipes-core              | core-image-minimal-gnome    |  OK   |       |      |
| meta-openembedded/meta-gnome/recipes-core              | core-image-gnome            |  OK   |       |      |
| meta-openembedded/meta-filesystems/recipes-filesystems | meta-filesystems-image-base |       |       |      |
| meta-openembedded/meta-multimedia/recipes-multimedia   | meta-multimedia-image-base  |       |       |      |
| meta-openembedded/meta-networking/recipes-core         | meta-networking-image-base  |       |       |      |
| meta-openembedded/meta-oe/recipes-core                 | meta-oe-image-base          |       |       |      |
| meta-openembedded/meta-python/recipes-core             | meta-python-image-base      |       |       |      |
| meta-openembedded/meta-webserver/recipes-core          | meta-webserver-image-base   |       |       |      |
