package model;


import returnStatus.TransactionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transactions {
    HashMap<Integer, Transaction> transactions;

    public Transactions() {
        transactions = new HashMap<>();
    }

    public HashMap<Integer, Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction t) {
        transactions.put(t.id(), t);
    }

    public void updateTransactionStatus(int id, TransactionStatus status) {
        Transaction t = transactions.get(id);
        Transaction updateT = new Transaction(t.id(), t.applicantName(), t.district(),
                t.requestedLeaf(), t.offeredLeaf(), t.requestedHours(), t.offeredHours(),
                status, t.closerName());
        transactions.replace(id, updateT);
    }

    public void updateTransactionCloser(Transaction transaction, String closerName) {
        Transaction t = transactions.get(transaction.id());
        Transaction updateT = new Transaction(t.id(), t.applicantName(), t.district(),
                t.requestedLeaf(), t.offeredLeaf(), t.requestedHours(), t.offeredHours(),
                t.status(), closerName);
        transactions.replace(transaction.id(), updateT);
    }


    public List<Transaction> searchByStatus(TransactionStatus status) {
        List<Transaction> temp = new ArrayList<>();
        for (Map.Entry<Integer, Transaction> entry : transactions.entrySet()) {
            if (entry.getValue().status().equals(status))
                temp.add(entry.getValue());

        }
        return temp;
    }

    public List<Transaction> searchByRequestedLeaf(String path) {
        List<Transaction> temp = new ArrayList<>();
        for (Map.Entry<Integer, Transaction> entry : transactions.entrySet()) {
            if (entry.getValue().requestedLeaf().equals(path))
                temp.add(entry.getValue());

        }
        return temp;
    }

    public List<Transaction> searchByOfferedLeaf(String path) {
        List<Transaction> temp = new ArrayList<>();
        for (Map.Entry<Integer, Transaction> entry : transactions.entrySet()) {
            if (entry.getValue().offeredLeaf().equals(path))
                temp.add(entry.getValue());

        }
        return temp;
    }

    public List<Transaction> searchByApplicant(String name) {
        List<Transaction> temp = new ArrayList<>();
        for (Map.Entry<Integer, Transaction> entry : transactions.entrySet()) {
            if (entry.getValue().applicantName().equals(name))
                temp.add(entry.getValue());

        }
        return temp;
    }


    public Transaction assignTransactionId(Transaction t, int id) {
        Transaction updateT = new Transaction(id, t.applicantName(), t.district(),
                t.requestedLeaf(), t.offeredLeaf(), t.requestedHours(), t.offeredHours(),
                t.status(), t.closerName());
        return updateT;
    }

    public Transaction getTransaction(int id) {
        return transactions.get(id);
    }


}