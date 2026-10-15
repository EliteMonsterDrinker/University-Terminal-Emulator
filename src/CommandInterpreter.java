import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter implements Runnable {

    private final BlockingQueue<String> queue;
    private final Consumer<String> out;
    private final Runnable onIdle;
    private final Map<String, Command> registry = new HashMap<>();
    private final CommandContext ctx;

    private static final Pattern ENV_PATTERN =
    Pattern.compile("\\$(?:\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}|([a-zA-Z_][a-zA-Z0-9_]*))");

    public CommandInterpreter(
        BlockingQueue<String> queue,
        Consumer<String> out,
        Runnable onIdle,
        Runnable onClear,
        Vfs vfs) {
        this.queue = queue;
        this.out = out;
        this.onIdle = onIdle;
        this.ctx = new CommandContext(out, onClear, () -> registry.values(), vfs);
        registerDefaults();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String line = queue.take();
                    try {
                        dispatch(expandEnvVars(line));
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
            if (parts.length == 0 || parts[0].isEmpty()) return;
            Command c = registry.get(parts[0]);
            if (c == null) {
                out.accept("Неизвестная команда: " + parts[0] + "\n");
                out.accept("Введите help для помощи \n");
                return;
            }
            List<String> args = Arrays.asList(parts).subList(1, parts.length);
            c.execute(args, ctx);
        }

        private void register(Command c) { registry.put(c.name(), c); }

        public void executeLine(String line) {
            try { dispatch(expandEnvVars(line)); }
            catch (Exception e) { out.accept("Ошибка: " + e.getMessage() + "\n"); }
        }

        public void executeLineStrict(String line) throws Exception {
            dispatch(expandEnvVars(line));
        }

        private String expandEnvVars(String line) {
            Matcher m = ENV_PATTERN.matcher(line);
            StringBuilder sb = new StringBuilder();
            while (m.find()) {
                String var = m.group(1) != null ? m.group(1) : m.group(2);
                String val = System.getenv(var);
                m.appendReplacement(sb, val != null ? Matcher.quoteReplacement(val) : "");
            }
            m.appendTail(sb);
            return sb.toString();
        }

        private void registerDefaults() {
            register(new PwdCommand());
            register(new CdCommand());
            register(new LsCommand());
            register(new ClearCommand());
            register(new HelpCommand());
            register(new ExitCommand());
            register(new VfsInfoCommand());
            register(new ChmodCommand());
        }
}
