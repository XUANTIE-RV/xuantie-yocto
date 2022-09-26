wpa_supplicant -u -B -i wlan0 -c /etc/wpa_supplicant.conf > /dev/null 2>&1
dhcpcd -b -q -t 0 wlan0
ifconfig eth0 up
# udhcpc -b -i wlan0 -S > /dev/null 2>&1 &
# udhcpc -b -i eth0 -S > /dev/null 2>&1 &
