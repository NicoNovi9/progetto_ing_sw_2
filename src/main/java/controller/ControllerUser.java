package controller;

import model.Model;
import model.Node;
import model.Transaction;
import model.User;

import java.util.List;

public class ControllerUser extends BaseController {

    private User currentUser;


    public ControllerUser(Model model) {
        super(model);
    }

    //precondition: exists a User with param name as name
    //postcondition: this.currentUser is set to a User object

    public void setCurrentUser(String name) {
        currentUser = model.setCurrentUser(name);
    }

    public User getCurrentUser() {
        return currentUser;
    }


    //precondition: Not null Node, Node, int
    //postcondition: transaction object with this.current user as applicant and attribute id null
    //                and status OPEN

    public Transaction createNewTransaction(Node requestedLeaf, Node offeredLeaf, int requestedHours) {
        return model.createNewTransaction(requestedLeaf, offeredLeaf, requestedHours, currentUser.name(), currentUser.district());
    }

    public Node getRoot() {

        return model.getRootNode();
    }


    //precondition: Not Null Transaction
    //postcondition: transaction is added to the model,
    //               the status is changed to "CLOSED" for the transactions what
    //               belongs of a closed set
    public void addTransaction(Transaction t) {
        model.addTransaction(t);

    }


    public List<Transaction> getTransactionByApplicant() {
        return model.searchByApplicant(currentUser.name());

    }


}
