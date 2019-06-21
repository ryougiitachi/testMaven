package per.itachi.test.springboot;

public class EntryTestSynchronised {
	
	public synchronized void executeSynchronisedObject() {
		try {
			Thread.sleep(1000l);
			System.out.println("execute synchronised object");
			Thread.sleep(1000l);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("finish synchronised object");
	}
	
	public static synchronized void executeSynchronisedClass() {
		try {
			System.out.println("execute synchronised class");
			Thread.sleep(3000l);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("finish synchronised class");
	}

	public static void main(String[] args) {
		EntryTestSynchronised entry = new EntryTestSynchronised(); 
		new Thread(EntryTestSynchronised::executeSynchronisedClass).start();
		new Thread(() -> {
			entry.executeSynchronisedObject();
		})
		.start();
	}

}
