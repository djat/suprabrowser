package ss.lab.dm3.blob.backend;

import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 */
public interface IBlobManagerBackEnd {

	/**
	 * @param pipeHeader
	 */
	void close(Long pipeId, Pipe.TransferState state );

	/**
	 */
	PipeHeader open(QualifiedObjectId<?> resourceId, BlobOpenKind blobOpenKind);

	/**
	 * @param resourceId
	 */
	void delete(QualifiedObjectId<?> resourceId);

	/**
	 */
	int read(Long pipeId, byte[] buff, int offset, int length);

	/**
	 * 
	 */
	void write(Long pipeId, byte[] buff, int offset, int length);

}
