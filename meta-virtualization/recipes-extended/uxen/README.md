# uXen : Open Source type-2 hypervisor support

For any issues with the uXen recipes please make sure you CC:

    christopher.w.clark@gmail.com

## uxen-guest-tools : software for within guest VMs, kernel modules

* uxenhc        : hypercall driver
* uxenfb        : framebuffer driver
* uxenhid       : Human Input Device driver
* uxennet       : virtual network device driver
* uxenplatform  : uXen platform device driver
* uxenstor      : virtual storage device driver
* uxenv4vlib    : v4v, Hypervisor-Mediated data eXchange
* v4vvsock      : v4v vsock virtual network device driver
* v4vtest       : basic v4v vsock test

To produce a bootable VM disk image file:

    wic create directdisk -e uxen-guest-image-minimal

To test, copy the .direct file that wic produces to the host with uXen installed and write the following to a new file: `linux-vm.json`. Update the file path within it to point to your wic-generated disk image:

    {
      "name" : "Linux Virtual Machine",
      "boot-order" : "c",
      "block" :
        { "id": "ich0", "proto" : "raw", "xsnapshot" : true,
          "path" : "c:/Users/Yocto/directdisk-202001010100-sda.direct" },
      "memory" : 768,
      "net" : { "type" : "nic", "model" : "e1000" },
      "hpet" : 1,
      "vcpus" : 1,
      "use-v4v-net" : 1,
      "v4v-storage" : true,

      "" : ""
    }

and then boot it:

    uxendm -F linux-vm.json

References:
  https://www.bromium.com/opensource
  https://github.com/uxen-virt
  https://www.platformsecuritysummit.com/2018/speaker/pratt/
