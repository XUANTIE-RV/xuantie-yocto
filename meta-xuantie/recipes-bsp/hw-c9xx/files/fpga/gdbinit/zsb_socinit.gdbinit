# Set gdb environment
set height 0
set confirm off


# GMAC configuration
set *(0x1900e01c)=0x0
set *(0x1900e004)=0x2000
set *(0x1900e008)=0x2000
set *(0x1900e000)=0x7e
set *(0x1900e00c)=0x4

set *(0x1900e00c)=0x80000004

set $is_use_ncore = 1
if ($is_use_ncore == 1)
	printf "is_use_ncore: %d\n", $is_use_ncore
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
else
	printf "is_use_ncore: %d\n", $is_use_ncore
end


shell $(sleep 2)
echo Finished SOCINIT\n\n
