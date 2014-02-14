package ss.framework.networking2.io;

import java.io.DataOutputStream;
import java.io.IOException;

public class PacketOutputStream {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PacketOutputStream .class);
	
	private final DataOutputStream dataout;
	
	/**
	 * @param datain
	 */
	public PacketOutputStream( final DataOutputStream dataout ) {
		super();
		this.dataout = dataout;
	}

	/**
	 * @throws IOException
	 * @see java.io.FilterInputStream#close()
	 */
	public void close() throws IOException {
		if ( logger.isDebugEnabled() ) {
			logger.debug( "closing output stream" );
		}
		this.dataout.close();
	}


	/**
	 * @return Read object from protocol in
	 * @throws IOException
	 */
	public synchronized void write( Packet packet ) throws IOException {
		final PacketHeader header = packet.getHeader();
		if ( logger.isDebugEnabled() ) {
			logger.debug( "sending packet " + header );
		}		
		try {
			header.save(this.dataout);
			if ( header.isShouldSendNotification() ) {
				this.dataout.flush();
			}
			this.dataout.write(packet.getRawData());
			this.dataout.flush();
			if ( logger.isDebugEnabled() ) {
				logger.debug( "packet was sent " + packet.getData().toString() );
			}
		}
		catch( IOException ex ) {
			logger.error( "Failed to sent packet " + header, ex);
			throw ex;
		}
	}

}
