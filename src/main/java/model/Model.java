package model;

import repository.InterfaceDatabase;
import returnStatus.AddLeafStatus;
import returnStatus.DatabaseStatus;
import returnStatus.TransactionStatus;

import java.util.ArrayList;
import java.util.List;

public class Model {
    public static final double LAMBDA = 0.001;
    private Node rootNode;
    private ConversionFactors conversionFactors;
    private Districts districts;
    private final TransactionManager transactionManager;
    InterfaceDatabase database;

    public Model(InterfaceDatabase database) {
        districts = new Districts();
        conversionFactors = new ConversionFactors();
        transactionManager = new TransactionManager();
        this.database = database;
    }


    public boolean addCategory(String name, String description) {
        for (Node n : rootNode.getChildren()) {
            if (n.getName().equals(name))
                return false;
        }
        try {
            rootNode.addChildren(new Node(name, description, false));
        } catch (LeafException e) {
            return false;
        }
        return true;
    }

    public boolean addSubCategory(String name, String description, Node parents) throws LeafException {
        for (Node n : parents.getChildren()) {
            if (n.getName().equals(name))
                return false;
        }
        parents.addChildren(new Node(name, description, false));
        return true;
    }

    public AddLeafStatus addLeaf(String name, String description, Node parents, Double factor, String conversionPath) throws LeafException {
        String currentPath = parents.getPath().toLowerCase() + "-" + name.toLowerCase();

        if (!factorsIsEmpty()) { // necessario dato che la prima volta factor e conversionPath saranno null
            if (factor == null) {
                return AddLeafStatus.INVALID_CONVERSION_FACTOR;
            }
            if (factor <= (0.5 - LAMBDA) || factor >= (2.0 + LAMBDA))
                return AddLeafStatus.INVALID_CONVERSION_FACTOR;
            if (!containsLeaf(conversionPath))
                return AddLeafStatus.INVALID_REFERENCE_FACTOR;
        }
        if (containsLeaf(currentPath))
            return AddLeafStatus.ALREADY_EXISTING_LEAF; // se c'è già un nodo con quel nome in quell'albero ritorna false

        Node child = new Node(name, description, true);

        parents.addChildren(child);
        addFactor(currentPath, factor, conversionPath);

        return AddLeafStatus.VALID_LEAF;
    }

    private void addFactor(String currentPath, Double factor, String conversionPath) {
        if (factorsIsEmpty()) {
            conversionFactors.addFirstFactor(currentPath);
        } else if (containsLeaf(conversionPath)) {
            conversionFactors.addFactor(currentPath, factor, conversionPath);
        }
    }

    public boolean containsLeaf(String path) {
        return doesNodeExistByPathHelper(rootNode, path);
    }

    private boolean doesNodeExistByPathHelper(Node node, String path) {
        if (node.getPath().equals(path)) {
            return true;
        } else {
            if (!node.isLeaf()) {
                for (Node child : node.getChildren()) {
                    if (doesNodeExistByPathHelper(child, path)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public ArrayList<Factor> findTripleFromPathDB(String path) {
        if (containsLeaf(path))
            return conversionFactors.findAllTripleValuePerLeaf(path);
        else return null;
    }

    public boolean factorsIsEmpty() {
        return conversionFactors.isEmpty();
    }

    public void addDistrict(String s) {
        districts.addDistrict(s);
    }

    public Districts getDistricts() {
        return districts;
    }

    public boolean containsDistrict(String s) {
        return districts.match(s);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public ConversionFactors getConversionFactors() {
        return conversionFactors;
    }

    public double findTripleValueInDB(String path, String path1) {
        return conversionFactors.findTripleValue(path, path1);
    }

    public ArrayList<DatabaseStatus> save() {
        ArrayList<DatabaseStatus> status = new ArrayList<>();

        status.add(database.saveTree(rootNode));
        status.add(database.saveConversionFactors(conversionFactors));
        status.add(database.saveTransactions(transactionManager.getTransactions()));
        status.add(database.saveDistricts(districts));

        return status;
    }

    public void load() {
        rootNode = database.loadTree();
        conversionFactors = database.loadConversionFactors();
        districts = database.loadDistricts();
        transactionManager.loadTransactionsManager(database.loadTransactions());
    }

    public int generateID() {
        return database.generateID();
    }

    public boolean isFirstLogin(String name, String psw, String tag) {
        return database.isFirstLogin(name, psw, tag);
    }

    public boolean login(String name, String psw, String tag) {
        return database.login(name, psw, tag);
    }

    public boolean firstLogin(String name, String psw, String newName, String newPsw, String mail, String district, String tag) {
        return database.firstLogin(name, psw, newName, newPsw, mail, district, tag);
    }

    public User getUserFromDB(String name) {
        return database.getUser(name);
    }

    public boolean incorrectIndex(Node node, int index) {
        return (index < 0 || index >= node.getChildren().size());
    }


    public User searchUser(String name) {
        return getUserFromDB(name);
    }

    public User setCurrentUser(String name) {
        return getUserFromDB(name);
    }


    // il model si occupa solo di creare la transazione dato che ha le info per poterla creare
    // (es: fattori di conversione)
    // a gestirle è poi il transacrionManager
    public Transaction createNewTransaction(Node requestedLeaf, Node offeredLeaf, int requestedHours, String applicantName, String applicantDistrict) {
        if (requestedLeaf.getPath().equals(offeredLeaf.getPath()))
            return null;
        if (requestedHours <= 0)
            return null;
        int offeredHours = (int) Math.round(requestedHours * (findTripleValueInDB(requestedLeaf.getPath(), offeredLeaf.getPath())));
        return new Transaction(generateID(), applicantName, applicantDistrict, requestedLeaf.getPath(),
                offeredLeaf.getPath(), requestedHours, offeredHours, TransactionStatus.OPEN, null);
    }

    public void changeTransactionStatus(Transaction t, TransactionStatus status) {
        transactionManager.changeTransactionStatus(t, status);
    }

    public List<Transaction> searchByApplicant(String name) {
        return transactionManager.searchByApplicant(name);
    }

    public void addTransaction(Transaction t) {
        transactionManager.addTransaction(t);
    }

    public List<Transaction> searchByStatus(TransactionStatus status) {
        return transactionManager.searchByStatus(status);
    }

    public List<Transaction> searchByRequestedLeaf(String path) {
        return transactionManager.searchByRequestedLeaf(path);
    }

    public List<Transaction> searchByOfferedLeaf(String path) {
        return transactionManager.searchByOfferedLeaf(path);
    }
}
