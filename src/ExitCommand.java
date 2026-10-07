import java.util.List;

/**
 * Exits the terminal.
 */
public class ExitCommand implements Command {
    @Override
    public String name() {
        return "exit";
    }

    @Override
    public String help() {
        return "Exit(закрыть)";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) throws Exception {
        ctx.println("Завершение работы...");
        System.exit(0);
    }
}
