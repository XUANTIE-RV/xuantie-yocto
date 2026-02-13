#!/bin/sh
#
# SPDX-License-Identifier: GPL-2.0-only
#

if [ -e /dev ] && grep -q devtmpfs /proc/filesystems; then
  mkdir -p /dev/shm
  mount -t tmpfs tmpfs /dev/shm
fi

