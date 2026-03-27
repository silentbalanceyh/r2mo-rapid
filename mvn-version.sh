#!/bin/bash
# mvn-version.sh - 更新 R2MO Rapid 框架版本
# 用法: ./mvn-version.sh <新版本号>
# 示例: ./mvn-version.sh 1.0.51

set -e

NEW_VERSION="${1:-}"
POM_FILE="pom.xml"

if [ -z "$NEW_VERSION" ]; then
    echo "用法: ./mvn-version.sh <新版本号>"
    echo "示例: ./mvn-version.sh 1.0.51"
    exit 1
fi

# 获取当前版本
CURRENT_VERSION=$(grep -o '<revision>[^<]*</revision>' "$POM_FILE" | sed 's/<revision>\|<\/revision>//g')

if [ -z "$CURRENT_VERSION" ]; then
    echo "错误: 无法从 $POM_FILE 中读取当前版本"
    exit 1
fi

# 版本比较函数
version_gt() {
    test "$(printf '%s\n' "$1" "$2" | sort -V | head -n 1)" != "$1"
}

# 检查新版本是否高于当前版本
if ! version_gt "$NEW_VERSION" "$CURRENT_VERSION"; then
    echo "错误: 新版本 $NEW_VERSION 必须高于当前版本 $CURRENT_VERSION"
    exit 1
fi

echo "=== 升级 R2MO Rapid 框架 ==="
echo "当前版本: $CURRENT_VERSION"
echo "新版本: $NEW_VERSION"
echo ""

# 更新 revision 属性
sed -i '' "s/<revision>$CURRENT_VERSION<\/revision>/<revision>$NEW_VERSION<\/revision>/g" "$POM_FILE"

# 更新 parent 版本
sed -i '' "s|<artifactId>rachel-momo-0216</artifactId>[[:space:]]*<version>$CURRENT_VERSION</version>|<artifactId>rachel-momo-0216</artifactId>\n        <version>$NEW_VERSION</version>|g" "$POM_FILE"

echo "✅ 已更新 $POM_FILE"
echo ""
echo "升级完成！请执行以下命令重新构建："
echo "  mvn clean install -DskipTests"