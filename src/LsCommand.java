import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lists files in a directory.
 */
public class LsCommand implements Command {
    @Override
    public String name() {
        return "ls";
    }

    @Override
    public String help() {
        return "list directory(показывает список файлов внутри директории)";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) throws IOException {
        Path dir;
        if (args.isEmpty()) {
            dir = ctx.cwd();
        } else {
            dir = ctx.cwd().resolve(args.get(0));
        }
        try (Stream<Path> s = Files.list(dir)) {
            s.sorted().forEach(p -> ctx.println(p.getFileName().toString()));
        }
    }
}
