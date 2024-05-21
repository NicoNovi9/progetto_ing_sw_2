
import model.Model;
import org.junit.jupiter.api.Test;
import model.GraphManager;
import repository.InterfaceDatabase;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GraphManagerTest {

    GraphManager graphManager;
    Model model;
    InterfaceDatabase database;
    public static final String RESOURCES_PATH = "src/test/resources/resources_path_test.txt";


    @Test
    public void graphResolutionTest(){
        graphManager =new GraphManager();
        graphManager.addEdge(0, 1);
        graphManager.addEdge(1, 3);
        graphManager.addEdge(3, 0);
        graphManager.addEdge(1, 4);
        graphManager.addEdge(1, 5);
        graphManager.addEdge(4, 5);
        graphManager.addEdge(4, 0);
        graphManager.addEdge(5, 0);
        graphManager.addEdge(0, 2);
        graphManager.addEdge(2, 0);
        graphManager.addEdge(5, 6);
        graphManager.addEdge(6, 2);
        graphManager.addEdge(3, 6);
        graphManager.addEdge(7, 0);
        int partenza= 1;

        ArrayList<Integer> destinazioni=searchValue(partenza);


        ArrayList<Integer> actualArray = new ArrayList<>(Arrays.asList(1,4,5,6,2,0));

        Assertions.assertEquals(actualArray, graphManager.findClosedPath(destinazioni, partenza));

    }

    public ArrayList<Integer> searchValue(int value) {
        ArrayList<Integer> foundKeys = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry :  graphManager.getGraph().entrySet()) {
            int key = entry.getKey();
            List<Integer> values = entry.getValue();
            if (values.contains(value)) {
                foundKeys.add(key);
            }
        }

        return foundKeys;
    }

}
