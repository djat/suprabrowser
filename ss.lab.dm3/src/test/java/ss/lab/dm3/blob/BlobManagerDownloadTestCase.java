package ss.lab.dm3.blob;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import ss.lab.dm3.blob.BlobTestUtils.TestBytesKind;
import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.testsupport.objects.Attachment;

public class BlobManagerDownloadTestCase extends TestCase {

	public void test() {
		final long id = 4L;
		final Attachment attachment = new Attachment();
		attachment.setId( id );
		attachment.setBlobState( BlobState.READY );
		attachment.setSize( -1 );
		final BlobConfiguration configuration = new BlobConfiguration();
		final byte[] content = BlobTestUtils.getTestBytes( TestBytesKind.XXLarge );
		BlobTestUtils.setFileBytes( new File( configuration.getBaseDir(), String.valueOf( id ) ), content );
		final BlobManager manager = BlobTestUtils.createBlobManager( configuration, attachment);
		final CallbackResultWaiter waiter = new CallbackResultWaiter();
		IProgressListener progressListener = new ProgressAdapter( waiter );
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		manager.beginDownload( QualifiedObjectId.create( Attachment.class, id ), out, progressListener );
		waiter.waitToResult();
		byte[] actual = out.toByteArray();
		assertTrue( Arrays.equals( actual, content ) );
		assertEquals( BlobState.READY, attachment.getBlobState() );
	}

}
