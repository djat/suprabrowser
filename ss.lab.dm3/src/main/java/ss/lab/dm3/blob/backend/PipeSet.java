package ss.lab.dm3.blob.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import ss.lab.dm3.blob.service.BlobOpenKind;
import ss.lab.dm3.orm.QualifiedObjectId;

/**
 * 
 * @author Dmitry Goncharov
 */
class PipeSet {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final PipeTimeOutController pipeTimeOutController = new PipeTimeOutController();
	
	private final HashMap<Long,Pipe> idToPipe = new HashMap<Long, Pipe>();
	
	private final HashMap<QualifiedObjectId<?>,List<Pipe>> resourceIdToPipes = new HashMap<QualifiedObjectId<?>, List<Pipe>>();

	/**
	 * @param pipeId
	 */
	public synchronized void close(Long pipeId, Pipe.TransferState state ) {
		Pipe pipe = find( pipeId );
		if ( pipe != null ) {
			pipe.close( state );
		}		
		else {
			this.log.warn( "Pipe not found " + pipeId );
		}
	}

	/**
	 * @param pipe
	 */
	synchronized void remove(Pipe pipe) {
		if ( pipe == null ) {
			throw new NullPointerException( "pipe" );
		}
		// Remove pipe from resourceIdToPipes
		QualifiedObjectId<?> resourceId = pipe.getResourceId();
		List<Pipe> pipes = this.resourceIdToPipes.get( resourceId );
		if ( pipes != null ) {
			pipes.remove( pipe );
			if ( pipes.size() == 0 ) {
				this.resourceIdToPipes.remove( resourceId );
			}
		}
		// Remove pipe from idToPipe
		this.idToPipe.remove(pipe.getId());
		// Remove pipe from controller
		this.pipeTimeOutController.remove(pipe);
	}

	/**
	 * @param pipe
	 */
	private synchronized void add(Pipe pipe) {
		final QualifiedObjectId<?> resourceId = pipe.getResourceId();
		// Add pipe to resourceIdToPipes
		List<Pipe> pipes = this.resourceIdToPipes.get( resourceId );
		if ( pipes == null ) {
			pipes = new ArrayList<Pipe>();
			this.resourceIdToPipes.put(resourceId, pipes);
		}
		pipes.add(pipe);
		// Add pipe to idToPipe		
		this.idToPipe.put(pipe.getId(), pipe);
		// Add pipe to controller
		this.pipeTimeOutController.add(pipe);
	}
	
	/**
	 * @param pipeId
	 * @return
	 */
	private Pipe find(Long pipeId) {
		return this.idToPipe.get(pipeId);
	}


	/**
	 * @param resourceId
	 */
	private Collection<Pipe> findPipes(QualifiedObjectId<?> resourceId) {
		return this.resourceIdToPipes.get(resourceId);
	}
	
	/**
	 * @param resourceId
	 */
	public synchronized void closeAll(QualifiedObjectId<?> resourceId) {
		List<Pipe> pipesToClose = new ArrayList<Pipe>( findPipes( resourceId ) );
		for( Pipe pipe : pipesToClose ) {
			pipe.close( Pipe.TransferState.INTERRUPED );
		}
	}


	/**
	 * @param blobInformation
	 * @param blobOpenKind 
	 */
	public synchronized Pipe createPipe(IBlobStreamProvider streamProvider, BlobInformation blobInformation, BlobOpenKind blobOpenKind) {
		final Pipe pipe;
		if ( blobOpenKind == BlobOpenKind.CREATE_OVERWRITE ) {
			pipe = new WritePipe( this, blobInformation, streamProvider.openWrite( blobInformation.getResourceId() ) );
		}
		else if ( blobOpenKind == BlobOpenKind.READ ) {
			pipe = new ReadPipe( this, blobInformation, streamProvider.openRead( blobInformation.getResourceId() ) );			
		}
		else {
			throw new IllegalArgumentException( "Unsupported value " + blobOpenKind );
		}
		add( pipe );
		return pipe;
	}

	/**
	 * @param class1 
	 * @param pipeId
	 */
	public <T extends Pipe> T get(Class<T> pipeClazz, Long pipeId) {
		Pipe pipe = find( pipeId );
		if ( pipe == null ) {
			throw new IllegalArgumentException( "Can't find pipe by id " + pipeId );
		}
		if ( !pipeClazz.isInstance(pipe) ) {
			throw new ClassCastException( "Can't cast " + pipe + " to " + pipeClazz );
		}
		return pipeClazz.cast(pipe);
	}	
	
}
