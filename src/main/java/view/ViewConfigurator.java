package view;

import controller.ControllerConfigurator;
import model.*;
import returnStatus.LoginStatus;
import returnStatus.TransactionStatus;
import view.mylib.MyMenu;

import java.util.List;


public class ViewConfigurator extends BaseView {
    private static final String TAG = "config";
    private final ControllerConfigurator controllerConfigurator;

    public ViewConfigurator(ControllerConfigurator controllerConfigurator) {
        super(controllerConfigurator);
        this.controllerConfigurator = controllerConfigurator;
    }

    public void start() {
        System.out.println();
        loginView();
        transactionWarning();

        String[] voci = {"Gestione comprensori", "Gestione delle categorie", "Salvare", "Visualizza Fattori", "Gestisci Transazioni"};
        MyMenu menu = new MyMenu("Menu Principale", voci);
        int index;
        do {
            index = menu.scegli();
            switch (index) {
                case 1:
                    subMenuDistricts();
                    break;
                case 2:
                    subMenuCategory();
                    break;
                case 3:
                    System.out.println(controllerConfigurator.saveDB());
                    break;
                case 4:
                    printConversionFactors();
                    break;
                case 5:
                    subMenuTransactionClosed();
                default:
                    break;
            }
        } while (index != 0);

    }

    public void transactionWarning() {
        if (!controllerConfigurator.getTransactionsByStatus(TransactionStatus.CLOSED).isEmpty())
            System.out.println("CI SONO DELLE TRANSAZIONI DA GESTIRE");
    }

    private void subMenuTransactionClosed() {
        List<Transaction> closedTransactions = controllerConfigurator.getTransactionsByStatus(TransactionStatus.CLOSED);
        System.out.println(toStringTransactions(closedTransactions));
        int index;
        do {
            try {
                System.out.print("digita indice per vedere info relative agli users >");
                index = scanner.nextInt();
                if (index < 0 && index > closedTransactions.size())
                    System.out.println("indice non valido");
            } catch (Exception e) {
                System.out.println("formato non valido:");
                index = -1;
            }
            scanner.nextLine();
        } while (index < 0 || index > closedTransactions.size());
        User applicant = controllerConfigurator.searchUser(closedTransactions.get(index).applicantName());
        User closer = controllerConfigurator.searchUser(closedTransactions.get(index).closerName());
        System.out.println("Applicant: " + toStringUser(applicant));
        System.out.println("Closer:" + toStringUser(closer));
        controllerConfigurator.changeTransactionStatus(closedTransactions.get(index), TransactionStatus.MANAGED);
    }

    private void printConversionFactors() {
        System.out.println(toStringConvFactor(controllerConfigurator.getConversionFactors()));
    }

    public LoginStatus checkLogin(String name, String password){
        return controllerConfigurator.checkLogin(name, password, TAG);
    }
    public void loginEff(String name){
        System.out.println("LOGIN EFFETTUATO");
    }
    public LoginStatus firstLoginS(String name, String password){
        return firstLoginView(name, password);
    }

    public LoginStatus firstLoginView(String name, String password) {
        System.out.println("\nPRIMO LOGIN");
        System.out.println("\n\n\n\n\nPRIMO LOGIN");
        String newName;
        String newPassword;
        do {
            System.out.print("Inserisci nuovo nome: ");
            newName = scanner.nextLine();
            System.out.print("Inserisci nuova password: ");
            newPassword = scanner.nextLine();
        } while (controllerConfigurator.firstLogin(
                name, password, newName, newPassword, "config@config", "district", TAG)
                != LoginStatus.LOGIN);
        return LoginStatus.LOGIN;
    }

    private void subMenuDistricts() {
        int scelta = 0;
        do {
            String[] voci = {"Aggiungi comprensorio", "Visualizza lista comprensori"};
            MyMenu menu = new MyMenu("Menu dei comprensori", voci);
            scelta = menu.scegli();
            switch (scelta) {
                case 1 -> addDistrictView();
                case 2 -> System.out.println(toStringDisticts(controllerConfigurator.getDistricts()));
            }
        } while (scelta != 0);

    }

    public void startTravel() {
        try {
            System.out.print(toStringCategory());
            System.out.print(INSERISCI_INDICE);
            int index = scanner.nextInt();
            scanner.nextLine();

            treeTravel(controllerConfigurator.getRootArray().get(index));

        } catch (Exception e) {
            System.out.println(FORMATO_NON_VALIDO);
            scanner.nextLine();
        }
    }

    public void subMenuCategory() {
        int scelta = 0;
        do {
            String[] voci = {"Aggiungi categoria radice", "Attraversa albero delle categorie",
                    "Visualizza categorie radice", "Visualizza gerarchie complete"};
            MyMenu menu = new MyMenu("Menu delle categorie", voci);
            scelta = menu.scegli();
            switch (scelta) {
                case 1 -> addCategoryView();
                case 2 -> startTravel();
                case 3 -> printRootCategory();
                case 4 -> printTreeMenu();
            }
        } while (scelta != 0);
    }

    public void printRootCategory() {
        System.out.println(toStringCategory());
    }

    public void addCategoryView() {

        System.out.print("Inserisci nome: ");
        String name = scanner.nextLine();

        System.out.print("Inserisci descrizione: ");
        String description = scanner.nextLine();

        if (name.isBlank() || description.isBlank())
            System.out.println("Non puoi inserire spazi o stringe vuote");
        else {
            try {
                if (controllerConfigurator.addCategory(name, description))
                    System.out.println("Inserimento riuscito");
                else
                    System.out.println("Categoria già esistente");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean travelIfIsLeaf(Node n) {
        String[] voci = {"Visualizza info", "Visualizza richieste "};
        MyMenu menu = new MyMenu("Menu categoria foglia", voci);

        switch (menu.scegli()) {
            case 1:
                System.out.println("La descrizione è: " + n.getDescription());
                displayLeafConvFactors(n);
                break;
            case 2:
                printLeafTransaction(n);

            default:
                return true;
        }
        return false;

    }

    public void printLeafTransaction(Node n) {
        System.out.println("transazioni in cui questa categoria compare come categoria richiesta\n");
        List<Transaction> temp = controllerConfigurator.getTransactionsByLeafR(n);
        System.out.println(toStringTransactions(temp));
        System.out.println("transazioni in cui questa categoria compare come categoria offerta\n");
        temp = controllerConfigurator.getTransactionsByLeafO(n);
        System.out.println(toStringTransactions(temp));
    }

    public void treeTravel(Node node) {
        boolean fine = false;
        Node currentNode = node;
        System.out.println(displayChildrenNode(currentNode));

        while (!fine) {
            System.out.println("la categoria corrente è: " + toStringNode(currentNode));

            if (currentNode.isLeaf()) {
                fine = travelIfIsLeaf(currentNode);
            } else {
                String[] options = {"Aggiungi sottocategoria", "Attraversa Albero delle categorie", "Aggiungi foglia", "Mostra sottocategorie/foglie"};
                MyMenu menu = new MyMenu("Menu viaggio", options);
                int choice = menu.scegli();

                switch (choice) {
                    case 1:
                        handleAddSubCategory(currentNode);
                        break;
                    case 2:
                        currentNode = handleTraverseTree(currentNode);
                        break;
                    case 3:
                        handleAddLeaf(currentNode);
                        break;
                    case 4:
                        handleDisplaySubcategoriesAndLeaves(currentNode);
                        break;
                    case 1000:
                        continue;
                    default:
                        fine = true;
                        break;
                }
            }
        }
    }

    private void handleAddSubCategory(Node currentNode) {
        if (currentNode.content() == 0 || currentNode.content() == -1) {
            addSubCategoryView(currentNode);
        } else {
            System.out.println("Non puoi aggiungere una sottocategoria.");
        }
    }

    private void handleAddLeaf(Node currentNode) {
        if (currentNode.content() == 1 || currentNode.content() == 0) {
            addLeafView(currentNode);
        } else {
            System.out.println("Questo nodo non può contenere una foglia");
        }
    }

    public void addSubCategoryView(Node parent) {
        System.out.print("Inserisci nome: ");
        String name = scanner.nextLine();

        System.out.print("Inserisci descrizione: ");
        String description = scanner.nextLine();
        if (name.isBlank() || description.isBlank())
            System.out.println("Non puoi inserire spazi o stringe vuote");
        else {
            try {
                if (controllerConfigurator.addSubCategory(name, description, parent))
                    System.out.println("Inserimento andato a buon fine");
                else
                    System.out.println("Sottocategoria già esistente");

            } catch (LeafException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void addLeafView(Node parent) {
        System.out.print("Inserisci nome foglia: ");
        String name = scanner.nextLine();
        System.out.print("Inserisci descrizione foglia: ");
        String description = scanner.nextLine();

        System.out.print("Inserisci path foglia di conversione: ");
        String path = scanner.nextLine();
        System.out.print("Inserisci fattore di conversione: ");
        double factor = scanner.nextDouble();
        scanner.nextLine();

        try {
            switch (controllerConfigurator.addLeaf(name, description, parent, factor, path)) {
                case INVALID_CONVERSION_FACTOR -> System.out.println("Fattore di conversione non valido");
                case ALREADY_EXISTING_LEAF -> System.out.println("La foglia esiste già");
                case INVALID_REFERENCE_FACTOR ->
                        System.out.println("La foglia usata come riferimento per la conversione non esiste");
                case VALID_LEAF -> System.out.println("Foglia aggiunta correttamente");
            }
        } catch (LeafException e) {
            System.out.println("Impossibile inserire una sottocategoria in una categoria foglia");
        }


    }

    public void addDistrictView() {
        System.out.print("Inserisci nome comprensorio: ");
        String district = scanner.nextLine();
        if (!controllerConfigurator.containsDistrict(district)) {
            controllerConfigurator.addDistrict(district);
            System.out.println("Inserimento effettuato");
        } else System.out.println("Nome già presente");
    }

    public void displayLeafConvFactors(Node n) {
        if (controllerConfigurator.findTripleFromNode(n) == null)
            System.out.println("Non ci sono corrispondenze con questa foglia");
        else {
            System.out.println("I fattori di conversione verso le altre foglie sono:");
            for (Factor f : controllerConfigurator.findTripleFromNode(n)) {
                System.out.println(toStringFactor(f));
            }
        }
    }

}
