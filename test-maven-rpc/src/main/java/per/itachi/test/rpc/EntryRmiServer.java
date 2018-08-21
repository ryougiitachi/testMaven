package per.itachi.test.rpc;

import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.rpc.rmi.impl.MiscWarehouseImpl;

public class EntryRmiServer {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryRmiServer.class);

	public static void main(String[] args) {
		logger.info("Starting RIM server. ");
		try {
			String strBasedURL = "rmi://localhost";
			logger.info("Start initialsing... ");
			
			logger.info("Constructing RIM server implementation class. ");
			MiscWarehouseImpl rmiMisc = new MiscWarehouseImpl();//可以通过读取配置文件列表的方式初始化一大堆warehouse类
			logger.info("Binding RIM server implementation class to registry. ");
			Context contextRegistry = new InitialContext();//centralWarehouse
			logger.info("RMI registry unbinding: ");
			NamingEnumeration<NameClassPair> enumeration = contextRegistry.list(strBasedURL);//configurable
			while (enumeration.hasMoreElements()) {
				NameClassPair pair = enumeration.next();
				logger.info("Unbind {}: {}", pair.getName(), pair.getClassName());
				contextRegistry.unbind("rmi:" + pair.getName());
			}
			contextRegistry.bind("rmi:miscellaneous_warehouse", rmiMisc);
			
			logger.info("Finish initialising. ");
			logger.info("Waiting for invocations from clients. ");
		} 
		catch (RemoteException | NamingException e) {
			logger.error("Error occured when initialising server warehouse. ", e);
		}
	}

}
