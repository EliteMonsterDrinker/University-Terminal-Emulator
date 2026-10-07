import java.util.List;

/**
 * Prints the current working directory.
 */
public class PwdCommand implements Command {
    @Override
    public String name() {
        return "pwd";
    }

    @Override
    public String help() {
        return "print working directory(вывод имени рабочей папки)";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) {
        ctx.println(ctx.cwd().toString());
    }
}
