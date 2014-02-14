package ss.common.operations;


public interface IOperation extends Runnable  {

	/**
	 * Add progress listener to the operation
	 */
	void addProgressListener( OperationProgressListener listener );
	
	/**
	 * Remove progress listener to the operation
	 */
	void removeProgressListener( OperationProgressListener  listener );
	
}
