import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;


//создаёт отдельные потоки с помощью runnable, чтобы EDT отвечал только за окна
public class CommandInterpreter implements Runnable{
    private final BlockingQueue<String> queue;
    private final Consumer<String> out;
    private final Runnable onIdle; //показать системное приглашение через UI
    private final Map<String, Command> registry = new HashMap<>(); //каждой текстовой команде сопоставляем рабочую команду
    private final CommandContext ctx;
    public CommandInterpreter(BlockingQueue<String> queue, Consumer<String> out, Runnable onIdle, Path initialCwd){
        this.queue = queue;
        this.out = out;
        this.onIdle = onIdle;
        this.ctx = new CommandContext(out, initialCwd);
        registerDefaults(); // зарегистрированные команды
    }

    @Override //переопределяем метод run, чтобы он не блокировал поток EDT
    public void run(){
        try{
            while(true){
                String line = queue.take(); //пытается взять команду из очереди
                try{
                    dispatch(line); //пытается исполнить команду
                } catch(Exception e){
                    out.accept("error: " + e.getMessage() + "\n");
                }
                onIdle.run(); //просим класс UI вывести приглашение
            }
        } catch(InterruptedException ie){
            Thread.currentThread().interrupt(); //не получилось взять команду - останавливаемся
        }
    }

    private void dispatch(String line) throws Exception{ //метод для исполнения команд
        String[] parts = line.split(" ");
        if(parts.length == 0 || parts[0].isEmpty()){
            return; //если длина строки 0 или строка пустая
        }
        Command c = registry.get(parts[0]); //получаем первую команду
        if(c == null){
            out.accept("Неизвестная команда: " + parts[0] + "\n");
            return;
        }
        List<String> args = Arrays.asList(parts).subList(1, parts.length);
        c.execute(args, ctx);
    }

    //регистрация команды(любой команды)
    private void register(Command c){
        registry.put(c.name(), c);
    }
    //создаём команды
    private void registerDefaults(){
        register(new PwdCommand());
        //register(new CdCommand());
        //register(new LsCommand());
        //register(new EchoCommand());
        //register(new HelpCommand());
        //register(new ExitCommand());
    }
}
