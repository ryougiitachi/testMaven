package per.itachi.test.rpc.rmi.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import per.itachi.test.rpc.rmi.MiscWarehouse;

public class MiscWarehouseImpl extends UnicastRemoteObject implements MiscWarehouse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3510098862185937945L;

	public MiscWarehouseImpl() throws RemoteException {
		super();
	}

	@Override
	public String getMiscWarehouseName() throws RemoteException {
		return "A Miscellaneous Java RMI Collection. ";
	}
	
}
