package blackbox;

import controller.ControllerConfigurator;
import model.LeafException;
import model.Node;

import java.util.ArrayList;

// classe per il supporto ai test con alberi complessi
public class TreeSupport {
    public static String A = "a", B = "b", C = "c";
    public static String AA = "aa", BB = "bb", CC = "cc";
    public static String AA0 = "aa0", AA1 = "aa1", AA2 = "aa2", AA3 = "aa3";
    public static String BB4 = "bb4", CC5 = "cc5", CC6 = "cc6", CC7 = "cc7";

    static void loadDefaultTree(ControllerConfigurator controllerConfigurator) throws LeafException {
        controllerConfigurator.addDistrict("unibs");
        controllerConfigurator.addDistrict("unimi");

        controllerConfigurator.addCategory(TreeSupport.A, "");
        controllerConfigurator.addCategory(B, "");
        controllerConfigurator.addCategory(C, "");


        ArrayList<Node> rootArray = controllerConfigurator.getRootArray();

        controllerConfigurator.addSubCategory(
                AA, "", rootArray.get(0));
        controllerConfigurator.addSubCategory(
                BB, "", rootArray.get(1));
        controllerConfigurator.addSubCategory(
                CC, "", rootArray.get(2));

        Node parent0 = rootArray.get(0).getChildren().get(0);
        Node parent1 = rootArray.get(1).getChildren().get(0);
        Node parent2 = rootArray.get(2).getChildren().get(0);

        controllerConfigurator.addLeaf(
                AA0, "", parent0, null, null);
        controllerConfigurator.addLeaf(
                AA1, "", parent0, 0.55, "--" + A + "-" + AA + "-" + AA0);
        controllerConfigurator.addLeaf(
                AA2, "", parent0, 1.5, "--" + A + "-" + AA + "-" + AA1);
        controllerConfigurator.addLeaf(
                AA3, "", parent0, 0.7, "--" + A + "-" + AA + "-" + AA0);
        controllerConfigurator.addLeaf(
                BB4, "", parent1, 1.2, "--" + A + "-" + AA + "-" + AA0);
        controllerConfigurator.addLeaf(
                CC5, "", parent2, 0.5, "--" + B + "-" + BB + "-" + BB4);
        controllerConfigurator.addLeaf(
                CC6, "", parent2, 0.53, "--" + C + "-" + CC + "-" + CC5);
        controllerConfigurator.addLeaf(
                CC7, "", parent2, 1.5, "--" + C + "-" + CC + "-" + CC6);

    }
}
