// DiscoveryServer.java

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class DiscoveryServer {

    public static void main(String[] args) {
        String[] files = new String[256];
        int[] ports = new int[256];

        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buf = new byte[256];
        int port = -1;

        if (((args.length % 2) == 0) || (args.length < 3)) {
            System.out.println("Usage: java DiscoveryServer serverPort " +
                    "nomeFile1 port1" +
                    "nomeFile2 port2" +
                    " .. " +
                    "nomeFileN portN");
            System.exit(-1);
        }

        port = Integer.parseInt(args[0]);

        if (port < 1024 || port > 65535) {
            System.out.println("La porta " + port + " non è utilizzabile. Le porte devono essere comprese tra 1024 e 65535.");
            System.exit(-1);
        }

        int index = 0;

        // salvataggio file & port nei rispettivi array
        for (int i = 1; i < args.length; i += 2) {
            files[index] = args[i];
            ports[index] = Integer.parseInt(args[i + 1]);

            if (ports[index] < 1024 || ports[index] > 65535) {
                System.out.println("La porta " + ports[index] + " non è utilizzabile. Le porte devono essere comprese tra 1024 e 65535.");
                System.exit(-1);
            }
            index++;
        }

        // check duplicati tra porte e files salvati
        if (hasDuplicatedFileNames(files)) {
            System.out.println("File duplicati trovati");
            System.exit(-1);
        }

        if (hasDuplicatedPorts(ports)) {
            System.out.println("Porte duplicate trovate");
            System.exit(-1);
        }

        // init dei RowSwapServer
        index = 0;

        for (int i = 1; i < args.length; i += 2) {
            File file = new File(args[i]);

            if (!file.exists() || file.isDirectory()) {
                System.out.println("Il file " + args[i] + " non è stato trovato oppure è una directory.");
                System.exit(-1);
            }

            new RowSwapServer(file, Integer.parseInt(args[i + 1])).start();
            index++;
        }

        try {
            socket = new DatagramSocket(port);
            packet = new DatagramPacket(buf, buf.length);
            System.out.println("Creata la socket: " + socket);
        } catch (SocketException e) {
            System.out.println("Problemi nella creazione della socket: ");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            String nomeFile = null;
            int numLinea = -1;
            StringTokenizer tokenizer = null;

            while (true) {
                buf = new byte[256];
                System.out.println("\n[DiscoveryServer] In attesa di richieste...");

                try {
                    packet.setData(buf);
                    socket.receive(packet);
                    System.out.println("[DiscoveryServer] Richiesta ricevuta");
                } catch (IOException e) {
                    System.err.println("[DiscoveryServer] Problemi nella ricezione del datagramma: "
                            + e.getMessage());
                    e.printStackTrace();
                    continue;
                }

                try {
                    tokenizer = new StringTokenizer(ByteUtility.bytesToStringUTF(packet.getData()));
                    nomeFile = tokenizer.nextToken();
                    System.out.println("[DiscoveryServer] Richiesto file " + nomeFile);
                } catch (IOException e) {
                    System.err.println("[DiscoveryServer] Problemi nella lettura della richiesta: "
                            + nomeFile + " " + numLinea);
                    e.printStackTrace();
                    continue;
                }

                int foundPort = -1;

                for (int i = 0; i < files.length && files[i] != null; i++) {
                    if (files[i].equals(nomeFile)) {
                        foundPort = ports[i];
                    }
                }

                try {
                    if (foundPort == -1) {
                        packet.setData(ByteUtility.intToBytes(-1));
                    } else {
                        packet.setData(ByteUtility.intToBytes(foundPort));
                    }

                    socket.send(packet);
                } catch (IOException e) {
                    System.err.println("[DiscoveryServer] Problemi nell'invio della risposta: "
                            + e.getMessage());
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[DiscoveryServer] termina...");
        socket.close();
    }

    private static boolean hasDuplicatedFileNames(String[] fileNames) {
        if (fileNames.length <= 1) {
            return false;
        }

        for (int i = 0; i < fileNames.length && fileNames[i] != null; i++) {
            for (int j = i + 1; j < fileNames.length && fileNames[i] != null; j++) {
                if (fileNames[i].equals(fileNames[j])) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean hasDuplicatedPorts(int[] ports) {
        if (ports.length <= 1) {
            return false;
        }

        for (int i = 0; i < ports.length && ports[i] != 0; i++) {
            for (int j = i + 1; j < ports.length && ports[j] != 0; j++) {
                if (ports[i] == ports[j]) {
                    return true;
                }
            }
        }

        return false;
    }
}