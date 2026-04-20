#!/usr/bin/env bash

set -u

# export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
BUILD_ARGS=(
  clean
  install
  -Dquickly
  -Dmaven.test.skip=true
  -Dmaven.javadoc.skip=true
)

run_with_mvn() {
  if ! command -v mvn >/dev/null 2>&1; then
    echo "❌ Maven CLI not found, fallback build is unavailable."
    return 1
  fi

  echo "🔁 Falling back to mvn..."
  mvn "${BUILD_ARGS[@]}"
}

if command -v mvnd >/dev/null 2>&1; then
  # 1. 停止 mvnd 后台守护进程 (清除常驻内存的脏状态)
  echo "🧹 Stopping mvnd daemons to clear cache..."
  mvnd --stop || true

  # 2. 优先使用 mvnd 构建，失败时自动回退到 mvn
  echo "🚀 Starting build with mvnd..."
  if mvnd "${BUILD_ARGS[@]}" -Dmvnd.log.target=console; then
    exit 0
  fi

  echo "⚠️ mvnd build failed, retrying with mvn..."
  mvnd --stop || true
  run_with_mvn
  exit $?
fi

echo "⚠️ mvnd not found, using mvn directly..."
run_with_mvn
