import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class Activator {

    private static final String ARG_VFS    = "--vfs=";
    private static final String ARG_SCRIPT = "--script=";
    private static final String ARG_CONFIG = "--config=";

    public static void main(String[] args) {
        try {
            Config config = parseArgs(args);
            Vfs vfs = initVfs(config);
            startUi(vfs, config);
        } catch (Exception e) {
            System.err.println("Ошибка запуска: " + e.getMessage());
            System.exit(1);
        }
    }


    private static Config parseArgs(String[] args) {
        String vfsPath = null, scriptPath = null, configPath = null;
        for (String arg : args) {
            if (arg.startsWith(ARG_VFS))         vfsPath    = arg.substring(ARG_VFS.length());
            else if (arg.startsWith(ARG_SCRIPT)) scriptPath = arg.substring(ARG_SCRIPT.length());
            else if (arg.startsWith(ARG_CONFIG)) configPath = arg.substring(ARG_CONFIG.length());
        }

        Map<String, String> fileConfig = readConfigFile(configPath);

        if (vfsPath == null)    vfsPath    = fileConfig.get("vfs");
        if (scriptPath == null) scriptPath = fileConfig.get("script");

        return new Config(vfsPath, scriptPath);
    }

    private static Map<String, String> readConfigFile(String configPath) {
        if (configPath == null) return Map.of();
        try {
            return parseYaml(Path.of(configPath));
        } catch (Exception e) {
            System.err.println("Ошибка чтения конфигурационного файла: " + e.getMessage());
            System.exit(1);
            return Map.of();
        }
    }


    private static Vfs initVfs(Config config) {
        if (config.vfsPath() == null) {
            System.out.println("[DEBUG] VFS по умолчанию (без --vfs)");
            return VfsLoader.defaultVfs();
        }
        Path vfsFile = Path.of(config.vfsPath());
        if (!Files.exists(vfsFile)) {
            System.err.println("Ошибка: Файл-источник VFS не найден: " + vfsFile);
            System.exit(1);
        }
        return loadVfs(vfsFile);
    }

    private static Vfs loadVfs(Path vfsFile) {
        try {
            Vfs vfs = VfsLoader.load(vfsFile);
            System.out.println("[DEBUG] VFS загружена: " + vfs.name()
            + " (" + vfsFile.toAbsolutePath() + ")");
            return vfs;
        } catch (IOException e) {
            System.err.println("Ошибка загрузки VFS: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }


    private static void startUi(Vfs vfs, Config config) {
        System.out.println("[DEBUG] Скрипт: "
        + (config.scriptPath() != null ? config.scriptPath() : "не задан"));

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        SwingUtilities.invokeLater(() -> createAndShowUi(queue, vfs, config.scriptPath()));
    }

    private static void createAndShowUi(BlockingQueue<String> queue, Vfs vfs, String scriptPath) {
        UI ui = new UI(queue);
        Consumer<String> output = s -> SwingUtilities.invokeLater(() -> ui.appendOutput(s));
        Runnable prompt = () -> SwingUtilities.invokeLater(ui::showPrompt);
        Runnable clear  = () -> SwingUtilities.invokeLater(ui::clearOutput);

        CommandInterpreter interp = new CommandInterpreter(queue, output, prompt, clear, vfs);

        if (scriptPath != null) runScriptFile(ui, interp, Path.of(scriptPath));

        Thread worker = new Thread(interp, "terminal-worker");
        worker.setDaemon(true);
        worker.start();
        ui.showPrompt();
    }


    private static void runScriptFile(UI ui, CommandInterpreter interp, Path scriptPath) {
        if (!Files.exists(scriptPath)) {
            ui.appendOutput("(Ошибка: скрипт не найден: " + scriptPath + ")\n");
            return;
        }
        ui.appendOutput("--- Выполнение скрипта: " + scriptPath + " ---\n");
        try {
            for (String line : Files.readAllLines(scriptPath)) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) continue;
                if (t.equals("exit")) continue;
                interp.executeLineStrict(t);
            }
        } catch (Exception e) {
            ui.appendOutput("Скрипт остановлен из-за ошибки: " + e.getMessage() + "\n");
        }
    }


    private static Map<String, String> parseYaml(Path path) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
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


    private record Config(String vfsPath, String scriptPath) {}
}
