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
public class BlobManagerBackEndReadTestCase extends TestCase {

	private TestAttachmentProvider attachmentInformationProvider;
	
	private final BlobConfiguration configuration = new BlobConfiguration();
	
	private byte[] content = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final Long id = 2L; 
		this.content = BlobTestUtils.getTestBytes( TestBytesKind.Large );
		BlobTestUtils.setFileBytes( new File( this.configuration.getBaseDir(), String.valueOf( id ) ), this.content );
		this.attachmentInformationProvider = TestAttachmentProvider.create(id, this.content.length );
	}
	
	public void test() {
		
		IBlobManagerBackEnd backEnd = new BlobManagerBackEnd( this.configuration, this.attachmentInformationProvider );
		QualifiedObjectId<?> resourceId = QualifiedObjectId.create( Attachment.class, 2L );
		final PipeHeader pipeHeader = backEnd.open(resourceId, BlobOpenKind.READ );
		byte [] actual = new byte[ (int) pipeHeader.getSize() ]; 
		backEnd.read(pipeHeader.getId(), actual, 0, actual.length );
		backEnd.close( pipeHeader.getId(), Pipe.TransferState.FINISHED );
		assertEquals( BlobState.READY, this.attachmentInformationProvider.getAttachment().getBlobState() );
		boolean same = Arrays.equals(this.content, actual  );
		assertTrue( "Expected " + this.content + ", Actual " + actual , same );
		assertEquals( actual.length, this.attachmentInformationProvider.getAttachment().getSize() );		
	}
}
