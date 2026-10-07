import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Интерпретатор команд
 *
 * <p>Работает в отдельном потоке и выполняет следующие функции:
 * <ol>
 *   <li>Извлекает команды из {@link BlockingQueue} (Producer-Consumer).</li>
 *   <li>Раскрывает переменные окружения вида {@code $VAR} и {@code ${VAR}}.</li>
 *   <li>Ищет команду в реестре и вызывает её метод {@code execute}.</li>
 *   <li>Сигнализирует UI о готовности к новой команде через {@code onIdle}.</li>
 * </ol>
 *
 * @see Command
 * @see CommandContext
 */
public class CommandInterpreter implements Runnable {

    /** Очередь команд */
    private final BlockingQueue<String> queue;

    /** Приёмник вывода. */
    private final Consumer<String> out;

    /** Действие, вызываемое после обработки каждой команды. */
    private final Runnable onIdle;

    /** Реестр зарегистрированных команд: имя:команда. */
    private final Map<String, Command> registry = new HashMap<>();

    /** Контекст выполнения, разделяемый всеми командами. */
    private final CommandContext ctx;

    /** Регулярное выражение для поиска переменных окружения. */
    private static final Pattern ENV_PATTERN =
    Pattern.compile("\\$(?:\\{([a-zA-Z_][a-zA-Z0-9_]*)\\}|([a-zA-Z_][a-zA-Z0-9_]*))");

    /**
     * Создаёт интерпретатор команд.
     *
     * @param queue      очередь входящих команд
     * @param out        приёмник вывода
     * @param onIdle     действие после обработки команды (показ приглашения)
     * @param onClear    действие очистки экрана
     * @param initialCwd начальная рабочая директория
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
        this.ctx = new CommandContext(out, onClear, () -> registry.values(), initialCwd);
        registerDefaults();
        }

        /**
         * Основной цикл обработки команд.
         * Блокируется на {@link BlockingQueue#take()} до поступления новой команды.
         */
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

        /**
         * Разбирает строку команды и выполняет её.
         *
         * @param line строка команды
         * @throws Exception если команда не найдена или её выполнение провалилось
         */
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

        /**
         * Регистрирует команду в реестре по её имени.
         *
         * @param c команда
         */
        private void register(Command c) {
            registry.put(c.name(), c);
        }

        /**
         * Выполняет команду из интерактивного ввода, перехватывая ошибки.
         *
         * @param line строка команды
         */
        public void executeLine(String line) {
            try {
                dispatch(expandEnvVars(line));
            } catch (Exception e) {
                out.accept("Ошибка: " + e.getMessage() + "\n");
            }
        }

        /**
         * Выполняет команду из скрипта, пробрасывая ошибки наверх
         * для остановки скрипта
         *
         * @param line строка команды
         * @throws Exception если выполнение провалилось
         */
        public void executeLineStrict(String line) throws Exception {
            dispatch(expandEnvVars(line));
        }

        /**
         * Раскрывает переменные окружения в строке команды.
         * Поддерживает формы {@code $VAR} и {@code ${VAR}}.
         *
         * @param line исходная строка
         * @return строка с подставленными значениями переменных
         */
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

        /**
         * Регистрирует все команды по умолчанию.
         */
        private void registerDefaults() {
            register(new PwdCommand());
            register(new CdCommand());
            register(new LsCommand());
            register(new ClearCommand());
            register(new HelpCommand());
            register(new ExitCommand());
        }
         }
