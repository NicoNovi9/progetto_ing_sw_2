package requirements;
import model.Node;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InterfaceDatabase;
import repository.LocalDatabase;

import java.nio.file.*;

import java.io.*;

public class RepositoryTest {
    public static String RESOURCES_PATH = "src/test/java/requirements/resources/resources_path_test.txt";
    InterfaceDatabase db;


    @BeforeEach
    public void setUp() {

         db = new LocalDatabase(RESOURCES_PATH);

    }

    @AfterAll
    static void cleanUpFiles() throws IOException {
        File resourceFile = new File(RESOURCES_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(resourceFile))) {
            String filePath;
            while ((filePath = br.readLine()) != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    FileWriter fw = new FileWriter(file, false);
                    fw.write("");
                }
            }
        }
    }

    @Test
    void testFirstLogin() {
        // Copio il file con le credenziali di deafult di test (permanente)
        // nel file usato per il test (così da averlo sempre pulito ad ogni test)
        Path sourcePath = Paths.get("src/test/resources/default_test.csv");
        Path destinationPath = Paths.get("src/test/java/requirements/resources/default_test.csv");
        Path sourcePath1 = Paths.get("src/test/resources/credentials_test.csv");
        Path destinationPath1 = Paths.get("src/test/java/requirements/resources/credentials_test.csv");

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(sourcePath1, destinationPath1, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Assertions.assertTrue(db.isFirstLogin("config1", "psw1", "config"));

    }
}
