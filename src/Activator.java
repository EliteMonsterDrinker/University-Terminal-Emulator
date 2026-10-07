import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 * Application entry point for the terminal emulator.
 */
public class Activator {
    private static final String[] SCRIPT_FILES = {
        "tests/commands.txt",
        "tests/mistake-commands.txt",
        "tests/commands-mixed.txt"
    };

    /**
     * Starts the terminal emulator.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        Path startDir = Path.of(System.getProperty("user.dir"));
        SwingUtilities.invokeLater(() -> createAndShowUi(queue, startDir));
    }

    private static void createAndShowUi(BlockingQueue<String> queue, Path startDir) {
        UI ui = new UI(queue);
        Consumer<String> output = s -> SwingUtilities.invokeLater(() -> ui.appendOutput(s));
        Runnable prompt = () -> SwingUtilities.invokeLater(ui::showPrompt);
        Runnable clear = () -> SwingUtilities.invokeLater(ui::clearOutput);

        CommandInterpreter interp = new CommandInterpreter(
            queue,
            output,
            prompt,
            clear,
            startDir);

        runScriptFiles(ui, interp, startDir);

        Thread worker = new Thread(interp, "terminal-worker");
        worker.setDaemon(true);
        worker.start();
        ui.showPrompt();
    }

    private static void runScriptFiles(UI ui, CommandInterpreter interp, Path startDir) {
        for (String file : SCRIPT_FILES) {
            Path p = startDir.resolve(file);
            if (!Files.exists(p)) {
                ui.appendOutput("(skip: " + file + " not found)\n");
                continue;
            }
            ui.appendOutput("--- " + file + " ---\n");
            readAndExecute(ui, interp, p);
        }
    }

    private static void readAndExecute(UI ui, CommandInterpreter interp, Path p) {
        try {
            for (String line : Files.readAllLines(p)) {
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) {
                    continue;
                }
                if (t.equals("exit")) {
                    continue;
                }
                interp.executeLine(t);
            }
        } catch (IOException e) {
            ui.appendOutput("error reading " + p + ": " + e.getMessage() + "\n");
        }
    }
}
