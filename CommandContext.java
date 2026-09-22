import java.nio.file.Path;
import java.util.function.Consumer;

public final class CommandContext{
    private final Consumer<String> out;
    private Path cwd;
    //конструктор
    public CommandContext(Consumer<String> out, Path cwd){
        this.out = out;
        this.cwd = cwd;
    }

    public void println(String s){
        out.accept(s +"\n");
    }
    public Path cwd(){
        return cwd;
    }
    public void setCwd(Path p){
        this.cwd = p;}
}
