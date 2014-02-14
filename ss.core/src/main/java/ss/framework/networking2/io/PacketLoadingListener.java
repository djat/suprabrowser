/**
 * 
 */
package ss.framework.networking2.io;

/**
 *
 */
public interface PacketLoadingListener {

	void beginPacket( PacketHeader header );

	void packetDataProgress( PacketHeader header, int loadedBytesCount );
	
	void endPacket( PacketHeader header );
	
}

