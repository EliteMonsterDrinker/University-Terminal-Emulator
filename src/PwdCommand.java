import java.util.List;
//pwd
public class PwdCommand implements Command{
    public String name() { return "pwd";}
    public String help() { return "print working directory(вывод имени рабочей папки)";}
    public void execute(List<String> args, CommandContext ctx){
       ctx.println(ctx.cwd().toString());
    }
}
