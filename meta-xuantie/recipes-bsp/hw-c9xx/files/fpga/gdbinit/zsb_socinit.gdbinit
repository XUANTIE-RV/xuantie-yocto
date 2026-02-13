# Set gdb environment
set height 0
set confirm off


# GMAC configuration
set $net_mode = 0
if ($net_mode == 0)
	set *(0x1900e01c)=0x0
	set *(0x1900e004)=0x2000
	set *(0x1900e008)=0x2000
	set *(0x1900e000)=0x7e
	set *(0x1900e00c)=0x4
	set *(0x1900e00c)=0x80000004
end
if ($net_mode == 1)
	set *(0x1900e01c)=0x1
	set *(0x1900e004)=0x2000
	set *(0x1900e008)=0x2200
	set *(0x1900e000)=0x7e
	set *(0x1900e00c)=0x4
	set *(0x1900e020)=0x1
	set *(0x1900e00c)=0x80000004
end

set $link_mode = 1
set $pmc_init = 0

if ($link_mode == 1)
	printf "Link Mode : %d,  Use NCore\n", $link_mode
	# Old Ncore configuration
	#   [0x0] close all; [0x3] enable snoop to cluster0 and cluster1
	set *0x26880040 = 0x3
	set *0x26881040 = 0x3
	set *0x26882040 = 0x3
	set *0x26883040 = 0x3
	#   [0x0] disable;   [0x3] enable DVM req to cluster0 and cluster1
	set *0x268ff040 = 0x3

	# New ncore configuration
	set *0x26b80040 = 0x3
	set *0x26bff040 = 0x3
end

if ($link_mode == 2)
	printf "Link Mode : %d,  Use Xuantie Link\n", $link_mode
	# Prepare Starlink v3
	set $stl3_base = 0xfffffff000
	set $l3prxcr = $stl3_base + 0xc0
	set $l3smpr = $stl3_base + 0x810
	# Enable Cluster 0
	set *($l3prxcr) = 0x01
	set *($l3smpr) = 0x1
	# Enable Cluster 1
	set *($l3prxcr) = 0x11
	set *($l3smpr) = 0x1
	# Enable Cluster 2
	set *($l3prxcr) = 0x21
	set *($l3smpr) = 0x1
	# Enable Cluster 3
	set *($l3prxcr) = 0x31
	set *($l3smpr) = 0x1
	# Disable proxy
	set *($l3prxcr) = 0x0
	## Close L3
	#set $l3decr = $stl3_base + 0x300
	#set $l3desr = $stl3_base + 0x308
	#set *($l3decr) = 0x0
	#set $cur_l3state = *($l3desr)
	#while($cur_l3state !=0)
	#	$cur_l3state = *($l3desr)
	#
end

printf "PMC init : %d\n", $pmc_init
if ($pmc_init == 1)
	# IPMC configuration(If no SBA)s

	## Release Core0
	set *0x26900100 = 0x18
	set *0x26900104 = 0x11

	## Release Core1
	set *0x26901100 = 0x18
	set *0x26901104 = 0x11

	## Release Core2
	set *0x26902100 = 0x18
	set *0x26902104 = 0x11

	## Release Core3
	set *0x26903100 = 0x18
	set *0x26903104 = 0x11
end

shell $(sleep 2)
echo Finished SOCINIT\n\n
