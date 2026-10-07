import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Общий контекст, передаваемый каждой команде при выполнении.
 *
 * <p>Содержит:
 * <ul>
 *   <li>приёмник вывода ({@link Consumer}{@code <String>});</li>
 *   <li>действие очистки экрана ({@link Runnable});</li>
 *   <li>поставщик зарегистрированных команд ({@link Supplier});</li>
 *   <li>текущую рабочую директорию ({@link Path}).</li>
 * </ul>
 * */

public final class CommandContext {

    /** Приёмник строк для вывода на терминал. */
    private final Consumer<String> out;

    /** Действие очистки экрана. */
    private final Runnable clear;

    /** Поставщик коллекции зарегистрированных команд. */
    private final Supplier<Collection<Command>> commands;

    /** Текущая рабочая директория. */
    private Path cwd;

    /**
     * Создаёт контекст выполнения.
     *
     * @param out              приёмник вывода
     * @param clear            действие очистки экрана
     * @param commandSupplier  поставщик зарегистрированных команд
     * @param cwd              начальная рабочая директория
     */
    public CommandContext(
        Consumer<String> out,
        Runnable clear,
        Supplier<Collection<Command>> commandSupplier,
        Path cwd) {
        this.out = out;
        this.clear = clear;
        this.commands = commandSupplier;
        this.cwd = cwd;
        }

        /**
         * Возвращает коллекцию зарегистрированных команд.
         *
         * @return коллекция команд
         */
        public Collection<Command> commands() {
            return commands.get();
        }

        /**
         * Печатает строку в вывод терминала, добавляя перевод строки.
         *
         * @param s строка для печати
         */
        public void println(String s) {
            out.accept(s + "\n");
        }

        /**
         * Возвращает текущую рабочую директорию.
         *
         * @return текущая рабочая директория
         */
        public Path cwd() {
            return cwd;
        }

        /**
         * Устанавливает новую рабочую директорию.
         *
         * @param p новый путь
         */
        public void setCwd(Path p) {
            this.cwd = p;
        }

        /**
         * Очищает вывод терминала.
         */
        public void clear() {
            clear.run();
        }
}
