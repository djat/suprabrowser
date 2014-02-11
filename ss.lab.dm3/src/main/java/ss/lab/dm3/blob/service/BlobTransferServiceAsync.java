package ss.lab.dm3.blob.service;

import ss.lab.dm3.blob.backend.Pipe;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.ServiceAsync;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public interface BlobTransferServiceAsync extends ServiceAsync {

	void open(QualifiedObjectId<?> resourceId, BlobOpenKind blobOpenKind, ICallbackHandler resultHandler) throws ServiceException;

	void write(Long pipeId, byte[] buff, ICallbackHandler resultHandler) throws ServiceException;

	void read(Long pipeId, ICallbackHandler resultHandler) throws ServiceException;

	void close(Long pipeId, Pipe.TransferState state, ICallbackHandler resultHandler) throws ServiceException;

	void delete(QualifiedObjectId<?> resourceId, ICallbackHandler resultHandler) throws ServiceException;

}
