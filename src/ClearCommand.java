import java.util.List;

public class ClearCommand implements Command {
    public String name() { return "clear"; }
    public String help() { return "очищает терминал"; }

    @Override
    public void execute(List<String> args, CommandContext ctx) {
        ctx.clear();
    }
}
