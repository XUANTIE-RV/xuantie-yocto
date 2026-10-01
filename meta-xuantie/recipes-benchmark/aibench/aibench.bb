DESCRIPTION = "AI benchmark for xuantie"
LICENSE = "CLOSED"

SRC_URI = "\
    ${@bb.utils.contains('MACHINECONFIG', 'public', \
    '${XUANTIE_GITSM}/xtai-benchmark.git;nobranch=1;protocol=https;name=xtai-benchmark', \
    '${XUANTIE_GITSM}/xtai-benchmark.git;branch=master;protocol=ssh;name=xtai-benchmark', d)} \
"

XUANTIE_LINUX_TAG ?= "${AUTOREV}"
SRCREV_xtai-benchmark = "${@bb.utils.contains('MACHINECONFIG', 'public', 'cdd13391006b09adf694ee1e0b1e005867e2ac78', '${XUANTIE_LINUX_TAG}', d)}"
PR = "r3"
PV = "1.0"
S = "${WORKDIR}/git"

AIBENCH_DOWNLOAD = "http://yocbook.oss-cn-hangzhou.aliyuncs.com/linux_image/XuanTie_V1.0.5/xtai-benchmark-bm"
SRC_URI:append:c907fdv = "${SRC_URI_C907FDV}"
SRC_URI:append:c908v = "${SRC_URI_C908V}"
SRC_URI:append:c920v2 = "${SRC_URI_C920V2}"
SRC_URI:append:c920v3 = "${SRC_URI_C920V3}"
SRC_URI:append:r908fdv = "${SRC_URI_R908FDV}"
SRC_URI:append:r908fdv-cp = "${SRC_URI_R908FDV}"

inherit autotools
inherit xuantie-set-package-arch-with-mcpu-benchmark
PACKAGE_ARCH = "${XUANTIE_MCPU_PACKAGE_ARCH}_external"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"


CFLAGS += " -Wno-implicit-function-declaration "
python do_compile() {
    import os
    cpu = None
    if d.getVar('XUANTIE_MCPU') == 'c907fdv':
        cpu = "../git/c907"
    elif d.getVar('XUANTIE_MCPU') == 'c908v':
        cpu = "../git/c908"
    elif d.getVar('XUANTIE_MCPU') == 'c920v2':
        cpu = "../git/c920v2"
    elif d.getVar('XUANTIE_MCPU') == 'c920v3':
        cpu = "../git/c920v3"
    elif d.getVar('XUANTIE_MCPU') == 'r908fdv' or d.getVar('XUANTIE_MCPU') == 'r908fdv-cp':
        cpu = "../git/r908"
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
            p = cpu.split("/")[2]
            if not p.startswith("r"):
                p = p[1:]
            bm = "{}_{}_{}_hhb.bm.tar.gz".format(p, quant_name, model)
            sen = "cd {} && mv {} {}".format(model_path, bm.strip(".tar.gz"), "hhb.bm")
            os.system(sen)

            cpu2 = None
            if cpu.split("/")[2]=="c907":
                cpu2 = "c907fdv"
            elif cpu.split("/")[2]=="c908":
                cpu2 = "c908v"
            elif cpu.split("/")[2]=="c920v2":
                cpu2 = "c920v2"
            elif cpu.split("/")[2]=="c920v3":
                cpu2 = "c920v3"
            elif cpu.split("/")[2]=="r908" and d.getVar('XUANTIE_MCPU') == 'r908fdv':
                cpu2 = "r908fdv"
            elif cpu.split("/")[2]=="r908" and d.getVar('XUANTIE_MCPU') == 'r908fdv-cp':
                cpu2 = "r908fdv-cp"
            out_path = "../image/usr/bin/aibench/{}/{}/{}".format(cpu2, quant_name, model)
            os.system("mkdir -p {}".format(out_path))

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
    elif d.getVar('XUANTIE_MCPU') == 'c920v2':
        cpu = "../git/c920v2"
    elif d.getVar('XUANTIE_MCPU') == 'c920v3':
        cpu = "../git/c920v3"
    elif d.getVar('XUANTIE_MCPU') == 'r908fdv' or d.getVar('XUANTIE_MCPU') == 'r908fdv-cp':
        cpu = "../git/r908"
    collect(cpu, d)
}

SRC_URI_C907FDV = " \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_bert_hhb.bm.tar.gz;name=907_float16_onnx_bert;subdir=git/c907/float16/onnx_bert \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_efficientnet_hhb.bm.tar.gz;name=907_float16_onnx_efficientnet;subdir=git/c907/float16/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_mobilenetVit_hhb.bm.tar.gz;name=907_float16_onnx_mobilenetVit;subdir=git/c907/float16/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=907_float16_onnx_mobilenetv2-12;subdir=git/c907/float16/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=907_float16_onnx_resnet50-v1-7;subdir=git/c907/float16/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_retinaface_hhb.bm.tar.gz;name=907_float16_onnx_retinaface;subdir=git/c907/float16/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=907_float16_onnx_shufflenet-v2-9;subdir=git/c907/float16/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/907_float16_onnx_swin_hhb.bm.tar.gz;name=907_float16_onnx_swin;subdir=git/c907/float16/onnx_swin \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_bert_hhb.bm.tar.gz;name=907_float32_onnx_bert;subdir=git/c907/float32/onnx_bert \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_efficientnet_hhb.bm.tar.gz;name=907_float32_onnx_efficientnet;subdir=git/c907/float32/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_mobilenetVit_hhb.bm.tar.gz;name=907_float32_onnx_mobilenetVit;subdir=git/c907/float32/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=907_float32_onnx_mobilenetv2-12;subdir=git/c907/float32/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=907_float32_onnx_resnet50-v1-7;subdir=git/c907/float32/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_retinaface_hhb.bm.tar.gz;name=907_float32_onnx_retinaface;subdir=git/c907/float32/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=907_float32_onnx_shufflenet-v2-9;subdir=git/c907/float32/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/907_float32_onnx_swin_hhb.bm.tar.gz;name=907_float32_onnx_swin;subdir=git/c907/float32/onnx_swin \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_bert_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_bert;subdir=git/c907/int8_asym_w_sym/onnx_bert \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_efficientnet_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_efficientnet;subdir=git/c907/int8_asym_w_sym/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_mobilenetVit_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_mobilenetVit;subdir=git/c907/int8_asym_w_sym/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_mobilenetv2-12;subdir=git/c907/int8_asym_w_sym/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_resnet50-v1-7;subdir=git/c907/int8_asym_w_sym/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_retinaface_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_retinaface;subdir=git/c907/int8_asym_w_sym/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/907_int8_asym_w_sym_onnx_swin_hhb.bm.tar.gz;name=907_int8_asym_w_sym_onnx_swin;subdir=git/c907/int8_asym_w_sym/onnx_swin \
"


SRC_URI_C908V = " \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_bert_hhb.bm.tar.gz;name=908_float16_onnx_bert;subdir=git/c908/float16/onnx_bert \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_efficientnet_hhb.bm.tar.gz;name=908_float16_onnx_efficientnet;subdir=git/c908/float16/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_mobilenetVit_hhb.bm.tar.gz;name=908_float16_onnx_mobilenetVit;subdir=git/c908/float16/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=908_float16_onnx_mobilenetv2-12;subdir=git/c908/float16/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=908_float16_onnx_resnet50-v1-7;subdir=git/c908/float16/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_retinaface_hhb.bm.tar.gz;name=908_float16_onnx_retinaface;subdir=git/c908/float16/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=908_float16_onnx_shufflenet-v2-9;subdir=git/c908/float16/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/908_float16_onnx_swin_hhb.bm.tar.gz;name=908_float16_onnx_swin;subdir=git/c908/float16/onnx_swin \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_bert_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_bert;subdir=git/c908/float16_w_int8/onnx_bert \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_efficientnet_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_efficientnet;subdir=git/c908/float16_w_int8/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_mobilenetVit_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_mobilenetVit;subdir=git/c908/float16_w_int8/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_mobilenetv2-12;subdir=git/c908/float16_w_int8/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_resnet50-v1-7;subdir=git/c908/float16_w_int8/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_retinaface_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_retinaface;subdir=git/c908/float16_w_int8/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/908_float16_w_int8_onnx_swin_hhb.bm.tar.gz;name=908_float16_w_int8_onnx_swin;subdir=git/c908/float16_w_int8/onnx_swin \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_bert_hhb.bm.tar.gz;name=908_float32_onnx_bert;subdir=git/c908/float32/onnx_bert \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_efficientnet_hhb.bm.tar.gz;name=908_float32_onnx_efficientnet;subdir=git/c908/float32/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_mobilenetVit_hhb.bm.tar.gz;name=908_float32_onnx_mobilenetVit;subdir=git/c908/float32/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=908_float32_onnx_mobilenetv2-12;subdir=git/c908/float32/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=908_float32_onnx_resnet50-v1-7;subdir=git/c908/float32/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_retinaface_hhb.bm.tar.gz;name=908_float32_onnx_retinaface;subdir=git/c908/float32/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=908_float32_onnx_shufflenet-v2-9;subdir=git/c908/float32/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/908_float32_onnx_swin_hhb.bm.tar.gz;name=908_float32_onnx_swin;subdir=git/c908/float32/onnx_swin \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_bert_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_bert;subdir=git/c908/int8_asym_w_sym/onnx_bert \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_efficientnet_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_efficientnet;subdir=git/c908/int8_asym_w_sym/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_mobilenetVit_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_mobilenetVit;subdir=git/c908/int8_asym_w_sym/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_mobilenetv2-12;subdir=git/c908/int8_asym_w_sym/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_resnet50-v1-7;subdir=git/c908/int8_asym_w_sym/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_retinaface_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_retinaface;subdir=git/c908/int8_asym_w_sym/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/908_int8_asym_w_sym_onnx_swin_hhb.bm.tar.gz;name=908_int8_asym_w_sym_onnx_swin;subdir=git/c908/int8_asym_w_sym/onnx_swin \
"

SRC_URI_C920V2 = " \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_bert_hhb.bm.tar.gz;name=920v2_float16_onnx_bert;subdir=git/c920v2/float16/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_efficientnet_hhb.bm.tar.gz;name=920v2_float16_onnx_efficientnet;subdir=git/c920v2/float16/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v2_float16_onnx_mobilenetVit;subdir=git/c920v2/float16/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v2_float16_onnx_mobilenetv2-12;subdir=git/c920v2/float16/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v2_float16_onnx_resnet50-v1-7;subdir=git/c920v2/float16/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_retinaface_hhb.bm.tar.gz;name=920v2_float16_onnx_retinaface;subdir=git/c920v2/float16/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v2_float16_onnx_shufflenet-v2-9;subdir=git/c920v2/float16/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v2_float16_onnx_swin_hhb.bm.tar.gz;name=920v2_float16_onnx_swin;subdir=git/c920v2/float16/onnx_swin \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_bert_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_bert;subdir=git/c920v2/float16_w_int8/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_efficientnet_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_efficientnet;subdir=git/c920v2/float16_w_int8/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_mobilenetVit;subdir=git/c920v2/float16_w_int8/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_mobilenetv2-12;subdir=git/c920v2/float16_w_int8/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_resnet50-v1-7;subdir=git/c920v2/float16_w_int8/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_retinaface_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_retinaface;subdir=git/c920v2/float16_w_int8/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_shufflenet-v2-9;subdir=git/c920v2/float16_w_int8/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v2_float16_w_int8_onnx_swin_hhb.bm.tar.gz;name=920v2_float16_w_int8_onnx_swin;subdir=git/c920v2/float16_w_int8/onnx_swin \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_bert_hhb.bm.tar.gz;name=920v2_float32_onnx_bert;subdir=git/c920v2/float32/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_efficientnet_hhb.bm.tar.gz;name=920v2_float32_onnx_efficientnet;subdir=git/c920v2/float32/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v2_float32_onnx_mobilenetVit;subdir=git/c920v2/float32/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v2_float32_onnx_mobilenetv2-12;subdir=git/c920v2/float32/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v2_float32_onnx_resnet50-v1-7;subdir=git/c920v2/float32/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_retinaface_hhb.bm.tar.gz;name=920v2_float32_onnx_retinaface;subdir=git/c920v2/float32/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v2_float32_onnx_shufflenet-v2-9;subdir=git/c920v2/float32/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v2_float32_onnx_swin_hhb.bm.tar.gz;name=920v2_float32_onnx_swin;subdir=git/c920v2/float32/onnx_swin \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_bert_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_bert;subdir=git/c920v2/int8_asym_w_sym/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_efficientnet_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_efficientnet;subdir=git/c920v2/int8_asym_w_sym/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_mobilenetVit;subdir=git/c920v2/int8_asym_w_sym/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_mobilenetv2-12;subdir=git/c920v2/int8_asym_w_sym/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_resnet50-v1-7;subdir=git/c920v2/int8_asym_w_sym/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_retinaface_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_retinaface;subdir=git/c920v2/int8_asym_w_sym/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_shufflenet-v2-9;subdir=git/c920v2/int8_asym_w_sym/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v2_int8_asym_w_sym_onnx_swin_hhb.bm.tar.gz;name=920v2_int8_asym_w_sym_onnx_swin;subdir=git/c920v2/int8_asym_w_sym/onnx_swin \
"

SRC_URI_C920V3 = " \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_bert_hhb.bm.tar.gz;name=920v3_float16_onnx_bert;subdir=git/c920v3/float16/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_efficientnet_hhb.bm.tar.gz;name=920v3_float16_onnx_efficientnet;subdir=git/c920v3/float16/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v3_float16_onnx_mobilenetVit;subdir=git/c920v3/float16/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v3_float16_onnx_mobilenetv2-12;subdir=git/c920v3/float16/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v3_float16_onnx_resnet50-v1-7;subdir=git/c920v3/float16/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_retinaface_hhb.bm.tar.gz;name=920v3_float16_onnx_retinaface;subdir=git/c920v3/float16/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v3_float16_onnx_shufflenet-v2-9;subdir=git/c920v3/float16/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v3_float16_onnx_swin_hhb.bm.tar.gz;name=920v3_float16_onnx_swin;subdir=git/c920v3/float16/onnx_swin \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_bert_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_bert;subdir=git/c920v3/float16_w_int8/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_efficientnet_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_efficientnet;subdir=git/c920v3/float16_w_int8/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_mobilenetVit;subdir=git/c920v3/float16_w_int8/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_mobilenetv2-12;subdir=git/c920v3/float16_w_int8/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_resnet50-v1-7;subdir=git/c920v3/float16_w_int8/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_retinaface_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_retinaface;subdir=git/c920v3/float16_w_int8/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_shufflenet-v2-9;subdir=git/c920v3/float16_w_int8/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v3_float16_w_int8_onnx_swin_hhb.bm.tar.gz;name=920v3_float16_w_int8_onnx_swin;subdir=git/c920v3/float16_w_int8/onnx_swin \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_bert_hhb.bm.tar.gz;name=920v3_float32_onnx_bert;subdir=git/c920v3/float32/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_efficientnet_hhb.bm.tar.gz;name=920v3_float32_onnx_efficientnet;subdir=git/c920v3/float32/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v3_float32_onnx_mobilenetVit;subdir=git/c920v3/float32/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v3_float32_onnx_mobilenetv2-12;subdir=git/c920v3/float32/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v3_float32_onnx_resnet50-v1-7;subdir=git/c920v3/float32/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_retinaface_hhb.bm.tar.gz;name=920v3_float32_onnx_retinaface;subdir=git/c920v3/float32/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v3_float32_onnx_shufflenet-v2-9;subdir=git/c920v3/float32/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v3_float32_onnx_swin_hhb.bm.tar.gz;name=920v3_float32_onnx_swin;subdir=git/c920v3/float32/onnx_swin \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_bert_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_bert;subdir=git/c920v3/int8_asym_w_sym/onnx_bert \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_efficientnet_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_efficientnet;subdir=git/c920v3/int8_asym_w_sym/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_mobilenetVit_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_mobilenetVit;subdir=git/c920v3/int8_asym_w_sym/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_mobilenetv2-12;subdir=git/c920v3/int8_asym_w_sym/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_resnet50-v1-7;subdir=git/c920v3/int8_asym_w_sym/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_retinaface_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_retinaface;subdir=git/c920v3/int8_asym_w_sym/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_shufflenet-v2-9;subdir=git/c920v3/int8_asym_w_sym/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/920v3_int8_asym_w_sym_onnx_swin_hhb.bm.tar.gz;name=920v3_int8_asym_w_sym_onnx_swin;subdir=git/c920v3/int8_asym_w_sym/onnx_swin \
"

SRC_URI_R908FDV = " \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_bert_hhb.bm.tar.gz;name=r908_float16_onnx_bert;subdir=git/r908/float16/onnx_bert \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_efficientnet_hhb.bm.tar.gz;name=r908_float16_onnx_efficientnet;subdir=git/r908/float16/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_mobilenetVit_hhb.bm.tar.gz;name=r908_float16_onnx_mobilenetVit;subdir=git/r908/float16/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=r908_float16_onnx_mobilenetv2-12;subdir=git/r908/float16/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=r908_float16_onnx_resnet50-v1-7;subdir=git/r908/float16/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_retinaface_hhb.bm.tar.gz;name=r908_float16_onnx_retinaface;subdir=git/r908/float16/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=r908_float16_onnx_shufflenet-v2-9;subdir=git/r908/float16/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/r908_float16_onnx_swin_hhb.bm.tar.gz;name=r908_float16_onnx_swin;subdir=git/r908/float16/onnx_swin \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_bert_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_bert;subdir=git/r908/float16_w_int8/onnx_bert \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_efficientnet_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_efficientnet;subdir=git/r908/float16_w_int8/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_mobilenetVit_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_mobilenetVit;subdir=git/r908/float16_w_int8/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_mobilenetv2-12;subdir=git/r908/float16_w_int8/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_resnet50-v1-7;subdir=git/r908/float16_w_int8/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_retinaface_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_retinaface;subdir=git/r908/float16_w_int8/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_shufflenet-v2-9;subdir=git/r908/float16_w_int8/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/r908_float16_w_int8_onnx_swin_hhb.bm.tar.gz;name=r908_float16_w_int8_onnx_swin;subdir=git/r908/float16_w_int8/onnx_swin \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_bert_hhb.bm.tar.gz;name=r908_float32_onnx_bert;subdir=git/r908/float32/onnx_bert \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_efficientnet_hhb.bm.tar.gz;name=r908_float32_onnx_efficientnet;subdir=git/r908/float32/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_mobilenetVit_hhb.bm.tar.gz;name=r908_float32_onnx_mobilenetVit;subdir=git/r908/float32/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=r908_float32_onnx_mobilenetv2-12;subdir=git/r908/float32/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=r908_float32_onnx_resnet50-v1-7;subdir=git/r908/float32/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_retinaface_hhb.bm.tar.gz;name=r908_float32_onnx_retinaface;subdir=git/r908/float32/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=r908_float32_onnx_shufflenet-v2-9;subdir=git/r908/float32/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/r908_float32_onnx_swin_hhb.bm.tar.gz;name=r908_float32_onnx_swin;subdir=git/r908/float32/onnx_swin \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_bert_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_bert;subdir=git/r908/int8_asym_w_sym/onnx_bert \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_efficientnet_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_efficientnet;subdir=git/r908/int8_asym_w_sym/onnx_efficientnet \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_mobilenetVit_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_mobilenetVit;subdir=git/r908/int8_asym_w_sym/onnx_mobilenetVit \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_mobilenetv2-12_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_mobilenetv2-12;subdir=git/r908/int8_asym_w_sym/onnx_mobilenetv2-12 \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_resnet50-v1-7_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_resnet50-v1-7;subdir=git/r908/int8_asym_w_sym/onnx_resnet50-v1-7 \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_retinaface_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_retinaface;subdir=git/r908/int8_asym_w_sym/onnx_retinaface \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_shufflenet-v2-9_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_shufflenet-v2-9;subdir=git/r908/int8_asym_w_sym/onnx_shufflenet-v2-9 \
    ${AIBENCH_DOWNLOAD}/r908_int8_asym_w_sym_onnx_swin_hhb.bm.tar.gz;name=r908_int8_asym_w_sym_onnx_swin;subdir=git/r908/int8_asym_w_sym/onnx_swin \
"

SRC_URI[907_float16_onnx_bert.sha256sum] = "c88d504aefa8ec903b5edf1cce6a89bd42fdde99c9642f42ab1c7500160dd739"
SRC_URI[907_float16_onnx_efficientnet.sha256sum] = "34c546b0a03eb97f88eb459b8ca12dcf06e0eacc3c9e2639f9250493176be10d"
SRC_URI[907_float16_onnx_mobilenetVit.sha256sum] = "ddc9c9df739e7e62979c8dd5a7387e629fb9935ad3d9c4c0a059d31d96d1edc2"
SRC_URI[907_float16_onnx_mobilenetv2-12.sha256sum] = "8609ef6914ab0f9f8e81fbb6a15172c911a17dc907fd219435f7f56f1d02d47c"
SRC_URI[907_float16_onnx_resnet50-v1-7.sha256sum] = "cabe21c5bd03f5e2000dc62d37c7d92e21d4a3ee0e03fe6e3d3047044fd6b55e"
SRC_URI[907_float16_onnx_retinaface.sha256sum] = "2fd8c0b68c3076767ee8a215c452d03957fe4c2eccb387942fb1864477d93324"
SRC_URI[907_float16_onnx_shufflenet-v2-9.sha256sum] = "0669d383a1911fd8b5182e72056fe254dd15574a293883deca235df6a0303573"
SRC_URI[907_float16_onnx_swin.sha256sum] = "540fce6e738e86ef961ab46a60104a4d2ec9a53923b65c5e049f45e076be65a8"
SRC_URI[907_float32_onnx_bert.sha256sum] = "26875d79b28a9d3df4ffbff5661c0308cc2f44a60bb2cb739240c1599413e321"
SRC_URI[907_float32_onnx_efficientnet.sha256sum] = "3b7d89e719135e071152c0ef5915f50695b903b931768acb48d218da63bac4d1"
SRC_URI[907_float32_onnx_mobilenetVit.sha256sum] = "0bb0da2d048954ea17dd8c109c9395863f3efd5249f3dd916d1ff6dfe23db7f7"
SRC_URI[907_float32_onnx_mobilenetv2-12.sha256sum] = "ddfce5672d4c5e065e4d40e69c6e8281230fc7f4527bf4e815cb26e68bf8327f"
SRC_URI[907_float32_onnx_resnet50-v1-7.sha256sum] = "ac927a0aa14c86e59682330dcb2f2b252628a30fcb34a0f09f4df47e34d4dbc4"
SRC_URI[907_float32_onnx_retinaface.sha256sum] = "3d77a278d37d25db89a8e23355d315dc2b6eb9e534c6bd61258dc934abf52f3a"
SRC_URI[907_float32_onnx_shufflenet-v2-9.sha256sum] = "8e862ca8a66d983631cc3041e675e30cd4d174a4609c9e94260b223e4dce8c35"
SRC_URI[907_float32_onnx_swin.sha256sum] = "d9d9032d1729af4d8b80108a0cc875aca066580bda68d107eed6a95304218e65"
SRC_URI[907_int8_asym_w_sym_onnx_bert.sha256sum] = "c463ecf0c202213669c67da7e1b8981f0eb180346f57bb4d71502d4cd8883099"
SRC_URI[907_int8_asym_w_sym_onnx_efficientnet.sha256sum] = "68ac2b8c15de00c47dc708645a00dfe00b08eea35555549b70a2bea0d2e774f2"
SRC_URI[907_int8_asym_w_sym_onnx_mobilenetVit.sha256sum] = "baa76f29f45fad4fcd74383ff72f72226f11937fd77e58f066d11b9be4260857"
SRC_URI[907_int8_asym_w_sym_onnx_mobilenetv2-12.sha256sum] = "a40758d72e9f20722f35e0c9135dcc17ff4d9b252109b9dd9d5e77946d1f25fd"
SRC_URI[907_int8_asym_w_sym_onnx_resnet50-v1-7.sha256sum] = "2e3c34b18babb1aa9b8ff14c80977edf923402a0b81e9414adf59217bd55bf9f"
SRC_URI[907_int8_asym_w_sym_onnx_retinaface.sha256sum] = "61e171040a00bc01db187fda1718c39fda067dcac5512f8b588b77c4522ca266"
SRC_URI[907_int8_asym_w_sym_onnx_swin.sha256sum] = "8528d58e65a2d0acdad3f97c9bc531b9f9d2309469ece42a7de29621b5e0edfd"
SRC_URI[908_float16_onnx_bert.sha256sum] = "ae671083ccc8cdbb99813b6bf24306dc22fcab0621f6f467f5b497c69602e524"
SRC_URI[908_float16_onnx_efficientnet.sha256sum] = "49e0735eec6bc03075d7ca5b9e600e1fb0ebf7cc90a356d8b2a27d36a8063c4e"
SRC_URI[908_float16_onnx_mobilenetVit.sha256sum] = "36e427a20c035ac1a5687c84229dfd644e3dfe8b98d9f4a9d6fb377d617b9e3c"
SRC_URI[908_float16_onnx_mobilenetv2-12.sha256sum] = "d697f9a5f04190d5f59e66b5fc7838ce462f76a998376d74938e7c7c7db69026"
SRC_URI[908_float16_onnx_resnet50-v1-7.sha256sum] = "34d579d927d823b3a4a88b2c56665b4f02845d7c2a5ca85e0bf4e25dc049963b"
SRC_URI[908_float16_onnx_retinaface.sha256sum] = "a077929e00a196a7abba6cdb438cf485e95f5d30a48cb88a4677ac96481c5ed4"
SRC_URI[908_float16_onnx_shufflenet-v2-9.sha256sum] = "8eb6932c1745fd6a39876011b3be7ac146021b400fabeeee39c22dd8a7830624"
SRC_URI[908_float16_onnx_swin.sha256sum] = "bde9f0c076ece6dddff09690dd5a4608071bedc937e92f43a18b79340986cd9a"
SRC_URI[908_float16_w_int8_onnx_bert.sha256sum] = "232b47002bc4ef6544b95a37bfdb71b8b9e5a4a977172079eb837b01f51622d1"
SRC_URI[908_float16_w_int8_onnx_efficientnet.sha256sum] = "39a4e3091196b5957a09f6e3c18d98d58cbe2e6bbbbe066a00285ebe58573757"
SRC_URI[908_float16_w_int8_onnx_mobilenetVit.sha256sum] = "76e039de301487884e01ee87194f6d5298abed4796dfef3fde5ea8aeb552fdde"
SRC_URI[908_float16_w_int8_onnx_mobilenetv2-12.sha256sum] = "0c4f5ffd26fd6cca6636597f01976a5e1e26ad81193dde07a492327ce4dedc66"
SRC_URI[908_float16_w_int8_onnx_resnet50-v1-7.sha256sum] = "235c968a87216acb802defb0245b64eba0ee6371b75d06c7a2aec1686d775f38"
SRC_URI[908_float16_w_int8_onnx_retinaface.sha256sum] = "310e2ffd75a21a3c8742ece3ca6ffaff24720dc4bd94cf8201454ca5115857fe"
SRC_URI[908_float16_w_int8_onnx_swin.sha256sum] = "875379ae48f29570f590bd964990c72ddf40a259c395f160c57d606b58b86c61"
SRC_URI[908_float32_onnx_bert.sha256sum] = "5f3c6e233f1ae122d2a6fb6fec6a215390227fe9f72940a0606b952c4dc6cc38"
SRC_URI[908_float32_onnx_efficientnet.sha256sum] = "cac897a839ec3468c52f06a1c0d6719680d74db232b9a492f9cd592c35396e87"
SRC_URI[908_float32_onnx_mobilenetVit.sha256sum] = "7343f1d4b363eee431404d325a357075df755cff6fd8a1c13c1df98c293f8e14"
SRC_URI[908_float32_onnx_mobilenetv2-12.sha256sum] = "8a055b9eaed06e43ce440606ea129d29af332779162cd4673c9b01eb7561b822"
SRC_URI[908_float32_onnx_resnet50-v1-7.sha256sum] = "fdbac10bfe4e5538a716e99054547bba53962e7a52bbbb331fb35c71929aad40"
SRC_URI[908_float32_onnx_retinaface.sha256sum] = "97c3bd1b693cbe28a9b65fe340234c0eecb33182f110c7cf4e213071610c3655"
SRC_URI[908_float32_onnx_shufflenet-v2-9.sha256sum] = "79bfca298c78e18289c1fcdef506591396bc5deb4ebdadee3b96a7fe517a9764"
SRC_URI[908_float32_onnx_swin.sha256sum] = "765f01d9f347fdb0598d0a9298d1007c813469d5d09e29f62522e8b129028c16"
SRC_URI[908_int8_asym_w_sym_onnx_bert.sha256sum] = "64a6b2883a17616cbbafb58f3cae427b57846f82fb7658bc74331101d61d92ee"
SRC_URI[908_int8_asym_w_sym_onnx_efficientnet.sha256sum] = "178e111bad6a3f31762c34dc2ad41852c383802224a2d0424d4b61a094be049b"
SRC_URI[908_int8_asym_w_sym_onnx_mobilenetVit.sha256sum] = "907342cc2d2c4bee9c675540490fcc71e1000a9eb50a1dc6a06261670262ff0d"
SRC_URI[908_int8_asym_w_sym_onnx_mobilenetv2-12.sha256sum] = "71e5a7e00a48d85ef838a5cf0f0cf31aed2590298f6c5f6aa5fb57e7f14f151e"
SRC_URI[908_int8_asym_w_sym_onnx_resnet50-v1-7.sha256sum] = "c30f3fc75097b80ad69fcfe08e3fc7d5b113b173aa33a366670abd69ce09a32a"
SRC_URI[908_int8_asym_w_sym_onnx_retinaface.sha256sum] = "131506c289d57d593bce75cad06b0c5201fce03b94220cb33190e6eac0c79acc"
SRC_URI[908_int8_asym_w_sym_onnx_swin.sha256sum] = "f4c703abdeede046792c5a696171f1fe4832d8a6d8a4cf2e5f91a7b765078970"
SRC_URI[920v2_float16_onnx_bert.sha256sum] = "a6b84e3b29328f9805d48b5e6d2c80ee5e189f2c0d623334531e337b4c6eadf8"
SRC_URI[920v2_float16_onnx_efficientnet.sha256sum] = "029b3192fa33fac0ea625fd0ffe2caa9e8d6b4e12d7b87334b19db3526b11851"
SRC_URI[920v2_float16_onnx_mobilenetVit.sha256sum] = "8d1588a64a9fd457b6e57afa303ad32e6d446d5cfcb9c307f26cb8876fbbebc6"
SRC_URI[920v2_float16_onnx_mobilenetv2-12.sha256sum] = "097b80f40ded05d3071a339d82c6d11f039df0321d19e32ddb1e902d9f755c47"
SRC_URI[920v2_float16_onnx_resnet50-v1-7.sha256sum] = "26b30df72c588bdf8bcf3050de6f876ba1f28f916e17a6ab5c98efd7d7573b1d"
SRC_URI[920v2_float16_onnx_retinaface.sha256sum] = "89c5a2db39bd6fe466f517a5a4e60fabbc8c94c7ae9227218d4d0f7cd55686a5"
SRC_URI[920v2_float16_onnx_shufflenet-v2-9.sha256sum] = "04b13217ada437822d80ac6192e9c2e5dc65848b787d846df3e8abd93e4735ec"
SRC_URI[920v2_float16_onnx_swin.sha256sum] = "c02c81b7ea73d35d36d9230f8ff5fc77229e86dbbfda60a215ecb55c69e433fd"
SRC_URI[920v2_float16_w_int8_onnx_bert.sha256sum] = "8205ea2ac0109aeabb044ecb7483f0c4dcbb5d16d4ee04941562d73939646875"
SRC_URI[920v2_float16_w_int8_onnx_efficientnet.sha256sum] = "e0cbe63b9b23c544bbef3210c73edb8b5be7130380d1a454b77b4f089599f987"
SRC_URI[920v2_float16_w_int8_onnx_mobilenetVit.sha256sum] = "8f7d77c6be6ca2203cf4e036cda0a6f7348b28b09fd21adef941e163cc53849e"
SRC_URI[920v2_float16_w_int8_onnx_mobilenetv2-12.sha256sum] = "bd521d69d7361d284f04bd80743ac156c64ff18d59cb932bec22d1f74b969dc8"
SRC_URI[920v2_float16_w_int8_onnx_resnet50-v1-7.sha256sum] = "bad5b34849cea48ec3c598cbb7a55b0cc3be8eab2d5707dadf32ab0ed2dd0858"
SRC_URI[920v2_float16_w_int8_onnx_retinaface.sha256sum] = "5d3d8a002c3f97395be9066de99f9c2ab29a67fceaf87c6e2173b076c113d282"
SRC_URI[920v2_float16_w_int8_onnx_shufflenet-v2-9.sha256sum] = "2ad749a0f1a08feb7fbcf9dd649f673ea32deeb1e1e83fe5c4ed4fbfac1efea9"
SRC_URI[920v2_float16_w_int8_onnx_swin.sha256sum] = "caa0ad744f3a3f447c9cb604e169aa9da5e559648dad5c5e42b4c7e0f3f76c11"
SRC_URI[920v2_float32_onnx_bert.sha256sum] = "84a2fe060efabecff05fec553842f2efa954582c2bb1a72d3e2a98a567b022c6"
SRC_URI[920v2_float32_onnx_efficientnet.sha256sum] = "9177df602492f86be2165f14920406a9e64d5427cd069e20ec3a31d30e3e1efb"
SRC_URI[920v2_float32_onnx_mobilenetVit.sha256sum] = "8dc3ccdb7d99550aa1ac83b6893aa70c6c079361ce3172882747446debf430aa"
SRC_URI[920v2_float32_onnx_mobilenetv2-12.sha256sum] = "dcb1d5642b86403150732d26a53892a5070bf6e273a39605ac268f7fab002dd8"
SRC_URI[920v2_float32_onnx_resnet50-v1-7.sha256sum] = "735efc71ec5faeeb22ddf407e64e4daffdbc7dd80c30d79f68ca5a317046ce16"
SRC_URI[920v2_float32_onnx_retinaface.sha256sum] = "4060ce7c6c924a207c3af15997c6636781901e32c1291f2577736f85f40abca9"
SRC_URI[920v2_float32_onnx_shufflenet-v2-9.sha256sum] = "4b67467f8dfd536497cd3ec359b82c4fb19fbf955ac30ae175cede2a45047c9b"
SRC_URI[920v2_float32_onnx_swin.sha256sum] = "a687d886645d5dea7f2a9d79fab9c56f7bcb2e97e7948cb209ae9d8348d66d8c"
SRC_URI[920v2_int8_asym_w_sym_onnx_bert.sha256sum] = "5da07f2ce6e642f5cbb80b91269cfc33cfb8bcd7f9b50c117d05dc0a18cb5ae5"
SRC_URI[920v2_int8_asym_w_sym_onnx_efficientnet.sha256sum] = "1acca828734328c6740fbf35851b04f3a03343166b75749954734f4063872d46"
SRC_URI[920v2_int8_asym_w_sym_onnx_mobilenetVit.sha256sum] = "272476796d8c7b4e5832f3ebae045f983419a99f41391c9cbcdceec9c8059d6d"
SRC_URI[920v2_int8_asym_w_sym_onnx_mobilenetv2-12.sha256sum] = "3ea874c0da40d072bdccafffda27e40420ee9487d9f78bdafd21680f83fb7ddf"
SRC_URI[920v2_int8_asym_w_sym_onnx_resnet50-v1-7.sha256sum] = "66c9a4add97b11179e4495d39afab2773d7560a3ca5eda17cea0a01f1eac84b1"
SRC_URI[920v2_int8_asym_w_sym_onnx_retinaface.sha256sum] = "5005724d9a8259188e5c9ea406e69cb935d1206a4a0076eab026be21c2d5c35d"
SRC_URI[920v2_int8_asym_w_sym_onnx_shufflenet-v2-9.sha256sum] = "91c01204204b1d9a23d14f3195b12d919a25300c6acbe848ab3bc0c7906ee03e"
SRC_URI[920v2_int8_asym_w_sym_onnx_swin.sha256sum] = "6581b8ac22d317bbf000e0de5256896cb777f059e5c5b0fe6c88606448e7add3"
SRC_URI[920v3_float16_onnx_bert.sha256sum] = "a4270a7dd11e8a18c9df4327aa21ed270367941c43a3f24ee9bb4e8c3b14d80a"
SRC_URI[920v3_float16_onnx_efficientnet.sha256sum] = "1dbe4c10e1ab9573085625ad08ec1797c7c873032f799ae0abb37b41de43477a"
SRC_URI[920v3_float16_onnx_mobilenetVit.sha256sum] = "298cee65304a0b15661ec6565bba570ab4d0ad6728b26b835f8ccecbadd5009f"
SRC_URI[920v3_float16_onnx_mobilenetv2-12.sha256sum] = "b27c530123adaf8148d8ff59eb9287ec75d2b8f757770093c197720f4b1ca396"
SRC_URI[920v3_float16_onnx_resnet50-v1-7.sha256sum] = "764bceef15259975c0d7d3c70d56904ad622ca8a88774963fb3b9676cd02694f"
SRC_URI[920v3_float16_onnx_retinaface.sha256sum] = "93076899ce9af22e773c4b23c2fad040cda8177be9bbd977c3a8b625e8bd0c1d"
SRC_URI[920v3_float16_onnx_shufflenet-v2-9.sha256sum] = "5473c07107ab2b49f6af2f93c7b20fa253c5813d3ceb3f9fddd4b786e8153833"
SRC_URI[920v3_float16_onnx_swin.sha256sum] = "1faaa3e45ea0d8e177c59693e869ae37a6dd4cec542e4bc1db3d98dd6524a07e"
SRC_URI[920v3_float16_w_int8_onnx_bert.sha256sum] = "3172e3b1e138c2086839273e8b4d6605b89d84bd87f10b4a2fa3ea9ee537ae96"
SRC_URI[920v3_float16_w_int8_onnx_efficientnet.sha256sum] = "5289df8392f53e252050c643f2db355c89890b62c3442760d7ce9f5dfbd50486"
SRC_URI[920v3_float16_w_int8_onnx_mobilenetVit.sha256sum] = "3fdfdb20017d4481de469583addaf83679270a4970329917f8e16324c62f57eb"
SRC_URI[920v3_float16_w_int8_onnx_mobilenetv2-12.sha256sum] = "6456ddbe405d961cec9d5e16f74975c9e7e69660c739cd50e95ceb3d4ebd4648"
SRC_URI[920v3_float16_w_int8_onnx_resnet50-v1-7.sha256sum] = "538b0599795d7ef3be473ca1604efeaa2d13a53c56c35cccf457ba6bb726144a"
SRC_URI[920v3_float16_w_int8_onnx_retinaface.sha256sum] = "e58a403c926119ac9295fd2b6209ab2b701a9c4da1b695270d524c3f01b1787f"
SRC_URI[920v3_float16_w_int8_onnx_shufflenet-v2-9.sha256sum] = "9709d54bcc4441dab8343ed15d7d704fc4c9efff1e53cf9b63b2735992e3325b"
SRC_URI[920v3_float16_w_int8_onnx_swin.sha256sum] = "c5e3b754fbfaa7de81d89137a6fe22a161504c8fb8b350fa18e005f80f0128a4"
SRC_URI[920v3_float32_onnx_bert.sha256sum] = "102135d861018d5e59e2a60db4f1c300917ce02fd21e1ec7d152fd7ef810283d"
SRC_URI[920v3_float32_onnx_efficientnet.sha256sum] = "1e0e39287ae71faf95c4fd51833d96543e8198723d32db6433fe157132d16126"
SRC_URI[920v3_float32_onnx_mobilenetVit.sha256sum] = "19447a7787e586fbeeb63bcb67e74c1909c9db6a917106c8aadd2b75425631fb"
SRC_URI[920v3_float32_onnx_mobilenetv2-12.sha256sum] = "3c933d3ce99837fbc7265f9a7ee99809528285ef233bc85bdd05512af1f68c6e"
SRC_URI[920v3_float32_onnx_resnet50-v1-7.sha256sum] = "5048bb95ec07aa1dd4f6112b2342310fc6778a7fb9b2719943c14c5bafba418a"
SRC_URI[920v3_float32_onnx_retinaface.sha256sum] = "409a446f28bd26be690d92b3d4f36de1be276119d493111094d8128c144bce9a"
SRC_URI[920v3_float32_onnx_shufflenet-v2-9.sha256sum] = "35900f65564adeff49448bca78fa086fc6683514222aea1e93b58cb8d0220b7e"
SRC_URI[920v3_float32_onnx_swin.sha256sum] = "7e3e6d10e7cff093039118c3e5d8bf03e8e1bf9b2ab25c773aee82dfc3128287"
SRC_URI[920v3_int8_asym_w_sym_onnx_bert.sha256sum] = "80eba933131ce95b4975239da84c044b7d50988c8251bae50cbafcd4f1cd2d2f"
SRC_URI[920v3_int8_asym_w_sym_onnx_efficientnet.sha256sum] = "a42ddac0fa9977cd970288c117a007b0eaf625f1f73936db488595b17a95d5e4"
SRC_URI[920v3_int8_asym_w_sym_onnx_mobilenetVit.sha256sum] = "90233e1df7f44a2068b3d077dc80a10c99ee0bed87c3bb98e1fad9c8542b79f4"
SRC_URI[920v3_int8_asym_w_sym_onnx_mobilenetv2-12.sha256sum] = "07de7cc3b84be69fc7a20613d9684d1fbe0cf1b6788c4805146bd1416cd4602a"
SRC_URI[920v3_int8_asym_w_sym_onnx_resnet50-v1-7.sha256sum] = "80cbdf688f62dc0b29a7893774705ed92bfde7195c801564907c3ee4902396f4"
SRC_URI[920v3_int8_asym_w_sym_onnx_retinaface.sha256sum] = "62ac67337d3d6f26845fc867a9ba32bfa12807baddc92d012ab009e670391014"
SRC_URI[920v3_int8_asym_w_sym_onnx_shufflenet-v2-9.sha256sum] = "d46ee757cc359976e45815bc3fed4a00ef8699dbb471a257cc6d9ea2688aba5d"
SRC_URI[920v3_int8_asym_w_sym_onnx_swin.sha256sum] = "f8ed8c01586004dee707b54d98c9dbb33f1f0e2aaac1926169ce53a6038de2f2"
SRC_URI[r908_float16_onnx_bert.sha256sum] = "5a276e0752574f437601225160ab03e01ccddf715e0fca1e09a415de7b84be44"
SRC_URI[r908_float16_onnx_efficientnet.sha256sum] = "34b09c9d8734443e97ceeb6b44025d8a0c75d7eaca1c57b01338e6c8a0c79a1c"
SRC_URI[r908_float16_onnx_mobilenetVit.sha256sum] = "2a0116af4da3ad07ea74f589e70944368ae01aca6e4912e2400c6faa5713c51c"
SRC_URI[r908_float16_onnx_mobilenetv2-12.sha256sum] = "3958489bf4dc1558558118052539b2b11759d9edb19fd059cf86fe0e5c04c9eb"
SRC_URI[r908_float16_onnx_resnet50-v1-7.sha256sum] = "0672c48b40c7153991604c56a6135e184bfcacff4326b5c6a2b97b1bd35c3797"
SRC_URI[r908_float16_onnx_retinaface.sha256sum] = "d0b92e2978daac06f918bf5c064735d1796f813793a6862008a90ad2509be986"
SRC_URI[r908_float16_onnx_shufflenet-v2-9.sha256sum] = "f0accd6540e990e774221356aa83aeebf32c522f4241b605cfe52eef181d0a74"
SRC_URI[r908_float16_onnx_swin.sha256sum] = "2786cab4bdbc061fd8ec2424dd0e818b99b8a1b149767a98e81d6cf4631714e5"
SRC_URI[r908_float16_w_int8_onnx_bert.sha256sum] = "22c6a4dd657370c85dd7dd05c3bd271b890f163a868cf251edf68b5a7380baf3"
SRC_URI[r908_float16_w_int8_onnx_efficientnet.sha256sum] = "8881f9a0c4e0a56ab9c0062d16be02dceb2191876e2f97871d0beda9e4c55c80"
SRC_URI[r908_float16_w_int8_onnx_mobilenetVit.sha256sum] = "e2fe1c980e33bc6416a377e1e8b5fd1c943b55fc46700dfd7487a33c8eaf5b72"
SRC_URI[r908_float16_w_int8_onnx_mobilenetv2-12.sha256sum] = "670d99f2d34611e586cef317bba0296b02e92972d3bbcd039a7ae3acdc27c833"
SRC_URI[r908_float16_w_int8_onnx_resnet50-v1-7.sha256sum] = "bd399e0fa4a0bc1a641ea866f5877b782ea1ede4a6a3f196322f43159c3dbc8e"
SRC_URI[r908_float16_w_int8_onnx_retinaface.sha256sum] = "0f10f45f0926e87f615b4b2d2e5c76edfd1092aeb78f10f728c42f30222ea129"
SRC_URI[r908_float16_w_int8_onnx_shufflenet-v2-9.sha256sum] = "8956adf8346ec60c0ccfaa7aa7bb6998f1a8395933f8deaf777def345e8e0330"
SRC_URI[r908_float16_w_int8_onnx_swin.sha256sum] = "5982b12cb188f647bd0b7a03f9304468f950e1aa0de1b43c093105e693330861"
SRC_URI[r908_float32_onnx_bert.sha256sum] = "ba3313b8c7a74bdf5dab25cd7b4ac31833994b8c0a45d6a258aae6a8cda4f9ea"
SRC_URI[r908_float32_onnx_efficientnet.sha256sum] = "e2f12eb8b9e63cf41ee77c4c6a5df3a411292c8e40f2298aec61cdc6a73bd4f5"
SRC_URI[r908_float32_onnx_mobilenetVit.sha256sum] = "a5b2739eb8dfdc38dcf17d04868fffe1b7b181cf5f67b1d306ca6f510e2b759a"
SRC_URI[r908_float32_onnx_mobilenetv2-12.sha256sum] = "8ffb52460a79f1c25328debca47338b65359761fd8185846e088320cc9869c88"
SRC_URI[r908_float32_onnx_resnet50-v1-7.sha256sum] = "29a13636e3fda29022d217e6659744dcf5a4152ca07dd82e3816a2a1f835e02b"
SRC_URI[r908_float32_onnx_retinaface.sha256sum] = "88843de17f308ec348072906ac2a1b5ae105d7654d4ef2341f4cff8083a885e5"
SRC_URI[r908_float32_onnx_shufflenet-v2-9.sha256sum] = "17e8189dc63252838010a90f5fe44011c5a3ddd0ea0bb64d14a7de71e5bca0dd"
SRC_URI[r908_float32_onnx_swin.sha256sum] = "dede278791f8bc7d227f803c2c8b28bf9c11186e0cd152b8404d3262cc73e494"
SRC_URI[r908_int8_asym_w_sym_onnx_bert.sha256sum] = "217923e3286e214b94578e418b66c65376bdba37912914c719ccfe2ac21364cc"
SRC_URI[r908_int8_asym_w_sym_onnx_efficientnet.sha256sum] = "2d448e0bef22f902f9dc4cd88020944e0279ff246a3eef0aa31869b1c0486b9a"
SRC_URI[r908_int8_asym_w_sym_onnx_mobilenetVit.sha256sum] = "a6b79f1a076af0ed3778127d05636b9894b9a204efb1a8f14a10c7d3e67de21a"
SRC_URI[r908_int8_asym_w_sym_onnx_mobilenetv2-12.sha256sum] = "d704ee2ef2514d4245734ef60d1610590dfaa34e6ef885d9ced11c839701c537"
SRC_URI[r908_int8_asym_w_sym_onnx_resnet50-v1-7.sha256sum] = "984e02a9d08896e914123b31d28737954053163aea24288a5ce7d1b095ec5ccc"
SRC_URI[r908_int8_asym_w_sym_onnx_retinaface.sha256sum] = "6e03741f9d3159f86876ecc11f823ed6524e37f3c4915f0688b14110ecdef4b5"
SRC_URI[r908_int8_asym_w_sym_onnx_shufflenet-v2-9.sha256sum] = "8caf95996b91b82274c75145eff92ba2a90290768830e6769ce2541f64592b5f"
SRC_URI[r908_int8_asym_w_sym_onnx_swin.sha256sum] = "9689cd5c8bed0b5f71977c68a79e596741ae4bccd3d0034e577d0103bcc67626"


FILES:aibench-c907fdv-float16-onnx-bert = "${bindir}/aibench/c907fdv/float16/onnx_bert/*"
FILES:aibench-c907fdv-float16-onnx-efficientnet = "${bindir}/aibench/c907fdv/float16/onnx_efficientnet/*"
FILES:aibench-c907fdv-float16-onnx-mobilenetvit = "${bindir}/aibench/c907fdv/float16/onnx_mobilenetVit/*"
FILES:aibench-c907fdv-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/c907fdv/float16/onnx_mobilenetv2-12/*"
FILES:aibench-c907fdv-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/c907fdv/float16/onnx_resnet50-v1-7/*"
FILES:aibench-c907fdv-float16-onnx-retinaface = "${bindir}/aibench/c907fdv/float16/onnx_retinaface/*"
FILES:aibench-c907fdv-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/c907fdv/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-c907fdv-float16-onnx-swin = "${bindir}/aibench/c907fdv/float16/onnx_swin/*"
FILES:aibench-c907fdv-float32-onnx-bert = "${bindir}/aibench/c907fdv/float32/onnx_bert/*"
FILES:aibench-c907fdv-float32-onnx-efficientnet = "${bindir}/aibench/c907fdv/float32/onnx_efficientnet/*"
FILES:aibench-c907fdv-float32-onnx-mobilenetvit = "${bindir}/aibench/c907fdv/float32/onnx_mobilenetVit/*"
FILES:aibench-c907fdv-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/c907fdv/float32/onnx_mobilenetv2-12/*"
FILES:aibench-c907fdv-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/c907fdv/float32/onnx_resnet50-v1-7/*"
FILES:aibench-c907fdv-float32-onnx-retinaface = "${bindir}/aibench/c907fdv/float32/onnx_retinaface/*"
FILES:aibench-c907fdv-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/c907fdv/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-c907fdv-float32-onnx-swin = "${bindir}/aibench/c907fdv/float32/onnx_swin/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-c907fdv-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/c907fdv/int8_asym_w_sym/onnx_swin/*"

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

FILES:aibench-c908v-float16-onnx-bert = "${bindir}/aibench/c908v/float16/onnx_bert/*"
FILES:aibench-c908v-float16-onnx-efficientnet = "${bindir}/aibench/c908v/float16/onnx_efficientnet/*"
FILES:aibench-c908v-float16-onnx-mobilenetvit = "${bindir}/aibench/c908v/float16/onnx_mobilenetVit/*"
FILES:aibench-c908v-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/c908v/float16/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/c908v/float16/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-float16-onnx-retinaface = "${bindir}/aibench/c908v/float16/onnx_retinaface/*"
FILES:aibench-c908v-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/c908v/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-c908v-float16-onnx-swin = "${bindir}/aibench/c908v/float16/onnx_swin/*"
FILES:aibench-c908v-float16-w-int8-onnx-bert = "${bindir}/aibench/c908v/float16_w_int8/onnx_bert/*"
FILES:aibench-c908v-float16-w-int8-onnx-efficientnet = "${bindir}/aibench/c908v/float16_w_int8/onnx_efficientnet/*"
FILES:aibench-c908v-float16-w-int8-onnx-mobilenetvit = "${bindir}/aibench/c908v/float16_w_int8/onnx_mobilenetVit/*"
FILES:aibench-c908v-float16-w-int8-onnx-mobilenetv2-12 = "${bindir}/aibench/c908v/float16_w_int8/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-float16-w-int8-onnx-resnet50-v1-7 = "${bindir}/aibench/c908v/float16_w_int8/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-float16-w-int8-onnx-retinaface = "${bindir}/aibench/c908v/float16_w_int8/onnx_retinaface/*"
FILES:aibench-c908v-float16-w-int8-onnx-swin = "${bindir}/aibench/c908v/float16_w_int8/onnx_swin/*"
FILES:aibench-c908v-float32-onnx-bert = "${bindir}/aibench/c908v/float32/onnx_bert/*"
FILES:aibench-c908v-float32-onnx-efficientnet = "${bindir}/aibench/c908v/float32/onnx_efficientnet/*"
FILES:aibench-c908v-float32-onnx-mobilenetvit = "${bindir}/aibench/c908v/float32/onnx_mobilenetVit/*"
FILES:aibench-c908v-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/c908v/float32/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/c908v/float32/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-float32-onnx-retinaface = "${bindir}/aibench/c908v/float32/onnx_retinaface/*"
FILES:aibench-c908v-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/c908v/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-c908v-float32-onnx-swin = "${bindir}/aibench/c908v/float32/onnx_swin/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-c908v-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/c908v/int8_asym_w_sym/onnx_swin/*"

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

FILES:aibench-c920v2-float16-onnx-bert = "${bindir}/aibench/c920v2/float16/onnx_bert/*"
FILES:aibench-c920v2-float16-onnx-efficientnet = "${bindir}/aibench/c920v2/float16/onnx_efficientnet/*"
FILES:aibench-c920v2-float16-onnx-mobilenetVit = "${bindir}/aibench/c920v2/float16/onnx_mobilenetVit/*"
FILES:aibench-c920v2-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v2/float16/onnx_mobilenetv2-12/*"
FILES:aibench-c920v2-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v2/float16/onnx_resnet50-v1-7/*"
FILES:aibench-c920v2-float16-onnx-retinaface = "${bindir}/aibench/c920v2/float16/onnx_retinaface/*"
FILES:aibench-c920v2-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v2/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v2-float16-onnx-swin = "${bindir}/aibench/c920v2/float16/onnx_swin/*"
FILES:aibench-c920v2-float16-w-int8-onnx-bert = "${bindir}/aibench/c920v2/float16_w_int8/onnx_bert/*"
FILES:aibench-c920v2-float16-w-int8-onnx-efficientnet = "${bindir}/aibench/c920v2/float16_w_int8/onnx_efficientnet/*"
FILES:aibench-c920v2-float16-w-int8-onnx-mobilenetVit = "${bindir}/aibench/c920v2/float16_w_int8/onnx_mobilenetVit/*"
FILES:aibench-c920v2-float16-w-int8-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v2/float16_w_int8/onnx_mobilenetv2-12/*"
FILES:aibench-c920v2-float16-w-int8-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v2/float16_w_int8/onnx_resnet50-v1-7/*"
FILES:aibench-c920v2-float16-w-int8-onnx-retinaface = "${bindir}/aibench/c920v2/float16_w_int8/onnx_retinaface/*"
FILES:aibench-c920v2-float16-w-int8-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v2/float16_w_int8/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v2-float16-w-int8-onnx-swin = "${bindir}/aibench/c920v2/float16_w_int8/onnx_swin/*"
FILES:aibench-c920v2-float32-onnx-bert = "${bindir}/aibench/c920v2/float32/onnx_bert/*"
FILES:aibench-c920v2-float32-onnx-efficientnet = "${bindir}/aibench/c920v2/float32/onnx_efficientnet/*"
FILES:aibench-c920v2-float32-onnx-mobilenetVit = "${bindir}/aibench/c920v2/float32/onnx_mobilenetVit/*"
FILES:aibench-c920v2-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v2/float32/onnx_mobilenetv2-12/*"
FILES:aibench-c920v2-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v2/float32/onnx_resnet50-v1-7/*"
FILES:aibench-c920v2-float32-onnx-retinaface = "${bindir}/aibench/c920v2/float32/onnx_retinaface/*"
FILES:aibench-c920v2-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v2/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v2-float32-onnx-swin = "${bindir}/aibench/c920v2/float32/onnx_swin/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-mobilenetVit = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v2-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/c920v2/int8_asym_w_sym/onnx_swin/*"

PACKAGES =+ " \
aibench-c920v2-float16-onnx-bert \
aibench-c920v2-float16-onnx-efficientnet \
aibench-c920v2-float16-onnx-mobilenetVit \
aibench-c920v2-float16-onnx-mobilenetv2-12 \
aibench-c920v2-float16-onnx-resnet50-v1-7 \
aibench-c920v2-float16-onnx-retinaface \
aibench-c920v2-float16-onnx-shufflenet-v2-9 \
aibench-c920v2-float16-onnx-swin \
aibench-c920v2-float16-w-int8-onnx-bert \
aibench-c920v2-float16-w-int8-onnx-efficientnet \
aibench-c920v2-float16-w-int8-onnx-mobilenetVit \
aibench-c920v2-float16-w-int8-onnx-mobilenetv2-12 \
aibench-c920v2-float16-w-int8-onnx-resnet50-v1-7 \
aibench-c920v2-float16-w-int8-onnx-retinaface \
aibench-c920v2-float16-w-int8-onnx-shufflenet-v2-9 \
aibench-c920v2-float16-w-int8-onnx-swin \
aibench-c920v2-float32-onnx-bert \
aibench-c920v2-float32-onnx-efficientnet \
aibench-c920v2-float32-onnx-mobilenetVit \
aibench-c920v2-float32-onnx-mobilenetv2-12 \
aibench-c920v2-float32-onnx-resnet50-v1-7 \
aibench-c920v2-float32-onnx-retinaface \
aibench-c920v2-float32-onnx-shufflenet-v2-9 \
aibench-c920v2-float32-onnx-swin \
aibench-c920v2-int8-asym-w-sym-onnx-bert \
aibench-c920v2-int8-asym-w-sym-onnx-efficientnet \
aibench-c920v2-int8-asym-w-sym-onnx-mobilenetVit \
aibench-c920v2-int8-asym-w-sym-onnx-mobilenetv2-12 \
aibench-c920v2-int8-asym-w-sym-onnx-resnet50-v1-7 \
aibench-c920v2-int8-asym-w-sym-onnx-retinaface \
aibench-c920v2-int8-asym-w-sym-onnx-shufflenet-v2-9 \
aibench-c920v2-int8-asym-w-sym-onnx-swin \
"

FILES:aibench-c920v3-float16-onnx-bert = "${bindir}/aibench/c920v3/float16/onnx_bert/*"
FILES:aibench-c920v3-float16-onnx-efficientnet = "${bindir}/aibench/c920v3/float16/onnx_efficientnet/*"
FILES:aibench-c920v3-float16-onnx-mobilenetvit = "${bindir}/aibench/c920v3/float16/onnx_mobilenetVit/*"
FILES:aibench-c920v3-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v3/float16/onnx_mobilenetv2-12/*"
FILES:aibench-c920v3-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v3/float16/onnx_resnet50-v1-7/*"
FILES:aibench-c920v3-float16-onnx-retinaface = "${bindir}/aibench/c920v3/float16/onnx_retinaface/*"
FILES:aibench-c920v3-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v3/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v3-float16-onnx-swin = "${bindir}/aibench/c920v3/float16/onnx_swin/*"
FILES:aibench-c920v3-float16-w-int8-onnx-bert = "${bindir}/aibench/c920v3/float16_w_int8/onnx_bert/*"
FILES:aibench-c920v3-float16-w-int8-onnx-efficientnet = "${bindir}/aibench/c920v3/float16_w_int8/onnx_efficientnet/*"
FILES:aibench-c920v3-float16-w-int8-onnx-mobilenetvit = "${bindir}/aibench/c920v3/float16_w_int8/onnx_mobilenetVit/*"
FILES:aibench-c920v3-float16-w-int8-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v3/float16_w_int8/onnx_mobilenetv2-12/*"
FILES:aibench-c920v3-float16-w-int8-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v3/float16_w_int8/onnx_resnet50-v1-7/*"
FILES:aibench-c920v3-float16-w-int8-onnx-retinaface = "${bindir}/aibench/c920v3/float16_w_int8/onnx_retinaface/*"
FILES:aibench-c920v3-float16-w-int8-onnx-swin = "${bindir}/aibench/c920v3/float16_w_int8/onnx_swin/*"
FILES:aibench-c920v3-float16-w-int8-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v3/float16_w_int8/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v3-float32-onnx-bert = "${bindir}/aibench/c920v3/float32/onnx_bert/*"
FILES:aibench-c920v3-float32-onnx-efficientnet = "${bindir}/aibench/c920v3/float32/onnx_efficientnet/*"
FILES:aibench-c920v3-float32-onnx-mobilenetvit = "${bindir}/aibench/c920v3/float32/onnx_mobilenetVit/*"
FILES:aibench-c920v3-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v3/float32/onnx_mobilenetv2-12/*"
FILES:aibench-c920v3-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v3/float32/onnx_resnet50-v1-7/*"
FILES:aibench-c920v3-float32-onnx-retinaface = "${bindir}/aibench/c920v3/float32/onnx_retinaface/*"
FILES:aibench-c920v3-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v3/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-c920v3-float32-onnx-swin = "${bindir}/aibench/c920v3/float32/onnx_swin/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_swin/*"
FILES:aibench-c920v3-int8-asym-w-sym-onnx-shufflenet-v2-9 = "${bindir}/aibench/c920v3/int8_asym_w_sym/onnx_shufflenet-v2-9/*"

PACKAGES =+ " \
aibench-c920v3-float16-onnx-bert \
aibench-c920v3-float16-onnx-efficientnet \
aibench-c920v3-float16-onnx-mobilenetvit \
aibench-c920v3-float16-onnx-mobilenetv2-12 \
aibench-c920v3-float16-onnx-resnet50-v1-7 \
aibench-c920v3-float16-onnx-retinaface \
aibench-c920v3-float16-onnx-shufflenet-v2-9 \
aibench-c920v3-float16-onnx-swin \
aibench-c920v3-float16-w-int8-onnx-bert \
aibench-c920v3-float16-w-int8-onnx-efficientnet \
aibench-c920v3-float16-w-int8-onnx-mobilenetvit \
aibench-c920v3-float16-w-int8-onnx-mobilenetv2-12 \
aibench-c920v3-float16-w-int8-onnx-resnet50-v1-7 \
aibench-c920v3-float16-w-int8-onnx-retinaface \
aibench-c920v3-float16-w-int8-onnx-swin \
aibench-c920v3-float16-w-int8-onnx-shufflenet-v2-9 \
aibench-c920v3-float32-onnx-bert \
aibench-c920v3-float32-onnx-efficientnet \
aibench-c920v3-float32-onnx-mobilenetvit \
aibench-c920v3-float32-onnx-mobilenetv2-12 \
aibench-c920v3-float32-onnx-resnet50-v1-7 \
aibench-c920v3-float32-onnx-retinaface \
aibench-c920v3-float32-onnx-shufflenet-v2-9 \
aibench-c920v3-float32-onnx-swin \
aibench-c920v3-int8-asym-w-sym-onnx-bert \
aibench-c920v3-int8-asym-w-sym-onnx-efficientnet \
aibench-c920v3-int8-asym-w-sym-onnx-mobilenetvit \
aibench-c920v3-int8-asym-w-sym-onnx-mobilenetv2-12 \
aibench-c920v3-int8-asym-w-sym-onnx-resnet50-v1-7 \
aibench-c920v3-int8-asym-w-sym-onnx-retinaface \
aibench-c920v3-int8-asym-w-sym-onnx-swin \
aibench-c920v3-int8-asym-w-sym-onnx-shufflenet-v2-9 \
"

FILES:aibench-r908fdv-float16-onnx-bert = "${bindir}/aibench/r908fdv/float16/onnx_bert/*"
FILES:aibench-r908fdv-float16-onnx-efficientnet = "${bindir}/aibench/r908fdv/float16/onnx_efficientnet/*"
FILES:aibench-r908fdv-float16-onnx-mobilenetvit = "${bindir}/aibench/r908fdv/float16/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv/float16/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv/float16/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-float16-onnx-retinaface = "${bindir}/aibench/r908fdv/float16/onnx_retinaface/*"
FILES:aibench-r908fdv-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-r908fdv-float16-onnx-swin = "${bindir}/aibench/r908fdv/float16/onnx_swin/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-bert = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_bert/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-efficientnet = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_efficientnet/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-mobilenetvit = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-retinaface = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_retinaface/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-swin = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_swin/*"
FILES:aibench-r908fdv-float16-w-int8-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv/float16_w_int8/onnx_shufflenet-v2-9/*"
FILES:aibench-r908fdv-float32-onnx-bert = "${bindir}/aibench/r908fdv/float32/onnx_bert/*"
FILES:aibench-r908fdv-float32-onnx-efficientnet = "${bindir}/aibench/r908fdv/float32/onnx_efficientnet/*"
FILES:aibench-r908fdv-float32-onnx-mobilenetvit = "${bindir}/aibench/r908fdv/float32/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv/float32/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv/float32/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-float32-onnx-retinaface = "${bindir}/aibench/r908fdv/float32/onnx_retinaface/*"
FILES:aibench-r908fdv-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-r908fdv-float32-onnx-swin = "${bindir}/aibench/r908fdv/float32/onnx_swin/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_swin/*"
FILES:aibench-r908fdv-int8-asym-w-sym-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv/int8_asym_w_sym/onnx_shufflenet-v2-9/*"

PACKAGES =+ " \
aibench-r908fdv-float16-onnx-bert \
aibench-r908fdv-float16-onnx-efficientnet \
aibench-r908fdv-float16-onnx-mobilenetvit \
aibench-r908fdv-float16-onnx-mobilenetv2-12 \
aibench-r908fdv-float16-onnx-resnet50-v1-7 \
aibench-r908fdv-float16-onnx-retinaface \
aibench-r908fdv-float16-onnx-shufflenet-v2-9 \
aibench-r908fdv-float16-onnx-swin \
aibench-r908fdv-float16-w-int8-onnx-bert \
aibench-r908fdv-float16-w-int8-onnx-efficientnet \
aibench-r908fdv-float16-w-int8-onnx-mobilenetvit \
aibench-r908fdv-float16-w-int8-onnx-mobilenetv2-12 \
aibench-r908fdv-float16-w-int8-onnx-resnet50-v1-7 \
aibench-r908fdv-float16-w-int8-onnx-retinaface \
aibench-r908fdv-float16-w-int8-onnx-swin \
aibench-r908fdv-float16-w-int8-onnx-shufflenet-v2-9 \
aibench-r908fdv-float32-onnx-bert \
aibench-r908fdv-float32-onnx-efficientnet \
aibench-r908fdv-float32-onnx-mobilenetvit \
aibench-r908fdv-float32-onnx-mobilenetv2-12 \
aibench-r908fdv-float32-onnx-resnet50-v1-7 \
aibench-r908fdv-float32-onnx-retinaface \
aibench-r908fdv-float32-onnx-shufflenet-v2-9 \
aibench-r908fdv-float32-onnx-swin \
aibench-r908fdv-int8-asym-w-sym-onnx-bert \
aibench-r908fdv-int8-asym-w-sym-onnx-efficientnet \
aibench-r908fdv-int8-asym-w-sym-onnx-mobilenetvit \
aibench-r908fdv-int8-asym-w-sym-onnx-mobilenetv2-12 \
aibench-r908fdv-int8-asym-w-sym-onnx-resnet50-v1-7 \
aibench-r908fdv-int8-asym-w-sym-onnx-retinaface \
aibench-r908fdv-int8-asym-w-sym-onnx-swin \
aibench-r908fdv-int8-asym-w-sym-onnx-shufflenet-v2-9 \
"

FILES:aibench-r908fdv-cp-float16-onnx-bert = "${bindir}/aibench/r908fdv-cp/float16/onnx_bert/*"
FILES:aibench-r908fdv-cp-float16-onnx-efficientnet = "${bindir}/aibench/r908fdv-cp/float16/onnx_efficientnet/*"
FILES:aibench-r908fdv-cp-float16-onnx-mobilenetvit = "${bindir}/aibench/r908fdv-cp/float16/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-cp-float16-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv-cp/float16/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-cp-float16-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv-cp/float16/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-cp-float16-onnx-retinaface = "${bindir}/aibench/r908fdv-cp/float16/onnx_retinaface/*"
FILES:aibench-r908fdv-cp-float16-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv-cp/float16/onnx_shufflenet-v2-9/*"
FILES:aibench-r908fdv-cp-float16-onnx-swin = "${bindir}/aibench/r908fdv-cp/float16/onnx_swin/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-bert = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_bert/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-efficientnet = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_efficientnet/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-mobilenetvit = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-retinaface = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_retinaface/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-swin = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_swin/*"
FILES:aibench-r908fdv-cp-float16-w-int8-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv-cp/float16_w_int8/onnx_shufflenet-v2-9/*"
FILES:aibench-r908fdv-cp-float32-onnx-bert = "${bindir}/aibench/r908fdv-cp/float32/onnx_bert/*"
FILES:aibench-r908fdv-cp-float32-onnx-efficientnet = "${bindir}/aibench/r908fdv-cp/float32/onnx_efficientnet/*"
FILES:aibench-r908fdv-cp-float32-onnx-mobilenetvit = "${bindir}/aibench/r908fdv-cp/float32/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-cp-float32-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv-cp/float32/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-cp-float32-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv-cp/float32/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-cp-float32-onnx-retinaface = "${bindir}/aibench/r908fdv-cp/float32/onnx_retinaface/*"
FILES:aibench-r908fdv-cp-float32-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv-cp/float32/onnx_shufflenet-v2-9/*"
FILES:aibench-r908fdv-cp-float32-onnx-swin = "${bindir}/aibench/r908fdv-cp/float32/onnx_swin/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-bert = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_bert/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-efficientnet = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_efficientnet/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-mobilenetvit = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_mobilenetVit/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-mobilenetv2-12 = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_mobilenetv2-12/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-resnet50-v1-7 = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_resnet50-v1-7/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-retinaface = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_retinaface/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-swin = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_swin/*"
FILES:aibench-r908fdv-cp-int8-asym-w-sym-onnx-shufflenet-v2-9 = "${bindir}/aibench/r908fdv-cp/int8_asym_w_sym/onnx_shufflenet-v2-9/*"

PACKAGES =+ " \
aibench-r908fdv-cp-float16-onnx-bert \
aibench-r908fdv-cp-float16-onnx-efficientnet \
aibench-r908fdv-cp-float16-onnx-mobilenetvit \
aibench-r908fdv-cp-float16-onnx-mobilenetv2-12 \
aibench-r908fdv-cp-float16-onnx-resnet50-v1-7 \
aibench-r908fdv-cp-float16-onnx-retinaface \
aibench-r908fdv-cp-float16-onnx-shufflenet-v2-9 \
aibench-r908fdv-cp-float16-onnx-swin \
aibench-r908fdv-cp-float16-w-int8-onnx-bert \
aibench-r908fdv-cp-float16-w-int8-onnx-efficientnet \
aibench-r908fdv-cp-float16-w-int8-onnx-mobilenetvit \
aibench-r908fdv-cp-float16-w-int8-onnx-mobilenetv2-12 \
aibench-r908fdv-cp-float16-w-int8-onnx-resnet50-v1-7 \
aibench-r908fdv-cp-float16-w-int8-onnx-retinaface \
aibench-r908fdv-cp-float16-w-int8-onnx-swin \
aibench-r908fdv-cp-float16-w-int8-onnx-shufflenet-v2-9 \
aibench-r908fdv-cp-float32-onnx-bert \
aibench-r908fdv-cp-float32-onnx-efficientnet \
aibench-r908fdv-cp-float32-onnx-mobilenetvit \
aibench-r908fdv-cp-float32-onnx-mobilenetv2-12 \
aibench-r908fdv-cp-float32-onnx-resnet50-v1-7 \
aibench-r908fdv-cp-float32-onnx-retinaface \
aibench-r908fdv-cp-float32-onnx-shufflenet-v2-9 \
aibench-r908fdv-cp-float32-onnx-swin \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-bert \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-efficientnet \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-mobilenetvit \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-mobilenetv2-12 \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-resnet50-v1-7 \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-retinaface \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-swin \
aibench-r908fdv-cp-int8-asym-w-sym-onnx-shufflenet-v2-9 \
"

