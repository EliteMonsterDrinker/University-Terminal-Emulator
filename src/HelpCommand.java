import java.util.List;

/**
 * Команда {@code help} — выводит список всех доступных команд с описанием.
 *
 * <p>Использование: {@code help}
 *
 */
public class HelpCommand implements Command {

    /**
     * {@inheritDoc}
     *
     * @return {@code "help"}
     */
    @Override
    public String name() {
        return "help";
    }

    /**
     * {@inheritDoc}
     *
     * @return описание команды
     */
    @Override
    public String help() {
        return "показывает список команд";
    }

    /**
     * Печатает имя и описание каждой зарегистрированной команды.
     *
     * @param ctx  контекст выполнения
     */
    @Override
    public void execute(List<String> args, CommandContext ctx) {
        for (Command c : ctx.commands()) {
            ctx.println(c.name() + " - " + c.help());
        }
    }
}
