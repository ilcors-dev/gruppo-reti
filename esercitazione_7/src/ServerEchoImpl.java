/**
 * ServerCongressoImpl.java
 * 		Implementazione del server
 * */

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerEchoImpl extends UnicastRemoteObject implements
    ServerEcho {

  // Costruttore
  public ServerEchoImpl() throws RemoteException {
    super();
  }

  @Override
  public String message(String msg) throws RemoteException {
    return msg.toUpperCase();
  }
  
  // Avvio del Server RMI
  public static void main(String[] args) {

    int registryRemotoPort = 1099;
    String registryRemotoName = "RegistryRemotoTag";
    String serviceName = "ServerEcho";

    // Controllo dei parametri della riga di comando
    if (args.length != 1 && args.length != 2) {
      System.out
          .println("Sintassi: ServerEchoImpl NomeHostRegistryRemoto [registryPort], registryPort intero");
      System.exit(1);
    }
    String registryRemotoHost = args[0];
    if (args.length == 2) {
      try {
        registryRemotoPort = Integer.parseInt(args[1]);
      } catch (Exception e) {
        System.out
            .println("Sintassi: ServerEchoImpl NomeHostRegistryRemoto [registryPort], registryPort intero");
        System.exit(2);
      }
    }

    // Impostazione del SecurityManager
    if (System.getSecurityManager() == null) {
      System.setSecurityManager(new RMISecurityManager());
    }

    // Registrazione del servizio RMI
    String completeRemoteRegistryName = "//" + registryRemotoHost + ":"
        + registryRemotoPort + "/" + registryRemotoName;

    try {
      RegistryRemotoTagServer registryRemoto = (RegistryRemotoTagServer) Naming
          .lookup(completeRemoteRegistryName);
      ServerCongressoImpl serverRMI = new ServerCongressoImpl();
      registryRemoto.aggiungi(serviceName, serverRMI);
      System.out.println("Server RMI: Servizio \"" + serviceName
          + "\" registrato");
      registryRemoto.associaTag(serviceName, Tag.ECHO);
    } catch (Exception e) {
      System.err.println("Server RMI \"" + serviceName + "\": "
          + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

 
}