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

# cpu
MODEL_FILE="/proc/device-tree/model"
if [ -f "$MODEL_FILE" ]; then
    MODEL_CONTENT=$(cat "$MODEL_FILE" | tr -d '\0')
    case "$MODEL_CONTENT" in
        Xuantie\ *)
            CPU=$(echo "$MODEL_CONTENT" | sed 's/^Xuantie //')
            if [ -n "$CPU" ]; then
                if echo "$APT_SOURCE_URIS" | grep -q 'rva23'; then
                    CPU_PATH="${APT_SOURCE_URIS}/${CPU}"
                else
                    CPU_PATH="${APT_SOURCE_URIS}/xuantie/${CPU}"
                fi
                sed -i "1i\\$CPU_PATH ./" "$APT_SOURCE_FILE"
            fi
            ;;
        *)
            ;;
    esac
fi

touch "$STATUS_FILE"

exit 0
