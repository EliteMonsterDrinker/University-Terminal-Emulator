import java.util.List;
//шаблон, которому должны следовать все команды
public interface Command{
    String name(); //имя
    String help(); //короткое обяснение команды
    void execute(List<String> args, CommandContext ctx) throws Exception;
}
