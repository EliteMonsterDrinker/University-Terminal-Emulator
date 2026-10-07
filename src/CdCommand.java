import java.nio.file.Path;
import java.util.List;

/**
 * Changes the current working directory.
 */
public class CdCommand implements Command {
  @Override
  public String name() {
    return "cd";
  }

  @Override
  public String help() {
    return "Changes directory(меняет папку)";
  }

  @Override
  public void execute(List<String> args, CommandContext ctx) throws Exception {
    Path target;
    if (args.isEmpty()) {
      target = Path.of(System.getProperty("user.home"));
    } else {
      String arg = args.get(0);
      if (arg.equals("~")) {
        target = Path.of(System.getProperty("user.home"));
      } else if (arg.startsWith("~/")) {
        Path home = Path.of(System.getProperty("user.home"));
        target = home.resolve(arg.substring(2));
      } else {
        target = ctx.cwd().resolve(arg).normalize();
      }
    }
    ctx.setCwd(target);
  }
}
