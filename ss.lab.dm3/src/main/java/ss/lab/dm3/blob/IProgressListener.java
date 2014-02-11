package ss.lab.dm3.blob;

import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public interface IProgressListener extends ICallbackHandler {

	/**
	 * @param targetId
	 * @param offset
	 */
	void dataTransfered(QualifiedObjectId<?> targetId, int length);

}
