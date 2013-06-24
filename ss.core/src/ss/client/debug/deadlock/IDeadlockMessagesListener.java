package ss.client.debug.deadlock;

public interface IDeadlockMessagesListener {

	/**
	 * 
	 */
	void started();

	/**
	 * 
	 */
	void ended();

	/**
	 * @param string
	 */
	void dumpPossibleDeadlock(String dump);

}
