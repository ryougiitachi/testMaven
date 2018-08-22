package per.itachi.test.rpc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MiscWarehouse extends Remote {
	
	String getMiscWarehouseName() throws RemoteException;
}
