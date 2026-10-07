import java.nio.file.Path;
import java.util.List;

/**
 * Команда {@code cd} — меняет текущую рабочую директорию.
 *
 * <p>Поддерживаются формы:
 * <ul>
 *   <li>{@code cd} — перейти в домашнюю директорию пользователя;</li>
 *   <li>{@code cd ~} — то же самое;</li>
 *   <li>{@code cd ~/sub} — перейти в подкаталог домашней директории;</li>
 *   <li>{@code cd /abs/path} или {@code cd rel/path} — перейти по указанному пути.</li>
 * </ul>
 *
 * <p>Работает с реальной файловой системой. На этапе 3 будет заменена
 * на работу с VFS в памяти. *
 */
public class CdCommand implements Command {

  /**
   * {@inheritDoc}
   *
   * @return {@code "cd"}
   */
  @Override
  public String name() {
    return "cd";
  }

  /**
   * {@inheritDoc}
   *
   * @return описание команды
   */
  @Override
  public String help() {
    return "Changes directory(меняет папку)";
  }

  /**
   * Меняет текущую рабочую директорию.
   *
   * @param args аргументы команды (опционально путь)
   * @param ctx  контекст выполнения
   * @throws Exception если директория не существует или не является папкой
   */
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

    if (!target.toFile().exists()) {
      throw new Exception("Директория не найдена: " + target);
    }
    if (!target.toFile().isDirectory()) {
      throw new Exception("Это не директория: " + target);
    }

    ctx.setCwd(target.toAbsolutePath().normalize());
  }
}
