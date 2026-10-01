import java.util.List;

public class HelpCommand implements Command {
    public String name() { return "help"; }
    public String help() { return "показывает список команд"; }

    @Override
    public void execute(List<String> args, CommandContext ctx) {
        for (Command c : ctx.commands()) {
            ctx.println(c.name() + " - " + c.help());
        }
    }
}
