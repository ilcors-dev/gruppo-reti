import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagClient extends RegistryRemotoClient{
    public Remote[] cercaTag(Tag tag) throws RemoteException;
}
