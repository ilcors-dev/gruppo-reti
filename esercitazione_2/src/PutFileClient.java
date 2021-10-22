// PutFileClient.java
package src;

import java.net.*;
import java.io.*;

public class PutFileClient {

	public static void main(String[] args) throws IOException {

		InetAddress addr = null;
		int port = -1;
		File directory = null;
		long limitDimFile = -1;

		try { // check args
			if (args.length == 4) {
				// controllo ip
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
				directory = new File(args[2]);
				limitDimFile = Long.parseLong(args[3]);
			} else {
				System.out.println("Usage: java PutFileClient serverAddr serverPort directoryPath limitDimFile");
				System.exit(1);
			}
		} // try
			// Per esercizio si possono dividere le diverse eccezioni
		catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out.println("Usage: java PutFileClient serverAddr serverPort directoryPath limitDimFile");
			System.exit(2);
		}

		// controllare funzione altri

		// verifico porta corretta
		if (port < 1024 || port > 65545) { // verificare limite superiore
			System.out.println("Usage: port is smaller than 1024 or bigger than 655..");
			System.exit(7);
		}

		// verifico che il valore di soglia sia nagguire di 0
		if (limitDimFile <= 0) {
			System.out.println("Usage: Limit dim byte file is negative or 0");
			System.exit(6);
		}

		// verifico che sia una directory
		if (!directory.isDirectory()) {
			System.out.println("Usage: Param directoryPath isn't directory");
			System.exit(3);
		}

		// verifico diritti di lettura
		if (!directory.canRead()) {
			System.out.println("Usage: Can't read in directoryPath");
			System.exit(4);
		}

		// elenco file contenuti nella directory (Attenzione! Potrebbero essere anche directory)
		File[] filesDirectory = directory.listFiles();

		int numFileDir = -1;
		if ((numFileDir = filesDirectory.length) == 0) {
			System.out.println("Usage: Param directoryPath is empty");
			System.exit(5);
		}

		System.out.println("Directory " + args[2] + ": file da trasferire " + numFileDir);

		// oggetti utilizzati dal client per la comunicazione e la lettura del file
		// locale
		Socket socket = null;
		FileInputStream inFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		String nomeFile = null;

		try {
			int count;
			// se ho un file ed è vuoto non ha senso aprire socket
			if (numFileDir == 1 && (filesDirectory[0].length() == 0 || filesDirectory[0].isDirectory())) {
				System.out.println("Directory " + args[0] + ": un solo file vuoto da scrivere o una directory, bye!");
				System.exit(0);
			} else { // aperutra della socket
				// creazione socket
				try {
					socket = new Socket(addr, port);
					socket.setSoTimeout(30000);
					System.out.println("Creata la socket: " + socket);
				} catch (Exception e) {
					System.out.println("Problemi nella creazione della socket: ");
					e.printStackTrace();
				}
				// creazione stream di input/output su socket
				try {
					inSock = new DataInputStream(socket.getInputStream());
					outSock = new DataOutputStream(socket.getOutputStream());
				} catch (IOException e) {
					System.out.println("Problemi nella creazione degli stream su socket: ");
					e.printStackTrace();
				}
			}
			long dimFile = -1;
			for (count = 0; count < numFileDir; count++) {
				// se è un file allora lo scrivo sulla socket
				if (filesDirectory[count].isFile()) {
					dimFile = filesDirectory[count].length();
					if (dimFile >= limitDimFile) {
						/* Invio file richiesto e attesa esito dal server */
						// creazione stream di input da file
						try {
							nomeFile = filesDirectory[count].getName();
							inFile = new FileInputStream(nomeFile);
						}
						/*
						 * abbiamo gia' verificato che esiste, a meno di inconvenienti, es.
						 * cancellazione concorrente del file da parte di un altro processo, non
						 * dovremmo mai incorrere in questa eccezione.
						 */
						catch (FileNotFoundException e) {
							System.out.println("Problemi nella creazione dello stream di input da " + nomeFile + ": ");
							e.printStackTrace();
							System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
							// il client continua l'esecuzione riprendendo dall'inizio del ciclo
							continue;
						}

						// trasmissione del nome
						try {
							outSock.writeUTF(nomeFile);
							System.out.println("Inviato il nome del file " + nomeFile);
						} catch (Exception e) {
							System.out.println("Problemi nell'invio del nome di " + nomeFile + ": ");
							e.printStackTrace();
							System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
							// il client continua l'esecuzione riprendendo dall'inizio del ciclo
							continue;
						}

						// attendo direttive server
						// ricezione esito
						String esito;
						try {
							esito = inSock.readUTF();
							System.out.println("Esito trasmissione nome file: " + esito);
							if (esito.equalsIgnoreCase("salta")) {
								System.out.println("File " + nomeFile + " già presente sul server!");
								continue;
							} else if (esito.equalsIgnoreCase("attiva")) {	
								try {
									// trasmissione dimensione
									outSock.writeLong(dimFile);
									// FileUtility.trasferisci_a_linee_UTF_e_stampa_a_video(new
									// DataInputStream(inFile), outSock);
									// trasferimento file
									FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);
									inFile.close(); // chiusura file
									System.out.println("Trasmissione di " + nomeFile + " terminata ");
								} catch (SocketTimeoutException ste) {
									System.out.println("Timeout scattato: ");
									ste.printStackTrace();
									socket.close();
									System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
									// il client continua l'esecuzione riprendendo dall'inizio del ciclo
									continue;
								} catch (Exception e) {
									System.out.println("Problemi nell'invio di " + nomeFile + ": ");
									e.printStackTrace();
									socket.close();
									System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
									// il client continua l'esecuzione riprendendo dall'inizio del ciclo
									continue;
								}
							}
						} catch (SocketTimeoutException ste) {
							System.out.println("Timeout scattato: ");
							ste.printStackTrace();
							socket.close();
							System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
							// il client continua l'esecuzione riprendendo dall'inizio del ciclo
							continue;
						} catch (Exception e) {
							System.out.println("Problemi nella ricezione dell'esito, i seguenti: ");
							e.printStackTrace();
							socket.close();
							System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
							continue;
							// il client continua l'esecuzione riprendendo dall'inizio del ciclo
						}

						// tutto ok, pronto per nuova richiesta
						System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome file: ");
					} else {
						System.out
								.println(filesDirectory[count].getName() + " presente nel direttorio è un file vuoto");
						System.out.println("Procedo con prossimo file ...");
						continue;
					}
				}
				// se la richiesta non � corretta non proseguo
				else {
					System.out.println(filesDirectory[count].getName() + " presente nel direttorio non è un file");
					System.out.println("Procedo con prossimo file ...");
					continue;
				}
			}
			socket.shutdownOutput(); // chiusura socket in upstream, invio l'EOF al server
			socket.close();
			System.out.println("PutFileClient: termino...");
		}
		// qui catturo le eccezioni non catturate all'interno del for
		// quali per esempio la caduta della connessione con il server
		// in seguito alle quali il client termina l'esecuzione
		catch (Exception e) {
			System.err.println("Errore irreversibile, il seguente: ");
			e.printStackTrace();
			System.err.println("Chiudo!");
			System.exit(3);
		}
	} // main
} // PutFileClient
