/**
 * ClientCongresso.java
 * Esercitazione 7 - Client
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

class Client {

  // Avvio del Client RMI
	public static void main(String[] args) {
		int registryRemotoPort = 1099;
		String registryRemotoHost = null;
		String registryRemotoName = "RegistryRemotoTag";
		String serviceName = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if (args.length != 1 && args.length != 2) {
			System.out.println("Sintassi: ClientCongresso NomeHostRegistryRemoto [registryPort], registryPort intero");
			System.exit(1);
		}
		registryRemotoHost = args[0];
		if (args.length == 2) {
			try {
				registryRemotoPort = Integer.parseInt(args[1]);
			} catch (Exception e) {
				System.out
					.println("Sintassi: ClientCongresso NomeHostRegistryRemoto [registryPort], registryPort intero");
				System.exit(1);
			}
		}
		
		// 	Impostazione del SecurityManager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		// Connessione al servizio RMI remoto
		try {
			String completeRemoteRegistryName = "//" + registryRemotoHost + ":"
					+ registryRemotoPort + "/" + registryRemotoName;
			RegistryRemotoTagClient registryRemoto = 
					(RegistryRemotoTagClient) Naming.lookup(completeRemoteRegistryName);

			ServerCongresso serverRMICongresso = null;
			ServerEcho serverRMIEcho = null;

			while(serverRMICongresso == null && serverRMIEcho == null) {
				System.out.print("Ricerca tramite 1 [Nome] o 2 [Tag]? ");
				int choice = -1;
				try {
					 choice = Integer.parseInt(stdIn.readLine().trim());
				} catch (NumberFormatException e) {
					System.err.println("Formato input non corretto: inserire solamente 1 [Nome] o 2 [Tag].");
					continue;
				}
				switch (choice) {
					case 1: 
						System.out.print("Inserire nome del servizio: ");
						serviceName = stdIn.readLine().trim();
						try {
							if (serviceName.equals("ServerCongresso")) {
								serverRMICongresso = (ServerCongresso) registryRemoto.cerca(serviceName);
							} else if (serviceName.equals("ServerEcho")) {
								serverRMIEcho =  (ServerEcho) registryRemoto.cerca(serviceName);
							}
							
						} catch(RemoteException e) {
							System.err.println(e.getMessage());
							continue;
						}
					break;
					case 2:
						System.out.print("Inserisci nome del tag: ");
						String nomeTag =stdIn.readLine().trim();
						String[] serviceList = null;
						Tag currentTag = null;
						if (nomeTag.equals("ECHO")) {
							serviceList = registryRemoto.cercaTag(Tag.ECHO);
							currentTag = Tag.ECHO;
						} else if (nomeTag.equals("CONGRESSO")) {
							serviceList = registryRemoto.cercaTag(Tag.ECHO);
							currentTag = Tag.CONGRESSO;
						}
						if (serviceList.length == 0) {
							System.err.println("Nessun servizio associato a questo tag");
							continue;
						} else {
							while (serviceName == null) {
								for (int count = 0; count < serviceList.length; count++) {
									System.out.println(count+". "+" "+serviceList[count]);
								}
								System.out.print("Inserire indice servizio a cui collegarsi: ");
								int sel = -1;
								try {
									sel =  Integer.parseInt(stdIn.readLine().trim());
								} catch (NumberFormatException e) {
									System.err.println("Formato input non corretto per selezione servizio");
									continue;
								}
								if (sel < 0 || sel > (serviceList.length-1)) {
									System.err.println("Selettore fuori renge: inserire valori da 0 a"+(serviceList.length-1));
									continue;
								}
								serviceName = serviceList[sel];			
							}
							try {
								if (currentTag == Tag.CONGRESSO) {
									serverRMICongresso = (ServerCongresso) registryRemoto.cerca(serviceName);
								} else if (currentTag == Tag.ECHO) {
									serverRMIEcho = (ServerEcho) registryRemoto.cerca(serviceName);
								}
								
							} catch(RemoteException e) {
								System.err.println(e.getMessage());
								continue;
							}		
						}
						break;
					default: 
						System.out.println("Modalità di ricerca non gestita");
					break;
				}
			}
				
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");
			
			System.out.println("\nRichieste di servizio fino a fine file");
			
			String service;
			System.out.print("Servizio (R=Registrazione, P=Programma del congresso): ");
			
			if (serverRMICongresso != null) {
				while ((service = stdIn.readLine()) != null) {
				
					if (service.equals("R")) {
						
						boolean ok = false;
						int g = 0;
						System.out.print("Giornata (1-3)? ");
						while (ok != true) {
							g = Integer.parseInt(stdIn.readLine());
							if (g < 1 || g > 3) {
								System.out.println("Giornata non valida");
								System.out.print("Giornata (1-3)? ");
								continue;
							} else
								ok = true;
						}
						ok = false;
						String sess = null;
						System.out.print("Sessione (S1 - S12)? ");
						
						while (ok != true) {
							sess = stdIn.readLine();
							if (!sess.equals("S1") && !sess.equals("S2") && !sess.equals("S3")
									&& !sess.equals("S4") && !sess.equals("S5")
									&& !sess.equals("S6") && !sess.equals("S7")
									&& !sess.equals("S8") && !sess.equals("S9")
									&& !sess.equals("S10") && !sess.equals("S11")
									&& !sess.equals("S12")) {
								System.out.println("Sessione non valida");
								System.out.print("Sessione (S1 - S12)? ");
								continue;
							} else
								ok = true;
						}
	
						System.out.print("Speaker? ");
						String speak = stdIn.readLine();
	
						// Tutto corretto
						if (serverRMICongresso.registrazione(g, sess, speak) == 0)
							System.out.println("Registrazione di " + speak
									+ " effettuata per giornata " + g + " sessione " + sess);
						else
							System.out.println("Sessione piena: giornata" + g + " sessione "+ sess);
					} // R
	
					else if (service.equals("P")) {
						int g = 0;
						boolean ok = false;
						System.out.print("Programma giornata (1-3)? ");
						
						while (ok != true) {
							// intercettare la NumberFormatException
							g = Integer.parseInt(stdIn.readLine());
							if (g < 1 || g > 3) {
								System.out.println("Giornata non valida");
								System.out.print("Programma giornata (1-3)? ");
								continue;
							} else
								ok = true;
						}
						System.out.println("Ecco il programma: ");
						serverRMICongresso.programma(g).stampa();
						
					} // P
	
					else System.out.println("Servizio non disponibile");
					
					System.out.print("Servizio (R=Registrazione, P=Programma del congresso): ");
				} // !EOF richieste utente
			} else if (serverRMIEcho != null) {
				System.out.print("Inserisci messaggio da inoltrare al server: ");
				while ((service = stdIn.readLine()) != null) {
					String result = serverRMIEcho.message(service);
					System.out.println("Risposta dal server: "+result);
				}
			}		

		} catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(2);
		}
	}
}