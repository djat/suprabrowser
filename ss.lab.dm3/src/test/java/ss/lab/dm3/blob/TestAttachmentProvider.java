package ss.lab.dm3.blob;

import ss.lab.dm3.blob.backend.BlobInformation;
import ss.lab.dm3.blob.backend.BlobInformationProvider;
import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.testsupport.objects.Attachment;

final class TestAttachmentProvider extends BlobInformationProvider {
	
	private final Attachment attachment;

	/**
	 * @param attachment
	 */
	public TestAttachmentProvider(Attachment attachment) {
		super();
		this.attachment = attachment;
	}
	
	public Attachment getAttachment() {
		return this.attachment;
	}

	@Override
	protected IBlobObject find(QualifiedObjectId<?> resourceId) {
		if ( this.attachment.getQualifiedId().equals( resourceId ) ) {
			return this.attachment;
		}
		else {
			return null;
		}
	}

	@Override
	protected void updateBlobBy(BlobInformation blobInfo) {
		blobInfo.writePropertiresTo( this.attachment );	
	}
	
	public static TestAttachmentProvider create( Long id, long size ) {
		Attachment attachment = new Attachment();
		attachment.setBlobState( BlobState.READY );
		attachment.setId( id );
		attachment.setSize( size );		
		return new TestAttachmentProvider( attachment );
	}
	
}