package repository;

import model.*;
import returnStatus.DatabaseStatus;

public interface InterfaceDatabase {

    DatabaseStatus saveTree(Node rootNode);

    DatabaseStatus saveConversionFactors(ConversionFactors conversionFactors);

    DatabaseStatus saveTransactions(Transactions transactions);

    DatabaseStatus saveDistricts(Districts districts);

    Node loadTree();

    ConversionFactors loadConversionFactors();

    Transactions loadTransactions();

    Districts loadDistricts();

    int generateID();

    void saveID(int id);

    User getUser(String name);

    boolean isFirstLogin(String name, String psw, String tag);

    boolean login(String name, String psw, String tag);

    boolean firstLogin(String name, String psw, String newName, String newPsw, String mail, String district, String tag);


}
