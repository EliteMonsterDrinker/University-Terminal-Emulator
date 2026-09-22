import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class Activator {
    public static void main(String[] args) {
        final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
        final Path startDir = Path.of(System.getProperty("user.dir"));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UI ui = new UI(queue);

                Consumer<String> output = new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        ui.appendOutput(s);
                    }
                };

                Runnable prompt = new Runnable() {
                    @Override
                    public void run() {
                        ui.showPrompt();
                    }
                };

                CommandInterpreter interp =
                new CommandInterpreter(queue, output, prompt, startDir);

                Thread worker = new Thread(interp, "terminal-worker");
                worker.setDaemon(true);
                worker.start();

                ui.showPrompt();
            }
        });
    }
}
