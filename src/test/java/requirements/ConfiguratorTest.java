package requirements;

import controller.ControllerConfigurator;
import model.LeafException;
import model.Model;
import model.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InterfaceDatabase;
import repository.LocalDatabase;
import model.ConversionFactors;
import returnStatus.AddLeafStatus;

import java.io.*;
import java.util.ArrayList;

public class ConfiguratorTest {

    public static String RESOURCES_PATH = "src/test/java/requirements/resources/resources_path_test.txt";
    public ControllerConfigurator controllerConfigurator;
    public Node rootNode;

    // i nomi riflettono la gerarchia [a] -> [aa] -> [aa0,aa1,aa2,aa3]
    public static String A = "a", B = "b", C = "c";
    public static String AA = "aa", BB = "bb", CC = "cc";
    public static String AA0 = "aa0", AA1 = "aa1", AA2 = "aa2", AA3 = "aa3";
    public static String BB4 = "bb4", CC5 = "cc5", CC6 = "cc6", CC7 = "cc7";

    @BeforeEach
    public void setUp() throws LeafException {

        InterfaceDatabase database = new LocalDatabase(RESOURCES_PATH);
        Model model = new Model(database);

        controllerConfigurator = new ControllerConfigurator(model);

        model.load();
        rootNode = model.getRootNode();

    }

    public void loadDefault() throws LeafException {
        controllerConfigurator.addDistrict("unibs");
        controllerConfigurator.addDistrict("unimi");

        controllerConfigurator.addCategory(A, "");
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
                AA1, "", parent0, 0.55, "--"+A+"-"+AA+"-"+AA0);
        controllerConfigurator.addLeaf(
                AA2, "", parent0, 1.5, "--"+A+"-"+AA+"-"+AA1);
        controllerConfigurator.addLeaf(
                AA3, "", parent0, 0.7, "--"+A+"-"+AA+"-"+AA0);
        controllerConfigurator.addLeaf(
                BB4, "", parent1, 1.2, "--"+A+"-"+AA+"-"+AA0);
        controllerConfigurator.addLeaf(
                CC5, "", parent2, 0.5, "--"+B+"-"+BB+"-"+BB4);
        controllerConfigurator.addLeaf(
                CC6, "", parent2, 0.53, "--"+C+"-"+CC+"-"+CC5);
        controllerConfigurator.addLeaf(
                CC7, "", parent2, 1.5, "--"+C+"-"+CC+"-"+CC6);

    }


    public static void cleanUpFiles() throws IOException {
        File resourceFile = new File(RESOURCES_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(resourceFile))) {
            String filePath;
            while ((filePath = br.readLine()) != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    FileWriter fw = new FileWriter(file, false);
                    fw.write("");
                }
            }
        }
    }

    @Test
    void testRootNode() throws LeafException {
        loadDefault();
        Assertions.assertEquals(rootNode.getName(), "-");
        Assertions.assertEquals(rootNode.getChildren().size(), 3);
        Assertions.assertEquals(rootNode.getChildren().get(0).getName(), A);
    }


    @Test
    void testConversionFactors() throws LeafException {
        loadDefault();
        ConversionFactors conversionFactors = controllerConfigurator.getConversionFactors();

        Assertions.assertEquals(
                0.55,
                conversionFactors.findTripleValue("--"+A+"-"+AA+"-"+AA1,
                        "--"+A+"-"+AA+"-"+AA0));
        Assertions.assertEquals(
                1.82,
                conversionFactors.findTripleValue("--"+A+"-"+AA+"-"+AA0,
                        "--"+A+"-"+AA+"-"+AA1));

        Assertions.assertEquals(
                2.08,
                conversionFactors.findTripleValue("--"+A+"-"+AA+"-"+AA0,
                        "--"+C+"-"+CC+"-"+CC7));

        Assertions.assertEquals(
                2.56,
                conversionFactors.findTripleValue("--"+A+"-"+AA+"-"+AA2,
                        "--"+C+"-"+CC+"-"+CC6));
    }

    @Test
    void testInvalidConversionFactor() throws LeafException {
        controllerConfigurator.addCategory(A,"");

        Node parent0 = controllerConfigurator.getRootArray().get(0);

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                AA0, "", parent0, null, null));
        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                AA1, "", parent0, -1.0, "--"+A+"-"+AA0));
        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                AA1, "", parent0, 2.1, "--"+A+"-"+AA0));
        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                AA1, "", parent0, 0.0, "--"+A+"-"+AA0));
        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                AA1, "", parent0, 0.49, "--"+A+"-"+AA0));

        Assertions.assertEquals(1, parent0.getChildren().size());

    }

    @Test
    void testInvalidReferenceFactor() throws LeafException {
        controllerConfigurator.addCategory(A,"");

        Node parent0 = controllerConfigurator.getRootArray().get(0);

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                AA0, "", parent0, null, null));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                AA1, "", parent0, 1.0, "--"+A+"-"+AA0));

        Assertions.assertEquals(AddLeafStatus.INVALID_REFERENCE_FACTOR, controllerConfigurator.addLeaf(
                AA1, "", parent0, 1.0, "non existing path"));
        Assertions.assertEquals(AddLeafStatus.INVALID_REFERENCE_FACTOR, controllerConfigurator.addLeaf(
                AA1, "", parent0, 1.0, null));

    }
}
