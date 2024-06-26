package view;

import controller.ControllerUser;
import model.Node;
import model.Transaction;
import returnStatus.LoginStatus;
import view.mylib.MyMenu;

import java.util.List;


public class ViewUser extends BaseView {
    private static final String TAG = "user";
    private final ControllerUser controllerUser;

    public ViewUser(ControllerUser controllerUser) {
        super(controllerUser);
        this.controllerUser = controllerUser;
    }

    public void start() {

        loginView();

        String[] voci = {"Menu delle categorie",

                "Visualizza info personali", "Salva", "Visualizza Richieste"};

        MyMenu menu = new MyMenu("Menu Principale", voci);
        int index;
        do {
            index = menu.scegli();
            switch (index) {
                case 0 -> controllerUser.setCurrentUser(null);
                case 1 -> menuGerarchie();
                case 2 -> menuInfoPersonali();
                case 3 -> System.out.println(controllerUser.saveDB());
                case 4 -> menuTransaction();
            }
        } while (index != 0);
    }

    private void menuTransaction() {
        List<Transaction> userTransactions = controllerUser.getTransactionByApplicant();
        if (userTransactions.isEmpty())
            System.out.println("non sono ancora state effettuate delle richieste");
        else {

            System.out.println(toStringTransactions(userTransactions));
            int index;
            boolean sceltaValida = false;
            do {
                index = 0;
                try {
                    System.out.print("digita indice della transazione >");
                    index = scanner.nextInt();
                    if (index >= 0 && index < userTransactions.size())
                        sceltaValida = true;
                    else System.out.println("indice non valido");
                } catch (Exception e) {
                    System.out.println("Formato non valido");
                }
                scanner.nextLine();
            } while (!sceltaValida);
            Transaction t = userTransactions.get(index);
            switch (t.status()) {
                case OPEN -> retireTransaction(t);
                case CLOSED ->
                        System.out.println(t + "\nquesta richiesta è stata soddisfatta, presto riceverà una mail con i dettagli");
                case RETIRED -> System.out.println(t + "\nquesta richiesta non è più attiva perchè è stata ritirata");
                case MANAGED ->
                        System.out.println(t + "\nquesta richiesta è stata soddisfatta, controlla la casella mail per trovare i dettagli");
            }

        }
    }

    private void retireTransaction(Transaction t) {
        int i;
        boolean fine = false;
        do {
            i = 0;
            try {
                System.out.print(t + "\nquesta richiesta ancora è aperta, vuoi ritirarla? (1=si/0=n0) > ");
                i = scanner.nextInt();
                if (i == 0 || i == 1) {
                    fine = true;
                } else System.out.println(INDICE_ERRATO);
            } catch (Exception e) {
                System.out.println("Formato non valido");
            }
            scanner.nextLine();
        } while (!fine);
        if (i == 1) {
            System.out.println("la transazione è stata ritirata");
            controllerUser.retireTransaction(t);
        } else System.out.println("operazione annullata");

    }

    private void menuInfoPersonali() {
        System.out.println(toStringUser(controllerUser.getCurrentUser()));
    }

    public void startTravel() {
        try {
            System.out.print(toStringCategory());

            System.out.print(INSERISCI_INDICE);
            int index = scanner.nextInt();
            treeTravel(controllerUser.getNodeFromIndex(index));
        } catch (Exception e) {
            System.out.println(FORMATO_NON_VALIDO);
        }

        scanner.nextLine();
    }

    private void menuGerarchie() {
        int scelta;
        do {
            String[] voci = {"Attraversa albero delle categorie", "Visualizza gerarchie complete"};
            MyMenu menu = new MyMenu("Menu delle categorie", voci);
            scelta = menu.scegli();
            switch (scelta) {
                case 1 -> startTravel();
                case 2 -> printTreeMenu();
            }
        } while (scelta != 0);

    }

    public void treeTravel(Node n) {
        Node currentNode = n;
        boolean fine = false;
        while (!fine) {

            System.out.println("la categoria corrente è: " + toStringNode(currentNode));
            if (currentNode.isLeaf()) {
                fine = travelIfIsLeaf(currentNode);

            } else {
                String[] voci = {"Attraversa Albero", "Mostra sottocategorie/foglie"};
                MyMenu menu = new MyMenu("Menu viaggio", voci);

                switch (menu.scegli()) {
                    case 1 -> currentNode = handleTraverseTree(currentNode);
                    case 2 -> handleDisplaySubcategoriesAndLeaves(currentNode);
                    default -> fine = true;
                }
            }
        }
    }

    public boolean travelIfIsLeaf(Node n) {
        String[] voci = {"Visualizza info", "applica richiesta"};
        MyMenu menu = new MyMenu("Menu foglia", voci);

        switch (menu.scegli()) {
            case 1:
                System.out.println("La descrizione è: " + n.getDescription());

                break;
            case 2:
                startRequestApplication(n);
            default:
                return true;
        }
        return false;
    }

    public void loginView() {
        LoginStatus loginStatus;
        do {
            System.out.println("LOGIN:");
            System.out.print("Inserisci nome: ");
            String name = scanner.nextLine();

            System.out.print("Inserisci password: ");
            String password = scanner.nextLine();
            loginStatus = controllerUser.checkLogin(name, password, TAG);

            switch (loginStatus) {
                case ERROR -> System.out.println("\nPASSWORD NON VALIDA\n");
                case LOGIN -> {
                    System.out.println("LOGIN EFFETTUATO");
                    controllerUser.setCurrentUser(name);
                }
                case FIRST_LOGIN -> loginStatus = firstLoginUserView(name, password);
            }
        } while (loginStatus != LoginStatus.LOGIN);
    }

    public LoginStatus firstLoginUserView(String name, String password) {
        System.out.println("\nPRIMO LOGIN");
        String newName, newPassword, email, district;
        LoginStatus status;
        do {
            System.out.print("Inserisci nuovo nome: ");
            newName = scanner.nextLine();
            System.out.print("Inserisci nuova password: ");
            newPassword = scanner.nextLine();
            System.out.println("MAIL");
            email = mailSelectionView();
            System.out.println("DISTRICT");
            district = districtSelectionView();
            status = controllerUser.firstLogin(
                    name, password, newName, newPassword, email, district, TAG);
            System.out.println(status);
        } while (status
                != LoginStatus.LOGIN);
        controllerUser.setCurrentUser(newName);

        return LoginStatus.LOGIN;
    }

    private String mailSelectionView() {
        String email;
        do {
            System.out.println("inserire una e-mail corretta: ");
            email = scanner.nextLine();
        } while (!email.matches(".*@.*"));
        return email;
    }

    private String districtSelectionView() {
        String district;
        do {
            System.out.println("Scegli tra uno dei seguenti distretti (scrivi il nome del distretto):");
            System.out.print(toStringDisticts(controllerUser.getDistricts()));
            district = scanner.nextLine();
            if (!controllerUser.containsDistrict(district)) {
                System.out.println("il distretto inserito non è presente");
                district = null;
            }

        } while (district == null);
        return district;
    }

    private void startRequestApplication(Node n) {

        Node n2 = FastTravel();
        int nH = hourSetting();
        Transaction t = controllerUser.createNewTransaction(n, n2, nH);
        if (t.equals(null))
            System.out.println("qualcosa è andato storto nella creazione della richiesta, riprova");
        {
            System.out.println("stai effettuando la seguente richiesta:");
            System.out.println(toStringTransaction(t));
            System.out.print("premi qualsiasi cifra per confermare/ 0 per annullare > ");
            int scelta = scanner.nextInt();
            scanner.nextLine();
            if (scelta != 0) {
                System.out.println("richiesta effettuata");
                controllerUser.addTransaction(t);
            } else System.out.println("richiesta abortita");
        }

    }

    private Node FastTravel() {
        System.out.println("scegli una categoria foglia di cui offrire la prestazione");
        Node currentNode = controllerUser.getRoot();
        boolean fine = false;
        while (!fine) {
            Node temp = handleTraverseTree(currentNode);
            if (temp.isLeaf()) {
                fine = true;
                currentNode = temp;
            } else if (temp.content() == 0)
                System.out.println("questa categoria non dispone di sottocategorie foglia");
            else currentNode = temp;

        }
        return currentNode;

    }

    private int hourSetting() {
        int scelta;
        do {
            try {
                System.out.print("inserisci un numero intero di ore di cui vuoi usufruire della categoria > ");
                scelta = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("formato non valido");
                scelta = -1;
            }
            scanner.nextLine();
        } while (scelta <= 0);
        return scelta;
    }
}