package ss.common.operations;

import java.util.EventListener;

public interface OperationProgressListener extends EventListener {

	/**
	 * Notify opeation started 
	 */
	void setupped( OperationProgressEvent e  );
	
	/**
	 * Notify opeation ended.
	 */
	void teardowned( OperationProgressEvent e  );
	
	/**
	 * Notify operation stopped.
	 * After operation stopped, teardowned will be called.
	 */
	void broke( OperationProgressEvent e  );
	
	/**
	 * Notify about operation progress
	 */
	void progress( OperationProgressEvent e );
}
