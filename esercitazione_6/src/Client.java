/**
 * ClientCongresso.java
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

class Client {

    public static void main(String[] args) {
        final int REGISTRYPORT = 1099;
        String registryHost = null; // host remoto con registry
        String serviceName = "";
        String fileName = "";
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        // Controllo dei parametri della riga di comando
        if (args.length != 2) {
            System.out.println("Sintassi: RMI_Registry_IP ServiceName");
            System.exit(1);
        }
        registryHost = args[0];
        serviceName = args[1];

        System.out.println("Invio richieste a " + registryHost + " per il servizio di nome " + serviceName);

        // Connessione al servizio RMI remoto
        try {
            String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
            IServerImpl serverRMI = (IServerImpl) Naming.lookup(completeName);
            System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

            System.out.println("\nRichieste di servizio fino a fine file");

            String service;
            System.out.print("Servizio (C = Conta righe, E = Elimina riga): ");

            /* ciclo accettazione richieste utente */
            while ((service = stdIn.readLine()) != null) {

                // C = CONTA RIGHE
                if (service.equals("C")) {
                    System.out.print("Path del file? ");
                    fileName = stdIn.readLine();
                    int wordsInLineCount = -1;
                    System.out.print("Numero di parole nella riga?");

                    while (wordsInLineCount <= -1) {
                        wordsInLineCount = Integer.parseInt(stdIn.readLine());
                        if (wordsInLineCount < 0) {
                            System.out.println("Inserisci un intero positivo!");
                            System.out.print("Numero di parole nella riga?");
                        }
                    }

                    try {
                        System.out.println("ClientRMI: righe che contengono un numero di parole maggiore dell’intero inviato: "
                                + serverRMI.conta_righe(fileName, wordsInLineCount));
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                }

                // E = ELIMINA RIGA
                else if (service.equals("E")) {
                    System.out.print("Path del file? ");
                    fileName = stdIn.readLine();
                    int rowToDelete = -1;
                    System.out.print("Indice riga da eliminare");

                    while (rowToDelete <= -1) {
                        rowToDelete = Integer.parseInt(stdIn.readLine());
                        if (rowToDelete < 0) {
                            System.out.println("Inserisci un numero di parole positive da contare!");
                        }
                    }

                    try {
                        String[] res = serverRMI.elimina_riga(fileName, rowToDelete + 1);
                        System.out.println("Nome del file modificato: " + res[0]);
                        System.out.println("Nuove totale righe nel file: " + res[1]);
                    } catch (RemoteException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Servizio non disponibile");
                }

                System.out.print("Servizio (C = Conta righe, E = Elimina riga): ");
            } // while (!EOF), fine richieste utente

        } catch (NotBoundException nbe) {
            System.err.println("ClientRMI: il nome fornito non risulta registrato; " + nbe.getMessage());
            nbe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ClientRMI: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}