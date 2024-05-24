package model;

import returnStatus.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionManager {
    private Transactions transactions;
    private final GraphManager graphManager;

    public TransactionManager() {
        this.graphManager = new GraphManager();
    }

    public void updateTransactionManager(Transaction transaction) {
        List<Integer> possibiliOfferte = addTransactionToGraph(transaction);
        if (!possibiliOfferte.isEmpty())
            resolveTransaction(transaction.id(), possibiliOfferte);

    }

    private void resolveTransaction(int transaction, List<Integer> possibiliOfferte) {
        List<Integer> closedTransaction = graphManager.findClosedPath(possibiliOfferte, transaction);
        if (closedTransaction.isEmpty())
            return;
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

    }

    private void changeTransactionCloser(Transaction t, String closerName) {
        transactions.updateTransactionCloser(t, closerName);
    }


    private ArrayList<Integer> addTransactionToGraph(Transaction transaction) {

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

    public void loadTransactionsManager(Transactions transactions) {
        this.transactions = transactions;
        for (Map.Entry<Integer, Transaction> entry : this.transactions.getTransactions().entrySet()) {
            Transaction t = entry.getValue();
            fillTransactionGraph(t);
        }

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

    public void addTransaction(Transaction t) {
        transactions.addTransaction(t);
        updateTransactionManager(t);
    }

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

    public Transactions getTransactions() {
        return transactions;
    }
}
