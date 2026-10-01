#!/bin/bash
set -eo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
META_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
DISTRO_DIR="$META_DIR/conf/distro"

echo "=== 调试信息 ==="
echo "SCRIPT_DIR: $SCRIPT_DIR"
echo "META_DIR: $META_DIR"
echo "DISTRO_DIR: $DISTRO_DIR"
echo ""

echo "=== 检查目录存在性 ==="
if [ -d "$DISTRO_DIR" ]; then
    echo "✓ DISTRO目录存在: $DISTRO_DIR"
else
    echo "✗ DISTRO目录不存在: $DISTRO_DIR"
    echo "查找可能的distro目录："
    find "$META_DIR" -name "distro" -type d 2>/dev/null || echo "未找到distro目录"
    exit 1
fi

echo ""
echo "=== 检查.conf文件 ==="
CONF_FILES=$(ls "$DISTRO_DIR"/*.conf 2>/dev/null || echo "")
if [ -n "$CONF_FILES" ]; then
    echo "✓ 找到.conf文件："
    ls -la "$DISTRO_DIR"/*.conf
else
    echo "✗ 未找到.conf文件"
    echo "目录内容："
    ls -la "$DISTRO_DIR"
fi

echo ""
echo "=== 提取DISTRO名称 ==="
ALL_DISTROS=$(ls "$DISTRO_DIR"/*.conf 2>/dev/null | sed 's|.*/||; s|\.conf$||' | sort)
echo "提取的DISTRO: $ALL_DISTROS"
echo "DISTRO数量: $(echo "$ALL_DISTROS" | wc -w)"
