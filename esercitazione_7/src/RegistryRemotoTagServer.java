import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagServer extends  RegistryRemotoServer, RegistryRemotoTagClient {
    public boolean associaTag(String nome_logico_server, Tag tag) throws RemoteException;
}
