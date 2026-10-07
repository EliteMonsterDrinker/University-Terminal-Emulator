#!/usr/bin/env bash
set -euo pipefail

# Move to the directory containing the script (Project Root)
cd "$(dirname "$(readlink -f "$0")")"

export GDK_BACKEND=x11
export _JAVA_AWT_WM_NONREPARENTING=1

# тесты и src добавлены в class path
exec java -cp "src:tests" src/Activator.java
