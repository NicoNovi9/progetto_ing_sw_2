package model;

import repository.InterfaceDatabase;
import returnStatus.AddLeafStatus;
import returnStatus.DatabaseStatus;
import returnStatus.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model {
    public static final double LAMBDA = 0.001;
    private Node rootNode;
    private ConversionFactors conversionFactors;
    private Districts districts;
    private Transactions transactions;
    private final GraphManager graphManager;
    InterfaceDatabase database;

    public Model(InterfaceDatabase database) {
        districts = new Districts();
        conversionFactors = new ConversionFactors();
        transactions = new Transactions();
        graphManager = new GraphManager();
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
            if (factor == null){
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
        status.add(database.saveTransactions(transactions));
        status.add(database.saveDistricts(districts));

        return status;
    }

    public void load() {
        rootNode = database.loadTree();
        conversionFactors = database.loadConversionFactors();
        districts = database.loadDistricts();
        transactions = database.loadTransactions();
        loadTransactionsManager();
    }

    private void loadTransactionsManager() {
        for (Map.Entry<Integer, Transaction> entry : transactions.getTransactions().entrySet()) {
            Transaction t = entry.getValue();
            fillTransactionGraph(t);
        }

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

    // funzione usata per il testing
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }


    public boolean incorrectIndex(Node node, int index) {
        return (index < 0 || index >= node.getChildren().size());
    }


    public User searchUser(String name) {
        return getUserFromDB(name);
    }

    //START TRANSACTION REGION
    public List<Transaction> searchByStatus(TransactionStatus status) {
        return transactions.searchByStatus(status);
    }

    public List<Transaction> searchByRequestedLeaf(String path) {
        return transactions.searchByRequestedLeaf(path);
    }

    public List<Transaction> searchByOfferedLeaf(String path) {
        return transactions.searchByOfferedLeaf(path);
    }

    public List<Transaction> searchByApplicant(String name) {
        return transactions.searchByApplicant(name);
    }


    public void changeTransactionStatus(Transaction t, TransactionStatus status) {
        transactions.updateTransactionStatus(t.id(), status);
    }

    private void changeTransactionCloser(Transaction t, String closerName) {
        transactions.updateTransactionCloser(t, closerName);
    }

    public void addTransaction(Transaction t) {
        transactions.addTransaction(t);
        updateTransactionManager(t);
    }


    public void updateTransactionManager(Transaction transaction) {
        List<Integer> possibiliOfferte = addTransactionToGraph(transaction);
        if (!possibiliOfferte.isEmpty())
            resolveTransaction(transaction.id(), possibiliOfferte);

    }

    public boolean resolveTransaction(int transaction, List<Integer> possibiliOfferte) {
        List<Integer> closedTransaction = graphManager.findClosedPath(possibiliOfferte, transaction);
        if (closedTransaction.isEmpty())
            return false;
        graphManager.removeNodes(closedTransaction);
        //questo ciclo for ha senso solo se dentro il closedTransaction i numeri hanno un ordine(da controllare)
        for (int i = 0; i < closedTransaction.size(); i++) {
            int id = closedTransaction.get(i);
            changeTransactionStatus(transactions.getTransaction(id), TransactionStatus.CLOSED);
            int id2;
            if (i == (closedTransaction.size() - 1))
                id2 = closedTransaction.get(0);
            else id2 = closedTransaction.get(i + 1);
            changeTransactionCloser(transactions.getTransaction(id), transactions.getTransaction(id2).applicantName());
        }
        return true;

    }


    public ArrayList<Integer> addTransactionToGraph(Transaction transaction) {

        ArrayList<Integer> temp = new ArrayList<>();
        for (Map.Entry<Integer, Transaction> entry : transactions.getTransactions().entrySet()) {
            Transaction t = entry.getValue();
            if ((t.status().equals(TransactionStatus.OPEN)) && transaction.district().equals(t.district())) {
                if (transaction.requestedLeaf().equals(t.offeredLeaf()) &&
                        (transaction.requestedHours() >= (t.offeredHours() - 2) || transaction.requestedHours() <= (t.offeredHours() + 2))) {
                    graphManager.addEdge(t.id(), transaction.id());
                    temp.add(t.id());
                }
                if (t.requestedLeaf().equals(transaction.offeredLeaf()) &&
                        (t.requestedHours() >= (transaction.offeredHours() - 2) || t.requestedHours() <= (transaction.offeredHours() + 2))) {
                    graphManager.addEdge(transaction.id(), t.id());

                }
            }
        }
        return temp;
    }

    private void fillTransactionGraph(Transaction transaction) {
        for (Map.Entry<Integer, Transaction> entry : transactions.getTransactions().entrySet()) {
            Transaction t = entry.getValue();
            if ((t.status().equals(TransactionStatus.OPEN)) && transaction.district().equals(t.district())) {
                if (transaction.requestedLeaf().equals(t.offeredLeaf()) &&
                        (transaction.requestedHours() >= (t.offeredHours() - 2) && transaction.requestedHours() <= (t.offeredHours() + 2)))
                    graphManager.addEdge(t.id(), transaction.id());
            }
        }

    }

    public Transaction createNewTransaction(Node requestedLeaf, Node offeredLeaf, int requestedHours, String applicantName, String applicantDistrict) {
        if (requestedLeaf.getPath().equals(offeredLeaf.getPath()))
            return null;
        if (requestedHours <= 0)
            return null;
        int offeredHours = (int) Math.round(requestedHours * (findTripleValueInDB(requestedLeaf.getPath(), offeredLeaf.getPath())));
        return new Transaction(generateID(), applicantName, applicantDistrict, requestedLeaf.getPath(),
                offeredLeaf.getPath(), requestedHours, offeredHours, TransactionStatus.OPEN, null);
    }

    public User setCurrentUser(String name) {
        return getUserFromDB(name);
    }


    public Transactions getTransactions() {
        return transactions;
    }
}
