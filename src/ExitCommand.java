import java.util.List;

/**
 * Команда {@code exit} — завершает работу эмулятора.
 *
 * <p>Использование: {@code exit} *
 */
public class ExitCommand implements Command {

    /**
     * {@inheritDoc}
     *
     * @return {@code "exit"}
     */
    @Override
    public String name() {
        return "exit";
    }

    /**
     * {@inheritDoc}
     *
     * @return описание команды
     */
    @Override
    public String help() {
        return "Exit(закрыть)";
    }

    /**
     * завершает выполнение программы.
     *
     * @param args аргументы (игнорируются)
     * @param ctx  контекст выполнения
     * @throws Exception не выбрасывается
     */
    @Override
    public void execute(List<String> args, CommandContext ctx) throws Exception {
        System.exit(0);
    }
}
