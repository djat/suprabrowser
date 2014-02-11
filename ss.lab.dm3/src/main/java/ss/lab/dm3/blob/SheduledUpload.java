package ss.lab.dm3.blob;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.BlobTransferService;
import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class SheduledUpload extends AbstractSheduledTransfer {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final InputStream in;

	/**
	 * @param listener
	 * @param targetId
	 * @param in
	 */
	public SheduledUpload(BlobTransferService blobTransferService, QualifiedObjectId<?> targetId, IProgressListener listener, InputStream in) {
		super(blobTransferService, targetId, listener);
		this.in = in;
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.t1.impl.AbstractBlobTransfer#doTransfer(ss.lab.dm3.blob.service.PipeHeader, ss.lab.dm3.blob.service.BlobTransferService)
	 */
	@Override
	protected void doTransfer(PipeHeader pipeHeader, BlobTransferService blobTransferService)
			throws IOException {
		int uploadSize = 0;
		byte[] buff = new byte[ BlobTransferService.BLOB_BUFFER_SIZE ];
		int readLength = 0;
		while( ( readLength = this.in.read(buff) ) > 0 ) {
			checkIsNotCanceled();
			final byte [] toUpload;
			if ( readLength == buff.length ) {
				toUpload = buff;
			}
			else {
				toUpload = Arrays.copyOf( buff, readLength );
			}
			blobTransferService.write( pipeHeader.getId(), toUpload );
			uploadSize += readLength; 
			if ( this.listener != null ) {
				this.listener.dataTransfered( pipeHeader.getResourceId(), uploadSize );
			}
		}		
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.t1.impl.AbstractBlobTransfer#getBlobOpenKind()
	 */
	@Override
	protected BlobOpenKind getBlobOpenKind() {
		return BlobOpenKind.CREATE_OVERWRITE;
	}




}
