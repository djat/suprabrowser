/**
 * 
 */
package ss.framework.networking2.io;

import ss.framework.identity.RuntimeIdGenerator;

/**
 *
 */
public final class PacketIdGenerator extends RuntimeIdGenerator {

	/**
	 * Singleton instance
	 */
	public final static PacketIdGenerator INSTANCE = new PacketIdGenerator();

	private PacketIdGenerator() {		
	}
	
}
