import java.util.List;

public class CdCommand implements Command {

  @Override public String name() { return "cd"; }
  @Override public String help() { return "Changes directory (меняет папку)"; }

  @Override
  public void execute(List<String> args, CommandContext ctx) throws Exception {
    String target;
    String home = homePath(ctx);

    if (args.isEmpty()) {
      target = home;
    } else {
      String arg = args.get(0);
      if (arg.equals("~"))           target = home;
      else if (arg.startsWith("~/")) target = Vfs.normalize(home, arg.substring(2));
      else                            target = Vfs.normalize(ctx.cwd(), arg);
    }

    VfsNode node = ctx.vfs().resolve(target);
    if (node == null)          throw new Exception("Директория не найдена: " + target);
    if (!node.isDirectory())   throw new Exception("Это не директория: " + target);
    ctx.setCwd(target);
  }

  private String homePath(CommandContext ctx) {
    return ctx.vfs().resolve("/home/user") != null ? "/home/user" : "/";
  }
}
