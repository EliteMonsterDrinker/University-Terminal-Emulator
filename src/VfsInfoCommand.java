import java.security.MessageDigest;
import java.util.List;

public class VfsInfoCommand implements Command {

    @Override public String name() { return "vfs-info"; }

    @Override public String help() {
        return "информация о VFS (имя и хеш SHA-256 данных)";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) throws Exception {
        Vfs vfs = ctx.vfs();
        if (vfs == null) {
            ctx.println("VFS не загружена");
            return;
        }
        ctx.println("name:   " + vfs.name());

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(vfs.rawBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        ctx.println("sha256: " + sb);
    }
}
