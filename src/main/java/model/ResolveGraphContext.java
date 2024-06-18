package model;

import java.util.List;

public class ResolveGraphContext {
    private ResolveGraphStrategy strategy;
    public void setStrategy(ResolveGraphStrategy strategy){
        this.strategy=strategy;
    }
    public List<Integer> resolve(Graph graph, int startNode){
        return strategy.resolve(graph, startNode);
    }
}
