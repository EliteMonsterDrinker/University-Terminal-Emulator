import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Команда {@code ls} — выводит содержимое директории.
 *
 * <p>Использование:
 * <ul>
 *   <li>{@code ls} — вывести содержимое текущей директории;</li>
 *   <li>{@code ls <путь>} — вывести содержимое указанной директории.</li>
 * </ul>
 *
 */
public class LsCommand implements Command {

    /**
     * {@inheritDoc}
     *
     * @return {@code "ls"}
     */
    @Override
    public String name() {
        return "ls";
    }

    /**
     * {@inheritDoc}
     *
     * @return описание команды
     */
    @Override
    public String help() {
        return "list directory(показывает список файлов внутри директории)";
    }

    /**
     * Выводит отсортированный список имён файлов в директории.
     *
     * @param args аргументы команды (опционально путь к директории)
     * @param ctx  контекст выполнения
     * @throws IOException если директория не читается
     */
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
