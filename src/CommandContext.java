import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Shared context passed to commands.
 */
public final class CommandContext {
    private final Consumer<String> out;
    private final Runnable clear;
    private final Supplier<Collection<Command>> commands;
    private Path cwd;

    /**
     * Creates a command context.
     *
     * @param out output consumer
     * @param clear callback that clears the terminal
     * @param commandSupplier supplier of registered commands
     * @param cwd current working directory
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
         * Returns all registered commands.
         *
         * @return registered commands
         */
        public Collection<Command> commands() {
            return commands.get();
        }

        /**
         * Prints a line to the terminal output.
         *
         * @param s text to print
         */
        public void println(String s) {
            out.accept(s + "\n");
        }

        /**
         * Returns the current working directory.
         *
         * @return current working directory
         */
        public Path cwd() {
            return cwd;
        }

        /**
         * Sets the current working directory.
         *
         * @param p new working directory
         */
        public void setCwd(Path p) {
            this.cwd = p;
        }

        /**
         * Clears the terminal output.
         */
        public void clear() {
            clear.run();
        }
}
