package per.itachi.test.commons;

public class TestInitialiseConstructor {
	
	static {
		staticA = 1;
		//System.out.println("staticA is " + staticA);// Cannot reference a field before it is defined. 
	}
	
	private static int staticA = 0;
	
	static {
		System.out.println("In <clinit>, staticA is " + staticA);//
	}
	
	public TestInitialiseConstructor() {
//		instanceA = 2;
	}
	
	{
		this.instanceA = 3;
	}
	
	private int instanceA = 1;
	
	public void showA() {
		System.out.println("staticA is " + staticA);
		System.out.println("instanceA is " + instanceA);
	}
	
	public static void main(String[] args) {
		TestInitialiseConstructor constructor = new TestInitialiseConstructor();
		constructor.showA();
	}
}
