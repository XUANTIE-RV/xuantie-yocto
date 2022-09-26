helloYocto
============

A simple Yocto hello world example

##Intructions:
1. Clone this repo to the meta-XXX/recipe-YYY/
2. In yocto `build` diretory, source the environment `source openembedded-core/oe-init-build-env xxx`
3. Using the `bitbake -s | grep hello` to see and validate the pacakge version
4. bitbake the bb file to build the manumaltest: `bitbake -b <path to the>manualtest.bb -v`
    or just using the `bitake hello` to build the target

