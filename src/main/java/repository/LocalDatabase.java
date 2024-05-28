package repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import interfaceRepository.InterfaceDatabase;
import model.*;
import returnStatus.DatabaseStatus;
import returnStatus.TransactionStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LocalDatabase implements InterfaceDatabase {
    String conversion_path, districts_path, tree_path, transactions_path, id_path;
    private AuthenticatorDB authenticatorDB;

    public LocalDatabase(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            conversion_path = br.readLine();
            districts_path = br.readLine();
            tree_path = br.readLine();
            String default_credentials_path = br.readLine();
            String real_credentials_path = br.readLine();
            transactions_path = br.readLine();
            id_path = br.readLine();
            authenticatorDB = new AuthenticatorDB(default_credentials_path, real_credentials_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node loadTree() {
        String treeString = loadTreeFromJson();
        JsonNode jsonRootNode = constructJsonTree(treeString);
        return convertJsonTreeToLocalTree(jsonRootNode, "");
    }

    private JsonNode constructJsonTree(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRootNode = null;
        try {
            jsonRootNode = mapper.readTree(jsonString);
            return jsonRootNode;
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    private Node convertJsonTreeToLocalTree(JsonNode jsonNode, String parentPath) {
        JsonNode jNode = jsonNode.get("name");

        if (jNode == null) {
            return loadNodeFromJsonIfIsEmpty();
        }

        String name = jsonNode.get("name").asText();

        String description = jsonNode.get("description").asText();
        boolean isLeaf = jsonNode.get("leaf").asBoolean();
        Node node = new Node(name, description, isLeaf);

        node.setPath(parentPath.isEmpty() ? name : parentPath + "-" + name);

        if (!isLeaf) {
            JsonNode childrenNode = jsonNode.get("children");
            if (childrenNode != null && childrenNode.isArray()) {
                for (JsonNode child : childrenNode) {
                    Node childNode = convertJsonTreeToLocalTree(child, node.getPath());
                    try {
                        node.addChildren(childNode);
                    } catch (LeafException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return node;
    }

    private Node loadNodeFromJsonIfIsEmpty() {
        return new Node("-", "root", false);
    }

    public String loadTreeFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tree_path);
        try {
            if (!file.exists()) {
                ObjectNode emptyJson = mapper.createObjectNode();
                mapper.writeValue(file, emptyJson);
            }
            JsonNode jsonRootNode = mapper.readTree(file);
            return jsonRootNode.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public DatabaseStatus saveTree(Node rootNode) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = null;
        try {
            json = mapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            return DatabaseStatus.NOT_SAVED_TREE;
        }
        try (FileWriter fileWriter = new FileWriter(tree_path)) {
            fileWriter.write(json);
            return DatabaseStatus.SAVED_TREE;
        } catch (IOException e) {
            return DatabaseStatus.NOT_SAVED_TREE;
        }
    }

    @Override
    public DatabaseStatus saveConversionFactors(ConversionFactors conversionFactors) {
        return saveToCSV(conversionToCSV(conversionFactors), conversion_path);
    }

    public String[][] conversionToCSV(ConversionFactors conversionFactors) {
        ArrayList<Factor> factors = conversionFactors.getFactors();
        String[][] csvData = new String[conversionFactors.getFactors().size()][3]; // Supponendo che ogni riga abbia 3 colonne

        for (int i = 0; i < factors.size(); i++) {
            Factor factor = factors.get(i);
            csvData[i] = factorToCSV(factor);
        }

        return csvData;
    }

    @Override
    public DatabaseStatus saveTransactions(Transactions transactions) {
        return saveToCSV(transactionsToCSV(transactions), transactions_path);
    }

    private String[][] transactionsToCSV(Transactions transactions) {
        HashMap<Integer, Transaction> tMap = transactions.getTransactions();
        String[][] csvMatrix = new String[tMap.size()][];

        int index = 0;
        for (Map.Entry<Integer, Transaction> entry : tMap.entrySet()) {
            Transaction t = entry.getValue();
            csvMatrix[index] = transactionToCSV(t);
            index++;
        }

        return csvMatrix;
    }

    private String[] transactionToCSV(Transaction transaction) {
        String[] csvArray = new String[9]; // Creiamo un array di 9 stringhe per i 9 valori

        csvArray[0] = String.valueOf(transaction.id());
        csvArray[1] = transaction.applicantName();
        csvArray[2] = transaction.district();
        csvArray[3] = transaction.requestedLeaf();
        csvArray[4] = transaction.offeredLeaf();
        csvArray[5] = String.valueOf(transaction.requestedHours());
        csvArray[6] = String.valueOf(transaction.offeredHours());
        csvArray[7] = transaction.status().toString();
        csvArray[8] = transaction.closerName();

        return csvArray;
    }

    private String[] factorToCSV(Factor factor) {
        String secondString = String.format("%.2f", factor.second()); // Formatta il valore del secondo elemento come stringa
        return new String[]{factor.first(), secondString, factor.third()}; // Crea e restituisce un array di stringhe
    }

    @Override
    public DatabaseStatus saveDistricts(Districts districts) {
        return saveToCSV(districtsToCSV(districts), districts_path);
    }


    public String[][] districtsToCSV(Districts districts) {
        Set<String> dSet = districts.getDistricts();
        String[][] csvData = new String[dSet.size()][1];
        int i = 0;
        for (String district : dSet) {
            csvData[i++][0] = district;
        }
        return csvData;
    }

    @Override
    public ConversionFactors loadConversionFactors() {
        return constructConversions(loadFromCSV(conversion_path));
    }

    @Override
    public Transactions loadTransactions() {
        return constructTransactions(loadFromCSV(transactions_path));
    }

    @Override
    public Districts loadDistricts() {
        return constructDistricts(loadFromCSV(districts_path));
    }

    private ConversionFactors constructConversions(String[][] factorsArray) {
        ConversionFactors conversionFactors = new ConversionFactors();
        for (String[] factorData : factorsArray) {
            if (factorData.length >= 3) {
                String first = factorData[0];
                double second = Double.parseDouble(factorData[1].replace(",", "."));
                String third = factorData[2];
                conversionFactors.getFactors().add(new Factor(first, second, third));
            }
        }
        return conversionFactors;
    }

    public Districts constructDistricts(String[][] districtsArray) {
        Districts districts = new Districts();
        for (String[] districtRow : districtsArray) {
            for (String district : districtRow) {
                districts.addDistrict(district);
            }
        }
        return districts;
    }

    public Transactions constructTransactions(String[][] transactionsArray) {
        Transactions transactions = new Transactions();
        for (String[] transactionData : transactionsArray) {
            if (transactionData.length >= 9) {
                int id = Integer.parseInt(transactionData[0]);
                String applicantName = transactionData[1];
                String district = transactionData[2];
                String requestedLeaf = transactionData[3];
                String offeredLeaf = transactionData[4];
                int requestedHours = Integer.parseInt(transactionData[5]);
                int offeredHours = Integer.parseInt(transactionData[6]);
                TransactionStatus status = TransactionStatus.valueOf(transactionData[7]);
                String closerName = transactionData[8];

                Transaction transaction = new Transaction(id, applicantName, district, requestedLeaf, offeredLeaf, requestedHours, offeredHours, status, closerName);
                transactions.addTransaction(transaction);
            }
        }
        return transactions;
    }


    private DatabaseStatus saveToCSV(String[][] arrayRecord, String filePath) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            for (String[] record : arrayRecord) {
                writer.writeNext(record);
            }
            return DatabaseStatus.SAVED_TO_CSV;
        } catch (IOException e) {
            return DatabaseStatus.NOT_SAVED_TO_CSV;
        }
    }

    private String[][] loadFromCSV(String filePath) {

        ArrayList<String[]> array = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                array.add(line);
            }
            return array.toArray(new String[0][]);
        } catch (IOException | CsvValidationException e) {
            return new String[0][];
        }
    }

    @Override
    public int generateID() {
        try (BufferedReader reader = new BufferedReader(new FileReader(id_path))) {
            String line = reader.readLine();
            int id = Integer.parseInt(line);
            saveID(id + 1);
            return id;
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void saveID(int id) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(id_path))) {
            writer.write(String.valueOf(id));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String name) {
        return authenticatorDB.getUser(name);
    }

    @Override
    public boolean isFirstLogin(String name, String psw, String tag) {
        return authenticatorDB.isFirstLogin(name, psw, tag);
    }

    @Override
    public boolean login(String name, String psw, String tag) {
        return authenticatorDB.login(name, psw, tag);
    }

    @Override
    public boolean firstLogin(String name, String psw, String newName, String newPsw, String mail, String district, String tag) {
        return authenticatorDB.firstLogin(name, psw, newName, newPsw, mail, district, tag);
    }

}
