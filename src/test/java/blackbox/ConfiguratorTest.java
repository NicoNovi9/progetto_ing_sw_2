package blackbox;

import controller.ControllerConfigurator;
import interfaceRepository.InterfaceDatabase;
import model.ConversionFactors;
import model.LeafException;
import model.Model;
import model.Node;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.LocalDatabase;
import returnStatus.AddLeafStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfiguratorTest {

    public static String RESOURCES_PATH = "src/test/java/blackbox/resources/resources_path_test.txt";
    public ControllerConfigurator controllerConfigurator;
    public Node rootNode;

    // *****************************************************************************************************
    // SETUP AMBIENTE PER IL TEST BLACKBOX
    @BeforeEach
    public void setUp() {

        InterfaceDatabase database = new LocalDatabase(RESOURCES_PATH);

        Model model = new Model(database);

        controllerConfigurator = new ControllerConfigurator(model);

        model.load();
        rootNode = model.getRootNode();


    }

    @AfterAll
    static void cleanUpFiles() throws IOException {
        File resourceFile = new File(RESOURCES_PATH);
        try (BufferedReader br = new BufferedReader(new FileReader(resourceFile))) {
            String filePath;
            while ((filePath = br.readLine()) != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }
    //***********************************************************************************************

    @Test
    void testConversionFactors() throws LeafException {
        // utilizzo una funzione ausiliaria per caricare un albero con struttura complessa
        TS.loadDefaultTree(controllerConfigurator);

        ConversionFactors conversionFactors = controllerConfigurator.getConversionFactors();

        Assertions.assertEquals(
                0.55,
                conversionFactors.findTripleValue("--" + TS.A + "-" + TS.AA + "-" + TS.AA1,
                        "--" + TS.A + "-" + TS.AA + "-" + TS.AA0));
        Assertions.assertEquals(
                1.82,
                conversionFactors.findTripleValue("--" + TS.A + "-" + TS.AA + "-" + TS.AA0,
                        "--" + TS.A + "-" + TS.AA + "-" + TS.AA1));

        Assertions.assertEquals(
                2.08,
                conversionFactors.findTripleValue("--" + TS.A + "-" + TS.AA + "-" + TS.AA0,
                        "--" + TS.C + "-" + TS.CC + "-" + TS.CC7));

        Assertions.assertEquals(
                2.56,
                conversionFactors.findTripleValue("--" + TS.A + "-" + TS.AA + "-" + TS.AA2,
                        "--" + TS.C + "-" + TS.CC + "-" + TS.CC6));
    }

    // test usando la tecnica delle classi di equivalenza e boundary value analysis
    @Test
    void testInvalidConversionFactor() throws LeafException {
        controllerConfigurator.addCategory("videogame", "");

        Node parent0 = controllerConfigurator.getRootArray().get(0);

        // la prima foglia non ha riferimeti
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "zelda", "", parent0, null, null));

        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, -1.0, "--videogame-zelda"));
        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 0.0, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 2.0, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "pacman", "", parent0, 0.5, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "space_invader", "", parent0, 1.0, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 2.1, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 0.49, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.INVALID_CONVERSION_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, null, "--videogame-zelda"));

    }

    @Test
    void testInvalidReferenceFactor() throws LeafException {
        controllerConfigurator.addCategory("videogame", "");

        Node parent0 = controllerConfigurator.getRootArray().get(0);

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "zelda", "", parent0, null, null));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "mariokart", "", parent0, 1.0, "--videogame-zelda"));

        Assertions.assertEquals(AddLeafStatus.INVALID_REFERENCE_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 1.0, "non existing path"));
        Assertions.assertEquals(AddLeafStatus.INVALID_REFERENCE_FACTOR, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 1.0, null));

    }

    @Test
    void testAlreadyExistingLeaf() throws LeafException {
        controllerConfigurator.addCategory("videogame", "");

        Node parent0 = controllerConfigurator.getRootArray().get(0);

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "zelda", "", parent0, null, null));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 1.0, "--videogame-zelda"));

        // A->[AA0,AA1]
        // aggiunta di una foglia con nome già esistente in uno stesso ramo
        Assertions.assertEquals(AddLeafStatus.ALREADY_EXISTING_LEAF, controllerConfigurator.addLeaf(
                "supermario", "", parent0, 1.0, "--videogame-zelda"));

    }

    @Test
    void testAlreadyExistingLeafButDifferentPath() throws LeafException {
        controllerConfigurator.addCategory("videogame", "");
        controllerConfigurator.addCategory("film", "");

        Node parent0 = controllerConfigurator.getRootArray().get(0);
        Node parent1 = controllerConfigurator.getRootArray().get(1);

        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "zelda", "", parent0, null, null));
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "interstellar", "", parent0, 1.0, "--videogame-zelda"));

        // A->[AA0,AA1]
        // aggiunta di una foglia con stesso nome in un altro ramo dell'albero
        // B -> [AA1]
        Assertions.assertEquals(AddLeafStatus.VALID_LEAF, controllerConfigurator.addLeaf(
                "interstellar", "", parent1, 1.0, "--videogame-zelda"));

    }
}
