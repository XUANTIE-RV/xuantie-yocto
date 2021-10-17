# Description
Yocto Project is an open-source community project that allows you to build your own Linux system, which makes embedded Linux simplely and accessibly. It helps developers create customized systems based on the Linux kernel by providing useful templates, tools, methods, and multiple hardware architecture support.

For more detailed information about Yocto, refer to the official website: https://docs.yoctoproject.org/

This is the yocto build code including all meta layers for the RISC-V based boards for Xuantie series CPU.


## RVB-ICE Board

### 1. Introduction

ICE is a Xuantie C910 based high performance SoC board developed by T-Head. The ICE SoC has integrated 3 Xuantie C910 cores (RISC-V 64) and 1 GPU core; featuring speed and intelligence with a high cost-effective ratio. The chip can provide 4K@60 HEVC/AVC/JPEG decoding ability, and varieties of high-speed interfaces and peripherals for controlling and data exchange; suits for 3D graphics, visual AI and multimedia processing.

More information can be found at: [RVB-ICE](https://occ.t-head.cn/vendor/detail/index?spm=a2cl5.14290816.0.0.d3ef1ae6HZSzL3&id=3906780664213024768&vendorId=3706716635429273600&module=4) Xuantie C910 board

### 2. Quick Start

#### 2.1 Build

- Get source code

  ```bash
  git clone https://github.com/T-head-Semi/xuantie-yocto.git
  ```

- Setup build environment
  
  ```bash
  cd xuantie-yocto
  source openembedded-core/oe-init-build-env thead-build/ice
  ```

- Build images

  ```bash
  bitbake thead-image-base
  ```

  Imges:
  ```bash
  ./tmp-glibc/deploy/images/ice/fw_jump.bin                   # opensbi
  ./tmp-glibc/deploy/images/ice/u-boot-with-spl.bin           # u-boot
  ./tmp-glibc/deploy/images/ice/uImge                         # kernel
  ./tmp-glibc/deploy/images/ice/ice.dtb                       # dtb
  ./tmp-glibc/deploy/images/ice/thead-image-base-ice.ext4     # rootfs
  ```

- Make boot.ext4

  Using `make_ext4fs` cmd: 

  If `make_ext4fs` is not found, please use `sudo pip install thead-tools` to install, or compile `make_ext4fs` from source code:

  ```bash
  git clone https://github.com/superr/make_ext4fs.git
  cd make_ext4fs
  make
  sudo cp make_ext4fs /usr/bin/
  ```

  Making boot.ext4: 

  ```bash
  mkdir boot
  cp ./tmp-glibc/deploy/images/ice/fw_jump.bin boot/.
  cp ./tmp-glibc/deploy/images/ice/uImge boot/.
  cp ./tmp-glibc/deploy/images/ice/ice.dtb boot/ice.dtb
  
  make_ext4fs -l 30M boot.ext4 boot
  ```

#### 2.2 Flash to Board

1. Prepare flash tools in host

   Using pip command install the thead-tools into your system. (Please using python2)

   ```bash
   sudo apt install python-pip
   sudo apt install fastboot
   
   sudo pip install thead-tools
   sudo pip install -i https://pypi.tuna.tsinghua.edu.cn/simple thead-tools
   ```

2. Flash the u-boot

   Connect RVB-ICE UART with USB Type-C to your host PC, ref: `RVB-ICE Type-C UART`

   ```bash
   root@linux > thead cct uart
   uart device list:
      /dev/ttyUSB0 - USB-Serial Controller
      /dev/ttyUSB1 - USB-Serial Controller
   
   root@linux > thead cct -u /dev/ttyUSB0 list
   Wait .....................
   CCT Version: 2
   memory device list:
     dev = ram0   , size =  256.0KB
     dev = emmc0  , size =    2.0MB
     dev = emmc1  , size =    2.0MB
     dev = emmc2  , size =    3.7GB
   ```

   We will put u-boot into the emmc0:

   ```bash
   thead cct -u /dev/ttyUSB0 download -f u-boot-with-spl.bin -d emmc0
   CCT Version: 2
   Send file 'u-boot-with-spl.bin' to 21:0 ...
   Writing at 0x00009800... (3%)
   
   Reset the board, and you will see:
   
   U-Boot 2020.01-g6cc5d59b0d (Dec 20 2020 - 08:37:37 +0000)
   
   CPU:   rv64imafdcvsu
   Model: T-HEAD c910 ice
   DRAM:  4 GiB
   GPU ChipDate is:0x20151217
   GPU Frequency is:500000KHz
   NPU ChipDate is:0x20190514
   DPU ChipDate is:0x20161213
   MMC:   mmc0@3fffb0000: 0
   Loading Environment from MMC... OK
   In:    serial@3fff73000
   Out:   serial@3fff73000
   Err:   serial@3fff73000
   Net:
   Warning: ethernet@3fffc0000 (eth0) using random MAC address -    e6:e2:ea:7a:30:ce
   eth0: ethernet@3fffc0000
   Hit any key to stop autoboot:  1
   ```

3. Prepare u-boot env setting

   In u-boot UART console to setup fastboot udp server.

   ```bash
   env default -a

   setenv uuid_rootfs "80a5a8e9-c744-491a-93c1-4f4194fd690b"
   setenv partitions "name=table,size=2031KB"
   setenv partitions "$partitions;name=boot,size=60MiB,type=boot"
   setenv partitions "$partitions;name=root,size=-,type=linux,   uuid=$uuid_rootfs"
   gpt write mmc 0 $partitions
   
   setenv ethaddr 00:a0:a0:a0:a0:a1
   setenv ipaddr 192.168.1.100 (Your IP)
   setenv netmask 255.255.255.0
   saveenv
   
   fastboot udp
   ```

4. Using fastboot install boot.ext4 & rootfs

   ```bash
   fastboot -s udp:192.168.1.100 flash boot boot.ext4
   fastboot -s udp:192.168.1.100 flash root rootfs.ext2
   ```


## D1-Nezha Board

## 1. Introduction

The T-Head C906 processor is based on the RV64GCV instruction set and includes customized arithmetic enhancement extension, bit manipulation extension, load store enhancement extension and TLB/Cache operations enhancement extension. The processor adopts a state of the 5 stages in-order pipeline. The C906 supports the Sv39 virtual address system with custom page attribute extensions. In addition, C906 includes standard CLINT and PLIC interrupt controllers, supports RV-compatible performance monitors.

More information can be found at: [D1-Nezha](https://occ.t-head.cn/vendor/detail/index?spm=a2cl5.14300867.0.0.38ff180fOy4Fih&id=3913672528451088384&vendorId=3878439890589003776&module=4) Xuantie C906 board


### 2. Quick Start

#### 2.1 Build

- Get source code

  ```bash
  git clone https://github.com/T-head-Semi/xuantie-yocto.git
  ```

- Setup Build Environment
  
  ```bash
  cd xuantie-yocto
  source openembedded-core/oe-init-build-env thead-build/d1
  ```

- Build Images

  ```bash
  bitbake thead-image-base
  ```

- Generate Image
  ```bash
  . pack/pack.sh
  
  # Image file
  pack/tina_d1-nezha_uart0.img
  ```

- Flash Image to Board
  
  [Flash Image](docs/Flash_image_D1.md)
