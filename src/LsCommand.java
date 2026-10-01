import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;

public class LsCommand implements Command{
    public String name(){return "ls";}
    public String help(){return "list directory(показывает список файлов внутри директории)";}
    public void execute(List<String> args, CommandContext ctx) throws IOException{
        //если путь пустой - переводит в домашнюю папку, иначе по аргументу
        Path dir;
        if(args.isEmpty()){
            dir = ctx.cwd();
        }
        else{
            dir = ctx.cwd().resolve(args.get(0));
        }
        try(Stream<Path> s = Files.list(dir)){
            s.sorted().forEach(p -> ctx.println(p.getFileName().toString())); //выводит отсортированный список всех файлов в директории
        }
    }
}
