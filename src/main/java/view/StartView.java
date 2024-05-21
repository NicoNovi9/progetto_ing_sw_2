package view;

import controller.ControllerConfigurator;
import controller.ControllerUser;
import view.mylib.MyMenu;

import java.util.Scanner;

public class StartView {
    ControllerConfigurator controllerConfigurator;
    ControllerUser controllerUser;
    Scanner scanner;
    ViewConfigurator viewConfigurator;
    ViewUser viewUser;

    public StartView(ControllerConfigurator controllerConfigurator, ControllerUser controllerUser) {
        this.controllerConfigurator = controllerConfigurator;
        this.controllerUser = controllerUser;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int scelta = 0;
        do {
            String[] voci = {"Accedi come fruitore", "Accedi come configuratore"};
            MyMenu menu = new MyMenu("Scegli modalità di accesso ", voci);
            scelta = menu.scegli();
            switch (scelta) {
                case 1 -> {
                    startViewUser();
                    scelta = 0;
                }
                case 2 -> {
                    startViewConfigurator();
                    scelta = 0;
                    // non si può tornare indietro da configuratore a user altrimenti ci potrebbero essere problemi di inconsistenza,
                    // se il config decidesse di uscire senza salvare e poi loggasse come utente vedrebbe delle cose che però non sono state salvate,
                    // forse si può sistemare forzando dei salvataggi ma per ora è più safe così
                }
            }
        } while (scelta != 0);

    }

    private void startViewUser() {
        viewUser = new ViewUser(controllerUser);
        viewUser.start();
    }

    private void startViewConfigurator() {
        viewConfigurator = new ViewConfigurator(controllerConfigurator);
        viewConfigurator.start();
    }

}
