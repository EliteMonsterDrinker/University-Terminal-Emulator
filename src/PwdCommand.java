import java.util.List;

/**
 * Команда {@code pwd} — печатает текущую рабочую директорию.
 *
 * <p>Использование: {@code pwd}
 *
 */
public class PwdCommand implements Command {

    /**
     * {@inheritDoc}
     *
     * @return {@code "pwd"}
     */
    @Override
    public String name() {
        return "pwd";
    }

    /**
     * {@inheritDoc}
     *
     * @return описание команды
     */
    @Override
    public String help() {
        return "print working directory(вывод имени рабочей папки)";
    }

    /**
     * Печатает абсолютный путь текущей рабочей директории.
     *
     * @param ctx  контекст выполнения
     */
    @Override
    public void execute(List<String> args, CommandContext ctx) {
        ctx.println(ctx.cwd().toString());
    }
}
