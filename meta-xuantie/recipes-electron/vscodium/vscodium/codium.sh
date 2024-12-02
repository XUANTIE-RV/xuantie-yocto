#!/bin/bash

if [ "$(id -u)" -eq 0 ]; then
  exec /usr/share/codium/codium --no-sandbox --use-gl=egl "$@"
else
  exec /usr/share/codium/codium --use-gl=egl "$@"
fi
