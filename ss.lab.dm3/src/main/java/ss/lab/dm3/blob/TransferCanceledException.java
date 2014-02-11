package ss.lab.dm3.blob;

import ss.lab.dm3.blob.backend.BlobException;

public class TransferCanceledException extends BlobException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3159716729958384856L;

	/**
	 * @param abstractSheduledTransfer
	 */
	public TransferCanceledException(AbstractSheduledTransfer abstractSheduledTransfer) {
		super( "Transfer " + abstractSheduledTransfer + " canceled" );
	}
}
