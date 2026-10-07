import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Reads commands from a queue and dispatches them to registered commands.
 */
public class CommandInterpreter implements Runnable {
    private final BlockingQueue<String> queue;
    private final Consumer<String> out;
    private final Runnable onIdle;
    private final Map<String, Command> registry = new HashMap<>();
    private final CommandContext ctx;

    /**
     * Creates a command interpreter.
     *
     * @param queue input command queue
     * @param out output consumer
     * @param onIdle callback invoked when the interpreter is idle
     * @param onClear callback that clears the terminal
     * @param initialCwd initial working directory
     */
    public CommandInterpreter(
        BlockingQueue<String> queue,
        Consumer<String> out,
        Runnable onIdle,
        Runnable onClear,
        Path initialCwd) {
        this.queue = queue;
        this.out = out;
        this.onIdle = onIdle;
        this.ctx = new CommandContext(
            out,
            onClear,
            () -> registry.values(),
                                      initialCwd);
        registerDefaults();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String line = queue.take();
                    try {
                        dispatch(line);
                    } catch (Exception e) {
                        out.accept("error: " + e.getMessage() + "\n");
                    }
                    onIdle.run();
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        private void dispatch(String line) throws Exception {
            String[] parts = line.split(" ");
            if (parts.length == 0 || parts[0].isEmpty()) {
                return;
            }
            Command c = registry.get(parts[0]);
            if (c == null) {
                out.accept("Неизвестная команда: " + parts[0] + "\n");
                out.accept("Введите help для помощи \n");
                return;
            }
            List<String> args = Arrays.asList(parts).subList(1, parts.length);
            c.execute(args, ctx);
        }

        private void register(Command c) {
            registry.put(c.name(), c);
        }

        /**
         * Executes a single command line without reading from the queue.
         *
         * @param line command line
         */
        public void executeLine(String line) {
            try {
                dispatch(line);
            } catch (Exception e) {
                out.accept("Ошибка: " + e.getMessage() + "\n");
            }
        }

        private void registerDefaults() {
            register(new PwdCommand());
            register(new CdCommand());
            register(new LsCommand());
            register(new ClearCommand());
            register(new HelpCommand());
            register(new ExitCommand());
        }
}
