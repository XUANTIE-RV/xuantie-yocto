#!/bin/sh

mkdir -p -m 755 /dev/by-name
ln -s /dev/ubi0_9  /dev/by-name/UDISK

mount -t ubifs /dev/by-name/UDISK /mnt/UDISK
mkdir -p /mnt/UDISK/upper /mnt/UDISK/workdir

# mount -n -t overlay overlayfs:/mnt/UDISK/overlay -o rw,noatime,lowerdir=/,upperdir=/mnt/UDISK/upper,workdir=/mnt/UDISK/workdir /mnt
mount -n -t overlay overlayfs:/mnt/UDISK/overlay -o rw,noatime,lowerdir=/home/root,upperdir=/mnt/UDISK/upper,workdir=/mnt/UDISK/workdir /home/root

mkdir -p /mnt/UDISK/data_upper /mnt/UDISK/data_workdir
mount -n -t overlay overlayfs:/mnt/UDISK/overlay-data -o rw,noatime,lowerdir=/data,upperdir=/mnt/UDISK/data_upper,workdir=/mnt/UDISK/data_workdir /data

mkdir -p /mnt/UDISK/etc_upper /mnt/UDISK/etc_workdir
mount -n -t overlay overlayfs:/mnt/UDISK/overlay-etc -o rw,noatime,lowerdir=/etc,upperdir=/mnt/UDISK/etc_upper,workdir=/mnt/UDISK/etc_workdir /etc

mkdir -p /mnt/UDISK/usr_upper /mnt/UDISK/usr_workdir
mount -n -t overlay overlayfs:/mnt/UDISK/overlay-usr -o rw,noatime,lowerdir=/usr,upperdir=/mnt/UDISK/usr_upper,workdir=/mnt/UDISK/usr_workdir /usr

[ -b /dev/mmcblk0p1 ] && {
    mount /dev/mmcblk0p1 /mnt/SDCARD
}

# unshare -m

# mount -n /proc -o noatime,move /mnt/proc
# pivot_root /mnt /mnt/rom

# mount -n /rom/mnt/UDISK -o noatime,move /mnt/UDISK
# mount -n /rom/dev -o noatime,move /dev
# mount -n /rom/tmp -o noatime,move /tmp
# mount -n /rom/sys -o noatime,move /sys
# mount -n /rom/run -o noatime,move /run
# mount -n /rom/etc/machine-id -o noatime,move /etc/machine-id
# mount -n /rom/var/volatile -o noatime,move /var/volatile
# mount -n /rom/var/spool -o noatime,move /var/spool
# mount -n /rom/var/cache -o noatime,move /var/cache
# mount -n /rom/var/lib -o noatime,move /var/lib
# umount /rom/overlay/