import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagClient extends RegistryRemotoClient{
    public String[] cercaTag(Tag tag) throws RemoteException;
}
