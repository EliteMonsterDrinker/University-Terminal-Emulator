import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;

public class Activator {
    public static void main(String[] args) {
        final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        final Path startDir = Path.of(System.getProperty("user.dir"));

        SwingUtilities.invokeLater(() -> {
            UI ui = new UI(queue);

            Consumer<String> output = s -> SwingUtilities.invokeLater(() -> ui.appendOutput(s));
            Runnable prompt = () -> SwingUtilities.invokeLater(ui::showPrompt);
            Runnable clear  = () -> SwingUtilities.invokeLater(ui::clearOutput);

            CommandInterpreter interp =
            new CommandInterpreter(queue, output, prompt, clear, startDir);

            // ---- run the three script files, in order ----
            for (String file : new String[] {
                "/home/elitemonsterdrinker/Projects/emulator/University-Terminal-Emulator/commands.txt",
                "/home/elitemonsterdrinker/Projects/emulator/University-Terminal-Emulator/mistake-commands.txt",
                "/home/elitemonsterdrinker/Projects/emulator/University-Terminal-Emulator/commands-mixed.txt" }) {
                Path p = startDir.resolve(file);
                if (!Files.exists(p)) {
                    ui.appendOutput("(skip: " + file + " not found)\n");
                    continue;
                }
                ui.appendOutput("--- " + file + " ---\n");
                try {
                    for (String line : Files.readAllLines(p)) {
                        String t = line.trim();
                        if (t.isEmpty() || t.startsWith("#")) continue;
                        if (t.equals("exit")) continue;   // see note below
                        interp.executeLine(t);
                    }
                } catch (IOException e) {
                    ui.appendOutput("error reading " + file + ": " + e.getMessage() + "\n");
                }
                }
                // ---------------------------------------------

                Thread worker = new Thread(interp, "terminal-worker");
                worker.setDaemon(true);
                worker.start();
                ui.showPrompt();
        });
    }
}
