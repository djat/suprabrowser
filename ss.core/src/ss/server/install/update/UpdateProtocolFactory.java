/**
 * 
 */
package ss.server.install.update;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ss.common.networking2.ProtocolStartUpInformation;
import ss.framework.networking2.Protocol;
import ss.server.networking2.ServerProtocolManager;

/**
 *
 */
public class UpdateProtocolFactory {

	/**
	 * Singleton instance
	 */
	public final static UpdateProtocolFactory INSTANCE = new UpdateProtocolFactory();

	private UpdateProtocolFactory() {
	}
	
	public void createAndStart(ProtocolStartUpInformation information, DataInputStream cdatain, DataOutputStream cdataout) {
		final Protocol protocol = new Protocol( cdatain, cdataout, information.generateProtocolDisplayName( "ApplicationUpdate") );
		protocol.registerHandler( new HelloCommandHandler() );
		protocol.start( ServerProtocolManager.INSTANCE );
	}
}
