package model;

import java.util.List;

public interface ResolveGraphStrategy {
    List resolve(GraphManager graphManager, int startNode);
}
