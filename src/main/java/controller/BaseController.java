package controller;


import model.Districts;
import model.Model;
import model.Node;
import model.Transaction;
import returnStatus.DatabaseStatus;
import returnStatus.LoginStatus;
import returnStatus.TransactionStatus;

import java.util.ArrayList;

public class BaseController {

    public Model model;

    public BaseController(Model model) {
        this.model = model;
    }

    //postcondition: if  return value is LoginStatus.Login the user/configuratror has successfully logged in
    public LoginStatus checkLogin(String name, String psw, String TAG) {
        if (model.isFirstLogin(name, psw, TAG))
            return LoginStatus.FIRST_LOGIN;
        else if (model.login(name, psw, TAG))
            return LoginStatus.LOGIN;
        else
            return LoginStatus.ERROR;
    }

    //postcondition: if  return value is LoginStatus.Login the user/configuratror  has successfully logged in
    //                 new record is created in real_credentials.csv with the param passed
    //                and deleted the record with the default credentials from default_credentials.csv
    public LoginStatus firstLogin(
            String name, String psw, String newName, String newPsw,
            String mail, String district, String TAG) {
        if (!checkNewPassword(newPsw))
            return LoginStatus.INCORRECT_NEW_PSW;
        if (model.firstLogin(
                name, psw, newName, newPsw, mail, district, TAG))
            return LoginStatus.LOGIN;
        else
            return LoginStatus.ERROR;
    }

    public boolean checkNewPassword(String newPsw) {
        return (newPsw.matches(".*\\d+.*") && newPsw.length() >= 8);
    }

    public boolean containsDistrict(String district) {
        return model.containsDistrict(district);
    }

    public ArrayList<Node> getRootArray() {
        return model.getRootNode().getChildren();
    }

    public Node getNodeFromIndex(int index) {
        return getRootArray().get(index);
    }

    public Districts getDistricts() {
        return model.getDistricts();
    }

    public boolean incorrectIndex(Node node, int index) {
        return model.incorrectIndex(node, index);
    }

    public void changeTransactionStatus(Transaction t, TransactionStatus status) {
        model.changeTransactionStatus(t, status);
    }

    //postcondition: all the structure in the class model are permanently saved on file
    public ArrayList<DatabaseStatus> saveDB() {
        return model.save();
    }
}
