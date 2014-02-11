package ss.lab.dm3.blob;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;

import ss.lab.dm3.blob.BlobTestUtils.TestBytesKind;
import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.testsupport.objects.Attachment;
import junit.framework.TestCase;

public class BlobManagerUploadTestCase extends TestCase {

	public void test() {
		final long id = 3L;
		Attachment attachment = new Attachment();
		attachment.setId( id );
		attachment.setBlobState( BlobState.CREATED_OVERWRITED );
		attachment.setSize( -1 );
		BlobConfiguration configuration = new BlobConfiguration();
		final BlobManager manager = BlobTestUtils.createBlobManager( configuration, attachment);
		final byte[] content = BlobTestUtils.getTestBytes( TestBytesKind.XXLarge );
		final CallbackResultWaiter waiter = new CallbackResultWaiter();
		IProgressListener progressListener = new ProgressAdapter( waiter );
		manager.bind( QualifiedObjectId.create( Attachment.class, id ), new ByteArrayInputStream( content ), progressListener );
		manager.beginBindedUploads();
		waiter.waitToResult();
		byte[] actual = BlobTestUtils.getFileBytes( new File( configuration.getBaseDir(), String.valueOf( id ) ) );
		assertTrue( Arrays.equals( actual, content ) );
		assertEquals( BlobState.READY, attachment.getBlobState() );
	}

}
