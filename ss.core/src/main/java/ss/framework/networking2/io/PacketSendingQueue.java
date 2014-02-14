package ss.framework.networking2.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import ss.common.operations.AbstractLoopOperation;
import ss.common.operations.OperationBreakException;
import ss.common.operations.OperationProgressEvent;

public final class PacketSendingQueue extends AbstractLoopOperation {

	/**
	 * 
	 */
	private static final int MAX_QUEUE_CAPACITY = 4096;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PacketSendingQueue.class);
	
	private final LinkedBlockingQueue<Packet> sendQueue = new LinkedBlockingQueue<Packet>( MAX_QUEUE_CAPACITY );

	private final PacketOutputStream packetOut;
	
	private PacketSendingQueueListener packetSendingQueueListener = null;
	
	private volatile boolean performSending = false;
	
	/**
	 * @param packetOut
	 */
	public PacketSendingQueue(PacketOutputStream packetOut, PacketSendingQueueListener packetSendingQueueListener ) {
		super();		
		this.packetOut = packetOut;
		this.packetSendingQueueListener = packetSendingQueueListener;
	}

	public PacketSendingQueue(DataOutputStream dataout, PacketSendingQueueListener packetSendingQueueListener, String displayName ) { 
		this( new PacketOutputStream(dataout), packetSendingQueueListener );
		super.setDisplayName(displayName);
	}
	/**
	 * Put object to queue 
	 * @param packet
	 */
	public final void put(Packet packet) throws InterruptedException  {
		this.sendQueue.put( packet );
	}
	
	/* (non-Javadoc)
	 * @see ss.common.operations.AbstractLoopOperation#performLoopAction()
	 */
	@Override
	protected final void performLoopAction() throws OperationBreakException {
		try {
			this.performSending = true;
			sendPacketImmediately(this.sendQueue.take());
		} 
		catch (IOException e) { 
			logger.error( "IOException ", e );
			queryBreak();
		}
		catch (InterruptedException e) {
			this.checkBroke();
		}
		finally {
			this.performSending = false;
		}
	}

	/**
	 * Sent packet to packet out immediately
	 * @param packet
	 * @throws IOException
	 */	
	private void sendPacketImmediately(final Packet packet) throws IOException {
		if ( logger.isDebugEnabled() ) {
			logger.debug( "Sending packet with command " + packet );
		}
		if ( this.packetSendingQueueListener != null ) {
			this.packetSendingQueueListener.beforePacketSend( new PacketSendingQueueEvent( this, packet ) );
		}
		this.packetOut.write( packet );
		if ( this.packetSendingQueueListener != null ) {
			this.packetSendingQueueListener.afterPacketSend( new PacketSendingQueueEvent( this, packet ) );
		}
	}

	/**
	 * @throws IOException
	 * @see ss.common.networking.ProtocolOutputStream#silentClose()
	 */
	public final void dispose() {
		queryBreak();
		try {
			this.packetOut.close();
		} catch (IOException ex) {
			logger.error("Cannot close connection ", ex);
		}
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.AbstractOperation#onTeardown(ss.common.operations.OperationProgressEvent)
	 */
	@Override
	protected void onTeardown(OperationProgressEvent e) {
		super.onTeardown(e);
		if ( this.packetSendingQueueListener != null ) {
			this.packetSendingQueueListener.queueTeardowned();
		}
	}
	
	public boolean isEmpty() {
		return this.sendQueue.size() == 0 && !this.performSending; 		
	}

	/**
	 * @return
	 */
	public int size() {
		return this.sendQueue.size();
	}

}
