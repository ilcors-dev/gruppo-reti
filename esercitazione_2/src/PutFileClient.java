package src;

import java.io.*;
import java.net.*;

public class PutFileClient {

	public static void main(String[] args) {

		InetAddress addr = null;
		int port = -1;
		File directory = null;
		long limitDimFile = -1;

		try {
			if (args.length == 4) {
				if (!args[0].matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}")) {
					System.out.println("IP Server "+" - not well formatter.");
					System.exit(1);
				}
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
				directory = new File(args[2]);
				limitDimFile = Long.parseLong(args[3]);
			} else {
				System.out.println("Usage: java PutFileClient serverAddr serverPort directoryPath limitDimFile");
				System.exit(1);
			}
		}
		catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out.println("Usage: java PutFileClient serverAddr serverPort directoryPath limitDimFile");
			System.exit(2);
		}

		if (port < 1024 || port > 65535) {
			System.out.println("Usage: port is smaller than 1024 or bigger than 65535");
			System.exit(7);
		}

		if (limitDimFile <= 0) {
			System.out.println("Usage: Limit dim byte file is negative or 0");
			System.exit(6);
		}

		if (!directory.isDirectory()) {
			System.out.println("Usage: Param directoryPath isn't directory");
			System.exit(3);
		}

		if (!directory.canRead()) {
			System.out.println("Usage: Can't read in directoryPath");
			System.exit(4);
		}

		File[] filesDirectory = directory.listFiles();
		int numFileDir;

		if ((numFileDir = filesDirectory.length) == 0) {
			System.out.println("Usage: Param directoryPath is empty");
			System.exit(5);
		}

		System.out.println("Directory " + args[2] + ": file da trasferire " + numFileDir);

		Socket socket = null;
		FileInputStream inFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		String nomeFile = null;

		try {
			int count;
			long dimFile;

//			if (numFileDir == 1 && (filesDirectory[0].length() == 0 || filesDirectory[0].isDirectory())) {
//				System.out.println("Directory " + args[0] + ": un solo file vuoto da scrivere o una directory");
//				System.exit(0);
//			} else {
			try {
				socket = new Socket(addr, port);
				socket.setSoTimeout(30000);
				System.out.println("Creata la socket: " + socket);
			} catch (Exception e) {
				System.out.println("Problemi nella creazione della socket: ");
				e.printStackTrace();
			}

			try {
				inSock = new DataInputStream(socket.getInputStream());
				outSock = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Problemi nella creazione degli stream su socket: ");
				e.printStackTrace();
			}
//			}

			for (count = 0; count < numFileDir; count++) {

				if (filesDirectory[count].isFile()) {
					dimFile = filesDirectory[count].length();
					if (dimFile >= limitDimFile) {
						try {
							nomeFile = filesDirectory[count].getName();
							inFile = new FileInputStream(filesDirectory[count]);
						}
						catch (FileNotFoundException e) {
							System.out.println("Problemi nella creazione dello stream di input da " + nomeFile + ": ");
							e.printStackTrace();
							// il client continua l'esecuzione riprendendo dall'inizio del ciclo
							continue;
						}

						try {
							outSock.writeUTF(nomeFile);
							System.out.println("Inviato il nome del file " + nomeFile);
						} catch (Exception e) {
							System.out.println("Problemi nell'invio del nome di " + nomeFile + ": ");
							e.printStackTrace();
							// il client continua l'esecuzione riprendendo dall'inizio del ciclo
							continue;
						}

						String esito;
						try {
							esito = inSock.readUTF();
							System.out.println("Esito trasmissione nome file: " + esito);
							if (esito.equalsIgnoreCase("salta")) {
								System.out.println("File " + nomeFile + " già presente sul server!");
							} else if (esito.equalsIgnoreCase("attiva")) {
								try {
									outSock.writeLong(dimFile);
									FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock, dimFile);
									inFile.close();
									System.out.println("Trasmissione di " + nomeFile + " terminata ");
								} catch (SocketTimeoutException ste) {
									System.out.println("Timeout scattato: ");
									ste.printStackTrace();
									socket.close();
								} catch (Exception e) {
									System.out.println("Problemi nell'invio di " + nomeFile + ": ");
									e.printStackTrace();
									socket.close();
								}
							}
						} catch (SocketTimeoutException ste) {
							System.out.println("Timeout scattato: ");
							ste.printStackTrace();
							socket.close();
						} catch (Exception e) {
							System.out.println("Problemi nella ricezione dell'esito, i seguenti: ");
							e.printStackTrace();
							socket.close();
						}

					} else {
						System.out
								.println(filesDirectory[count].getName() + " presente nel non soddisfa i requisiti dimensionali");
						System.out.println("Procedo con prossimo file ...");
					}
				}
				else {
					System.out.println(filesDirectory[count].getName() + " presente nel direttorio non è un file");
					System.out.println("Procedo con prossimo file ...");
				}
			}
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
			System.out.println("PutFileClient: termino...");
		}
		catch (Exception e) {
			System.err.println("Errore irreversibile, il seguente: ");
			e.printStackTrace();
			System.err.println("Chiudo!");
			System.exit(3);
		}
	}
}