# Скрипт реальной ОС для тестирования эмулятора (требование этапа 2, п.8)

echo "=== Тест 1: Все параметры через CLI ==="
./run.sh --vfs=vfs_source.json --script=tests/commands.txt

echo "=== Тест 2: Запуск с YAML-конфигом ==="
./run.sh --config=config.yaml

echo "=== Тест 3: CLI перекрывает YAML ==="
./run.sh --config=config.yaml --script=tests/mistake-commands.txt

echo "=== Тест 4: Ошибка — файл VFS не найден ==="
./run.sh --vfs=does_not_exist.json || echo "Завершено с ошибкой"
