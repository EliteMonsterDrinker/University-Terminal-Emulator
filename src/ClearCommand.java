import java.util.List;

/**
 * Команда {@code clear} — очищает вывод терминала.
 *
 * <p>Использование: {@code clear}
 *
 */
public class ClearCommand implements Command {

    /**
     * {@inheritDoc}
     *
     * @return {@code "clear"}
     */
    @Override
    public String name() {
        return "clear";
    }

    /**
     * {@inheritDoc}
     *
     * @return описание команды
     */
    @Override
    public String help() {
        return "очищает терминал";
    }

    /**
     * Вызывает очистку области вывода UI.     *
     * @param ctx  контекст выполнения
     */
    @Override
    public void execute(List<String> args, CommandContext ctx) {
        ctx.clear();
    }
}
