/**
 * 
 */
package ss.framework.networking2.io;

import java.util.EventListener;

/**
 *
 */
public interface PacketSendingQueueListener extends EventListener {

	void beforePacketSend(PacketSendingQueueEvent e);
	
	void afterPacketSend( PacketSendingQueueEvent e );
	
	void queueTeardowned();
	
}
