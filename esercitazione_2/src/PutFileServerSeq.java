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
				System.out
					.println("Usage: java PutFileServerSeq or java PutFileServerSeq port");
				System.exit(1);
			}
		}
		catch (Exception e) {
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
		}
		catch (Exception e) {
			System.err.println("Problemi nella creazione della server socket: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}

		try {
			Socket clientSocket = null;
			DataInputStream inSock = null;
			DataOutputStream outSock = null;
			while(true) {
				System.out.println("\nIn attesa di richieste...");
				try {
					clientSocket = serverSocket.accept();
					clientSocket.setSoTimeout(30000);

					System.out.println("Connessione accettata: " + clientSocket + "\n");

					inSock = new DataInputStream(clientSocket.getInputStream());
					outSock = new DataOutputStream(clientSocket.getOutputStream());
				} catch (SocketTimeoutException te) {
					System.err
							.println("Non ho ricevuto nulla dal client per 30 sec., interrompo "
									+ "la comunicazione e accetto nuove richieste.");
					continue;
				} catch (Exception e) {
					System.err.println("Problemi nella accettazione della connessione: "
							+ e.getMessage());
					e.printStackTrace();
					continue;
				}

				while (!clientSocket.isClosed()) {
					String nomeFile;
					try {
						nomeFile = inSock.readUTF();
					} catch (SocketTimeoutException ste) {
						System.out.println("Timeout scattato: ");
						ste.printStackTrace();
						clientSocket.close();
						continue;
					} catch (IOException e) {
						System.out
								.println("Problemi nella lettura dal Client del nome del file, termino connessione ");
						clientSocket.close();
						break;
					}

					FileOutputStream outFile = null;
					String esito = "attiva";
					if (nomeFile == null) {
						System.out.println("Problemi nella ricezione del nome del file: ");
					}
					else {
						File curFile = new File(nomeFile);
						if (curFile.exists()) {
							esito = "salta";
						}

						try {
							outSock.writeUTF(esito);
						} catch (SocketTimeoutException ste) {
							System.out.println("Timeout scattato: ");
							ste.printStackTrace();
							clientSocket.close();
							continue;
						}
						catch (IOException e) {
							System.out.println("Problemi nell' invio al Client dell' esito, termino la connessione ");
							clientSocket.close();
							break;
						}

						if(esito.equals("attiva")) {
							outFile = new FileOutputStream(nomeFile);

							long dimFile = -1;

							try {
								System.out.println("Ricevo il file " + nomeFile + ": \n");
								try {
									dimFile = inSock.readLong();
								}catch (IOException e) {
									System.out.println("Problemi nella ricezione della lunghezza del file, termino la connessione ");
									clientSocket.close();
									break;
								}

								FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile), dimFile);
								System.out.println("\nRicezione del file " + nomeFile + " terminata\n");
								outFile.close();
							}
							catch (SocketTimeoutException ste) {
								System.out.println("Timeout scattato: ");
								ste.printStackTrace();
								clientSocket.close();
							}
							catch (IOException e) {
								System.out.println("Problemi nella ricezione del contenuto del file, termino la connessione ");
								clientSocket.close();
								break;
							}
							catch (Exception e) {
								System.err
										.println("\nProblemi durante la ricezione e scrittura del file: "
												+ e.getMessage());
								e.printStackTrace();
								clientSocket.close();
								System.out.println("Terminata connessione con " + clientSocket);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			serverSocket.close();
			System.out.println("Errore irreversibile, PutFileServerSeq: termino...");
			System.exit(3);
		}
	}
}
