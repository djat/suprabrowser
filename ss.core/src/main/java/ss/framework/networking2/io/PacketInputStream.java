package ss.framework.networking2.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ss.common.ArgumentNullPointerException;
import ss.common.CantRestoreObjectFromByteArrayException;

public class PacketInputStream {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PacketInputStream.class);

	public static final int BLOCK_SIZE = 512;
	
	private final DataInputStream datain;
		
	private final List<PacketLoadingListener> listeners = new ArrayList<PacketLoadingListener>();
	/**
	 * @param datain
	 */
	public PacketInputStream(final DataInputStream datain) {
		super();
		this.datain = datain;
	}

	/**
	 * @throws IOException
	 * @see java.io.FilterInputStream#close()
	 */
	public void close() throws IOException {
		if ( logger.isDebugEnabled() ) {
			logger.info( "closing input stream" );
		}
		this.datain.close();
	}


	/**
	 * @return Read object from protocol in
	 * @throws IOException
	 * @throws CantRestoreObjectFromByteArrayException 
	 * @throws ClassNotFoundException 
	 */
	public synchronized Packet read() throws IOException, CantRestoreObjectFromByteArrayException {
		if ( logger.isDebugEnabled() ) {
			logger.debug( "trying to read packet" );
		}
		// TODO: Begin Idle
		PacketHeader header = PacketHeader.load( this.datain );
		// TODO: End Idle
		if ( logger.isDebugEnabled() ) {
			logger.debug( "loaded " + header );
		}
		final byte[] rawData = loadPacketData(header);
		if ( logger.isDebugEnabled() ) {
			logger.debug( "packet data successfully loaded " + header );
		}
		return new Packet( header, rawData );
	}

	/**
	 * @param header
	 * @param buff 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	private byte[] loadPacketData(PacketHeader header) throws IOException {
		final boolean shouldSendNotification = header.isShouldSendNotification();
		if ( shouldSendNotification ) {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "notify packet begin for " + header );
			}
			notifyBeginPacket(header);			
		}
		else {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "loading silently " + header );
			}
		}
		try {
			final byte[] rawData = new byte[ header.getDataSize() ];
			int totalLoadedBytes = 0; 
			while( totalLoadedBytes < rawData.length ) {
				final int totalBytesToRead = rawData.length - totalLoadedBytes;
				if ( totalBytesToRead > 0 ) {
					final int bytesToRead = Math.min( BLOCK_SIZE, totalBytesToRead );
					final int lastReadBytesCount = this.datain.read(rawData, totalLoadedBytes, bytesToRead );
					if ( lastReadBytesCount < 0 ) {
						final String message = "Reach end of stream for " + header + ". Read " + totalLoadedBytes;
						throw new IOException( message );
					}
					else if ( lastReadBytesCount == 0 ) {
						logger.error( "Read 0 bytes for " + header + ". Read " + totalLoadedBytes + ", trying to read " + bytesToRead );
					}
					else {
						totalLoadedBytes += lastReadBytesCount;
					}
				}
				else {
					break;
				}
				if ( shouldSendNotification ) {					
					notifyPacketDataProgress(header, totalLoadedBytes);
				}
				PacketDetailsTracer.INSTANCE.packetDataBytesLoaded( header, totalLoadedBytes );
			}	
			if ( totalLoadedBytes != rawData.length ) {
				String message = "Inconsistent packet. Loaded bytes " + totalLoadedBytes + ". Expected bytes " + rawData.length + ". Packet " + header;
				throw new IOException( message );
			}			
			return rawData;
		}
		finally {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "notify packet end for " + header );
			}
			if ( shouldSendNotification ) {				
				notifyEndPacket(header);
			}			
		}
	}
	
	public void addPacketLoadingListener( PacketLoadingListener listener ) {
		if ( listener == null ) {
			throw new ArgumentNullPointerException( "listener" );
		}
		this.listeners.add(listener);
	}
		
	public void removePacketLoadingListener( PacketLoadingListener listener ) {
		this.listeners.remove(listener);
	}
	
	/**
	 * @param header
	 */
	private void notifyEndPacket(PacketHeader header) {
		try {
			for( PacketLoadingListener listener : this.listeners ) {
				listener.endPacket(header);
			}
		}
		catch( Exception ex ) {
			logger.error( "Listener failed", ex );
		}
	}

	/**
	 * @param header
	 * @param totalLoadedBytes
	 */
	private void notifyPacketDataProgress(PacketHeader header, int totalLoadedBytes) {
		try {
			for( PacketLoadingListener listener : this.listeners ) {
				listener.packetDataProgress( header, totalLoadedBytes );						
			}
		}
		catch( Exception ex ) {
			logger.error( "Listener failed", ex );
		}
	}

	/**
	 * @param header
	 */
	private void notifyBeginPacket(PacketHeader header) {
		try {
			for( PacketLoadingListener listener : this.listeners ) {
				listener.beginPacket(header);
			}
		}
		catch( Exception ex ) {
			logger.error( "Listener failed", ex );
		}
	}

	
}
