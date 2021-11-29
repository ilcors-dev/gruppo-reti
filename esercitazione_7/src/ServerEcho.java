/**
 * Interfaccia remota di servizio
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerEcho extends Remote {
	String message(String msg)
			throws RemoteException;
}