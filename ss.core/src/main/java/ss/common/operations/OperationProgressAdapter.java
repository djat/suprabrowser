package ss.common.operations;
 
public class OperationProgressAdapter implements OperationProgressListener {
 
 	/* (non-Javadoc)
 	 * @see ss.common.operations.OperationProgressListener#ended(ss.common.operations.OperationProgressEvent)
 	 */
	public void teardowned(OperationProgressEvent e) {
 	}
 
 	/* (non-Javadoc)
 	 * @see ss.common.operations.OperationProgressListener#stared(ss.common.operations.OperationProgressEvent)
 	 */
	public void setupped(OperationProgressEvent e) {		
 	}
 
	/* (non-Javadoc)
 	 * @see ss.common.operations.OperationProgressListener#stoped(ss.common.operations.OperationProgressEvent)
 	 */
	public void broke(OperationProgressEvent e) {		
 	}

	/* (non-Javadoc)
	 * @see ss.common.operations.OperationProgressListener#progress(ss.common.operations.OperationProgressEvent)
	 */
	public void progress(OperationProgressEvent e) {		
	}

}
