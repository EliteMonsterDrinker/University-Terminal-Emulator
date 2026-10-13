import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LsCommand implements Command {

    @Override public String name() { return "ls"; }
    @Override public String help() { return "list directory (показывает список файлов внутри директории)"; }

    @Override
    public void execute(List<String> args, CommandContext ctx) throws Exception {
        String target = args.isEmpty()
        ? ctx.cwd()
        : Vfs.normalize(ctx.cwd(), args.get(0));

        VfsNode node = ctx.vfs().resolve(target);
        if (node == null) throw new Exception("Директория не найдена: " + target);

        if (!node.isDirectory()) {
            ctx.println(node.name());
            return;
        }
        List<String> names = new ArrayList<>(node.children().keySet());
        Collections.sort(names);
        for (String n : names) ctx.println(n);
    }
}
