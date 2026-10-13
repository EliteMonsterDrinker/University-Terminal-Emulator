import java.util.List;

public class ChmodCommand implements Command {

    @Override public String name() { return "chmod"; }

    @Override public String help() {
        return "chmod <режим> <файл> — меняет права доступа (например: chmod 755 file)";
    }

    @Override
    public void execute(List<String> args, CommandContext ctx) throws Exception {
        if (args.size() < 2) {
            throw new Exception("Использование: chmod <режим> <путь>");
        }
        String mode = args.get(0);
        String target = Vfs.normalize(ctx.cwd(), args.get(1));

        VfsNode node = ctx.vfs().resolve(target);
        if (node == null) {
            throw new Exception("Файл не найден: " + target);
        }
        node.setPermissions(parseMode(mode));
    }

    private String parseMode(String mode) {
        if (mode.matches("[0-7]{3,4}")) {
            if (mode.length() == 4) mode = mode.substring(1);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                int d = mode.charAt(i) - '0';
                sb.append((d & 4) != 0 ? 'r' : '-');
                sb.append((d & 2) != 0 ? 'w' : '-');
                sb.append((d & 1) != 0 ? 'x' : '-');
            }
            return sb.toString();
        }
        if (mode.matches("[rwx-]{9}")) {
            return mode;
        }
        throw new IllegalArgumentException("Неверный режим: " + mode);
    }
}
