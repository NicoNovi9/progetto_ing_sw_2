package model;

import java.util.List;

public interface ResolveGraphStrategy {
   public  List resolve(GraphManager graphManager, int startNode);
}
