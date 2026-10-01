import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CommandContext{
    private final Consumer<String> out;
    private final Runnable clear;
    private final Supplier<Collection<Command>> commands;
    private Path cwd;
    //конструктор
    public CommandContext(Consumer<String> out, Runnable clear, Supplier<Collection<Command>> Commands, Path cwd){
        this.out = out;
        this.clear = clear;
        this.commands = Commands;
        this.cwd = cwd;
    }

    public Collection<Command> commands(){
        return commands.get();
    }

    public void println(String s){
        out.accept(s +"\n");
    }
    public Path cwd(){
        return cwd;
    }
    public void setCwd(Path p){
        this.cwd = p;}

    public void clear(){
        clear.run();
    }
}
