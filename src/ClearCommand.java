import java.util.List;

/**
 * Clears the terminal output.
 */
public class ClearCommand implements Command {
    @Override
    public String name() {
        return "clear";
    }

    @Override
    public String help() {
        return "очищает терминал";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) {
        ctx.clear();
    }
}
