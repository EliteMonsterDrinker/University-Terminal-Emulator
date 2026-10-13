set -euo pipefail
cd "$(dirname "$(readlink -f "$0")")/.."

echo "=== Тест A: минимальная VFS ==="
./run.sh --vfs=tests/vfs_minimal.json --script=tests/stage3_commands.txt || true

echo "=== Тест B: вложенная VFS (3+ уровня) ==="
./run.sh --vfs=tests/vfs_nested.json --script=tests/stage3_commands.txt || true

echo "=== Тест C: VFS + chmod (этап 5) ==="
./run.sh --vfs=vfs_source.json --script=tests/stage5_commands.txt || true

echo "=== Тест D: ошибка — файл VFS не найден ==="
./run.sh --vfs=does_not_exist.json || echo "Завершено с ошибкой (ожидаемо)"

echo "=== Тест E: VFS не задана (default) ==="
./run.sh --script=tests/stage3_commands.txt || true
