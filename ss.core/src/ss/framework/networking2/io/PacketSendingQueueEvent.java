/**
 * 
 */
package ss.framework.networking2.io;

import java.util.EventObject;

/**
 *
 */
public class PacketSendingQueueEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6629335330133799458L;
	
	private final Packet packet;

	/**
	 * @param source
	 */
	public PacketSendingQueueEvent(PacketSendingQueue source, Packet packet ) {
		super(source);
		this.packet = packet;
	}

	/**
	 * @return the packet
	 */
	public Packet getPacket() {
		return this.packet;
	}
	
	
	
	
}
