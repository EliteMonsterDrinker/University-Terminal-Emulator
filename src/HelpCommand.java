import java.util.List;

/**
 * Prints available commands.
 */
public class HelpCommand implements Command {
    @Override
    public String name() {
        return "help";
    }

    @Override
    public String help() {
        return "показывает список команд";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) {
        for (Command c : ctx.commands()) {
            ctx.println(c.name() + " - " + c.help());
        }
    }
}
