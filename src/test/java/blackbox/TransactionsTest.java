package blackbox;

import controller.ControllerConfigurator;
import model.Model;
import model.Node;
import org.junit.jupiter.api.BeforeEach;
import repository.InterfaceDatabase;
import repository.LocalDatabase;

public class TransactionsTest {
    public static String RESOURCES_PATH = "src/test/java/requirements/resources/resources_path_test.txt";
    public ControllerConfigurator controllerConfigurator;

    @BeforeEach
    public void setUp() {

        InterfaceDatabase database = new LocalDatabase(RESOURCES_PATH);

        Model model = new Model(database);

        controllerConfigurator = new ControllerConfigurator(model);

        model.load();
        Node rootNode = model.getRootNode();

    }
}
