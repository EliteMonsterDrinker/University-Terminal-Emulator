import java.util.List;


public interface Command {
    String name();
    String help();
    void execute(List<String> args, CommandContext ctx) throws Exception;
}
