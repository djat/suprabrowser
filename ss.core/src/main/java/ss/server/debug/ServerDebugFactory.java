/**
 * 
 */
package ss.server.debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ss.common.networking2.ProtocolStartUpInformation;
import ss.framework.networking2.Protocol;
import ss.server.networking2.ServerProtocolManager;

/**
 *
 */
public class ServerDebugFactory {

	/**
	 * Singleton instance
	 */
	public final static ServerDebugFactory INSTANCE = new ServerDebugFactory();

	private ServerDebugFactory() {
	}
	
	public void createAndStart() {
		
	}

	/**
	 * @param session
	 * @param cdatain
	 * @param cdataout
	 */
	public void createAndStart(ProtocolStartUpInformation startUpInfo, DataInputStream dataIn, DataOutputStream dataOut) {
		Protocol protocol = new Protocol( dataIn, dataOut, startUpInfo.generateProtocolDisplayName( "Debug" ) );
		protocol.registerHandler( new ThreadsDumpCommandHandler() );
		protocol.registerHandler( new RunRemoveCommandHandler() );
		protocol.registerHandler( new DumpSupraSphereHandler() );
		protocol.start( ServerProtocolManager.INSTANCE );
		
	}
}
