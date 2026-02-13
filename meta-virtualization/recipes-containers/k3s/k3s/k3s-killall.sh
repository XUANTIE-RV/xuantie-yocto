#!/bin/sh

# Based on: k3s-killall.sh installed when running Rancher Lab's K3S install.sh
# In open-source project: https://github.com/k3s-io/k3s
#
# Original file: Copyright (c) 2021 Rancher Labs and Contributors.
# Modifications: Copyright (c) 2021 Arm Limited and Contributors. All rights reserved.
#
# Modifications:
# - Change systemd service directory location
# - Fix PID parsing to run on core image
# - Remove service stopping code (as this is intended to run as part of service
#   stop)
# - Changes to resolve warnings from the ShellCheck static analysis tool
#
# SPDX-License-Identifier: Apache License 2.0

[ "$(id -u)" -eq 0 ] || exec sudo "$0" "$@"

for bin in /var/lib/rancher/k3s/data/**/bin/; do
    [ -d "$bin" ] && export PATH=$PATH:$bin:$bin/aux
done

set -x

pschildren() {
    ps -e -o ppid= -o pid= | sed -e 's/^\s*//g; s/\s\s*/\t/g;' | grep -w "^$1" | cut -f2
}

pstree() {
    for pid in "$@"; do
        echo "$pid"
        for child in $(pschildren "$pid"); do
            pstree "$child"
        done
    done
}

killtree() {
    while read -r pid; do
        if [ -n "${pid}" ]; then
                kill -9 "${pid}" 2>/dev/null
        fi
    done <<EOF
$({ set +x; } 2>/dev/null; pstree "$@"; set -x;)
EOF
}

getshims() {
    ps -e -o pid= -o args= | sed -e 's/^ *//; s/\s\s*/\t/;' | grep -w '[^/]*/bin/containerd-shim' | cut -f1
}

killtree "$({ set +x; } 2>/dev/null; getshims; set -x)"

# shellcheck disable=SC2016
do_unmount_and_remove() {
    set +x
    while read -r _ path _; do
        case "$path" in $1*) echo "$path" ;; esac
    done < /proc/self/mounts | sort -r | xargs -r -t -n 1 sh -c 'umount "$0" && rm -rf "$0"'
    set -x
}

do_unmount_and_remove '/run/k3s'
do_unmount_and_remove '/var/lib/rancher/k3s'
do_unmount_and_remove '/var/lib/kubelet/pods'
do_unmount_and_remove '/var/lib/kubelet/plugins'
do_unmount_and_remove '/run/netns/cni-'

# Remove CNI namespaces
ip netns show 2>/dev/null | grep cni- | xargs -r -t -n 1 ip netns delete

# Delete network interface(s) that match 'master cni0'
ip link show 2>/dev/null | grep 'master cni0' | while read -r _ iface _; do
    iface=${iface%%@*}
    [ -z "$iface" ] || ip link delete "$iface"
done
ip link delete cni0
ip link delete flannel.1
ip link delete flannel-v6.1
rm -rf /var/lib/cni/
iptables-save | grep -v KUBE- | grep -v CNI- | iptables-restore
