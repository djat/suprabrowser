/**
 * 
 */
package ss.framework.networking2.io;


/**
 *
 */
final class PacketDetailsTracer {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PacketDetailsTracer.class);
	
	/**
	 * Singleton instance
	 */
	public final static PacketDetailsTracer INSTANCE = new PacketDetailsTracer();

	private PacketDetailsTracer() {
	}

	/**
	 * @param packet
	 */
	public void packetRestored(Packet packet) {
		if (logger.isDebugEnabled()) {
			logger.debug( "Packet Restored " + packet.getHeader() + " Data " + packet.getData() );
		}
	}

	/**
	 * @param packet
	 */
	public void packetCreated(Packet packet) {
		if (logger.isDebugEnabled()) {
			logger.debug( "Packet Created " + packet.getHeader() + " Data " + packet.getData() );
		}
	}

	/**
	 * @param header
	 * @param totalLoadedBytes
	 */
	public void packetDataBytesLoaded(PacketHeader header, int totalLoadedBytes) {
//		if (logger.isDebugEnabled()) {
//			logger.debug( "Packet data bytes loaded " + header + ", Loaded bytes Data " + totalLoadedBytes );
//		} 
	}
	
}
