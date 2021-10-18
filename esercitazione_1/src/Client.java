// Client.java

import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

public class Client {

    public static void main(String[] args) {

        InetAddress addr = null;
        int port = -1;
        String fileName = null;

        try {
            if (args.length == 3) {
                addr = InetAddress.getByName(args[0]);
                port = Integer.parseInt(args[1]);
                fileName = args[2];
            } else {
                System.out.println("Usage: java Client serverIP serverPort fileName");
                System.exit(-1);
            }
        } catch (UnknownHostException e) {
            System.out
                    .println("Problemi nella determinazione dell'endpoint del server : ");
            e.printStackTrace();
            System.out.println("Client: interrompo...");
            System.exit(-1);
        }

        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buf = new byte[256];
        int rowSwapServerPort = -1;

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(30000);
            packet = new DatagramPacket(buf, buf.length, addr, port);
            System.out.println("\nClient: avviato");
            System.out.println("Creata la socket: " + socket);
        } catch (SocketException e) {
            System.out.println("Problemi nella creazione della socket: ");
            e.printStackTrace();
            System.out.println("Client: interrompo...");
            System.exit(-1);
        }

        try {
            packet.setData(ByteUtility.stringUTFToBytes(fileName));
            socket.send(packet);
            System.out.println("Richiesta inviata a " + addr + ", " + port);
        } catch (IOException e) {
            System.out.println("Problemi nell'invio della richiesta: ");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            packet.setData(buf);
            socket.receive(packet);
        } catch (IOException e) {
            System.out.println("Problemi nella ricezione del datagramma: ");
            e.printStackTrace();
            System.exit(-1);
        }

        try {
            rowSwapServerPort = ByteUtility.bytesToInt(packet.getData());

            if (rowSwapServerPort < 0) {
                System.out.println("Il server non ha trovato il file specificato..");
                System.exit(-1);
            }

            System.out.println("RowSwapServer trovato alla porta " + rowSwapServerPort + "!");
        } catch (IOException e) {
            System.out.println("Problemi nella lettura della risposta: ");
            e.printStackTrace();
            System.exit(-1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String readInput = null;
        Pattern patternChecker = Pattern.compile("^\\d+(-\\d+)$");

        System.out.println("^D(Unix)/^Z(Win)+invio per uscire.");
        System.out.println("Inserisci righe da scambiare (separate da '-'): ");

        try {
            while ((readInput = stdIn.readLine()) != null) {
                if (!patternChecker.matcher(readInput).matches()) {
                    System.out.println("Gli indici devono essere numerici e devono essere separati da un solo '-'!");
                    System.out.println("Inserisci righe da scambiare (separate da '-'): ");
                } else {
                    try {
                        packet.setPort(rowSwapServerPort);
                        packet.setData(ByteUtility.stringUTFToBytes(readInput));
                        socket.send(packet);
                        System.out.println("Richiesta inviata al server [RowSwap-" + rowSwapServerPort + "]");
                    } catch (IOException e) {
                        System.out.println("Problemi nell'invio della richiesta: ");
                        e.printStackTrace();
                        System.out.println("Riprova inserendo nuove righe.");
                    }

                    try {
                        packet.setData(buf);
                        socket.receive(packet);
                    } catch (IOException e) {
                        System.out.println("Problemi nella ricezione del datagramma: ");
                        e.printStackTrace();
                        System.out.println("Riprova inserendo nuove righe.");
                    }

                    try {
                        int requestStatus = ByteUtility.bytesToInt(packet.getData());
                        System.out.println("Il server ha risposto con un codice di " + (requestStatus < 0 ? "errore" : "successo"));
                        System.out.println("Inserisci righe da scambiare (separate da '-'): ");
                    } catch (IOException e) {
                        System.out.println("Problemi nella lettura della risposta: ");
                        e.printStackTrace();
                        System.out.println("Riprova inserendo nuove righe.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Client: termino...");
        socket.close();
    }
}