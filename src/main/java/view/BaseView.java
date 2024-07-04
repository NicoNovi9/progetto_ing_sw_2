package view;

import controller.BaseController;
import model.*;
import returnStatus.LoginStatus;

import java.util.List;
import java.util.Scanner;

public abstract class BaseView {
    public static final String FORMATO_NON_VALIDO = "Formato non valido";
    public static final String INSERISCI_INDICE = "Inserisci indice: ";
    public static final String INDICE_ERRATO = "INDICE ERRATO";
    public Scanner scanner;
    BaseController baseController;

    public BaseView(BaseController baseController) {
        this.baseController = baseController;
        this.scanner = new Scanner(System.in);
    }

    public void printTreeMenu() {
        try {
            System.out.print(toStringCategory());
            System.out.print(INSERISCI_INDICE);

            int index = scanner.nextInt();
            scanner.nextLine();

            printTree(baseController.getRootArray().get(index), 0);

        } catch (Exception e) {
            System.out.println(FORMATO_NON_VALIDO);
            scanner.nextLine();
        }

    }
    public void loginView() {
        LoginStatus loginStatus;
        do {
            System.out.println("LOGIN:");
            System.out.print("Inserisci nome: ");
            String name = scanner.nextLine();

            System.out.print("Inserisci password: ");
            String password = scanner.nextLine();
            loginStatus = checkLogin(name, password);
            switch (loginStatus) {
                case ERROR -> System.out.println("\nPASSWORD NON VALIDA\n");
                case LOGIN -> loginEff(name);
                case FIRST_LOGIN -> loginStatus = firstLoginS(name, password);
            }
        } while (loginStatus != LoginStatus.LOGIN);
    }
    public void printTree(Node n, int depth) {
        if (depth == 0) {
            System.out.println(toStringNode(n));
        } else {
            System.out.print("|");
            for (int i = 0; i < 4 * depth - 1; i++) {
                System.out.print(" ");
            }
            System.out.println("└── " + toStringNode(n));
        }

        if (!(n.isLeaf())) {
            if (n.getChildren() != null) {
                for (Node node : n.getChildren()) {
                    printTree(node, depth + 1);
                }
            }
        }
    }

    public Node handleTraverseTree(Node currentNode) {
        int index;
        if (currentNode.content() == 0) {
            System.out.println("Questa categoria non contiene delle sottocategorie o categorie foglie");
            return (currentNode);
        } else {
            do {
                System.out.println(displayChildrenNode(currentNode));
                System.out.print(INSERISCI_INDICE);

                index = scanner.nextInt();
                scanner.nextLine();

                if (incorrectNodeIndex(currentNode, index)) {
                    System.out.println(INDICE_ERRATO);
                }
            } while (incorrectNodeIndex(currentNode, index));
            return currentNode.getChildren().get(index);
        }
    }

    public void handleDisplaySubcategoriesAndLeaves(Node currentNode) {
        if (currentNode.content() == 0) {
            System.out.println("la categoria corrente non possiede sottocategorie");
        } else if (currentNode.content() == 1) {
            System.out.print("Le categoire-foglia sono: ");
            System.out.println(displayChildrenNode(currentNode));
        } else if (currentNode.content() == -1) {
            System.out.println("Le sottocategorie sono: ");
            System.out.println(displayChildrenNode(currentNode));
        }
    }

    public String toStringCategory() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < baseController.getRootArray().size(); i++) {
            buffer.append("[").append(i).append("]").append(toStringNode(baseController.getRootArray().get(i))).append("\n");
        }
        return buffer.toString();
    }

    public boolean incorrectNodeIndex(Node node, int index) {
        return (baseController.incorrectIndex(node, index));
    }

    public String toStringDisticts(Districts districts) {
        return "Lista dei distretti presenti:" +
                '\n' + districts.getDistricts() + '\n';
    }

    public String toStringNode(Node node) {
        return node.getName();
    }

    public String toStringTransaction(Transaction transaction) {
        String s = ("richiesta: [" + transaction.requestedLeaf() + ", " + transaction.requestedHours() + " ore]"
                + "\nofferta: [" + transaction.offeredLeaf() + ", " + transaction.offeredHours() + " ore]");
        return s;
    }

    public String toStringTransactions(List<Transaction> transactionList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Transazioni:\n");
        for (int i = 0; i < transactionList.size(); i++) {
            stringBuilder.append(i).append("\t").append(transactionList.get(i).toString()).append("\n\n");
        }
        return stringBuilder.toString();
    }

    public String toStringFactor(Factor factor) {
        return "( " + factor.first() +
                " -> " + factor.second() +
                " -> " + factor.third() +
                " )";
    }

    public String toStringConvFactor(ConversionFactors conversionFactors) {
        StringBuffer buffer = new StringBuffer();
        for (Factor f : conversionFactors.getFactors()) {
            buffer.append(toStringFactor(f)).append('\n');
        }
        return buffer.toString();
    }

    public String toStringUser(User user) {
        return user.name() + "," + user.eMailAddress() + "," + user.district();
    }

    public String displayChildrenNode(Node node) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < node.getChildren().size(); i++) {
            buffer.append("[").append(i).append("]").append(toStringNode(node.getChildren().get(i))).append("\n");
        }
        return buffer.toString();
    }
    abstract LoginStatus checkLogin(String name, String password);
    abstract void loginEff(String name);
    abstract LoginStatus firstLoginS(String name, String password);
}
