package blackbox;

import controller.ControllerConfigurator;
import controller.ControllerUser;
import model.LeafException;
import model.Model;
import model.Node;
import model.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InterfaceDatabase;
import repository.LocalDatabase;
import returnStatus.TransactionStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TransactionsTest {
    public static String RESOURCES_PATH = "src/test/java/blackbox/resources/resources_path_test.txt";
    public ControllerConfigurator controllerConfigurator;
    public ControllerUser controllerUser;
    public Model model;

    // *****************************************************************************************************
    // SETUP AMBIENTE PER IL TEST BLACKBOX

    @BeforeEach
    public void setUp() throws LeafException {

        loadCredentialsForUser();

        InterfaceDatabase database = new LocalDatabase(RESOURCES_PATH);

        model = new Model(database);

        controllerConfigurator = new ControllerConfigurator(model);
        controllerUser = new ControllerUser(model);

        model.load();

        TS.loadDefaultTree(controllerConfigurator);
        model.save();


    }

    // per testare le transazioni in modo balck-box è necessario avere i file delle credenziali utente
    // e il file con l'id da assegnare alla prossima transazione creata
    private static void loadCredentialsForUser() {
        Path sourcePath1 = Paths.get("src/test/resources/credentials_test.csv");
        Path destinationPath1 = Paths.get("src/test/java/blackbox/resources/credentials_test.csv");
        Path idSourcePath1 = Paths.get("src/test/resources/id_test.txt");
        Path idDestinationPath1 = Paths.get("src/test/java/blackbox/resources/id_test.txt");

        try {
            Files.copy(sourcePath1, destinationPath1, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(idSourcePath1, idDestinationPath1, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void cleanUpFiles() throws IOException {
        File resourceFile = new File(RESOURCES_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(resourceFile))) {
            String filePath;
            while ((filePath = br.readLine()) != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    //********************************************************************************************************

    @Test
    void testAddTransaction(){
        // con la view è guidato nella selezione del nodo
        Node n1 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(0);
        Node n2 = controllerConfigurator.getRootArray().get(1).getChildren().get(0).getChildren().get(0);

        controllerUser.setCurrentUser("test1");
        int requested_hours = 10;
        Transaction t = controllerUser.createNewTransaction(n2, n1, requested_hours);
        controllerUser.addTransaction(t);

        Assertions.assertEquals(0, t.id());
        Assertions.assertEquals(n1.getPath(), t.offeredLeaf());
        Assertions.assertEquals(n2.getPath(), t.requestedLeaf());
        Assertions.assertEquals(controllerUser.getCurrentUser().district(), t.district());
        Assertions.assertEquals(requested_hours, t.requestedHours());
        Assertions.assertEquals(
                requested_hours * model
                        .getConversionFactors()
                        .findTripleValue(n2.getPath(),n1.getPath()),
                t.offeredHours());
        Assertions.assertEquals(TransactionStatus.OPEN,t.status());

    }

    @Test
    void testClosedTransactions4Cicle(){
        // con la view l'utente è guidato nella selezione del nodo
        // per i test vado a prendere i nodi "manualemente" dalla radice
        Node n1 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(0);
        Node n2 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(1);
        Node n3 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(2);
        Node n4 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(3);

        controllerUser.setCurrentUser("test1");
        Transaction t = controllerUser.createNewTransaction(n2, n1, 10);
        controllerUser.addTransaction(t);

        controllerUser.setCurrentUser("test2");
        Transaction t1 = controllerUser.createNewTransaction(n3, n2, 8);
        controllerUser.addTransaction(t1);

        controllerUser.setCurrentUser("test3");
        Transaction t2 = controllerUser.createNewTransaction(n4, n3, 7);
        controllerUser.addTransaction(t2);

        controllerUser.setCurrentUser("test1");
        Transaction t3 = controllerUser.createNewTransaction(n1, n4, 7);
        controllerUser.addTransaction(t3);

        // transazione non nel ciclo
        controllerUser.setCurrentUser("test1");
        Transaction t4 = controllerUser.createNewTransaction(n1, n4, 7);
        controllerUser.addTransaction(t4);

        // numero di transizioni chiuse
        Assertions.assertEquals(4, controllerConfigurator.getTransactionsByStatus(TransactionStatus.CLOSED).size());

        // numero di transazioni aperte
        Assertions.assertEquals(1, controllerConfigurator.getTransactionsByStatus(TransactionStatus.OPEN).size());

    }

    @Test
    void testCicleNotClosedBecauseDifferentDistrict(){
        // con la view l'utente è guidato nella selezione del nodo
        // per i test vado a prendere i nodi "manualemente" dalla radice
        Node n1 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(0);
        Node n2 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(1);
        Node n3 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(2);
        Node n4 = controllerConfigurator.getRootArray().get(0).getChildren().get(0).getChildren().get(3);

        controllerUser.setCurrentUser("test1");
        Transaction t = controllerUser.createNewTransaction(n2, n1, 10);
        controllerUser.addTransaction(t);

        controllerUser.setCurrentUser("test2");
        Transaction t1 = controllerUser.createNewTransaction(n3, n2, 8);
        controllerUser.addTransaction(t1);

        // l'utente "test4" è di un distretto differente rispetto a 1 e 2
        controllerUser.setCurrentUser("test4");
        Transaction t2 = controllerUser.createNewTransaction(n1, n3, 7);
        controllerUser.addTransaction(t2);

        // numero di transizioni chiuse
        Assertions.assertEquals(0, controllerConfigurator.getTransactionsByStatus(TransactionStatus.CLOSED).size());

        // numero di transazioni aperte
        Assertions.assertEquals(3, controllerConfigurator.getTransactionsByStatus(TransactionStatus.OPEN).size());

        // l'offerta di di test3 chiude il ciclo di 3 transazioni e rimane aperta l'offerta di 4
        controllerUser.setCurrentUser("test3");
        Transaction t3 = controllerUser.createNewTransaction(n1, n3, 7);
        controllerUser.addTransaction(t3);

        // numero di transizioni chiuse
        Assertions.assertEquals(3, controllerConfigurator.getTransactionsByStatus(TransactionStatus.CLOSED).size());

        // numero di transazioni aperte
        Assertions.assertEquals(1, controllerConfigurator.getTransactionsByStatus(TransactionStatus.OPEN).size());

    }

}
