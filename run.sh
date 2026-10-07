#!/usr/bin/env bash
set -euo pipefail

# Перейти в директорию скрипта (корень проекта)
cd "$(dirname "$(readlink -f "$0")")"

export GDK_BACKEND=x11
export _JAVA_AWT_WM_NONREPARENTING=1

# Все аргументы командной строки передаются в Activator
exec java -cp "src:tests" src/Activator.java "$@"
