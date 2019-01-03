package per.itachi.test.nio;


public interface ControllableRunnable extends Runnable {
	
	/**
	 * Try to stop the current runnable. 
	 * */
	void tryToTerminate();
	
	/**
	 * Check whether terminate action has been triggered for the current runnable.
	 * */
	boolean isTerminated();
	
	/**
	 * Check whether the current runnable is still running. 
	 * */
	boolean isRunning();
}
