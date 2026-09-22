import java.util.List;
public class ExitCommand implements Command{
    public String name(){return "exit";}
    public String help(){return "Exit(закрыть)";}
    public void execute(List<String> args, CommandContext ctx) throws Exception{
        ctx.println("Завершение работы...");
        System.exit(0);
    }
}
