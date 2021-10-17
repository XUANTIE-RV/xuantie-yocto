#!/bin/sh

mkdir -p -m 755 /dev/by-name
ln -s /dev/ubi0_9  /dev/by-name/UDISK

mount -t ubifs /dev/by-name/UDISK /mnt/UDISK
mkdir -p /mnt/UDISK/upper /mnt/UDISK/workdir

mount -n -t overlay overlayfs:/mnt/UDISK/overlay -o rw,noatime,lowerdir=/,upperdir=/mnt/UDISK/upper,workdir=/mnt/UDISK/workdir /mnt

mount -n /proc -o noatime,move /mnt/proc
pivot_root /mnt /mnt/rom

mount -n /rom/dev -o noatime,move /dev
