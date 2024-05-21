package repository;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import model.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthenticatorDB {
    public String csvDefaultFilePath;
    public String csvPersonalFilePath;

    public AuthenticatorDB(String csvDefaultFilePath, String csvPersonalFilePath) {
        this.csvDefaultFilePath = csvDefaultFilePath;
        this.csvPersonalFilePath = csvPersonalFilePath;
    }

    public String getPasswordFromName(String csvFile, String name) {
        String password = null;

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length >= 2 && line[0].equals(name)) {
                    password = line[1];
                    break;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return password;
    }

    public String getTagFromName(String csvFile, String name) {
        String tag = null;

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length >= 2 && line[0].equals(name)) {
                    tag = line[4];
                    break;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return tag;
    }

    // Metodo per verificare se il nome è presente nel file CSV
    public boolean isNamePresent(String csvFile, String name) {
        boolean isPresent = false;

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length >= 1 && line[0].equals(name)) {
                    isPresent = true;
                    break;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        return isPresent;
    }

    // Metodo per rimuovere un record dal file CSV
    public void removeRecordFromCSV(String username, String csvFilePath) {
        List<String[]> newRows = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] nextRecord;
            while ((nextRecord = reader.readNext()) != null) {
                String recordUsername = nextRecord[0];
                if (!recordUsername.equals(username)) {
                    newRows.add(nextRecord);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath, false))) {
            writer.writeAll(newRows);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addCredentialToCSV(String username, String password, String email, String district, String tag, String csvFilePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath, true))) {
            String[] record = {username, password, email, district, tag};
            writer.writeNext(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFirstLogin(String username, String password, String tag) {
        return isNamePresent(csvDefaultFilePath, username) &&
                getPasswordFromName(csvDefaultFilePath, username).equals(password) &&
                getTagFromName(csvDefaultFilePath, username).equals(tag);
    }

    // Method to handle the first login with default credentials and setting up personal credentials
    public boolean firstLogin(
            String username, String password, String newUsername, String newPassword,
            String email, String district, String tag) {
        if (isNamePresent(csvPersonalFilePath, newUsername))
            return false;
        if (isFirstLogin(username, password, tag)) {
            addCredentialToCSV(newUsername, newPassword, email, district, tag, csvPersonalFilePath);
            removeRecordFromCSV(username, csvDefaultFilePath);
            return true;
        } else
            return false;
    }

    // Method to perform subsequent logins using personal credentials
    public boolean login(String username, String password, String tag) {
        return isNamePresent(csvPersonalFilePath, username)
                && getPasswordFromName(csvPersonalFilePath, username).equals(password)
                && getTagFromName(csvPersonalFilePath, username).equals(tag);
    }


    public User getUser(String searchString) {
        try (CSVReader reader = new CSVReader(new FileReader(csvPersonalFilePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line[0].equals(searchString)) {
                    if (line.length >= 4) {
                        return new User(line[0], line[2], line[3]);
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return null;
    }
}


