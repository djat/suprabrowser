package ss.lab.dm3.persist;

import ss.lab.dm3.connection.ICallbackHandler;

/**
 * @author Dmitry Goncharov
 * 
 * Wish list:
 * - Object version control (on the client and on the server) -
 * - Objects properties constraints on the client side (login field for example)
 * 
 */
public interface Transaction {
	
	boolean isEditable();

	void beginCommit(final ICallbackHandler commitHandler);
	
	void rollback();

	void beginCommit();
	
	void commit();

	void dispose();

}
