package ss.lab.dm3.blob;

import java.io.IOException;
import java.io.OutputStream;

import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.BlobTransferService;
import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class SheduledDownload extends AbstractSheduledTransfer {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final OutputStream out;
	
	/**
	 * @param listener
	 * @param targetId
	 */
	public SheduledDownload(BlobTransferService blobTransferService, QualifiedObjectId<?> targetId, IProgressListener listener, OutputStream out) {
		super(blobTransferService, targetId, listener );
		this.out = out;
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.t1.impl.AbstractBlobTransfer#doTransfer(ss.lab.dm3.blob.service.PipeHeader, ss.lab.dm3.blob.service.BlobTransferService)
	 */
	@Override
	protected void doTransfer(PipeHeader pipeHeader, BlobTransferService blobTransferService) throws IOException {
		final long size = pipeHeader.getSize();
		int downloadedLength = 0;
		byte[] buff;
		while( (buff = blobTransferService.read( pipeHeader.getId() ) ) != null ) {
			checkIsNotCanceled();			
			this.out.write(buff);
			downloadedLength += buff.length;
			if ( this.listener != null ) {
				this.listener.dataTransfered( pipeHeader.getResourceId(), downloadedLength );
			}
			if ( downloadedLength == size && size > 0 ) {
				break;
			}
		}		 
	}


	/* (non-Javadoc)
	 * @see ss.lab.dm3.blob.t1.impl.AbstractBlobTransfer#getBlobOpenKind()
	 */
	@Override
	protected BlobOpenKind getBlobOpenKind() {
		return BlobOpenKind.READ;
	}


}
