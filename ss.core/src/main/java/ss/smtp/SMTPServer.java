/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.smtp;

import java.net.ServerSocket;
import java.net.Socket;

import ss.common.ThreadUtils;
import ss.common.domainmodel2.SsDomain;
import ss.global.LoggerConfiguration;
import ss.global.SSLogger;

public class SMTPServer extends Thread {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger;

	StringBuffer responseBuffer = new StringBuffer(256);

	public static void main(String[] args) {
		SSLogger.initialize(LoggerConfiguration.SMTP);
		logger = ss.global.SSLogger.getLogger(SMTPServer.class);
		try {
			ThreadUtils.initializeDefaultExceptionHandler();
			SsDomain.initialize( new SmtpDataProviderConnector() );
			SMTPServer smtpServer = new SMTPServer();
			logger.warn("starting smtp");
			smtpServer.initializeLogger();
			smtpServer.openSocket(25);
		}
		catch(Throwable ex) {
			logger.fatal( "Smpt Server stopped", ex );
		}

	}

	public void initializeLogger() {
	}

	public void openSocket(int serverPort) {

		try {
			ServerSocket server = new ServerSocket(serverPort);
			do {
				Socket connection = server.accept();
				// Create a new thread to handle each connection
				logger.info("starting new mail transporting thread");
				// handleConnection(connection);
				ConnectionHandler handler = new ConnectionHandler(connection);
				handler.start();
			} while (true);
		} catch (Throwable ex) {
			logger.error("Smpt Server stopped", ex );			
			System.exit( -1 );
		}

	}

	public void run() {

	}

}
