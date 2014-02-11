package ss.lab.dm3.blob.service;

import ss.lab.dm3.blob.backend.Pipe;
import ss.lab.dm3.connection.service.Service;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public interface BlobTransferService extends Service {

	public static final int BLOB_BUFFER_SIZE = 512;
	
	PipeHeader open(QualifiedObjectId<?> resourceId, BlobOpenKind blobOpenKind) throws ServiceException;

	void write(Long pipeId, byte[] buff) throws ServiceException;

	byte[] read(Long pipeId) throws ServiceException;

	void close(Long pipeId, Pipe.TransferState state ) throws ServiceException;

	void delete(QualifiedObjectId<?> resourceId) throws ServiceException;

}
