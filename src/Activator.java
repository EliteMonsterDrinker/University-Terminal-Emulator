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
        String vfsSourcePath = null;
        String scriptPath = null;
        String configPath = null;

        for (String arg : args) {
            if (arg.startsWith(ARG_VFS))         vfsSourcePath = arg.substring(ARG_VFS.length());
            else if (arg.startsWith(ARG_SCRIPT)) scriptPath    = arg.substring(ARG_SCRIPT.length());
            else if (arg.startsWith(ARG_CONFIG)) configPath    = arg.substring(ARG_CONFIG.length());
        }

        Map<String, String> config = new HashMap<>();
        if (configPath != null) {
            try { config = parseYaml(Path.of(configPath)); }
            catch (Exception e) {
                System.err.println("Ошибка чтения конфигурационного файла: " + e.getMessage());
                System.exit(1);
            }
        }

        if (vfsSourcePath == null) vfsSourcePath = config.get("vfs");
        if (scriptPath == null)    scriptPath    = config.get("script");

        Vfs vfs;
        if (vfsSourcePath != null) {
            Path vfsFile = Path.of(vfsSourcePath);
            if (!Files.exists(vfsFile)) {
                System.err.println("Ошибка: Файл-источник VFS не найден: " + vfsFile);
                System.exit(1);
                return;
            }
            try {
                vfs = VfsLoader.load(vfsFile);
                System.out.println("[DEBUG] VFS загружена: " + vfs.name()
                + " (" + vfsFile.toAbsolutePath() + ")");
            } catch (IOException e) {
                System.err.println("Ошибка загрузки VFS: " + e.getMessage());
                System.exit(1);
                return;
            }
        } else {
            vfs = VfsLoader.defaultVfs();
            System.out.println("[DEBUG] VFS по умолчанию (без --vfs)");
        }
        System.out.println("[DEBUG] Скрипт: " + (scriptPath != null ? scriptPath : "не задан"));

        final String  finalScriptPath = scriptPath;
        final Vfs     finalVfs        = vfs;

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        SwingUtilities.invokeLater(() -> createAndShowUi(queue, finalVfs, finalScriptPath));
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
}
