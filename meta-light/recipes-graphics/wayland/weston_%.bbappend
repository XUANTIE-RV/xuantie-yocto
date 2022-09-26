FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " \
                  file://0001-Backporting-for-upstream-patch-to-support-app_ids-of.patch \
                  file://0002-Backporting-upstream-patch-to-fix-input-segment-faul.patch \
                  file://0001-Reset-all-planes-state-due-to-hotplug-dirty-state.patch \
                  file://0001-REVERTME-always-set-primary-zpos-as-zpos_min-0.patch \
                  file://0001-REVERTME-continue-repaint-after-reset-due-to-BSP-ato.patch \
                  file://0001-Limited-plane-overlay-assignment-for-light-board.patch \
                  file://0002-Accept-window-positon-parameters-over-wayland-in-des.patch \
                  file://0001-Destory-fade-view-for-each-fade-done-as-shell-output.patch \
"
