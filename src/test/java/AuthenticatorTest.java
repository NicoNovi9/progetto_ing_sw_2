import org.junit.jupiter.api.*;
import repository.AuthenticatorDB;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticatorTest {
    private static final String DEFAULT_CSV_FILE_PATH = "src/test/resources/dc_test.csv";
    private static final String REAL_CSV_FILE_PATH = "src/test/resources/cc_test.csv";

    private AuthenticatorDB authenticatorDB;

    public static void cleanCsv(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        authenticatorDB = new AuthenticatorDB(DEFAULT_CSV_FILE_PATH, REAL_CSV_FILE_PATH);
        authenticatorDB.addCredentialToCSV("admin", "admin", "admin@gmail.com", "unibs","config",DEFAULT_CSV_FILE_PATH);
        authenticatorDB.addCredentialToCSV("admin1", "admin1", "admin1@gmail.com", "unibs", "config",DEFAULT_CSV_FILE_PATH);

    }

    @AfterEach
    void tearDown() {
        // Delete temporary CSV files created during the test
        cleanCsv(DEFAULT_CSV_FILE_PATH);
        cleanCsv(REAL_CSV_FILE_PATH);
    }

    @Test
    void testFirstLogin() {
        assertTrue(authenticatorDB.firstLogin("admin", "admin", "user", "password", "","","config"));
        assertTrue(authenticatorDB.login("user", "password", "config"));
        assertFalse(authenticatorDB.firstLogin("admin1", "admin1", "user", "password", "","", "config")); // Cannot add existing user
    }

    @Test
    void testLogin() {
        authenticatorDB.firstLogin("admin", "admin", "user", "password", "","", "config");
        assertTrue(authenticatorDB.login("user", "password", "config"));
        assertFalse(authenticatorDB.login("user", "wrong_password", "config"));
        assertFalse(authenticatorDB.login("non_existing_user", "password", "config"));
    }

    @Test
    void testLoadCredentialsToMap() {
        authenticatorDB.addCredentialToCSV("user", "password", "user@gmail.com", "unibs", "config", REAL_CSV_FILE_PATH);
        assertTrue(authenticatorDB.isNamePresent(REAL_CSV_FILE_PATH, "user"));
        assertEquals("password", authenticatorDB.getPasswordFromName(REAL_CSV_FILE_PATH, "user"));

    }

    @Test
    void testRemoveRecordFromCSV() throws IOException {
        authenticatorDB.addCredentialToCSV("user1", "password1", "user1@gmail.com", "unibs", "config", REAL_CSV_FILE_PATH);
        authenticatorDB.removeRecordFromCSV("user1", REAL_CSV_FILE_PATH);
        assertFalse(authenticatorDB.login(REAL_CSV_FILE_PATH, "user1", "config"));

    }
}

