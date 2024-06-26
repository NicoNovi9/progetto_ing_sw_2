package model;

import java.util.List;

public class ResolveGraphContext {
    private ResolveGraphStrategy strategy;

    public void setStrategy(ResolveGraphStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Integer> resolve(GraphManager graphManager, int startNode) {
        return strategy.resolve(graphManager, startNode);
    }
}
