import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CdCommand implements Command{
    public String name() {return "cd";}
    public String help() {return "Changes directory(меняет папку)";}
    public void execute(List<String> args, CommandContext ctx) throws Exception{
      Path target;
      if(args.isEmpty()){
        target=Path.of(System.getProperty("user.home"));
      }
      else{
        target = ctx.cwd().resolve(args.get(0)).normalize();
      }
      //проверяем, существует ли целевая директория
      if(!Files.exists(target)){
        throw new Exception("no such directory: " + target); //если не существует, кидаем исключение
      }
      ctx.setCwd(target.toRealPath()); //если существует, вызывает метод setCwd
    }
}
