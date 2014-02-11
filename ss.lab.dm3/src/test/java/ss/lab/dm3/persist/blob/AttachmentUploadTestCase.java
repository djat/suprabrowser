package ss.lab.dm3.persist.blob;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;

import ss.lab.dm3.blob.BlobTestUtils;
import ss.lab.dm3.blob.ProgressAdapter;
import ss.lab.dm3.blob.BlobTestUtils.TestBytesKind;
import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.testsupport.objects.Attachment;

public class AttachmentUploadTestCase extends AbstractDomainTestCase {

	private CallbackResultWaiter waiter;
	
	private byte[] content;

	public void test() {
		this.content = BlobTestUtils.getTestBytes( TestBytesKind.Large );
		this.waiter = new CallbackResultWaiter();
		upload(getDomain());
		this.waiter.waitToResult();
		Configuration cfg = getSystemConnectionProvider().getConfigurationProvider().get();
		BlobConfiguration blobCfg = cfg.getBlobConfiguration();
		byte[] actual = BlobTestUtils.getFileBytes(new File( blobCfg.getBaseDir(), "5" ));		
		assertEquals( this.content.length, actual.length );
		for( int n = 0; n < this.content.length; ++ n ) {
			assertEquals( "Byte #" + n,  this.content[ n ], actual[ n ] );
		}
		assertTrue( Arrays.equals( this.content, actual ) );
		// TODO check attachment state
		
	}
	
	public void upload(Domain domain) {
		Transaction tx = domain.beginTrasaction();
		Attachment attachment = domain.createObject( Attachment.class, 5L );
		attachment.setData( new ByteArrayInputStream( this.content ), new ProgressAdapter( this.waiter ) );
		tx.beginCommit();
	}
}
