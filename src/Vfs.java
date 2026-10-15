import java.util.ArrayList;
import java.util.List;

public class Vfs {
    private final String name;
    private final VfsNode root;
    private final byte[] rawBytes;

    public Vfs(String name, VfsNode root, byte[] rawBytes) {
        this.name = name;
        this.root = root;
        this.rawBytes = rawBytes;
    }

    public String name()     { return name; }
    public VfsNode root()    { return root; }
    public byte[] rawBytes() { return rawBytes; }

    public VfsNode resolve(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) return root;
        VfsNode cur = root;
        for (String p : path.split("/")) {
            if (p.isEmpty() || p.equals(".")) continue;
            if (p.equals("..")) continue;
            if (cur == null || !cur.isDirectory()) return null;
            cur = cur.child(p);
            if (cur == null) return null;
        }
        return cur;
    }

    public static String normalize(String base, String path) {
        String combined;
        if (path.startsWith("/")) {
            combined = path;
        } else {
            combined = (base.endsWith("/") ? base : base + "/") + path;
        }
        List<String> stack = new ArrayList<>();
        for (String p : combined.split("/")) {
            if (p.isEmpty() || p.equals(".")) continue;
            if (p.equals("..")) {
                if (!stack.isEmpty()) stack.remove(stack.size() - 1);
            } else {
                stack.add(p);
            }
        }
        return "/" + String.join("/", stack);
    }
}
