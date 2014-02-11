package ss.lab.dm3.blob;

import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.orm.QualifiedObjectId;

public interface IBlobObject {

	/**
	 * 
	 */
	public static final int UNKNOWN_BLOB_SIZE = -1;

	void setBlobState( BlobState blobState );
	BlobState getBlobState();

	long getSize();
	
	void setSize( long size );

	QualifiedObjectId<?> getQualifiedId();
	
}
