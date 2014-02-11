/**
 * 
 */
package ss.lab.dm3.persist;

import ss.lab.dm3.connection.Waiter;

/**
 *
 */
public interface IDomainLockStrategy {

	void install(Domain domain);
	
	Domain uninstall();
	
	/**
	 * @param runnable
	 */
	void executeFromNotDomainThread(Runnable runnable);
	
	/**
	 * @return
	 */
	Waiter createWaiter();

}
