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
        if (isRootOrEmpty(path)) return root;

        VfsNode cur = root;
        for (String segment : path.split("/")) {
            if (isSkippable(segment)) continue;
            if (!isDirectory(cur)) return null;
            cur = cur.child(segment);
            if (cur == null) return null;
        }
        return cur;
    }

    private static boolean isRootOrEmpty(String path) {
        return path == null || path.isEmpty() || path.equals("/");
    }

    private static boolean isSkippable(String segment) {
        return segment.isEmpty() || segment.equals(".") || segment.equals("..");
    }

    private static boolean isDirectory(VfsNode node) {
        return node != null && node.isDirectory();
    }


    public static String normalize(String base, String path) {
        String combined = combine(base, path);
        List<String> stack = new ArrayList<>();
        for (String segment : combined.split("/")) {
            applySegment(stack, segment);
        }
        return "/" + String.join("/", stack);
    }

    private static String combine(String base, String path) {
        if (path.startsWith("/")) return path;
        return (base.endsWith("/") ? base : base + "/") + path;
    }

    private static void applySegment(List<String> stack, String segment) {
        if (segment.isEmpty() || segment.equals(".")) return;
        if (segment.equals("..")) {
            if (!stack.isEmpty()) stack.remove(stack.size() - 1);
            return;
        }
        stack.add(segment);
    }
}
