package ss.server.errorreporting;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ss.common.ReflectionUtils;
import ss.common.networking2.ProtocolStartUpInformation;
import ss.framework.errorreporting.network.handlers.InitializeCommandHandler;
import ss.framework.errorreporting.network.handlers.LogEventHandler;
import ss.framework.networking2.ActiveMessageHandler;
import ss.framework.networking2.Protocol;
import ss.server.networking2.ServerProtocolManager;

public final class ClientLogHandler {

	private final Protocol protocol;

	private ClientLogHandler(ProtocolStartUpInformation startUpInfo, DataInputStream cdatain, DataOutputStream cdataout) {
		this.protocol = new Protocol( cdatain, cdataout, startUpInfo.generateProtocolDisplayName( "ClientLogs" ));
		registerHandler( InitializeCommandHandler.class );
		registerHandler( LogEventHandler.class );
	}
	
	/**
	 * @param name
	 */
	private void registerHandler(Class<? extends ActiveMessageHandler> clazz) {
		ActiveMessageHandler handler = ReflectionUtils.create( clazz, DbLogStorage.INSTACE );
		this.protocol.registerHandler( handler );
	}

	private void start() {
		this.protocol.start( ServerProtocolManager.INSTANCE );
	}

	/**
	 * @param information
	 * @param cdatain
	 * @param cdataout
	 */
	public static void createAndStart(ProtocolStartUpInformation information, DataInputStream cdatain, DataOutputStream cdataout) {
		ClientLogHandler clientLogHandler = new ClientLogHandler( information, cdatain, cdataout );
		clientLogHandler.start();		
	}
}
