#!/bin/sh

CONFIG_GZ="/proc/config.gz"
APT_SOURCE_FILE="/etc/apt/sources.list"
STATUS_FILE="/etc/.update-apt-sources-list"
APT_SOURCE_URIS=$(grep '^deb' /etc/apt/sources.list | head -n 1 | sed -E 's|^(.*)(/)([^/]+)\s.*|\1|')

if [ -f "$STATUS_FILE" ]; then
    exit 0
fi

if [ ! -f "$CONFIG_GZ" ]; then
    exit 1
fi

KERNEL_VERSION=$(cat /proc/version)
case "$KERNEL_VERSION" in
    *yocto-standard-rva23u64-k6.6-ali6000-debug*)
        APT_KERNEL_PATH="${APT_SOURCE_URIS}/kernel_6.6_ali6000_debug"
        ;;
    *yocto-standard-rva23u64-k6.6-ali6000*)
        APT_KERNEL_PATH="${APT_SOURCE_URIS}/kernel_6.6_ali6000"
        ;;
    *)
        KVER=$(uname -r | cut -d'.' -f1-2)
        if zcat "$CONFIG_GZ" | grep -q '^CONFIG_FTRACE=y'; then
            APT_KERNEL_PATH="${APT_SOURCE_URIS}/kernel_${KVER}_debug"
        else
            APT_KERNEL_PATH="${APT_SOURCE_URIS}/kernel_${KVER}_default"
        fi
        ;;
esac

echo "$APT_KERNEL_PATH ./" >> "$APT_SOURCE_FILE"
touch "$STATUS_FILE"

exit 0
