package per.itachi.test.rpc;

import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.rpc.rmi.MiscWarehouse;

public class EntryRmiClient {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryRmiClient.class);

	public static void main(String[] args) {
		logger.info("Starting RIM client. ");
		try {
			String strBasedURL = "rmi://localhost/";
			Context contextRegistry = new InitialContext();
			logger.info("RMI registry binding: ");
			NamingEnumeration<NameClassPair> enumeration = contextRegistry.list(strBasedURL);//configurable
			while (enumeration.hasMoreElements()) {
				NameClassPair pair = enumeration.next();
				logger.info("{}: {}", pair.getName(), pair.getClassName());
			}
			String strWarehouseURL = strBasedURL + "miscellaneous_warehouse";//可通过文件配置
			Object object = contextRegistry.lookup(strWarehouseURL);
			MiscWarehouse misc = (MiscWarehouse)object;
			logger.info("{}", misc.getMiscWarehouseName());
		} 
		catch (NamingException | RemoteException e) {
			logger.error("Error occured when initialising client pointend. ", e);
		}
	}

}
