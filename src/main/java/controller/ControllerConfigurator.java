package controller;

import model.*;
import returnStatus.AddLeafStatus;
import returnStatus.TransactionStatus;

import java.util.List;


public class ControllerConfigurator extends BaseController {

    public ControllerConfigurator(Model model) {
        super(model);
    }

    //precondition: Not Null String, String
    //postcondition:  if return value true new Category is added to Model.rootNode.children
    public boolean addCategory(String name, String description) throws LeafException {
        return model.addCategory(name, description);
    }

    //precondition: Not Null String, String
    //postcondition: new Category is added to param Node.children
    public boolean addSubCategory(String name, String description, Node parent) throws LeafException {
        return model.addSubCategory(name, description, parent);
    }

    //precondition: Not Null String, String
    //postcondition: new Category is added to param Node.children
    //                added a new Factor to Model.conversionFactors foreach leaf Category in Model.rootNode
    public AddLeafStatus addLeaf(String name, String description, Node parent, Double factor, String conversionPath) throws LeafException {
        return model.addLeaf(name, description, parent, factor, conversionPath);
    }

    public List<Factor> findTripleFromNode(Node n) {
        return model.findTripleFromPathDB(n.getPath());
    }

    public ConversionFactors getConversionFactors() {
        return model.getConversionFactors();
    }

    //precondition: Not Null String
    //               param String not contained in Model.Districts
    //postcondition: a new String is added to Model.districts
    public void addDistrict(String district) {
        model.addDistrict(district);
    }


    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return model.searchByStatus(status);
    }

    public List<Transaction> getTransactionsByLeafR(Node n) {
        return model.searchByRequestedLeaf(n.getPath());

    }

    public List<Transaction> getTransactionsByLeafO(Node n) {
        return model.searchByOfferedLeaf(n.getPath());

    }


    public User searchUser(String name) {
        return model.searchUser(name);
    }
}
