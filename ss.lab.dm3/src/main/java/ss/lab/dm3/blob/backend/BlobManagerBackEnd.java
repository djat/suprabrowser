package ss.lab.dm3.blob.backend;

import ss.lab.dm3.blob.configuration.BlobConfiguration;
import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.blob.service.PipeHeader;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 *
 */
public class BlobManagerBackEnd implements IBlobManagerBackEnd {

	private final BlobConfiguration blobConfiguration;
	
	private final PipeSet pipes;

	private final BlobInformationProvider blobInformationProvider;

	private final IBlobStreamProvider blobStreamProvider;

	/**
	 * @param blobInformationProvider
	 * @param blobStreamProvider
	 */
	public BlobManagerBackEnd(BlobConfiguration configuration, BlobInformationProvider blobInformationProvider ) {
		super();
		this.pipes = new PipeSet();
		this.blobConfiguration = configuration;
		this.blobInformationProvider = blobInformationProvider;
		this.blobStreamProvider = new BlobStreamProvider( this.blobConfiguration );
	}

	public void close(Long pipeId, Pipe.TransferState state) {
		this.pipes.close(pipeId, state );
	}
	
	public void delete(QualifiedObjectId<?> resourceId) {
		this.pipes.closeAll(resourceId);
		this.blobStreamProvider.delete(resourceId);
	}

	public PipeHeader open(QualifiedObjectId<?> resourceId, BlobOpenKind blobOpenKind) {
		BlobInformation blobInformation = this.blobInformationProvider.open(resourceId,
				blobOpenKind);
		return this.pipes.createPipe( this.blobStreamProvider, blobInformation, blobOpenKind).getHeader();
	}

	public int read(Long pipeId, byte[] buff, int offset, int length) {
		ReadPipe pipe = this.pipes.get(ReadPipe.class, pipeId);
		return pipe.read(buff, offset, length);
	}

	public void write(Long pipeId, byte[] buff, int offset, int length) {
		WritePipe pipe = this.pipes.get(WritePipe.class, pipeId);
		pipe.write(buff, offset, length);
	}

}
