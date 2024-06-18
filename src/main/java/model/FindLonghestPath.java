package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindLonghestPath implements ResolveGraphStrategy {

    List<List<Integer>> foundPaths;


    public FindLonghestPath() {
        this.foundPaths = new ArrayList<>();
    }

    @Override
    public  List resolve(Graph graph, int startNode ) {
        List<Integer> endNodes =searchValue(graph, startNode);
        findClosedPath(graph, endNodes, startNode);

        return null;
    }
    public List<Integer> findClosedPath(Graph graph, List<Integer> nodiDiArrivo, int startingNode) {

        foundPaths.clear();
        List<Integer> longhestPath = new ArrayList<>();

        for (int node : nodiDiArrivo) {
            findPaths(graph, startingNode, node);
        }
        for (List<Integer> p : foundPaths) {
            if (p.size() > longhestPath.size())
                longhestPath = p;
        }
        return longhestPath;

    }
    private void findPaths(Graph graph, int source, int destination) {
        Map<Integer, Boolean> visited = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : graph.getGraph().entrySet()) {
            int temp = entry.getKey();
            if (!visited.containsKey(temp))
                visited.put(temp, false);
            for (int i : entry.getValue())
                if (!visited.containsKey(i))
                    visited.put(i, false);
        }

        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(source);
        findPathsUtil(graph, source, destination, visited, currentPath);
    }
    public List<Integer> searchValue(Graph graph, int value) {
        List<Integer> foundKeys = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry :  graph.getGraph().entrySet()) {
            int key = entry.getKey();
            List<Integer> values = entry.getValue();
            if (values.contains(value)) {
                foundKeys.add(key);
            }
        }

        return foundKeys;
    }
    private void findPathsUtil(Graph graph,int u, int destination, Map<Integer, Boolean> visited, List<Integer> currentPath) {
        visited.replace(u, true);


        if (u == destination) {
            updateAllPathFound(currentPath);
        } else {
            for (Integer i : graph.getAdjacent(u)) {
                if (!visited.get(i)) {
                    currentPath.add(i);
                    findPathsUtil(graph, i, destination, visited, currentPath);
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

}
