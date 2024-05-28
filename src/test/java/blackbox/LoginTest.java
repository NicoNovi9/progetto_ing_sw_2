package blackbox;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interfaceRepository.InterfaceDatabase;
import repository.LocalDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LoginTest {
    public static String RESOURCES_PATH = "src/test/java/blackbox/resources/resources_path_test.txt";
    InterfaceDatabase db;

    // *****************************************************************************************************
    // SETUP AMBIENTE PER IL TEST BLACKBOX
    @BeforeEach
    public void setUp() {

        db = new LocalDatabase(RESOURCES_PATH);

        // Copio il file con le credenziali di deafult di test (permanente)
        // nel file usato per il test (così da averlo sempre pulito ad ogni test)
        Path sourcePath = Paths.get("src/test/resources/default_test.csv");
        Path destinationPath = Paths.get("src/test/java/blackbox/resources/default_test.csv");
        Path sourcePath1 = Paths.get("src/test/resources/credentials_test.csv");
        Path destinationPath1 = Paths.get("src/test/java/blackbox/resources/credentials_test.csv");

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(sourcePath1, destinationPath1, StandardCopyOption.REPLACE_EXISTING);
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
    //***********************************************************************************************+

    @Test
    void testFirstLoginCorrect() {
        // primo login corretto
        Assertions.assertTrue(db.firstLogin(
                "config1", "psw1", "TestReal", "test123", "", "", "config"));

        // utilizzo delle nuove credenziali
        Assertions.assertTrue(db.login("TestReal", "test123", "config"));

    }

    @Test
    void testFirstLoginUsed() {
        // uso le credenizali
        Assertions.assertTrue(db.firstLogin(
                "config1", "psw1", "TestReal", "test123", "", "", "config"));
        // primo login con credenziali di default già usate
        Assertions.assertFalse(db.firstLogin(
                "config1", "psw1", "TestReal", "test123", "", "", "config"));

    }

    @Test
    void testFirstLoginWrong() {

        // primo login con nome di default sbagliato
        Assertions.assertFalse(db.firstLogin(
                "config123", "psw1", "test", "test123", "", "", "config"));

        // primo login con psw sbagliata
        Assertions.assertFalse(db.firstLogin(
                "config2", "psw_wrong", "test", "test123", "", "", "config"));
    }

    @Test
    void testFirstLoginWrongTag() {
        // uso le credenizali di default config per user
        Assertions.assertFalse(db.firstLogin(
                "config1", "psw1", "TestReal", "test123", "", "", "user"));

    }

    @Test
    void testLoginCorrect() {
        // login corretto
        Assertions.assertTrue(db.login("test1", "12345678", "user"));

    }

    @Test
    void testLoginWrong() {

        // login nome inesistente
        Assertions.assertFalse(db.login("not_found", "12345678", "user"));

        // login con psw sbagliata
        Assertions.assertFalse(db.login("test1", "9999999", "config"));
    }

}
