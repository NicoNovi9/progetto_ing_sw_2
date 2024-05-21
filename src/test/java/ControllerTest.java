import controller.ControllerConfigurator;
import model.ConversionFactors;
import model.LeafException;
import model.Model;
import model.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.InterfaceDatabase;
import repository.LocalDatabase;
import returnStatus.AddLeafStatus;

import java.util.ArrayList;

public class ControllerTest {

    ArrayList<Node> rootArray;
    private ControllerConfigurator controller;
    String name0, name1, name2, name3, name4, name5, name6, name7, descr;


    @BeforeEach
    @Test
    void setUp() throws LeafException {
        InterfaceDatabase db = new LocalDatabase("src/test/resources/resources_path_test.txt");
        Model model = new Model(db);
        controller = new ControllerConfigurator(model);

        model.setRootNode(db.loadTree());

        controller.addDistrict("unibs");

        name0 = "musica";
        name1 = "storia";
        name2 = "chitarra";
        name3 = "pianoforte";
        descr = "descrizione casuale";
        name4 = "teoria";
        name5 = "pratica";
        name6 = "esame";
        name7 = "simulazione";

        controller.addCategory(name0, "Lezioni di musica");
        controller.addCategory(name1, "Lezioni di stroria");

        rootArray = controller.getRootArray();

        name2 = "chitarra";
        name3 = "pianoforte";

        controller.addSubCategory(name2, "Lezioni di chitarra",rootArray.get(0));
        controller.addSubCategory(name3, "Lezioni di pianoforte",rootArray.get(0));

        Node parent = rootArray.get(0).getChildren().get(0);

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controller.addLeaf(name4, descr, parent, null, null));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controller.addLeaf(name5, descr, parent, 1.0, "--musica-chitarra-teoria"));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controller.addLeaf(name6, descr, parent, 2.0, "--musica-chitarra-teoria"));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controller.addLeaf(name7, descr, parent, 1.5, "--musica-chitarra-teoria"));
    }

    @Test
    void testAddCategory() {
        ArrayList<Node> rootArray = controller.getRootArray();
        Assertions.assertNotNull(rootArray);
        Assertions.assertEquals(2, rootArray.size());

        Assertions.assertEquals(rootArray.get(0).getName(), name0);
        Assertions.assertEquals(rootArray.get(1).getName(), name1);
    }

    @Test
    void testAddSubCategory() {

        ArrayList<Node> children = rootArray.get(0).getChildren();

        Assertions.assertEquals(children.get(0).getName(), name2);
        Assertions.assertEquals(children.get(1).getName(), name3);

        Assertions.assertEquals(children.get(0).getPath(), "--"+name0+"-"+name2);
        Assertions.assertEquals(children.get(1).getPath(), "--"+name0+"-"+name3);

    }

    @Test
    void returnStatusAddLeaf() throws LeafException {
        Node parent = rootArray.get(0).getChildren().get(0);

        Assertions.assertEquals(
                AddLeafStatus.ALREADY_EXISTING_LEAF, controller.addLeaf(name7, descr, parent, 1.5, "--musica-chitarra-teoria"));
        Assertions.assertEquals(
                AddLeafStatus.INVALID_CONVERSION_FACTOR, controller.addLeaf("prova", descr, parent, 2.1, "--musica-chitarra-teoria"));
        Assertions.assertEquals(
                AddLeafStatus.INVALID_REFERENCE_FACTOR, controller.addLeaf("prova", descr, parent, 1.5, "musica-viola-teoria"));

    }

    @Test
    void testConversionFactors() {

        Node parent = rootArray.get(0).getChildren().get(0);

        ConversionFactors conversionFactors = controller.getConversionFactors();

        Assertions.assertEquals(
                1.0,
                conversionFactors.findTripleValue(
                        parent.getPath()+"-"+name4,
                        parent.getPath()+"-"+name5));
        Assertions.assertEquals(
                2.0,
                conversionFactors.findTripleValue(
                        parent.getPath()+"-"+name6,
                        parent.getPath()+"-"+name4));
        Assertions.assertEquals(
                0.67,
                conversionFactors.findTripleValue(
                        parent.getPath()+"-"+name4,
                        parent.getPath()+"-"+name7));
        Assertions.assertEquals(
                1.5,
                conversionFactors.findTripleValue(
                        parent.getPath()+"-"+name7,
                        parent.getPath()+"-"+name5));
        Assertions.assertEquals(
                1.33,
                conversionFactors.findTripleValue(
                        parent.getPath()+"-"+name6,
                        parent.getPath()+"-"+name7));
        Assertions.assertEquals(
                0.75,
                conversionFactors.findTripleValue(
                        parent.getPath()+"-"+name7,
                        parent.getPath()+"-"+name6));
    }
}
