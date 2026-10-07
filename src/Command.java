import java.util.List;

/**
 * Template that all terminal commands must implement.
 */
public interface Command {
    /**
     * Returns the command name.
     *
     * @return command name
     */
    String name();

    /**
     * Returns a short command description.
     *
     * @return help text
     */
    String help();

    /**
     * Executes the command.
     *
     * @param args command arguments
     * @param ctx command context
     * @throws Exception if execution fails
     */
    void execute(List<String> args, CommandContext ctx) throws Exception;
}
