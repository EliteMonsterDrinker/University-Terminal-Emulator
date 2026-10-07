import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 *
 * <p>Класс выполняет следующие задачи:
 * <ol>
 *   <li>Разбирает аргументы командной строки ({@code --vfs}, {@code --script}, {@code --config}).</li>
 *   <li>Читает YAML-конфигурационный файл (при наличии).</li>
 *   <li>Применяет логику приоритетов: значения CLI перекрывают значения из конфигурации.</li>
 *   <li>Проверяет существование источника VFS (JSON-файла) на диске.</li>
 *   <li>Создает и запускает графический интерфейс {@link UI} в потоке EDT.</li>
 *   <li>Запускает рабочий поток {@link CommandInterpreter} для обработки команд.</li>
 *   <li>Выполняет стартовый скрипт (если задан), останавливаясь на первой ошибке.</li>
 * </ol>
 *
 * <p>
 *
 * @author Студент
 * @version 2.0
 * @see CommandInterpreter
 * @see UI
 */
public class Activator {

    /**
     * Ключ аргумента командной строки для указания пути к источнику VFS.
     */
    private static final String ARG_VFS = "--vfs=";

    /**
     * Ключ аргумента командной строки для указания пути к стартовому скрипту.
     */
    private static final String ARG_SCRIPT = "--script=";

    /**
     * Ключ аргумента командной строки для указания пути к YAML-конфигу.
     */
    private static final String ARG_CONFIG = "--config=";

    /**
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // 1. Разбор аргументов командной строки
        String vfsSourcePath = null;
        String scriptPath = null;
        String configPath = null;

        for (String arg : args) {
            if (arg.startsWith(ARG_VFS)) {
                vfsSourcePath = arg.substring(ARG_VFS.length());
            } else if (arg.startsWith(ARG_SCRIPT)) {
                scriptPath = arg.substring(ARG_SCRIPT.length());
            } else if (arg.startsWith(ARG_CONFIG)) {
                configPath = arg.substring(ARG_CONFIG.length());
            }
        }

        // 2. Чтение YAML
        Map<String, String> config = new HashMap<>();
        if (configPath != null) {
            try {
                config = parseYaml(Path.of(configPath));
            } catch (Exception e) {
                System.err.println("Ошибка чтения конфигурационного файла: " + e.getMessage());
                System.exit(1);
            }
        }

        // 3. Логика приоритетов: CLI > YAML
        if (vfsSourcePath == null) {
            vfsSourcePath = config.get("vfs");
        }
        if (scriptPath == null) {
            scriptPath = config.get("script");
        }

        // Проверка существования источника VFS
        if (vfsSourcePath != null) {
            Path vfsFile = Path.of(vfsSourcePath);
            if (!Files.exists(vfsFile)) {
                System.err.println("Ошибка: Файл-источник VFS не найден: " + vfsFile);
                System.exit(1);
            }
            System.out.println("[DEBUG] Источник VFS (JSON): " + vfsFile.toAbsolutePath());
        } else {
            System.out.println("[DEBUG] Источник VFS не задан. Работа с реальной ФС.");
        }

        System.out.println("[DEBUG] Скрипт: " + (scriptPath != null ? scriptPath : "не задан"));

        // Стартовая директория для эмулятора
        Path startDir = Path.of(System.getProperty("user.dir"));

        final String finalScriptPath = scriptPath;
        final Path finalStartDir = startDir;

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        SwingUtilities.invokeLater(() -> createAndShowUi(queue, finalStartDir, finalScriptPath));
    }

    /**
     * Создает и отображает графический интерфейс, а также запускает
     * фоновый поток обработки команд.
     *
     * @param queue      очередь команд, разделяемая между UI и интерпретатором
     * @param startDir   начальная рабочая директория
     * @param scriptPath путь к стартовому скрипту
     */
    private static void createAndShowUi(BlockingQueue<String> queue, Path startDir, String scriptPath) {
        UI ui = new UI(queue);
        Consumer<String> output = s -> SwingUtilities.invokeLater(() -> ui.appendOutput(s));
        Runnable prompt = () -> SwingUtilities.invokeLater(ui::showPrompt);
        Runnable clear = () -> SwingUtilities.invokeLater(ui::clearOutput);

        CommandInterpreter interp = new CommandInterpreter(
            queue,
            output,
            prompt,
            clear,
            startDir
        );

        if (scriptPath != null) {
            runScriptFile(ui, interp, Path.of(scriptPath));
        }

        Thread worker = new Thread(interp, "terminal-worker");
        worker.setDaemon(true);
        worker.start();
        ui.showPrompt();
    }

    /**
     * Выполняет стартовый скрипт строка за строкой, останавливаясь
     * на первой ошибке.
     *
     * @param ui         графический интерфейс для вывода сообщений
     * @param interp     интерпретатор команд
     * @param scriptPath путь к файлу скрипта
     */
    private static void runScriptFile(UI ui, CommandInterpreter interp, Path scriptPath) {
        if (!Files.exists(scriptPath)) {
            ui.appendOutput("(Ошибка: скрипт не найден: " + scriptPath + ")\n");
            return;
        }
        ui.appendOutput("--- Выполнение скрипта: " + scriptPath + " ---\n");
        try {
            for (String line : Files.readAllLines(scriptPath)) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) {
                    continue;
                }
                if (t.equals("exit")) {
                    continue;
                }
                interp.executeLineStrict(t);
            }
        } catch (Exception e) {
            ui.appendOutput("Скрипт остановлен из-за ошибки: " + e.getMessage() + "\n");
        }
    }

    /**
     * построчный парсер YAML-файла.
     * Поддерживает только пары {@code key: value} и комментарии {@code #}.
     *
     * @param path путь к YAML-файлу
     * @return карта «ключ — значение»
     * @throws IOException если файл не удаётся прочитать
     */
    private static Map<String, String> parseYaml(Path path) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int colon = line.indexOf(':');
            if (colon > 0) {
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                map.put(key, value);
            }
        }
        return map;
    }
}
