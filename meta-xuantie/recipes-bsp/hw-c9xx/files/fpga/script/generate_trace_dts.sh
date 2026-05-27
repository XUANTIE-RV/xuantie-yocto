#!/bin/bash

# 检查 jq 是否安装
if ! command -v jq &> /dev/null; then
    echo "错误: 需要 'jq' 工具来解析 JSON。请先安装 jq。"
    echo "  Ubuntu/Debian: sudo apt install jq"
    echo "  CentOS/RHEL:   sudo yum install jq"
    echo "  macOS:         brew install jq"
    exit 1
fi

# 默认配置文件
CONFIG_FILE="config.json"

# 显示帮助
show_help() {
    cat <<EOF
用法: $0 [选项]

从 config.json 生成 XuanTie Nexus Trace 的 ntrace-memory.dtsi 和 ntrace.dtsi（每行缩进 2 个 tab）

选项:
  -c, --config FILE    配置文件 (默认: config.json)
  -h, --help           显示此帮助
EOF
}

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -c|--config)
            CONFIG_FILE="$2"; shift 2 ;;
        -h|--help)
            show_help; exit 0 ;;
        *)
            echo "错误: 未知选项 $1"; show_help; exit 1 ;;
    esac
done

# 读取 JSON 配置
if [ ! -f "$CONFIG_FILE" ]; then
    echo "错误: 配置文件 '$CONFIG_FILE' 不存在"
    exit 1
fi

# 读取参数
TRACE_VERSION=$(jq -r '.trace_version // "v1"' "$CONFIG_FILE")
CLUSTERS=$(jq -r '.clusters // 1' "$CONFIG_FILE")
CORES_PER_CLUSTER_STR=$(jq -r '.cores_per_cluster // "[1]" | @sh' "$CONFIG_FILE")
ENABLED_CORES_STR=$(jq -r '.enabled_cores // "[]" | @sh' "$CONFIG_FILE")
DMI_APB_BASE=$(jq -r '.dmi_apb_base // "0x26000000"' "$CONFIG_FILE")
SINK_TYPE=$(jq -r '.sink_type // "smem"' "$CONFIG_FILE")
RESERVED_MEM_START=$(jq -r '.reserved_mem_start // "0x80000000"' "$CONFIG_FILE")
RESERVED_MEM_SIZE=$(jq -r '.reserved_mem_size // "0x1000000"' "$CONFIG_FILE")
OUTPUT_DIR=$(jq -r '.output_dir // "."' "$CONFIG_FILE")
SBA_ENABLE=$(jq -r '.sba_dmi_addr // "false"' "$CONFIG_FILE")

# 确保输出目录存在
mkdir -p "$OUTPUT_DIR"

RESERVED_DTSI="$OUTPUT_DIR/ntrace-memory.dtsi"
NTRACE_DTSI="$OUTPUT_DIR/ntrace.dtsi"

# 转换数组
eval "CORES_PER_CLUSTER=($CORES_PER_CLUSTER_STR)"
eval "ENABLED_CORES=($ENABLED_CORES_STR)"

# 验证 cores_per_cluster 长度
if [ ${#CORES_PER_CLUSTER[@]} -ne $CLUSTERS ]; then
    echo "错误: cores_per_cluster 必须提供 $CLUSTERS 个值，当前: ${#CORES_PER_CLUSTER[@]}"
    exit 1
fi

# 校验 DMI_APB_BASE 是否为合法十六进制
if ! [[ ${DMI_APB_BASE#0x} =~ ^[0-9A-Fa-f]+$ ]]; then
    echo "错误: dmi_apb_base 必须是十六进制地址（如 0x26000000）"
    exit 1
fi

# 验证 trace_version
if [[ "$TRACE_VERSION" != "v1" && "$TRACE_VERSION" != "v2" ]]; then
    echo "错误: trace_version 必须是 'v1' 或 'v2'"
    exit 1
fi

# 构建所有 CPU 列表并建立 cluster/core -> cpu_id 映射
declare -A cpu_id_map
ALL_CPUS=()
CPU_ID=0
for ((i=0; i<CLUSTERS; i++)); do
    for ((j=0; j<${CORES_PER_CLUSTER[i]}; j++)); do
        cpu_id_map[$i,$j]=$CPU_ID
        ALL_CPUS+=($CPU_ID)
        ((CPU_ID++))
    done
done

# 如果未指定 enabled_cores，则启用全部
if [ ${#ENABLED_CORES[@]} -eq 0 ]; then
    ENABLED_CORES=("${ALL_CPUS[@]}")
fi

# 验证 enabled_cores 是否在有效范围内
for cpu in "${ENABLED_CORES[@]}"; do
    if ! [[ " ${ALL_CPUS[*]} " =~ " $cpu " ]]; then
        echo "错误: CPU $cpu 不在有效范围 [0-${#ALL_CPUS[@]}) 内"
        exit 1
    fi
done

# 将十六进制字符串（带或不带 0x）转为十进制
hex_to_dec() {
    local val="$1"
    if [[ $val == 0x* ]]; then
        val="${val#0x}"
    fi
    echo $((16#$val))
}

# 计算总线地址：base + dmi_offset * 4
calc_addr() {
    local dmi_offset=$1
    local base=$(hex_to_dec ${DMI_APB_BASE})
    local addr=$((base + dmi_offset * 4))
    printf "0x%X" $addr
}

# === 辅助函数：拆分 64 位地址为高32 + 低32 ===
split_64bit() {
    local val="$1"
    val="${val#0x}"
    val="${val#0X}"
    printf -v val "%016s" "$val"
    val="${val// /0}"
    high="0x${val:0:8}"
    low="0x${val:8:8}"
    echo "$high $low"
}

# === 第一步：生成 reserved_memory.dtsi（带 2-tab 缩进）===
read start_high start_low <<< $(split_64bit "$RESERVED_MEM_START")
read size_high size_low <<< $(split_64bit "$RESERVED_MEM_SIZE")

cat <<EOF | sed 's/^/\t\t/' > "$RESERVED_DTSI"
reserved-memory {
	#address-cells = <0x2>;
	#size-cells = <0x2>;
	ranges;

	xuantie_ntrace_reserved: memory@$(echo $RESERVED_MEM_START | cut -d'x' -f2) {
		reg = <$start_high $start_low $size_high $size_low>;
		no-map;
	};
};
EOF

echo "✅ 已生成: $RESERVED_DTSI"

# === 第二步：准备 ntrace.dtsi ===
> "$NTRACE_DTSI"  # 清空文件

# 根据 trace_version 选择生成逻辑
if [ "$TRACE_VERSION" = "v1" ]; then
    # V1 规则 (C907/C908): 只有一个 funnel2，每个多核 cluster 有一个 funnel1
    
    # 预计算地址
    declare -A encoder_addrs
    declare -A funnel1_addrs
    
    base_addr=$(hex_to_dec ${DMI_APB_BASE})
    
    # 计算所有 encoder 地址
    CPU_ID=0
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        cluster_base=$((base_addr + cluster * 0x20000))
        for ((core=0; core<${CORES_PER_CLUSTER[cluster]}; core++)); do
            encoder_addr=$((cluster_base + (core + 1) * 0x1000))
            encoder_addrs[$CPU_ID]=$(printf "0x%X" $encoder_addr)
            ((CPU_ID++))
        done
    done
    
    # 计算所有 funnel1 地址（仅多核 cluster）
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        if [ ${CORES_PER_CLUSTER[cluster]} -gt 1 ]; then
            cluster_base=$((base_addr + cluster * 0x20000))
            funnel1_addr=$((cluster_base + 0x11000))
            funnel1_addrs[$cluster]=$(printf "0x%X" $funnel1_addr)
        fi
    done
    
    # Level2 Funnel 和 Sink 地址
    funnel2_addr=$(printf "0x%X" $((base_addr + 0x404000)))  # 0x26404000
    smem_sink_addr=$(printf "0x%X" $((base_addr + 0x401000)))  # 0x26401000
    
    # 生成 encoder 节点
    CPU_ID=0
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        for ((core=0; core<${CORES_PER_CLUSTER[cluster]}; core++)); do
            if [[ " ${ENABLED_CORES[@]} " =~ " $CPU_ID " ]]; then
                encoder_name="encoder$CPU_ID"
                addr=${encoder_addrs[$CPU_ID]}
                
                # 确定输出目标
                if [ ${CORES_PER_CLUSTER[cluster]} -eq 1 ]; then
                    # 单核 cluster，直接连接到 funnel2
                    output_target="funnel2"
                else
                    # 多核 cluster，连接到 funnel1
                    output_target="funnel1_$cluster"
                fi
                
                cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$encoder_name: xtntrace_encoder@$(echo $addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,encoder-controller";
	reg = <0x0 $addr 0x0 0x1000>;
	cpu = <$CPU_ID>;
	trace_encoder_format = <0x1>;
	trace_encoder_inst_mode = <0x6>;
	trace_encoder_context = "true";
	trace_encoder_inst_stall_enable = "true";
	trace_encoder_inhibit_src = "false";
	trace_encoder_src_id = <$CPU_ID>;
	trace_encoder_inst_sync_mode = <0x0>;
	trace_encoder_inst_sync_max = <0xffff>;
	trace_encoder_inst_trigger_enable = "true";
	trace_timestamp_enable = "true";
	trace_timestamp_runindebugmode = "false";
	trace_timestamp_mode = <0x1>;
	trace_timestamp_prescale = <0x0>;
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
            fi
            ((CPU_ID++))
        done
    done
    
    # 生成 funnel1 节点（仅多核 cluster）
    CPU_ID=0
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        if [ ${CORES_PER_CLUSTER[cluster]} -gt 1 ]; then
            # 检查该 cluster 中是否有启用的 CPU
            cluster_has_enabled=false
            for ((core=0; core<${CORES_PER_CLUSTER[cluster]}; core++)); do
                if [[ " ${ENABLED_CORES[@]} " =~ " $CPU_ID " ]]; then
                    cluster_has_enabled=true
                    break
                fi
                ((CPU_ID++))
            done
            
            # 如果该 cluster 有启用的 CPU，则生成 funnel1
            if [ "$cluster_has_enabled" = true ]; then
                funnel1_name="funnel1_$cluster"
                addr=${funnel1_addrs[$cluster]}
                
                # 构建 input_port
                input_ports=""
                port_idx=0
                temp_cpu_id=$((CPU_ID - ${CORES_PER_CLUSTER[cluster]}))
                for ((core=0; core<${CORES_PER_CLUSTER[cluster]}; core++)); do
                    if [[ " ${ENABLED_CORES[@]} " =~ " $temp_cpu_id " ]]; then
                        input_ports+="		port$port_idx {
			endpoint = <&encoder$temp_cpu_id>;
		};
"
                        ((port_idx++))
                    fi
                    ((temp_cpu_id++))
                done
                
                cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$funnel1_name: xtntrace_funnel@$(echo $addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,funnel-controller";
	reg = <0x0 $addr 0x0 0x1000>;
	level = <1>;
	input_port {
$input_ports
	};
	output_port {
		port0 {
			endpoint = <&funnel2>;
		};
	};
};

EOF
            else
                # 跳过未启用的 cluster 的 CPU ID
                ((CPU_ID += ${CORES_PER_CLUSTER[cluster]}))
            fi
        else
            # 单核 cluster，跳过
            ((CPU_ID++))
        fi
    done
    
    # 生成 funnel2 节点（只有一个）
    # 构建 input_port
    funnel2_inputs=""
    port_idx=0
    
    CPU_ID=0
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        if [ ${CORES_PER_CLUSTER[cluster]} -eq 1 ]; then
            # 单核 cluster
            if [[ " ${ENABLED_CORES[@]} " =~ " $CPU_ID " ]]; then
                funnel2_inputs+="		port$port_idx {
			endpoint = <&encoder$CPU_ID>;
		};
"
                ((port_idx++))
            fi
            ((CPU_ID++))
        else
            # 多核 cluster，检查是否有启用的 CPU
            cluster_has_enabled=false
            for ((core=0; core<${CORES_PER_CLUSTER[cluster]}; core++)); do
                if [[ " ${ENABLED_CORES[@]} " =~ " $CPU_ID " ]]; then
                    cluster_has_enabled=true
                fi
                ((CPU_ID++))
            done
            
            if [ "$cluster_has_enabled" = true ]; then
                funnel2_inputs+="		port$port_idx {
			endpoint = <&funnel1_$cluster>;
		};
"
                ((port_idx++))
            fi
        fi
    done
    
    cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
funnel2: xtntrace_funnel@$(echo $funnel2_addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,funnel-controller";
	reg = <0x0 $funnel2_addr 0x0 0x1000>;
	level = <2>;
	input_port {
$funnel2_inputs
	};
	output_port {
		port0 {
			endpoint = <&sink0>;
		};
	};
};

EOF
    
    # 生成 sink 节点
    case "$SINK_TYPE" in
        smem) sink_addr=$smem_sink_addr ;;
        sram) sink_addr=$(printf "0x%X" $((base_addr + 0x400000))) ;;  # 0x26400000
        pib)  sink_addr=$(printf "0x%X" $((base_addr + 0x403000))) ;;   # 0x26403000
        *) echo "错误: 不支持的 sink 类型 '$SINK_TYPE'"; exit 1 ;;
    esac
    
    cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
sink0: xtntrace_${SINK_TYPE}@$(echo $sink_addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,sink-controller";
	reg = <0x0 $sink_addr 0x0 0x1000>;
	memory-region = <&xuantie_ntrace_reserved>;
	working_mode = "circulating buffer";
	format = "original";
	input_port {
		port0 {
			endpoint = <&funnel2>;
		};
	};
};
EOF

else
    # V2 规则 (C930): 原有的 C930 逻辑
    # 首先确定哪些 cluster 有启用的 CPU
    declare -A cluster_has_enabled
    CPU_ID=0
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        cluster_has_enabled[$cluster]=false
        for ((core=0; core<${CORES_PER_CLUSTER[cluster]}; core++)); do
            if [[ " ${ENABLED_CORES[@]} " =~ " $CPU_ID " ]]; then
                cluster_has_enabled[$cluster]=true
            fi
            ((CPU_ID++))
        done
    done
    
    # 找出第一个和最后一个启用的 cluster
    first_enabled_cluster=-1
    last_enabled_cluster=-1
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        if [ "${cluster_has_enabled[$cluster]}" = true ]; then
            if [ $first_enabled_cluster -eq -1 ]; then
                first_enabled_cluster=$cluster
            fi
            last_enabled_cluster=$cluster
        fi
    done
    
    # 如果没有启用任何 cluster
    if [ $first_enabled_cluster -eq -1 ]; then
        echo "错误: 没有启用任何 CPU"
        exit 1
    fi
    
    # 检查是否为部分启用场景（不是所有 cluster 都启用）
    partial_enable=false
    for ((cluster=0; cluster<CLUSTERS; cluster++)); do
        if [ "${cluster_has_enabled[$cluster]}" = false ]; then
            partial_enable=true
            break
        fi
    done
    
    if [ "$partial_enable" = true ]; then
        # 使用增强的 C930 逻辑支持部分 cluster 启用
        # 声明变量
        declare -A funnel_addrs
        declare -A cluster_last_funnel1

        # 预注册地址 - 只为启用的 cluster 生成组件
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            num_cores=${CORES_PER_CLUSTER[cluster]}
            if [ "${cluster_has_enabled[$cluster]}" = true ]; then
                funnel2_name="funnel2_$cluster"
                dmi_funnel2=$((0x10000 * cluster + 0x400))
                funnel_addrs[$funnel2_name]=$(calc_addr $dmi_funnel2)

                for ((core=0; core<num_cores; core++)); do
                    cpu_id=${cpu_id_map[$cluster,$core]}
                    if [ $num_cores -gt 1 ]; then
                        funnel1_name="funnel1_$cpu_id"
                        dmi_funnel1=$((0x10000 * cluster + 0xc00 + 0x800 * core))
                        funnel_addrs[$funnel1_name]=$(calc_addr $dmi_funnel1)
                        cluster_last_funnel1[$cluster]=$funnel1_name
                    fi
                done
            fi
        done

        # 生成 encoder - 只为启用的 cluster
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            if [ "${cluster_has_enabled[$cluster]}" = false ]; then
                continue
            fi
            num_cores=${CORES_PER_CLUSTER[cluster]}
            for ((core=0; core<num_cores; core++)); do
                cpu_id=${cpu_id_map[$cluster,$core]}
                if [[ ! " ${ENABLED_CORES[@]} " =~ " $cpu_id " ]]; then
                    continue
                fi

                encoder_name="encoder$cpu_id"
                dmi_encoder=$((0x10000 * cluster + 0x800 + 0x800 * core))
                addr_encoder=$(calc_addr $dmi_encoder)

                if [ $num_cores -eq 1 ]; then
                    output_target="funnel2_$cluster"
                else
                    output_target="funnel1_$cpu_id"
                fi

                cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$encoder_name: xtntrace_encoder@$(echo $addr_encoder | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,encoder-controller";
	reg = <0x0 $addr_encoder 0x0 0x1000>;
	cpu = <$cpu_id>;
	trace_encoder_format = <0x1>;
	trace_encoder_inst_mode = <0x6>;
	trace_encoder_context = "true";
	trace_encoder_inst_stall_enable = "true";
	trace_encoder_inhibit_src = "false";
	trace_encoder_src_id = <$cpu_id>;
	trace_encoder_inst_sync_mode = <0x0>;
	trace_encoder_inst_sync_max = <0xffff>;
	trace_encoder_inst_trigger_enable = "true";
	trace_timestamp_enable = "true";
	trace_timestamp_runindebugmode = "false";
	trace_timestamp_mode = <0x1>;
	trace_timestamp_prescale = <0x0>;
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
            done
        done

        # 生成 funnel1 - 只为启用的 cluster
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            if [ "${cluster_has_enabled[$cluster]}" = false ]; then
                continue
            fi
            num_cores=${CORES_PER_CLUSTER[cluster]}
            if [ $num_cores -le 1 ]; then
                continue
            fi

            for ((core=0; core<num_cores; core++)); do
                cpu_id=${cpu_id_map[$cluster,$core]}
                funnel1_name="funnel1_$cpu_id"
                addr=${funnel_addrs[$funnel1_name]}

                if [ $core -eq $((num_cores-1)) ]; then
                    output_target="funnel2_$cluster"
                else
                    next_cpu_id=${cpu_id_map[$cluster,$((core+1))]}
                    # 检查下一个 core 是否启用
                    if [[ " ${ENABLED_CORES[@]} " =~ " $next_cpu_id " ]]; then
                        output_target="funnel1_$next_cpu_id"
                    else
                        # 找到下一个启用的 core
                        found_next=false
                        for ((next_core=core+1; next_core<num_cores; next_core++)); do
                            next_cpu_id=${cpu_id_map[$cluster,$next_core]}
                            if [[ " ${ENABLED_CORES[@]} " =~ " $next_cpu_id " ]]; then
                                output_target="funnel1_$next_cpu_id"
                                found_next=true
                                break
                            fi
                        done
                        if [ "$found_next" = false ]; then
                            output_target="funnel2_$cluster"
                        fi
                    fi
                fi

                input_lines=""
                port_idx=0
                if [[ " ${ENABLED_CORES[@]} " =~ " $cpu_id " ]]; then
                    input_lines+="		port$port_idx {
			endpoint = <&encoder$cpu_id>;
		};
"
                    ((port_idx++))
                fi
                if [ $core -gt 0 ]; then
                    prev_cpu_id=${cpu_id_map[$cluster,$((core-1))]}
                    if [[ " ${ENABLED_CORES[@]} " =~ " $prev_cpu_id " ]]; then
                        input_lines+="		port$port_idx {
			endpoint = <&funnel1_$prev_cpu_id>;
		};
"
                    fi
                fi

                cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$funnel1_name: xtntrace_funnel@$(echo $addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,funnel-controller";
	reg = <0x0 $addr 0x0 0x1000>;
	level = <1>;
	input_port {
$input_lines
	};
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
            done
        done

        # 生成 funnel2 - 只为启用的 cluster
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            if [ "${cluster_has_enabled[$cluster]}" = false ]; then
                continue
            fi
            funnel2_name="funnel2_$cluster"
            addr=${funnel_addrs[$funnel2_name]}

            input_lines=""
            num_cores=${CORES_PER_CLUSTER[cluster]}
            if [ $cluster -eq $first_enabled_cluster ]; then
                # 第一个启用的 cluster
                if [ $num_cores -eq 1 ]; then
                    cpu_id=${cpu_id_map[$cluster,0]}
                    input_lines="		port0 {
			endpoint = <&encoder$cpu_id>;
		};
"
                else
                    last_funnel1=${cluster_last_funnel1[$cluster]}
                    input_lines="		port0 {
			endpoint = <&$last_funnel1>;
		};
"
                fi
            else
                # 非第一个启用的 cluster
                if [ $num_cores -eq 1 ]; then
                    cpu_id=${cpu_id_map[$cluster,0]}
                    local_input="encoder$cpu_id"
                else
                    local_input=${cluster_last_funnel1[$cluster]}
                fi
                # 找到前一个启用的 cluster
                prev_enabled_cluster=-1
                for ((prev_cluster=cluster-1; prev_cluster>=0; prev_cluster--)); do
                    if [ "${cluster_has_enabled[$prev_cluster]}" = true ]; then
                        prev_enabled_cluster=$prev_cluster
                        break
                    fi
                done
                prev_funnel2="funnel2_$prev_enabled_cluster"
                input_lines="		port0 {
			endpoint = <&$local_input>;
		};
		port1 {
			endpoint = <&$prev_funnel2>;
		};
"
            fi

            if [ $cluster -eq $last_enabled_cluster ]; then
                output_target="sink0"
            else
                # 找到下一个启用的 cluster
                next_enabled_cluster=-1
                for ((next_cluster=cluster+1; next_cluster<CLUSTERS; next_cluster++)); do
                    if [ "${cluster_has_enabled[$next_cluster]}" = true ]; then
                        next_enabled_cluster=$next_cluster
                        break
                    fi
                done
                if [ $next_enabled_cluster -ne -1 ]; then
                    output_target="funnel2_$next_enabled_cluster"
                else
                    output_target="sink0"
                fi
            fi

            cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$funnel2_name: xtntrace_funnel@$(echo $addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,funnel-controller";
	reg = <0x0 $addr 0x0 0x1000>;
	level = <2>;
	input_port {
$input_lines
	};
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
        done

        # 生成 sink
        last_funnel2="funnel2_$last_enabled_cluster"

        # 检查是否启用了 SBA
        if [ "$SBA_ENABLE" = "true" ]; then
            # SBA 模式：SMEM Sink 地址计算时 cluster 数量 +1（SBA占位）
            sink_dmi_base=$((0x10000 * (CLUSTERS + 1)))
            sink_dmi_offset=0x400  # smem offset
            sink_dmi=$((sink_dmi_base + sink_dmi_offset))
            sink_addr=$(calc_addr $sink_dmi)
        else
            # 原有逻辑
            sink_dmi_base=$((0x20000))
            case "$SINK_TYPE" in
                smem) sink_dmi_offset=0x400 ;;
                sram) sink_dmi_offset=0x0 ;;
                pib)  sink_dmi_offset=0xc00 ;;
                *) echo "错误: 不支持的 sink 类型 '$SINK_TYPE'"; exit 1 ;;
            esac
            sink_dmi=$((sink_dmi_base + sink_dmi_offset))
            sink_addr=$(calc_addr $sink_dmi)
        fi

        cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
sink0: xtntrace_${SINK_TYPE}@$(echo $sink_addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,sink-controller";
	reg = <0x0 $sink_addr 0x0 0x1000>;
	memory-region = <&xuantie_ntrace_reserved>;
	working_mode = "circulating buffer";
	format = "original";
	input_port {
		port0 {
			endpoint = <&$last_funnel2>;
		};
	};
};
EOF

    else
        # 使用原有的完整 C930 逻辑（保持向后兼容）
        # 声明变量
        declare -A funnel_addrs
        declare -A cluster_last_funnel1

        # 预注册地址
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            num_cores=${CORES_PER_CLUSTER[cluster]}
            funnel2_name="funnel2_$cluster"
            dmi_funnel2=$((0x10000 * cluster + 0x400))
            funnel_addrs[$funnel2_name]=$(calc_addr $dmi_funnel2)

            for ((core=0; core<num_cores; core++)); do
                cpu_id=${cpu_id_map[$cluster,$core]}
                if [ $num_cores -gt 1 ]; then
                    funnel1_name="funnel1_$cpu_id"
                    dmi_funnel1=$((0x10000 * cluster + 0xc00 + 0x800 * core))
                    funnel_addrs[$funnel1_name]=$(calc_addr $dmi_funnel1)
                    cluster_last_funnel1[$cluster]=$funnel1_name
                fi
            done
        done

        # 生成 encoder
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            num_cores=${CORES_PER_CLUSTER[cluster]}
            for ((core=0; core<num_cores; core++)); do
                cpu_id=${cpu_id_map[$cluster,$core]}
                if [[ ! " ${ENABLED_CORES[@]} " =~ " $cpu_id " ]]; then
                    continue
                fi

                encoder_name="encoder$cpu_id"
                dmi_encoder=$((0x10000 * cluster + 0x800 + 0x800 * core))
                addr_encoder=$(calc_addr $dmi_encoder)

                if [ $num_cores -eq 1 ]; then
                    output_target="funnel2_$cluster"
                else
                    output_target="funnel1_$cpu_id"
                fi

                cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$encoder_name: xtntrace_encoder@$(echo $addr_encoder | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,encoder-controller";
	reg = <0x0 $addr_encoder 0x0 0x1000>;
	cpu = <$cpu_id>;
	trace_encoder_format = <0x1>;
	trace_encoder_inst_mode = <0x6>;
	trace_encoder_context = "true";
	trace_encoder_inst_stall_enable = "true";
	trace_encoder_inhibit_src = "false";
	trace_encoder_src_id = <$cpu_id>;
	trace_encoder_inst_sync_mode = <0x0>;
	trace_encoder_inst_sync_max = <0xffff>;
	trace_encoder_inst_trigger_enable = "true";
	trace_timestamp_enable = "true";
	trace_timestamp_runindebugmode = "false";
	trace_timestamp_mode = <0x1>;
	trace_timestamp_prescale = <0x0>;
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
            done
        done

        # 生成 funnel1
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            num_cores=${CORES_PER_CLUSTER[cluster]}
            if [ $num_cores -le 1 ]; then
                continue
            fi

            for ((core=0; core<num_cores; core++)); do
                cpu_id=${cpu_id_map[$cluster,$core]}
                funnel1_name="funnel1_$cpu_id"
                addr=${funnel_addrs[$funnel1_name]}

                if [ $core -eq $((num_cores-1)) ]; then
                    output_target="funnel2_$cluster"
                else
                    next_cpu_id=${cpu_id_map[$cluster,$((core+1))]}
                    output_target="funnel1_$next_cpu_id"
                fi

                input_lines=""
                port_idx=0
                if [[ " ${ENABLED_CORES[@]} " =~ " $cpu_id " ]]; then
                    input_lines+="		port$port_idx {
			endpoint = <&encoder$cpu_id>;
		};
"
                    ((port_idx++))
                fi
                if [ $core -gt 0 ]; then
                    prev_cpu_id=${cpu_id_map[$cluster,$((core-1))]}
                    input_lines+="		port$port_idx {
			endpoint = <&funnel1_$prev_cpu_id>;
		};
"
                fi

                cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$funnel1_name: xtntrace_funnel@$(echo $addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,funnel-controller";
	reg = <0x0 $addr 0x0 0x1000>;
	level = <1>;
	input_port {
$input_lines
	};
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
            done
        done

        # 生成 funnel2
        for ((cluster=0; cluster<CLUSTERS; cluster++)); do
            funnel2_name="funnel2_$cluster"
            addr=${funnel_addrs[$funnel2_name]}

            input_lines=""
            if [ $cluster -eq 0 ]; then
                if [ ${CORES_PER_CLUSTER[cluster]} -eq 1 ]; then
                    cpu_id=${cpu_id_map[$cluster,0]}
                    input_lines="		port0 {
			endpoint = <&encoder$cpu_id>;
		};
"
                else
                    last_funnel1=${cluster_last_funnel1[$cluster]}
                    input_lines="		port0 {
			endpoint = <&$last_funnel1>;
		};
"
                fi
            else
                if [ ${CORES_PER_CLUSTER[cluster]} -eq 1 ]; then
                    cpu_id=${cpu_id_map[$cluster,0]}
                    local_input="encoder$cpu_id"
                else
                    local_input=${cluster_last_funnel1[$cluster]}
                fi
                prev_funnel2="funnel2_$((cluster-1))"
                input_lines="		port0 {
			endpoint = <&$local_input>;
		};
		port1 {
			endpoint = <&$prev_funnel2>;
		};
"
            fi

            if [ $cluster -eq $((CLUSTERS-1)) ]; then
                output_target="sink0"
            else
                output_target="funnel2_$((cluster+1))"
            fi

            cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
$funnel2_name: xtntrace_funnel@$(echo $addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,funnel-controller";
	reg = <0x0 $addr 0x0 0x1000>;
	level = <2>;
	input_port {
$input_lines
	};
	output_port {
		port0 {
			endpoint = <&$output_target>;
		};
	};
};

EOF
        done

        # 生成 sink
        last_funnel2="funnel2_$((CLUSTERS-1))"

        # 检查是否启用了 SBA
        if [ "$SBA_ENABLE" = "true" ]; then
            # SBA 模式：SMEM Sink 地址计算时 cluster 数量 +1（SBA占位）
            sink_dmi_base=$((0x10000 * (CLUSTERS + 1)))
            sink_dmi_offset=0x400  # smem offset
            sink_dmi=$((sink_dmi_base + sink_dmi_offset))
            sink_addr=$(calc_addr $sink_dmi)
        else
            # 原有逻辑
            sink_dmi_base=$((0x10000 * CLUSTERS))
            case "$SINK_TYPE" in
                smem) sink_dmi_offset=0x400 ;;
                sram) sink_dmi_offset=0x0 ;;
                pib)  sink_dmi_offset=0xc00 ;;
                *) echo "错误: 不支持的 sink 类型 '$SINK_TYPE'"; exit 1 ;;
            esac
            sink_dmi=$((sink_dmi_base + sink_dmi_offset))
            sink_addr=$(calc_addr $sink_dmi)
        fi

        cat <<EOF | sed 's/^/\t\t/' >> "$NTRACE_DTSI"
sink0: xtntrace_${SINK_TYPE}@$(echo $sink_addr | cut -d'x' -f2) {
	compatible = "xuantie_ntrace,sink-controller";
	reg = <0x0 $sink_addr 0x0 0x1000>;
	memory-region = <&xuantie_ntrace_reserved>;
	working_mode = "circulating buffer";
	format = "original";
	input_port {
		port0 {
			endpoint = <&$last_funnel2>;
		};
	};
};
EOF
    fi
fi

echo "✅ 已生成: $NTRACE_DTSI"