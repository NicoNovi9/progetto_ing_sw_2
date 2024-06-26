package model;


import java.util.*;

public class GraphManager {

    List<List<Integer>> foundPaths;
    ResolveGraphContext resolveGraphCtx;

    private final Map<Integer, List<Integer>> graph;

    public Map<Integer, List<Integer>> getGraph() {
        return graph;
    }

    public GraphManager() {
        graph = new HashMap<>();
        foundPaths = new ArrayList<>();
        resolveGraphCtx = new ResolveGraphContext();
        resolveGraphCtx.setStrategy(new FindLonghestPath());
    }

    public void addEdge(int from, int to) {
        graph.putIfAbsent(from, new LinkedList<>());
        graph.get(from).add(to);
    }

    public List<Integer> getAdjacent(int nodo) {

        return graph.getOrDefault(nodo, new LinkedList<>());
    }

    public List<Integer> findClosedPath(int startingNode) {
        return resolveGraphCtx.resolve(this, startingNode);
    }

    //usage only for tests
    public void printGraph() {
        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            int node = entry.getKey();
            List<Integer> adjacent = entry.getValue();
            System.out.print("Nodo " + node + " -> ");
            for (int i : adjacent) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public void removeNodes(List<Integer> toRemoveNodes) {
        for (int node : toRemoveNodes) {
            graph.remove(node);
            for (List<Integer> adjacent : graph.values()) {
                adjacent.removeIf(n -> n == node);
            }
        }
    }

}
