import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;

public class LsCommand implements Command{
    public String name(){return "ls";}
    public String help(){return "list directory(показывает список файлов внутри директории)";}
    public void execute(List<String> args, CommandContext ctx) throws IOException{
        ctx.println("ls");
    }
}
