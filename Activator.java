import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class Activator {
    public static void main(String[] args) {
        final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        final Path startDir = Path.of(System.getProperty("user.dir"));

        SwingUtilities.invokeLater(() -> {
            UI ui = new UI(queue);

            Consumer<String> output = s -> SwingUtilities.invokeLater(() -> ui.appendOutput(s)); // forwards text to the UI
            Runnable prompt = () -> SwingUtilities.invokeLater(ui::showPrompt); // показывает системное приглашение
            Runnable clear  = () -> SwingUtilities.invokeLater(() -> { //очищает терминал
                ui.clearOutput();   // must exist in UI
            });

            CommandInterpreter interp =
            new CommandInterpreter(queue, output, prompt, clear, startDir);

            Thread worker = new Thread(interp, "terminal-worker");
            worker.setDaemon(true);
            worker.start();
            ui.showPrompt();
        });
    }
}
