package ss.lab.dm3.blob;

import ss.lab.dm3.connection.CallbackHandler;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class ProgressAdapter extends CallbackHandler implements IProgressListener {

	
	/**
	 * 
	 */
	public ProgressAdapter() {
		super();
	}

	/**
	 * @param internalHandler
	 */
	public ProgressAdapter(ICallbackHandler internalHandler) {
		super(internalHandler);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.t1.IProgressListener#dataLoaded(ss.lab.dm3.orm.QualifiedObjectId, int)
	 */
	public void dataTransfered(QualifiedObjectId<?> targetId, int length) {
		if ( this.log.isDebugEnabled() ) {
			this.log.debug( "Data loaded to " + targetId + " size " + length );
		}
	}

}
