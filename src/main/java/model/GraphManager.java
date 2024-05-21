package model;

import java.util.*;

public class GraphManager {


    public Map<Integer, List<Integer>> getGraph() {
        return graph;
    }

    List<List<Integer>> foundPaths;

    private final Map<Integer, List<Integer>> graph;

    public GraphManager() {
        graph = new HashMap<>();
        foundPaths = new ArrayList<>();
    }


    public void addEdge(int from, int to) {
        graph.putIfAbsent(from, new LinkedList<>());
        graph.get(from).add(to);
    }

    public List<Integer> getAdjacent(int nodo) {
        return graph.getOrDefault(nodo, new LinkedList<>());
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

    public List<Integer> findClosedPath(List<Integer> nodiDiArrivo, int startingNode) {
        foundPaths.clear();
        List<Integer> longhestPath = new ArrayList<>();


        for (int node : nodiDiArrivo) {
            findPaths(startingNode, node);
        }
        for (List<Integer> p : foundPaths) {
            if (p.size() > longhestPath.size())
                longhestPath = p;
        }
        return longhestPath;

    }

    private void findPaths(int source, int destination) {
        Map<Integer, Boolean> visited = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            int temp = entry.getKey();
            if (!visited.containsKey(temp))
                visited.put(temp, false);
            for (int i : entry.getValue())
                if (!visited.containsKey(i))
                    visited.put(i, false);
        }

        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(source);
        findPathsUtil(source, destination, visited, currentPath);
    }

    private void findPathsUtil(int u, int destination, Map<Integer, Boolean> visited, List<Integer> currentPath) {
        visited.replace(u, true);


        if (u == destination) {
            updateAllPathFound(currentPath);
        } else {
            for (Integer i : getAdjacent(u)) {
                if (!visited.get(i)) {
                    currentPath.add(i);
                    findPathsUtil(i, destination, visited, currentPath);
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }

        visited.replace(u, false);
    }

    private void updateAllPathFound(List<Integer> currentPath) {
        List<Integer> temp = new ArrayList<>();
        for (int i : currentPath)
            temp.add(i);
        foundPaths.add(temp);
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
