import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CommandContext {
    private final Consumer<String> out;
    private final Runnable clear;
    private final Supplier<Collection<Command>> commands;
    private Vfs vfs;
    private String cwd;

    public CommandContext(Consumer<String> out,
                          Runnable clear,
                          Supplier<Collection<Command>> commandSupplier,
                          Vfs vfs) {
        this.out = out;
        this.clear = clear;
        this.commands = commandSupplier;
        this.vfs = vfs;
        this.cwd = "/";
                          }

                          public Collection<Command> commands() { return commands.get(); }
                          public void println(String s)         { out.accept(s + "\n"); }
                          public Vfs vfs()                      { return vfs; }
                          public void setVfs(Vfs v)             { this.vfs = v; }
                          public String cwd()                   { return cwd; }
                          public void setCwd(String p)          { this.cwd = p; }
                          public void clear()                   { clear.run(); }
}
