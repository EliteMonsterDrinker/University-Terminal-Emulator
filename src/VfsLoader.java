import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class VfsLoader {

    @SuppressWarnings("unchecked")
    public static Vfs load(Path file) throws IOException {
        if (!Files.exists(file)) {
            throw new IOException("Файл VFS не найден: " + file);
        }
        byte[] bytes = Files.readAllBytes(file);
        String text = new String(bytes, StandardCharsets.UTF_8);
        Object parsed;
        try {
            parsed = Json.parse(text);
        } catch (RuntimeException e) {
            throw new IOException("Неверный формат JSON: " + e.getMessage());
        }
        if (!(parsed instanceof Map)) {
            throw new IOException("Корневой элемент JSON должен быть объектом");
        }
        Map<String, Object> obj = (Map<String, Object>) parsed;
        String name = obj.get("name") != null
        ? obj.get("name").toString()
        : file.getFileName().toString();
        VfsNode root;
        Object rootObj = obj.get("root");
        if (rootObj instanceof Map) {
            root = parseNode("/", (Map<String, Object>) rootObj);
        } else {
            root = new VfsNode("/", true);
        }
        return new Vfs(name, root, bytes);
    }

    /** Создаёт VFS по умолчанию, если --vfs не задан. */
    public static Vfs defaultVfs() {
        VfsNode root = new VfsNode("/", true);
        VfsNode home = new VfsNode("home", true);
        VfsNode user = new VfsNode("user", true);
        VfsNode readme = new VfsNode("readme.txt", false);
        readme.setContent("Добро пожаловать в эмулятор терминала!\n");
        user.addChild(readme);
        home.addChild(user);
        root.addChild(home);
        return new Vfs("default", root, new byte[0]);
    }

    @SuppressWarnings("unchecked")
    private static VfsNode parseNode(String name, Map<String, Object> obj) {
        String type = obj.get("type") != null ? obj.get("type").toString() : "dir";
        boolean dir = "dir".equalsIgnoreCase(type) || "directory".equalsIgnoreCase(type);
        VfsNode node = new VfsNode(name, dir);
        if (obj.get("permissions") != null) node.setPermissions(obj.get("permissions").toString());
        if (obj.get("owner") != null)       node.setOwner(obj.get("owner").toString());

        if (dir) {
            Object children = obj.get("children");
            if (children instanceof Map) {
                for (Map.Entry<String, Object> e : ((Map<String, Object>) children).entrySet()) {
                    if (e.getValue() instanceof Map) {
                        node.addChild(parseNode(e.getKey(), (Map<String, Object>) e.getValue()));
                    }
                }
            }
        } else {
            Object content = obj.get("content");
            if (content != null) node.setContent(content.toString());
        }
        return node;
    }
}
