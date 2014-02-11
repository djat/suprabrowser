package ss.lab.dm3.blob.backend;

import ss.lab.dm3.blob.IBlobObject;
import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 */
public abstract class BlobInformationProvider {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	/**
	 * @param blobInfo
	 */
	protected abstract void updateBlobBy(BlobInformation blobInfo);

	protected abstract IBlobObject find(QualifiedObjectId<?> resourceId);

		/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.blob.backend.IBlobInformationProvider#getBlobInformation(ss.lab.dm3.orm.QualifiedObjectId)
	 */
	public final BlobInformation getBlobInformation(QualifiedObjectId<?> resourceId) {
		final IBlobObject obj = find(resourceId);
		return new BlobInformation( this, obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.blob.backend.IBlobInformationProvider#open(ss.lab.dm3.orm.QualifiedObjectId,
	 *      ss.lab.dm3.blob.service.BlobOpenKind)
	 */
	public final BlobInformation open(QualifiedObjectId<?> resourceId, BlobOpenKind blobOpenKind) {
		BlobInformation blobInformation = getBlobInformation(resourceId);
		blobInformation.open(blobOpenKind);
		updateBlobBy(blobInformation);
		return blobInformation;
	}

}
