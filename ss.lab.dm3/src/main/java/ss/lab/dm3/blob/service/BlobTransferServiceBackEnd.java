package ss.lab.dm3.blob.service;

import java.util.Arrays;

import ss.lab.dm3.blob.backend.IBlobManagerBackEnd;
import ss.lab.dm3.blob.backend.Pipe;
import ss.lab.dm3.connection.service.ServiceBackEnd;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class BlobTransferServiceBackEnd extends ServiceBackEnd implements BlobTransferService {
	
	private static final byte[] EMPTY = new byte[ 0 ];

	private IBlobManagerBackEnd backend;
	
	
	@Override
	protected void initializing() {
		super.initializing();
		this.backend = getContext().getBlobManagerBackEnd();
	}

	public void close(Long pipeId, Pipe.TransferState state ) throws ServiceException {
		this.backend.close( pipeId, state );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.service.IBlobTransferService#delete(ss.lab.dm3.orm.QualifiedObjectId)
	 */
	public void delete(QualifiedObjectId<?> resourceId) throws ServiceException {
		this.backend.delete( resourceId );
	}

	public PipeHeader open(QualifiedObjectId<?> resourceId, BlobOpenKind blobOpenKind) throws ServiceException {
		return this.backend.open( resourceId, blobOpenKind );
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.service.IBlobTransferService#read(ss.lab.dm3.blob.service.PipeHeader)
	 */
	public byte[] read(Long pipeId) throws ServiceException {
		byte[] buff = new byte[ BlobTransferService.BLOB_BUFFER_SIZE ];
		int count = this.backend.read( pipeId, buff, 0, buff.length );
		if ( count == -1 ) {
			return null;
		}
		else if ( count == 0 ) {
			return EMPTY;
		}
		else if ( count < buff.length ) {
			return Arrays.copyOfRange( buff, 0, count );
		}
		else {
			return buff;
		}
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.service.IBlobTransferService#write(ss.lab.dm3.blob.service.PipeHeader, byte[])
	 */
	public void write(Long pipeId, byte[] buff) throws ServiceException {
		this.backend.write( pipeId, buff, 0, buff.length );
	}

}
