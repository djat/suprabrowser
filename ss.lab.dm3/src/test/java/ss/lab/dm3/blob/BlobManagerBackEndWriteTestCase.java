package ss.lab.dm3.blob;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import ss.lab.dm3.blob.BlobTestUtils.TestBytesKind;
import ss.lab.dm3.blob.backend.BlobManagerBackEnd;
import ss.lab.dm3.blob.backend.IBlobManagerBackEnd;
import ss.lab.dm3.blob.backend.Pipe;
import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.BlobState;
import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.testsupport.objects.Attachment;

/**
 * 
 * @author Dmitry Goncharov
 * 
 * TODO add boundary test cases
 * 
 *
 */
public class BlobManagerBackEndWriteTestCase extends TestCase {

	private TestAttachmentProvider attachmentInformationProvider;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Attachment attachment = new Attachment();
		attachment.setBlobState( BlobState.CREATED_OVERWRITED );
		attachment.setId( 2L );
		attachment.setSize( -1 );
		this.attachmentInformationProvider = new TestAttachmentProvider( attachment );
	}
	
	public void test() {
		final BlobConfiguration configuration = new BlobConfiguration();
		IBlobManagerBackEnd backEnd = new BlobManagerBackEnd( configuration, this.attachmentInformationProvider );
		QualifiedObjectId<?> resourceId = QualifiedObjectId.create( Attachment.class, 2L );
		final PipeHeader pipeHeader = backEnd.open(resourceId, BlobOpenKind.CREATE_OVERWRITE );
		byte[] content = BlobTestUtils.getTestBytes( TestBytesKind.Medium );
		backEnd.write( pipeHeader.getId(), content, 0, content.length );
		backEnd.close( pipeHeader.getId(), Pipe.TransferState.FINISHED );
		assertEquals( BlobState.READY, this.attachmentInformationProvider.getAttachment().getBlobState() );
		final byte[] actual = BlobTestUtils.getFileBytes( new File( configuration.getBaseDir(), "2" ) );
		boolean same = Arrays.equals(actual, content  );
		assertTrue( "Expected " + content + ", Actual " + actual, same );
		assertEquals( content.length, this.attachmentInformationProvider.getAttachment().getSize() );		
	}
}
