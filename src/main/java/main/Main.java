package main;

import controller.ControllerConfigurator;
import controller.ControllerUser;
import interfaceRepository.InterfaceDatabase;
import model.LeafException;
import model.Model;
import model.Node;
import repository.LocalDatabase;
import view.StartView;

import java.util.ArrayList;

public class Main {
    public static final String RESOURCES_PATH = "resources/resources_path.txt";

    public static void main(String[] args) throws LeafException {
        InterfaceDatabase database = new LocalDatabase(RESOURCES_PATH);
        Model model = new Model(database);

        ControllerConfigurator controllerConfigurator = new ControllerConfigurator(model);
        ControllerUser controllerUser = new ControllerUser(model);

        model.load();
        Node rootNode = model.getRootNode();

        if (rootNode.getChildren().isEmpty()) {
            loadDefault(controllerConfigurator);
        }

        model.save();

        StartView startView;
        startView = new StartView(controllerConfigurator, controllerUser);
        startView.start();

    }

    public static void loadDefault(ControllerConfigurator controller) throws LeafException {
        controller.addDistrict("unibs");
        controller.addDistrict("unimi");

        controller.addCategory("musica", "Lezioni di musica");
        controller.addCategory("storia", "Lezioni di stroria");
        controller.addCategory("italiano", "Lezioni di musica");


        ArrayList<Node> rootArray = controller.getRootArray();

        controller.addSubCategory(
                "chitarra", "Lezioni di chitarra", rootArray.get(0));
        controller.addSubCategory(
                "pianoforte", "Lezioni di pianoforte", rootArray.get(0));
        controller.addSubCategory(
                "storia di Francia", "Lezioni di storia di Francia", rootArray.get(1));
        controller.addSubCategory(
                "Dante", "Lezioni sulle opere di Dante", rootArray.get(2));

        Node parent0 = rootArray.get(0).getChildren().get(0);
        Node parent1 = rootArray.get(1).getChildren().get(0);
        Node parent2 = rootArray.get(2).getChildren().get(0);
        controller.addLeaf(
                "teoria", "Lezioni di teoria", parent0, null, null);
        controller.addLeaf(
                "pratica", "Lezioni di pratica", parent0, 0.55, "--musica-chitarra-teoria");
        controller.addLeaf(
                "ripasso", "Lezioni di ripasso", parent0, 1.5, "--musica-chitarra-teoria");
        controller.addLeaf(
                "simulazione", "Lezioni di simulazione", parent0, 0.7, "--musica-chitarra-teoria");
        controller.addLeaf(
                "napoleone", "Lezioni su Napoleone", parent1, 1.2, "--musica-chitarra-teoria");
        controller.addLeaf(
                "inferno", "Lezioni sull'inferno di Dante", parent2, 0.5, "--musica-chitarra-teoria");
        controller.addLeaf(
                "purgatorio", "Lezioni sul purgatorio di Dante", parent2, 0.53, "--musica-chitarra-teoria");
        controller.addLeaf(
                "paradiso", "Lezioni sul paradiso di Dante", parent2, 1.5, "--musica-chitarra-teoria");

    }
}
