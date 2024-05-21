package model;

import java.util.ArrayList;

public class Node {
    private final String name;
    private final String description;
    private ArrayList<Node> children;
    private final boolean isLeaf;
    private String path;

    public Node(String name, String description, boolean isLeaf) {
        this.name = name.toLowerCase();
        this.description = description;
        this.isLeaf = isLeaf;
        if (!isLeaf)
            children = new ArrayList<>();
        this.path = name;
    }

    public void addChildren(Node temp) throws LeafException {
        if (isLeaf)
            throw new LeafException();
        else {
            temp.setPath(path + "-" + temp.name);
            children.add(temp);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String root1) {
        this.path = root1;
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    // -1 se può contenere solo sottocategorie,
    // 0 se è vuoto, quindi potrebbe contenere sottocategorie e foglie,
    // 1 se può contenere solo foglie
    public int content() {
        if (children.isEmpty())
            return 0;
        else if (children.get(0).isLeaf)
            return 1;
        else
            return -1;
    }

    public String getName() {
        return name;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public String getDescription() {
        return description;
    }
}
