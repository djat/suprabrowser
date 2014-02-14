/**
 * 
 */
package ss.client.ui.messagedeliver;

import ss.common.operations.OperationProgressEvent;
import ss.common.operations.OperationProgressListener;

/**
 * @author zobo
 *
 */
class InsertListOperationProgressAdaptor implements OperationProgressListener{

	/* (non-Javadoc)
	 * @see ss.common.operations.OperationProgressListener#broke(ss.common.operations.OperationProgressEvent)
	 */
	public final void broke(OperationProgressEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.OperationProgressListener#progress(ss.common.operations.OperationProgressEvent)
	 */
	public final void progress(OperationProgressEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.OperationProgressListener#setupped(ss.common.operations.OperationProgressEvent)
	 */
	public void setupped(OperationProgressEvent e) {
		
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.OperationProgressListener#teardowned(ss.common.operations.OperationProgressEvent)
	 */
	public void teardowned(OperationProgressEvent e){
		
	}
}
