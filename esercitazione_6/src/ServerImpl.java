/**
 * ServerCongressoImpl.java
 * Implementazione del server
 */

import java.io.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements IServerImpl {
    static Programma prog[];

    // Costruttore
    public ServerImpl() throws RemoteException {
        super();
    }

    @Override
    public int conta_righe(String filePath, int wordsInLineCount) throws RemoteException {
        File file = new File(filePath);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int c;
            int rowWords = 0;
            int result = 0;

            while ((c = reader.read()) != -1) {
                if (c == 13) {
                    rowWords++;

                    if (rowWords > wordsInLineCount) {
                        result++;
                    }

                    rowWords = 0;
                } else {
                    if (c == ' ') {
                        rowWords++;
                    }
                }
            }
            reader.close();

            return result;
        } catch (FileNotFoundException e) {
            throw new RemoteException("Server RMI: Il file " + filePath + " non esiste!");
        } catch (IOException e) {
            throw new RemoteException("Server RMI: Errore nella lettura del file!");
        }
    }

    @Override
    public String[] elimina_riga(String filePath, int rowToDelete) throws RemoteException {
        String[] result = new String[2];
        File file = new File(filePath);
        File tempOut = new File(filePath + "_temp");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            FileWriter writer = new FileWriter(tempOut);
            int c;
            int rowIndex = 0;

            while ((c = reader.read()) != -1) {
                if (c == 13) {
                    rowIndex++;
                    if(rowIndex != rowToDelete){
                        writer.write('\n');
                    }
                } else {
                    if (rowIndex != rowToDelete) {
                        writer.write(c);
                    }
                }
            }

            reader.close();
            writer.close();

            if (rowIndex < rowToDelete) {
                throw new RemoteException("Server RMI: il file contiene solo " + rowIndex + "righe, riga: " + rowToDelete + " inesistente!");
            }

            file.delete();
            tempOut.renameTo(file);

            result[0] = filePath;
            result[1] = String.valueOf(rowIndex - 1);

            return result;
        } catch (FileNotFoundException e) {
            throw new RemoteException("Server RMI: Il file " + filePath + " non esiste!");
        } catch (IOException e) {
            throw new RemoteException("Server RMI: Errore nella lettura del file!");
        }
    }

    // Avvio del Server RMI
    public static void main(String[] args) throws RemoteException {
        System.setProperty("java.rmi.server.hostname", "localhost");
        LocateRegistry.createRegistry(1099);

        final int REGISTRYPORT = 1099;
        String registryHost = "localhost";
        String serviceName = "ServerImpl"; // lookup name...

        // Registrazione del servizio RMI
        String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
        try {
            ServerImpl serverRMI = new ServerImpl();
            Naming.rebind(completeName, serverRMI);
            System.out.println("Server RMI: Servizio \"" + serviceName + "\" registrato");
        } catch (Exception e) {
            System.err.println("Server RMI \"" + serviceName + "\": " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}