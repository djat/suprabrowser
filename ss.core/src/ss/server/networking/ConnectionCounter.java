package ss.server.networking;

public class ConnectionCounter {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ConnectionCounter.class);
	
	public final static ConnectionCounter INSTANCE = new ConnectionCounter();

	private volatile int count;
	
	private ConnectionCounter() {
	}
	
	public synchronized void startUpServerPartOfProtocol( String protocolName ) {
		++ this.count;
		logger.info( "Start server part of protocol. Created protocols count." + this.count  + ". Current " + protocolName);
	}
	
}
