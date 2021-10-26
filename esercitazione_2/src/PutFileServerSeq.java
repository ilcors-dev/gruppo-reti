package src;

import java.io.*;
import java.net.*;

public class PutFileServerSeq {
    public static final int PORT = 54321;

    public static void main(String[] args) throws IOException {
        int port = -1;

        try {
            if (args.length == 1) {
                port = Integer.parseInt(args[0]);

                if (port < 1024 || port > 65535) {
                    System.out.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
                    System.exit(1);
                }
            } else if (args.length == 0) {
                port = PORT;
            } else {
                System.out.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Problemi, i seguenti: ");
            e.printStackTrace();
            System.out
                    .println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
            System.exit(1);
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("PutFileServerSeq: avviato ");
            System.out.println("Creata la server socket: " + serverSocket);
        } catch (Exception e) {
            System.err.println("Problemi nella creazione della server socket: "
                    + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }

        try {
            Socket clientSocket = null;
            DataInputStream inSock = null;
            DataOutputStream outSock = null;

            while (true) {
                System.out.println("\nIn attesa di richieste...");
                try {
                    clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(30000);

                    System.out.println("Connessione accettata: " + clientSocket + "\n");

                    inSock = new DataInputStream(clientSocket.getInputStream());
                    outSock = new DataOutputStream(clientSocket.getOutputStream());
                } catch (SocketTimeoutException te) {
                    System.err.println("Non ho ricevuto nulla dal client per 30 sec., interrompo " + "la comunicazione e accetto nuove richieste.");
                    continue;
                } catch (Exception e) {
                    System.err.println("Problemi nella accettazione della connessione: " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }

                String esito = null;
                String nomeDir = null;
                long reference = 0;

                try {
                    nomeDir = inSock.readUTF();
                    reference = System.nanoTime();
                    File dir = new File(nomeDir);
                    esito = dir.exists() ? "saltaDir" : "attivaDir";

                    if (esito.equals("attivaDir")) {
                        dir.mkdirs();
                        System.out.println("Ricevuta la cartella " + nomeDir);
                    }
                } catch (SocketTimeoutException ste) {
                    System.out.println("Timeout scattato: ");
                    ste.printStackTrace();
                    clientSocket.close();
                    continue;
                }
                catch (IOException e) {
                    System.out.println("Problemi nella lettura dal Client del nome del direttorio, termino connessione ");
                    clientSocket.close();
                    continue;
                }

                try {
                    outSock.writeUTF(esito);
                } catch (IOException e) {
                    System.out.println("Problemi nell' invio al Client dell' esito, termino la connessione ");
                    clientSocket.close();
                    break;
                }

                while (!clientSocket.isClosed()) {
                    String nomeFile;
                    FileOutputStream outFile = null;
                    File curFile = null;
                    long dimFile = -1;

                    try {
                        nomeFile = nomeDir + "/" + inSock.readUTF();

                        if (nomeFile == null) {
                            System.out.println("Problemi nella ricezione del nome del file: ");
                            break;
                        }
                    }
                    catch (SocketTimeoutException ste) {
                        System.out.println("Timeout scattato: ");
                        ste.printStackTrace();
                        clientSocket.close();
                        break;
                    }
                    catch (IOException e) {
                        System.out.println("Il Cliente ha terminato la connessione, chiudo il canale");
                        clientSocket.close();
                        break;
                    }

                    curFile = new File(nomeFile);
                    esito = curFile.exists() ? "salta" : "attiva";

                    try {
                        outSock.writeUTF(esito);
                    } catch (IOException e) {
                        System.out.println("Problemi nell' invio al Client dell' esito, termino la connessione ");
                        clientSocket.close();
                        break;
                    }

                    if (esito.equals("attiva")) {
                        outFile = new FileOutputStream(nomeFile);

                        try {
                            try {
                                dimFile = inSock.readLong();
                                System.out.println("dimensione di " + nomeFile + ": " + dimFile);
                            } catch (SocketTimeoutException ste) {
                                System.out.println("Timeout scattato: ");
                                ste.printStackTrace();
                                clientSocket.close();
                                break;
                            }
                            catch (IOException e) {
                                System.out.println("Problemi nella ricezione della lunghezza del file, termino la connessione ");
                                clientSocket.close();
                                break;
                            }

                            System.out.println("Ricevo il file " + nomeFile + ":");
                            FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile), dimFile);
                            System.out.println("Ricezione del file " + nomeFile + " terminata");
                            outFile.close();
                        } catch (SocketTimeoutException ste) {
                            System.out.println("Timeout scattato: ");
                            ste.printStackTrace();
                            clientSocket.close();
                        }
                        catch (Exception e) {
                            System.err.println("Problemi durante la ricezione e scrittura del file, termino la connessione ");
                            e.printStackTrace();
                            clientSocket.close();
                        }
                    }

                }
                long finish = System.nanoTime();
                long time = (finish - reference) / 1000000;
                BufferedWriter out = new BufferedWriter(new FileWriter("./benchmarks.txt", true));
                out.write(String.valueOf(time) + "ms" + System.lineSeparator());
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            serverSocket.close();
            System.out.println("Errore irreversibile, PutFileServerSeq: termino...");
            System.exit(3);
        }
    }
}
