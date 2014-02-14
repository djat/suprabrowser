package ss.server.networking;

/*
 *
 * The main login screen, passes session, including passphrase, on to
 * MessagePane.java
 *
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.dom4j.Document;

import ss.common.LocationUtils;
import ss.common.ThreadUtils;
import ss.common.XmlDocumentUtils;
import ss.common.domainmodel2.SsDomain;
import ss.framework.domainmodel2.StringConvertor;
import ss.global.SSLogger;
import ss.global.LoggerConfiguration;
import ss.server.domainmodel2.ServerDataProviderConnector;
import ss.util.VariousUtils;

/**
 * @author root
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SupraServer extends Thread {

	/**
	 * 
	 */
	private static final int UNUSABLE_PORT = -1;

	private static final int DEFAULT_PORT = 3000;

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger;

	
	public static void main(String[] args) {
		SSLogger.initialize(LoggerConfiguration.SERVER);
		logger = ss.global.SSLogger.getLogger(SupraServer.class);
		try {
			logger.warn("Starting SupraServer");
			ThreadUtils.initializeDefaultExceptionHandler();
			LocationUtils.init();
			SsDomain.initialize(new ServerDataProviderConnector());			
			final int port = args.length > 0 ? tryParsePort( args[ 0 ] ) : UNUSABLE_PORT;
			SupraServer server = new SupraServer( port );
			server.start();
		} catch (Throwable ex) {
			logger.fatal("Server main function failed", ex);
		}
	}

	private ServerSocket server = null;

	private final int serverPort;

	/** Creates new EstServer */
	public SupraServer() {
		this( UNUSABLE_PORT );
	}

	public SupraServer(int port) {
		if ( port < 0 ) {
			port = loadPortFromDynServer();
		}
		this.serverPort = port;
	}

	/**
	 * @return
	 */
	private static int tryParsePort( String portStr ) {
		return StringConvertor.stringToInt( portStr, UNUSABLE_PORT );
	}

	@SuppressWarnings("unused")
	private void setFileOutput() {
		long longnum = System.currentTimeMillis();
		String moment = (Long.toString(longnum));
		String name = "output." + moment;
		try {
			File outputFile = VariousUtils.getSupraFile(name);
			FileOutputStream fout = new FileOutputStream(outputFile);
			PrintStream ps = new PrintStream(fout);
			System.setErr(ps);
			System.setOut(ps);
		} catch (Exception ex) {
		}

	}

	@SuppressWarnings("unused")
	private void setFileOutput(String port) {
		long longnum = System.currentTimeMillis();
		String moment = (Long.toString(longnum));
		String name = "output." + moment + "." + port;
		try {
			File outputFile = VariousUtils.getSupraFile(name);
			FileOutputStream fout = new FileOutputStream(outputFile);
			PrintStream ps = new PrintStream(fout);
			System.setErr(ps);
			System.setOut(ps);
		} catch (Exception ex) {
		}
	}

	private static int loadPortFromDynServer() {
		final File file = VariousUtils.getSupraFile("dyn_server.xml");
		try {
			final Document doc = XmlDocumentUtils.load(file);
			return new Integer(doc.getRootElement().element("port")
					.attributeValue("value")).intValue();
		} catch (Exception e) {
			logger.error( "Can't load port from " + file + ". User port " + DEFAULT_PORT, e);
			return DEFAULT_PORT;
		}
	}

	public void run() {
		try {
			logger.warn( "Starting Server on port "  + this.serverPort );
			this.server = new ServerSocket(this.serverPort);
			logger.info( "SocketCreated" );
			do {
				Socket client = this.server.accept();
				client.setKeepAlive(true);
				// Create a new thread to handle each connection
				logger.info("Accepted connection");
				new ServerThread(client).start();
			} while (true);
		} catch (Throwable ex) {
			logger.fatal( "Server socket failed. Please see stack trace to details.", ex );
			logger.fatal( "System exit!" );
			System.exit( UNUSABLE_PORT );
		}
	}
}
