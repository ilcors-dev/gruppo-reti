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
		DataInputStream inSock;
		DataOutputStream outSock;
		try {
			String nomeFile;
			try {
				inSock = new DataInputStream(clientSocket.getInputStream());
				outSock = new DataOutputStream(clientSocket.getOutputStream());
				nomeFile = inSock.readUTF();
			}
			catch(SocketTimeoutException ste){
				System.out.println("Timeout scattato: ");
				ste.printStackTrace();
				clientSocket.close();
				System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
				return;          
			}        
			catch (IOException ioe) {
				System.out
					.println("Problemi nella creazione degli stream di input/output "
							+ "su socket: ");
				ioe.printStackTrace();
				return;
			}
			catch (Exception e) {
				System.out
					.println("Problemi nella creazione degli stream di input/output "
							+ "su socket: ");
				e.printStackTrace();
				return;
			}
			
			FileOutputStream outFile = null;
			String esito;
			if (nomeFile == null) {
				System.out.println("Problemi nella ricezione del nome del file: ");
				clientSocket.close();
				return;
			} else {
				File curFile = new File(nomeFile);
				if (curFile.exists()) {
					try {
						esito = "Sovrascritto file esistente";
						curFile.delete();
					}
					catch (Exception e) {
						System.out.println("Problemi nella notifica di file esistente: ");
						e.printStackTrace();
						return;
					}
				} else esito = "Creato nuovo file";
				outFile = new FileOutputStream(nomeFile);
			}

			try {
				System.out.println("Ricevo il file " + nomeFile + ": \n");
				FileUtility.trasferisci_a_byte_file_binario(inSock,
						new DataOutputStream(outFile),12);
				System.out.println("\nRicezione del file " + nomeFile + " terminata\n");
				// chiusura file
				outFile.close();
				clientSocket.shutdownInput();
				outSock.writeUTF(esito + ", file salvato lato server");
				outSock.flush();
				clientSocket.shutdownOutput();
				System.out.println("\nTerminata connessione con " + clientSocket);
				clientSocket.close();
			}
			catch(SocketTimeoutException ste){
				System.out.println("Timeout scattato: ");
				ste.printStackTrace();
				clientSocket.close();
				System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
				return;          
			}              
			catch (Exception e) {
				System.err
					.println("\nProblemi durante la ricezione e scrittura del file: "
						+ e.getMessage());
				e.printStackTrace();
				clientSocket.close();
				System.out.println("Terminata connessione con " + clientSocket);
				return;
			}
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    	System.out
	          .println("Errore irreversibile, PutFileServerThread: termino...");
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
	    		System.out
	    			.println("Usage: java PutFileServerThread or java PutFileServerThread port");
	    		System.exit(1);
	    	}
	    }
	    catch (Exception e) {
	    	System.out.println("Problemi, i seguenti: ");
	    	e.printStackTrace();
	    	System.out
	          	.println("Usage: java PutFileServerThread or java PutFileServerThread port");
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
	    		.println("Server: problemi nella creazione della server socket: "
	    				+ e.getMessage());
	    	e.printStackTrace();
	    	System.exit(1);
	    }

	    try {

	    	while (true) {
	    		System.out.println("Server: in attesa di richieste...\n");

	    		try {
	    			clientSocket = serverSocket.accept();
	    			clientSocket.setSoTimeout(30000);
	    			System.out.println("Server: connessione accettata: " + clientSocket);
	    		}
	    		catch (Exception e) {
	    			System.err
	    				.println("Server: problemi nella accettazione della connessione: "
	    						+ e.getMessage());
	    			e.printStackTrace();
	    			continue;
	    		}

	    		try {
	    			new PutFileServerThread(clientSocket).start();
	    		}
	    		catch (Exception e) {
	    			System.err.println("Server: problemi nel server thread: "
	    					+ e.getMessage());
	    			e.printStackTrace();
	    			continue;
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
