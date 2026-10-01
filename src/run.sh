#!/usr/bin/env bash
set -euo pipefail

# run from this script's directory so java finds the sibling .java files
cd "$(dirname "$(readlink -f "$0")")"

export GDK_BACKEND=x11
export _JAVA_AWT_WM_NONREPARENTING=1

exec java Activator.java
