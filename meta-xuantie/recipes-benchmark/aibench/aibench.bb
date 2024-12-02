DESCRIPTION = "AI benchmark for xuantie"
LICENSE = "CLOSED"

SRC_URI = "git://git@gitee.com/xuantie-yocto/xtai-benchmark.git;branch=1.0;protocol=ssh;name=xtai-benchmark"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV = "b1f182d9c6f3621116fe12b81e13bf1e47105fb3"
PV = "1.0"
S = "${WORKDIR}/git"

inherit autotools
inherit xuantie-set-package-arch-with-mcpu
PACKAGE_ARCH = "${MACHINE_ARCH}"
do_patch:prepend() {
    #fetch submodule
    os.system("cd {} && git submodule init && git submodule update".format(d.getVar('S')))
}

python do_compile() {
    import os
    cpu = None
    if d.getVar('XUANTIE_MCPU') == 'c907fdv':
        cpu = "../git/c907"
    elif d.getVar('XUANTIE_MCPU') == 'c908v':
        cpu = "../git/c908"
    quant_names = os.listdir(cpu)
    for j, quant_name in enumerate(quant_names):
        qt_path = os.path.join(cpu, quant_name)
        models = os.listdir(qt_path)
        for k, model in enumerate(models):
            model_path = os.path.join(qt_path, model)
            for n, ff in enumerate(os.listdir(model_path)):
                if ff.split(".")[0] == "makefile":
                    makefile = ff
                    sen1 = "cd {}/build/{}".format(d.getVar('WORKDIR'), model_path)
                    sen2 = "make CC=riscv64-linux-gcc -f {}".format(makefile)
                    os.system("{} && {}".format(sen1,sen2))
                    break
}

def collect(cpu, d):
    makefile = None
    package_sen = ""
    quant_names = os.listdir(cpu)
    for j, quant_name in enumerate(quant_names):
        qt_path = os.path.join(cpu, quant_name)
        models = os.listdir(qt_path)
        for k, model in enumerate(models):
            model_path = os.path.join(qt_path, model)
            bm = "{}_{}_{}_hhb.bm.tar.gz".format(cpu.split("/")[2][1:], quant_name, model)
            sen="wget http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.0.5/xtai-benchmark-bm/{}".format(bm)
            sen2 = "cd {} && {} && tar --no-same-owner -xzvf {} && mv {} {}".format(model_path, sen, bm, bm.strip(".tar.gz"), "hhb.bm")
            sen3 = "cd {} && tar --no-same-owner -xzvf {} && mv {} {}".format(model_path, bm, bm.strip(".tar.gz"), "hhb.bm")
            if os.path.exists(os.path.join(model_path, bm)):
                os.system(sen3)
            else:
                os.system(sen2)
            out_path = "../image/usr/bin/aibench/{}/{}/{}".format(cpu.split("/")[2], quant_name, model)
            os.system("mkdir -p {}".format(out_path))

            oo_path = "{}/aibench/{}/{}/{}".format(d.getVar('bindir'), cpu.split("/")[2], quant_name, model)
            cpu2 = None
            if cpu.split("/")[2]=="c907":
                cpu2 = "c907fdv"
            elif cpu.split("/")[2]=="c908":
                cpu2 = "c908v"
            package = "aibench-{}-{}-{}".format(cpu2, quant_name.lower().replace('_', '-'), model.lower().replace('_', '-'))
            package_sen += package
            package_sen += " \ \n"
            FILE_name = "FILES:{}".format(package)
            #bb.plain("{} = {}".format(FILE_name, "\"aibench/{}/{}/{}/*\"".format(cpu.split("/")[2], quant_name, model)));
            os.system("cp {} {}".format(os.path.join(model_path,"*.bin"), out_path))
            os.system("cp {} {}".format(os.path.join(model_path,"makefile*"), out_path))
            os.system("cp {} {}".format(os.path.join(model_path,"hhb_runtime"), out_path))
            os.system("cp {} {}".format(os.path.join(model_path,"hhb.bm"), out_path))
    #bb.plain(package_sen)

python do_install() {
    cpu = None
    if d.getVar('XUANTIE_MCPU') == 'c907fdv':
        cpu = "../git/c907"
    elif d.getVar('XUANTIE_MCPU') == 'c908v':
        cpu = "../git/c908"
    collect(cpu, d)
}

FILES:aibench-c907fdv-float16-onnx-bert = "${bindir}/aibench/c907/float16/onnx_bert/*"
FILES:aibench-c907fdv-float16-onnx-efficientnet = "${bindir}/aibench/c907/float16/onnx_efficientnet/*"
FILES:aibench-c907fdv-float16-onnx-mobilenetvit = "${bindir}/aibench/c907/float16/onnx_mobilenetVit/*"
FILES:aibench-c907fdv-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/c907/float16/onnx_mobilenetv2-12/*"
FILES:aibench-c907fdv-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/c907/float16/onnx_resnet50-v1-7/*"
FILES:aibench-c907fdv-float16-onnx-retinaface = "${bindir}/aibench/c907/float16/onnx_retinaface/*"
FILES:aibench-c907fdv-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/c907/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-c907fdv-float16-onnx-swin = "${bindir}/aibench/c907/float16/onnx_swin/*"
FILES:aibench-c907fdv-float32-onnx-bert = "${bindir}/aibench/c907/float32/onnx_bert/*"
FILES:aibench-c907fdv-float32-onnx-efficientnet = "${bindir}/aibench/c907/float32/onnx_efficientnet/*"
FILES:aibench-c907fdv-float32-onnx-mobilenetvit = "${bindir}/aibench/c907/float32/onnx_mobilenetVit/*"
FILES:aibench-c907fdv-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/c907/float32/onnx_mobilenetv2-12/*"
FILES:aibench-c907fdv-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/c907/float32/onnx_resnet50-v1-7/*"
FILES:aibench-c907fdv-float32-onnx-retinaface = "${bindir}/aibench/c907/float32/onnx_retinaface/*"
FILES:aibench-c907fdv-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/c907/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-c907fdv-float32-onnx-swin = "${bindir}/aibench/c907/float32/onnx_swin/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/c907/int8_asym_w_sym/onnx_swin/*"

PACKAGES = " \
aibench-c907fdv-float16-onnx-bert \
aibench-c907fdv-float16-onnx-efficientnet \
aibench-c907fdv-float16-onnx-mobilenetvit \
aibench-c907fdv-float16-onnx-mobilenetv2-12 \
aibench-c907fdv-float16-onnx-resnet50-v1-7 \
aibench-c907fdv-float16-onnx-retinaface \
aibench-c907fdv-float16-onnx-shufflenet-v2-9 \
aibench-c907fdv-float16-onnx-swin \
aibench-c907fdv-float32-onnx-bert \
aibench-c907fdv-float32-onnx-efficientnet \
aibench-c907fdv-float32-onnx-mobilenetvit \
aibench-c907fdv-float32-onnx-mobilenetv2-12 \
aibench-c907fdv-float32-onnx-resnet50-v1-7 \
aibench-c907fdv-float32-onnx-retinaface \
aibench-c907fdv-float32-onnx-shufflenet-v2-9 \
aibench-c907fdv-float32-onnx-swin \
aibench-c907fdv-int8-asym-w-sym-onnx-bert \
aibench-c907fdv-int8-asym-w-sym-onnx-efficientnet \
aibench-c907fdv-int8-asym-w-sym-onnx-mobilenetvit \
aibench-c907fdv-int8-asym-w-sym-onnx-mobilenetv2-12 \
aibench-c907fdv-int8-asym-w-sym-onnx-resnet50-v1-7 \
aibench-c907fdv-int8-asym-w-sym-onnx-retinaface \
aibench-c907fdv-int8-asym-w-sym-onnx-swin \
"

FILES:aibench-c908v-float16-onnx-bert = "${bindir}/aibench/c908/float16/onnx_bert/*"
FILES:aibench-c908v-float16-onnx-efficientnet = "${bindir}/aibench/c908/float16/onnx_efficientnet/*"
FILES:aibench-c908v-float16-onnx-mobilenetvit = "${bindir}/aibench/c908/float16/onnx_mobilenetVit/*"
FILES:aibench-c908v-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/c908/float16/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/c908/float16/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-float16-onnx-retinaface = "${bindir}/aibench/c908/float16/onnx_retinaface/*"
FILES:aibench-c908v-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/c908/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-c908v-float16-onnx-swin = "${bindir}/aibench/c908/float16/onnx_swin/*"
FILES:aibench-c908v-float16-w-int8-onnx-bert = "${bindir}/aibench/c908/float16_w_int8/onnx_bert/*"
FILES:aibench-c908v-float16-w-int8-onnx-efficientnet = "${bindir}/aibench/c908/float16_w_int8/onnx_efficientnet/*"
FILES:aibench-c908v-float16-w-int8-onnx-mobilenetvit = "${bindir}/aibench/c908/float16_w_int8/onnx_mobilenetVit/*"
FILES:aibench-c908v-float16-w-int8-onnx-mobilenetv2-12 = "${bindir}/aibench/c908/float16_w_int8/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-float16-w-int8-onnx-resnet50-v1-7 = "${bindir}/aibench/c908/float16_w_int8/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-float16-w-int8-onnx-retinaface = "${bindir}/aibench/c908/float16_w_int8/onnx_retinaface/*"
FILES:aibench-c908v-float16-w-int8-onnx-swin = "${bindir}/aibench/c908/float16_w_int8/onnx_swin/*"
FILES:aibench-c908v-float32-onnx-bert = "${bindir}/aibench/c908/float32/onnx_bert/*"
FILES:aibench-c908v-float32-onnx-efficientnet = "${bindir}/aibench/c908/float32/onnx_efficientnet/*"
FILES:aibench-c908v-float32-onnx-mobilenetvit = "${bindir}/aibench/c908/float32/onnx_mobilenetVit/*"
FILES:aibench-c908v-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/c908/float32/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/c908/float32/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-float32-onnx-retinaface = "${bindir}/aibench/c908/float32/onnx_retinaface/*"
FILES:aibench-c908v-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/c908/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-c908v-float32-onnx-swin = "${bindir}/aibench/c908/float32/onnx_swin/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/c908/int8_asym_w_sym/onnx_swin/*"

PACKAGES =+ " \
aibench-c908v-float16-onnx-bert \
aibench-c908v-float16-onnx-efficientnet \
aibench-c908v-float16-onnx-mobilenetvit \
aibench-c908v-float16-onnx-mobilenetv2-12 \
aibench-c908v-float16-onnx-resnet50-v1-7 \
aibench-c908v-float16-onnx-retinaface \
aibench-c908v-float16-onnx-shufflenet-v2-9 \
aibench-c908v-float16-onnx-swin \
aibench-c908v-float16-w-int8-onnx-bert \
aibench-c908v-float16-w-int8-onnx-efficientnet \
aibench-c908v-float16-w-int8-onnx-mobilenetvit \
aibench-c908v-float16-w-int8-onnx-mobilenetv2-12 \
aibench-c908v-float16-w-int8-onnx-resnet50-v1-7 \
aibench-c908v-float16-w-int8-onnx-retinaface \
aibench-c908v-float16-w-int8-onnx-swin \
aibench-c908v-float32-onnx-bert \
aibench-c908v-float32-onnx-efficientnet \
aibench-c908v-float32-onnx-mobilenetvit \
aibench-c908v-float32-onnx-mobilenetv2-12 \
aibench-c908v-float32-onnx-resnet50-v1-7 \
aibench-c908v-float32-onnx-retinaface \
aibench-c908v-float32-onnx-shufflenet-v2-9 \
aibench-c908v-float32-onnx-swin \
aibench-c908v-int8-asym-w-sym-onnx-bert \
aibench-c908v-int8-asym-w-sym-onnx-efficientnet \
aibench-c908v-int8-asym-w-sym-onnx-mobilenetvit \
aibench-c908v-int8-asym-w-sym-onnx-mobilenetv2-12 \
aibench-c908v-int8-asym-w-sym-onnx-resnet50-v1-7 \
aibench-c908v-int8-asym-w-sym-onnx-retinaface \
aibench-c908v-int8-asym-w-sym-onnx-swin \
"
