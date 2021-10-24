package src;

import java.io.*;
import java.net.*;

class PutFileServerThread extends Thread {

	private Socket clientSocket = null;

	/**
	 * Constructor
	 * @param clientSocket
	 */
	public PutFileServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		try {
			DataInputStream inSock = null;
			DataOutputStream outSock = null;
			try {
				inSock = new DataInputStream(clientSocket.getInputStream());
				outSock = new DataOutputStream(clientSocket.getOutputStream());
			}
			catch (SocketTimeoutException te) {
				System.err.println("Non ho ricevuto nulla dal client per 30 sec., interrompo "+ "la comunicazione e accetto nuove richieste.");
				clientSocket.close();
				return;
			}
			catch (Exception e) {
				System.err.println("Problemi nella accettazione della connessione: "+ e.getMessage());
				e.printStackTrace();
				clientSocket.close();
				return;
			}

/*//			Ricezione della Directory
			String nomeDir = inSock.readUTF();
			new File(nomeDir).mkdirs();
			System.out.println("Ricevuta la cartella "+nomeDir);*/
			String esito = null;
			String nomeDir = null;
			long reference = 0;
			try {
				nomeDir = inSock.readUTF();
				reference = System.nanoTime();
				File dir = new File(nomeDir);
				synchronized (dir.getCanonicalPath().intern()) {
					esito = dir.exists() ? "saltaDir" : "attivaDir";

					if (esito.equals("attivaDir")) {
						dir.mkdirs();
						System.out.println("Ricevuta la cartella " + nomeDir);
					}
				}
			}
			catch (SocketTimeoutException ste) {
				System.out.println("Timeout scattato: ");
				ste.printStackTrace();
				clientSocket.close();
				return;
			}
			catch (IOException e) {
				System.out.println("Problemi nella lettura dal Client del nome del direttorio, termino connessione ");
				clientSocket.close();
				return;
			}

			try {
				outSock.writeUTF(esito);
			}
			catch (IOException e) {
				System.out.println("Problemi nell' invio al Client dell' esito, termino la connessione ");
				clientSocket.close();
				System.exit(4);
			}

			while (!clientSocket.isClosed()) {
				String nomeFile;
				try {
					nomeFile = nomeDir+"/"+inSock.readUTF();
					//nomeFile = inSock.readUTF();
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
					System.out.println("Problemi nella lettura dal Client del nome del file, termino connessione ");clientSocket.close();
					break;
				}

				FileOutputStream outFile = null;
				long dimFile = -1;

				File curFile = new File(nomeFile);
				synchronized (curFile.getCanonicalPath().intern()) {
					esito = curFile.exists() ? "salta" : "attiva";
					if (esito.equals("attiva")) outFile = new FileOutputStream(nomeFile);
				}

				try {
					outSock.writeUTF(esito);
				} catch (IOException e) {
					System.out.println("Problemi nell' invio al Client dell' esito, termino la connessione ");
					clientSocket.close();
					break;
				}

				if (esito.equals("attiva")) {
					try {
						try {
							dimFile = inSock.readLong();
							System.out.println("dimensione di " + nomeFile + ": " + dimFile);
						}
						catch (SocketTimeoutException ste) {
							System.out.println("Timeout scattato: ");
							ste.printStackTrace();
							clientSocket.close();
							break;
						} catch (IOException e) {
							System.out.println("Problemi nella ricezione della lunghezza del file, termino la connessione ");
							clientSocket.close();
							break;
						}

						System.out.println("Ricevo il file " + nomeFile + ":");
						FileUtility.trasferisci_a_byte_file_binario(inSock, new DataOutputStream(outFile), dimFile);
						System.out.println("Ricezione del file " + nomeFile + " terminata");
						outFile.close();
					}
					catch (SocketTimeoutException ste) {
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
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Errore irreversibile, PutFileServerSeq: termino...");
			System.exit(3);
		}
	}
}

public class PutFileServerCon {
	public static final int PORT = 54321;

	public static void main(String[] args) throws IOException {

		int port = -1;

	    try {
	    	if (args.length == 1) {
	    		port = Integer.parseInt(args[0]);
	    		if (port < 1024 || port > 65535) {
	    			System.out.println("Usage: java LineServer [serverPort>1024]");
	    			System.exit(1);
	    		}
	    	} else if (args.length == 0) {
	    		port = PORT;
	    	} else {
	    		System.out.println("Usage: java PutFileServerThread or java PutFileServerThread port");
	    		System.exit(1);
	    	}
	    }
	    catch (Exception e) {
	    	System.out.println("Problemi, i seguenti: ");
	    	e.printStackTrace();
	    	System.out.println("Usage: java PutFileServerThread or java PutFileServerThread port");
	    	System.exit(1);
	    }

	    ServerSocket serverSocket = null;
	    Socket clientSocket = null;

	    try {
	    	serverSocket = new ServerSocket(port);
	    	serverSocket.setReuseAddress(true);
	    	System.out.println("PutFileServerCon: avviato ");
	    	System.out.println("Server: creata la server socket: " + serverSocket);
	    }
	    catch (Exception e) {
	    	System.err
	    		.println("Server: problemi nella creazione della server socket: "+ e.getMessage());
	    	e.printStackTrace();
	    	System.exit(1);
	    }

	    try {
	    	while (true) {
	    		System.out.println("Server: in attesa di richieste...");

	    		try {
	    			clientSocket = serverSocket.accept();
	    			clientSocket.setSoTimeout(30000);
	    			System.out.println("Server: connessione accettata: " + clientSocket);
	    		}
	    		catch (Exception e) {
	    			System.err.println("Server: problemi nella accettazione della connessione: "+ e.getMessage());
	    			e.printStackTrace();
	    			continue;
	    		}

	    		try {
	    			new PutFileServerThread(clientSocket).start();
	    		}
	    		catch (Exception e) {
	    			System.err.println("Server: problemi nel server thread: "+ e.getMessage());
	    			e.printStackTrace();
	    		}
	    	}
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    	serverSocket.close();
	    	System.out.println("PutFileServerCon: termino...");
	    	System.exit(2);
	    }
	}
}
