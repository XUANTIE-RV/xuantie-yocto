#!/bin/sh
#bt_hciattach="hciattach-d1"

start_hci_attach()
{
	h=`ps | grep "$bt_hciattach" | grep -v grep`
	[ -n "$h" ] && {
		killall "$bt_hciattach"
		sleep 1
	}

	#xradio init
	echo 0 > /sys/class/rfkill/rfkill0/state;
	sleep 1
	echo 1 > /sys/class/rfkill/rfkill0/state;
	sleep 1

	#"$bt_hciattach" -n ttyS1 xradio >/dev/null 2>&1 &
	hciattach-d1  -n ttyS1 xradio
	sleep 1

	wait_hci0_count=0
	while true
	do
		[ -d /sys/class/bluetooth/hci0 ] && break
		sleep 1
		let wait_hci0_count++
		[ $wait_hci0_count -eq 8 ] && {
			echo "bring up hci0 failed"
			exit 1
		}
	done
	echo "D1 BT initial finished"
}

start() {

    hcidump_xr=$(ps | grep "hcidump_xr" | grep -v grep | awk '{print $1}')
    if [ -n "$hcidump_xr" ] ;then
        echo "hcidump_xr existed"
    else
        echo "hcidump_xr start"
        #hcidump_xr &
    fi

	if [ -d "/sys/class/bluetooth/hci0" ];then
		echo "Bluetooth init has been completed!!"
	else
		start_hci_attach
	fi

    #d=`ps | grep bluetoothd | grep -v grep`
	#[ -z "$d" ] && {
		#/etc/bluetooth/bluetoothd start
		#sleep 1
	#	echo "bluetoothd should be started here..let's check later"
    #}
}

ble_start() {
	if [ -d "/sys/class/bluetooth/hci0" ];then
		echo "Bluetooth init has been completed!!"
	else
		start_hci_attach
	fi

	hci_is_up=`hciconfig hci0 | grep RUNNING`
	[ -z "$hci_is_up" ] && {
		hciconfig hci0 up
	}

	MAC_STR=`hciconfig | grep "BD Address" | awk '{print $3}'`
	LE_MAC=${MAC_STR/2/C}
	OLD_LE_MAC_T=`cat /sys/kernel/debug/bluetooth/hci0/random_address`
	OLD_LE_MAC=$(echo $OLD_LE_MAC_T | tr [a-z] [A-Z])
	if [ -n "$LE_MAC" ];then
		if [ "$LE_MAC" != "$OLD_LE_MAC" ];then
			hciconfig hci0 lerandaddr $LE_MAC
		else
			echo "the ble random_address has been set."
		fi
	fi
}

stop() {
	echo "nothing to do."
}

case "$1" in
  start|"")
		echo "bt_init start"
        start
        ;;
  stop)
  		echo "bt_init stop"
        stop
        ;;
  ble_start)
  		echo "bt_init ble_start"
	    ble_start
		;;
  *)
        echo "Usage: $0 {start|stop}"
        exit 1
esac
