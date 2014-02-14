/*
 * Created on Feb 16, 2005
 *
 */
package ss.server.presense;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ss.common.networking2.ProtocolStartUpInformation;
import ss.framework.networking2.Protocol;
import ss.framework.networking2.properties.IClassificableProperty;
import ss.framework.networking2.properties.ProtocolPropertiesBuilder;
import ss.framework.networking2.properties.ProtocolProperty;
import ss.server.networking2.ServerProtocolManager;
import ss.server.presense.handlers.AbstractPresenceEventHandler;
import ss.server.presense.handlers.ActivityEventHandler;
import ss.server.presense.handlers.KeyTypedEventHandler;
import ss.server.presense.handlers.UserLogginedEventHandler;


public class ServerPresence  {

	@SuppressWarnings("unused")
	private	static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(ServerPresence.class);

	public final static PresenceMark PRESENCE_MARK = new PresenceMark(); 
	
	private final ProtocolStartUpInformation startUpInfo;
	
	private final Protocol protocol;
	
	/**
	 * @param session
	 * @param cdatain
	 * @param cdataout
	 */
	private ServerPresence(ProtocolStartUpInformation startUpInfo, DataInputStream cdatain, DataOutputStream cdataout) {
		this.startUpInfo = startUpInfo;
		ProtocolPropertiesBuilder propertiesBuilder = new ProtocolPropertiesBuilder( startUpInfo.generateProtocolDisplayName( "Presence" ) );
		propertiesBuilder.add( PRESENCE_MARK );
		this.protocol = new Protocol( cdatain, cdataout, propertiesBuilder.getResult() );
		registerHandler( new KeyTypedEventHandler() );
		registerHandler( new ActivityEventHandler() );
		registerHandler( new UserLogginedEventHandler() );
	}
	
	/**
	 * @param handler
	 */
	private void registerHandler(AbstractPresenceEventHandler handler) {
		handler.initialize( this.startUpInfo );
		this.protocol.registerHandler(handler);		
	}

	/**
	 * 
	 */
	private void start() {
		this.protocol.start( ServerProtocolManager.INSTANCE );
	}
	
	/**
	 *
	 */
	public static void createAndStart( ProtocolStartUpInformation startUpInfo, DataInputStream cdatain, DataOutputStream cdataout ) {
		ServerPresence serverPresence = new ServerPresence( startUpInfo, cdatain, cdataout );
		serverPresence.start();
		logger.info( "ServerPresence created " + serverPresence );
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.protocol.toString();
	}

	private static class PresenceMark extends ProtocolProperty<String> implements IClassificableProperty {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5729091056951858305L;

		/**
		 * @param value
		 */
		public PresenceMark() {
			super( "PresenceMark" );
		}		
	}
	
	  
	
}

