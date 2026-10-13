import java.util.LinkedHashMap;
import java.util.Map;

public class VfsNode {
    private final String name;
    private final boolean directory;
    private final Map<String, VfsNode> children;
    private String content;
    private String permissions;
    private String owner;

    public VfsNode(String name, boolean directory) {
        this.name = name;
        this.directory = directory;
        this.children = directory ? new LinkedHashMap<>() : null;
        this.permissions = directory ? "rwxr-xr-x" : "rw-r--r--";
        this.owner = "user";
    }

    public String name()                    { return name; }
    public boolean isDirectory()            { return directory; }
    public Map<String, VfsNode> children()  { return children; }
    public String content()                 { return content; }
    public void setContent(String content)  { this.content = content; }
    public String permissions()             { return permissions; }
    public void setPermissions(String p)    { this.permissions = p; }
    public String owner()                   { return owner; }
    public void setOwner(String o)          { this.owner = o; }

    public VfsNode child(String n) {
        return children == null ? null : children.get(n);
    }

    public void addChild(VfsNode node) {
        if (children != null) children.put(node.name(), node);
    }
}
