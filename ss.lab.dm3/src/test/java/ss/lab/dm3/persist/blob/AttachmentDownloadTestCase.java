package ss.lab.dm3.persist.blob;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import ss.lab.dm3.blob.BlobTestUtils;
import ss.lab.dm3.blob.ProgressAdapter;
import ss.lab.dm3.blob.BlobTestUtils.TestBytesKind;
import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.testsupport.objects.Attachment;

public class AttachmentDownloadTestCase extends AbstractDomainTestCase {

	private CallbackResultWaiter waiter;
	
	private byte[] content;

	private ByteArrayOutputStream out;

	public void test() {
		Configuration cfg = getSystemConnectionProvider().getConfigurationProvider().get();
		BlobConfiguration blobCfg = cfg.getBlobConfiguration();
		this.content = BlobTestUtils.getTestBytes( TestBytesKind.XXLarge );
		BlobTestUtils.setFileBytes( new File( blobCfg.getBaseDir(), "10" ), this.content );
		this.waiter = new CallbackResultWaiter();
		download( getDomain() );
		this.waiter.waitToResult();
		byte[] actual = this.out.toByteArray();
		assertTrue( Arrays.equals( this.content, actual ) );
	}
	
	public void download(Domain domain) {
		Attachment attachment = domain.resolve( Attachment.class, 10L );
		ProgressAdapter addapter = new ProgressAdapter( this.waiter ) {
			@Override
			public void dataTransfered(QualifiedObjectId<?> targetId, int length) {
				System.out.println( "dataLoaded " + targetId + " length " + length );
			}
		};
		this.out = new ByteArrayOutputStream();
		attachment.beginDownloadTo( this.out, addapter );
	}
}
