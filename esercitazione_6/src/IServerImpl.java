/**
 * Interfaccia remota di servizio
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServerImpl extends Remote {

	int conta_righe(String filePath, int wordsInLineCount) throws RemoteException;

	/**
	 * Deletes a row from a file, if the row exists
	 *
	 * @param filePath
	 * @param rowToDelete
	 * @return String[2] containing at [0] the filename & at [1] the new rows in the file
	 * @throws RemoteException
	 */
	String[] elimina_riga(String filePath, int rowToDelete) throws RemoteException;
}