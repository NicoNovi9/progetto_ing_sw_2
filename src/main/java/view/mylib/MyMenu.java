package view.mylib;

import java.util.Scanner;

/*
Questa classe rappresenta un menu testuale generico a piu' voci
Si suppone che la voce per uscire sia sempre associata alla scelta 0 
e sia presentata in fondo al menu

*/
public class MyMenu {
    final private static String CORNICE = "--------------------------------";
    final private static String VOCE_USCITA = "0\tEsci";
    final private static String RICHIESTA_INSERIMENTO = "Digita il numero dell'opzione desiderata > ";

    private final String titolo;
    private final String[] voci;


    public MyMenu(String titolo, String[] voci) {
        this.titolo = titolo;
        this.voci = voci;
    }

    public int scegli() {
        stampaMenu();
        Scanner scanner = new Scanner(System.in);
        System.out.print(RICHIESTA_INSERIMENTO);
        try {
            int scelta = scanner.nextInt();
            scanner.nextLine();
            return scelta;
        } catch (Exception e) {
            System.out.println("Formato non valido");
            return 1000;
        }
        //return InputDati.leggiIntero(RICHIESTA_INSERIMENTO, 0, voci.length);

    }

    public void stampaMenu() {
        System.out.println();
        System.out.println(CORNICE);
        System.out.println(titolo);
        System.out.println(CORNICE);
        for (int i = 0; i < voci.length; i++) {
            System.out.println((i + 1) + "\t" + voci[i]);
        }
        System.out.println();
        System.out.println(VOCE_USCITA);
        System.out.println();
    }

}

