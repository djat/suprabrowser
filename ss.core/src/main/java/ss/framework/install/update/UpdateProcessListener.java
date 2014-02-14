/**
 * 
 */
package ss.framework.install.update;

/**
 *
 */
public interface UpdateProcessListener {

	void applicationIsUpToDate();
	
	void updated(UpdateResult updateResult);
	
	void cantUpdate( String message, boolean actualClientCanWorkWithServer );	

	boolean askUserToProceedUpdate();
	
}
