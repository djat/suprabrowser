/*
 * Created on Feb 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.smtp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import ss.smtp.reciever.EmailProcessor;

/**
 * @author david
 * 
 */
public class ConnectionHandler extends Thread {

	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ConnectionHandler.class);

	private final static String CURRENT_HELO_MODE = "CURRENT_HELO_MODE"; // HELO

	// or
	// EHLO

	private final static String SENDER = "SENDER_ADDRESS"; // Sender's email

	// address

	private final static String MESG_FAILED = "MESG_FAILED"; // Message

	// failed flag

	private final static String MESG_SIZE = "MESG_SIZE"; // The size of the

	// message

	private final static String RCPT_LIST = "RCPT_LIST"; // The message

	private String smtpID;

	private final static RFC822DateFormat rfc822DateFormat = new RFC822DateFormat();

	/**
	 * The character array that indicates termination of an SMTP connection
	 */
	private final static char[] SMTPTerminator = { '\r', '\n', '.', '\r', '\n' };

	/**
	 * Static Random instance used to generate SMTP ids
	 */
	private final static Random random = new Random();

	/**
	 * Static RFC822DateFormat used to generate date headers
	 */
	// private final static RFC822DateFormat rfc822DateFormat = new
	// RFC822DateFormat();
	/**
	 * The text string for the SMTP HELO command.
	 */
	private final static String COMMAND_HELO = "HELO";

	/**
	 * The text string for the SMTP EHLO command.
	 */
	private final static String COMMAND_EHLO = "EHLO";

	/**
	 * The text string for the SMTP AUTH command.
	 */
	private final static String COMMAND_AUTH = "AUTH";

	/**
	 * The text string for the SMTP MAIL command.
	 */
	private final static String COMMAND_MAIL = "MAIL";

	/**
	 * The text string for the SMTP RCPT command.
	 */
	private final static String COMMAND_RCPT = "RCPT";

	/**
	 * The text string for the SMTP NOOP command.
	 */
	private final static String COMMAND_NOOP = "NOOP";

	/**
	 * The text string for the SMTP RSET command.
	 */
	private final static String COMMAND_RSET = "RSET";

	/**
	 * The text string for the SMTP DATA command.
	 */
	private final static String COMMAND_DATA = "DATA";

	/**
	 * The text string for the SMTP QUIT command.
	 */
	private final static String COMMAND_QUIT = "QUIT";

	/**
	 * The text string for the SMTP HELP command.
	 */
	private final static String COMMAND_HELP = "HELP";

	/**
	 * The text string for the SMTP VRFY command.
	 */
	private final static String COMMAND_VRFY = "VRFY";

	/**
	 * The text string for the SMTP EXPN command.
	 */
	private final static String COMMAND_EXPN = "EXPN";

	/**
	 * The text string for the SMTP AUTH type PLAIN.
	 */
	// private final static String AUTH_TYPE_PLAIN = "PLAIN";
	/**
	 * The text string for the SMTP AUTH type LOGIN.
	 */
	// private final static String AUTH_TYPE_LOGIN = "LOGIN";
	/**
	 * The text string for the SMTP MAIL command SIZE option.
	 */
	private final static String MAIL_OPTION_SIZE = "SIZE";

	/**
	 * The mail attribute holding the SMTP AUTH user name, if any.
	 */
	// private final static String SMTP_AUTH_USER_ATTRIBUTE_NAME =
	// "org.apache.james.SMTPAuthUser";
	private String helloName = "SUPRAMAIL";

	/**
	 * The thread executing this handler
	 */
	// private Thread handlerThread;
	/**
	 * The TCP/IP socket over which the SMTP dialogue is occurring.
	 */
	private Socket socket;

	/**
	 * The incoming stream of bytes coming from the socket.
	 */
	private InputStream in;

	/**
	 * The writer to which outgoing messages are written.
	 */
	private PrintWriter out;

	/**
	 * A Reader wrapper for the incoming stream of bytes coming from the socket.
	 */
	private BufferedReader inReader;

	/**
	 * The remote host name obtained by lookup on the socket.
	 */
	private String remoteHost;

	/**
	 * The remote IP address of the socket.
	 */
	private String remoteIP;

	private HashMap<String, Object> state = new HashMap<String, Object>();

	StringBuffer responseBuffer = new StringBuffer(256);

	public ConnectionHandler() {

	}

	public ConnectionHandler(Socket socket) {

		this.socket = socket;

	}

	public void run() {

		try {

			this.in = new BufferedInputStream(this.socket.getInputStream(),
					1024);
			// An ASCII encoding can be used because all transmissions other
			// that those in the DATA command are guaranteed
			// to be ASCII
			this.inReader = new BufferedReader(new InputStreamReader(this.in,
					"ASCII"), 512);

			this.remoteIP = this.socket.getInetAddress().getHostAddress();
			this.remoteHost = this.socket.getInetAddress().getHostName();
			this.smtpID = random.nextInt(1024) + "";

		} catch (Exception e) {
			StringBuffer exceptionBuffer = new StringBuffer(256).append(
					"Cannot open connection from ").append(this.remoteHost)
					.append(" (").append(this.remoteIP).append("): ").append(
							e.getMessage());
			String exceptionString = exceptionBuffer.toString();
			logger.error(exceptionString, e);
			throw new RuntimeException(exceptionString);
		}

		if (logger.isInfoEnabled()) {
			StringBuffer infoBuffer = new StringBuffer(128).append(
					"Connection from ").append(this.remoteHost).append(" (")
					.append(this.remoteIP).append(")");
			logger.info(infoBuffer.toString());
		}

		try {

			this.out = new InternetPrintWriter(
					new BufferedWriter(new OutputStreamWriter(this.socket
							.getOutputStream()), 1024), false);

			// Initially greet the connector
			// Format is: Sat, 24 Jan 1998 13:16:09 -0500

			this.responseBuffer.append("220 ").append("Hello").append(
					" SMTP Server (").append(this.helloName).append(") ready ")
					.append(rfc822DateFormat.format(new Date()));

			String responseString = clearResponseBuffer();
			writeLoggedFlushedResponse(responseString);

			while (parseCommand(readCommandLine())) {
				try {
					sleep(2000);

				} catch (InterruptedException exc) {

				}

			}
			logger.info("Closing socket.");
		} catch (SocketException se) {

			StringBuffer errorBuffer = new StringBuffer(64)
					.append("Socket to ").append(this.remoteHost).append(" (")
					.append(this.remoteIP).append(") closed remotely.");
			logger.error(errorBuffer.toString(), se);

		} catch (InterruptedIOException iioe) {
			if (logger.isDebugEnabled()) {
				StringBuffer errorBuffer = new StringBuffer(64).append(
						"Socket to ").append(this.remoteHost).append(" (")
						.append(this.remoteIP).append(") timeout.");
				logger.error(errorBuffer.toString(), iioe);
			}
		} catch (IOException ioe) {
			if (logger.isDebugEnabled()) {
				StringBuffer errorBuffer = new StringBuffer(256).append(
						"Exception handling socket to ")
						.append(this.remoteHost).append(" (").append(
								this.remoteIP).append(") : ").append(
								ioe.getMessage());
				logger.error(errorBuffer.toString(), ioe);
			}
		} catch (Exception e) {

			logger.error("Exception opening socket: " + e.getMessage(), e);

		} finally {
		}
	}

	final String readCommandLine() throws IOException {

		for (;;)
			try {
				String commandLine = this.inReader.readLine();
				if (commandLine != null) {
					commandLine = commandLine.trim();
				}
				logger.info("Read command : " + commandLine);
				return commandLine;
			} catch (Exception te) {

				writeLoggedFlushedResponse("501 Syntax error at character position "
						+ ". CR and LF must be CRLF paired.  See RFC 2821 #2.7.1.");
				return null;

			}

	}

	final void writeLoggedFlushedResponse(String responseString) {
		logger.info("Sending response string: " + responseString);
		this.out.println(responseString);
		this.out.flush();
		logResponseString(responseString);
	}

	private final void logResponseString(String responseString) {
		if (logger.isDebugEnabled()) {
			logger.info("Sent: " + responseString);
		}
	}

	private boolean parseCommand(String command) throws Exception {
		String argument = null;
		boolean returnValue = true;

		if (command == null) {
			return false;
		}
		if ((this.state.get(MESG_FAILED) == null) && (logger.isDebugEnabled())) {
			logger.info("Command received: " + command);
		}
		int spaceIndex = command.indexOf(" ");
		if (spaceIndex > 0) {
			argument = command.substring(spaceIndex + 1);
			command = command.substring(0, spaceIndex);
		}
		command = command.toUpperCase(Locale.US);
		if (command.equals(COMMAND_HELO)) {
			logger.info("command_helo");
			doHELO(argument);
		} else if (command.equals(COMMAND_EHLO)) {
			logger.info("command_ehlo");
			doEHLO(argument);
		} else if (command.equals(COMMAND_AUTH)) {
			logger.info("command_auth");
			// doAUTH(argument);
		} else if (command.equals(COMMAND_MAIL)) {
			logger.info("command_mail");
			doMAIL(argument);
		} else if (command.equals(COMMAND_RCPT)) {
			logger.info("command_rcpt");
			doRCPT(argument);
		} else if (command.equals(COMMAND_NOOP)) {
			logger.info("command_noop");
			// doNOOP(argument);
		} else if (command.equals(COMMAND_RSET)) {
			logger.info("command_rset");
			// doRSET(argument);
		} else if (command.equals(COMMAND_DATA)) {
			logger.info("command_data");
			doDATA(argument);
		} else if (command.equals(COMMAND_QUIT)) {
			logger.info("command_quit");
			// doQUIT(argument);
			returnValue = false;
		} else if (command.equals(COMMAND_VRFY)) {
			logger.info("command_vrfy");
			// doVRFY(argument);
		} else if (command.equals(COMMAND_EXPN)) {
			logger.info("command_expn");
			// doEXPN(argument);
		} else if (command.equals(COMMAND_HELP)) {
			// doHELP(argument);
		} else {
			if (this.state.get(MESG_FAILED) == null) {
				// doUnknownCmd(command, argument);
			}
		}
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	private void doRCPT(String recipientAddress) {

		Collection rcptColl = (Collection) this.state.get(RCPT_LIST);
		if (rcptColl == null) {
			rcptColl = new ArrayList();
		}
		rcptColl.add(recipientAddress);
		this.state.put(RCPT_LIST, rcptColl);
		this.responseBuffer.append("250 Recipient <").append(recipientAddress)
				.append("> OK");
		String responseString = clearResponseBuffer();
		writeLoggedFlushedResponse(responseString);
	}

	public void doDATA(String argument) {

		String responseString = null;
		if ((argument != null) && (argument.length() > 0)) {
			responseString = "500 Unexpected argument provided with DATA command";
			writeLoggedFlushedResponse(responseString);
		}
		if (!this.state.containsKey(SENDER)) {
			responseString = "503 No sender specified";
			writeLoggedFlushedResponse(responseString);
		} else {
			responseString = "354 Ok Send data ending with <CRLF>.<CRLF>";
			writeLoggedFlushedResponse(responseString);
			InputStream msgIn = new CharTerminatedInputStream(this.in,
					SMTPTerminator);
			try {
				msgIn = new BytesReadResetInputStream(msgIn, 10000);

				// Removes the dot stuffing
				msgIn = new DotStuffingInputStream(msgIn);

				// Parse out the message headers
				MailHeaders headers = new MailHeaders(msgIn);
				headers = processMailHeaders(headers);
				processMail(headers, msgIn);
				headers = null;
			} catch (MessagingException me) {
				logger.error("Unknown error occurred while processing DATA.",
						me);
				writeLoggedFlushedResponse(responseString);
				return;
			} finally {
				if (msgIn != null) {
					try {
						msgIn.close();
					} catch (Exception e) {
						// Ignore close exception
					}
					msgIn = null;
				}
			}

			responseString = "250 Message received";
			writeLoggedFlushedResponse(responseString);
		}

	}

	private void doMAIL(String argument) {
		String responseString = null;

		String sender = null;
		if ((argument != null) && (argument.indexOf(":") > 0)) {
			int colonIndex = argument.indexOf(":");
			sender = argument.substring(colonIndex + 1);
			argument = argument.substring(0, colonIndex);
		}
		if (this.state.containsKey(SENDER)) {
			responseString = "503 Sender already specified";
			writeLoggedFlushedResponse(responseString);
		} else if (argument == null
				|| !argument.toUpperCase(Locale.US).equals("FROM")
				|| sender == null) {
			responseString = "501 Usage: MAIL FROM:<sender>";
			writeLoggedFlushedResponse(responseString);
		} else {
			sender = sender.trim();
			// the next gt after the first lt ... AUTH may add more <>
			int lastChar = sender.indexOf('>', sender.indexOf('<'));
			// Check to see if any options are present and, if so, whether they
			// are correctly formatted
			// (separated from the closing angle bracket by a ' ').
			if ((lastChar > 0) && (sender.length() > lastChar + 2)
					&& (sender.charAt(lastChar + 1) == ' ')) {
				String mailOptionString = sender.substring(lastChar + 2);

				// Remove the options from the sender
				sender = sender.substring(0, lastChar + 1);

				StringTokenizer optionTokenizer = new StringTokenizer(
						mailOptionString, " ");
				while (optionTokenizer.hasMoreElements()) {
					String mailOption = optionTokenizer.nextToken();
					int equalIndex = mailOptionString.indexOf('=');
					String mailOptionName = mailOption;
					String mailOptionValue = "";
					if (equalIndex > 0) {
						mailOptionName = mailOption.substring(0, equalIndex)
								.toUpperCase(Locale.US);
						mailOptionValue = mailOption.substring(equalIndex + 1);
					}

					// Handle the SIZE extension keyword

					if (mailOptionName.startsWith(MAIL_OPTION_SIZE)) {
						if (!(doMailSize(mailOptionValue))) {
							return;
						}
					} else {
						// Unexpected option attached to the Mail command
						if (logger.isDebugEnabled()) {
							StringBuffer debugBuffer = new StringBuffer(128)
									.append(
											"MAIL command had unrecognized/unexpected option ")
									.append(mailOptionName).append(
											" with value ").append(
											mailOptionValue);
							logger.info(debugBuffer.toString());
						}
					}
				}
			}
			if (!sender.startsWith("<") || !sender.endsWith(">")) {
				responseString = "501 Syntax error in MAIL command";
				writeLoggedFlushedResponse(responseString);
				if (logger.isDebugEnabled()) {
					StringBuffer errorBuffer = new StringBuffer(128).append(
							"Error parsing sender address: ").append(sender)
							.append(": did not start and end with < >");
					logger.error(errorBuffer.toString());
				}
				return;
			}
			MailAddress senderAddress = null;
			// Remove < and >
			sender = sender.substring(1, sender.length() - 1);
			if (sender.length() == 0) {
				// This is the <> case. Let senderAddress == null
			} else {
				if (sender.indexOf("@") < 0) {
					sender = sender + "@localhost";
				}
				try {
					senderAddress = new MailAddress(sender);
				} catch (Exception pe) {
					responseString = "501 Syntax error in sender address";
					writeLoggedFlushedResponse(responseString);
					if (logger.isDebugEnabled()) {
						StringBuffer errorBuffer = new StringBuffer(256)
								.append("Error parsing sender address: ")
								.append(sender).append(": ").append(
										pe.getMessage());
						logger.error(errorBuffer.toString());
					}
					return;
				}
			}

			this.state.put(SENDER, senderAddress);
			this.responseBuffer.append("250 Sender <").append(sender).append(
					"> OK");
			responseString = clearResponseBuffer();
			writeLoggedFlushedResponse(responseString);
		}
	}

	private void doHELO(String argument) {
		String responseString = null;
		if (argument == null) {
			responseString = "501 Domain address required: " + COMMAND_HELO;
			writeLoggedFlushedResponse(responseString);
		} else {
			// resetState();
			this.state.put(CURRENT_HELO_MODE, COMMAND_HELO);

			this.responseBuffer.append("250 ");

			this.responseBuffer.append(this.helloName).append(" Hello ")
					.append(argument).append(" (").append(this.remoteHost)
					.append(" [").append(this.remoteIP).append("])");
			responseString = clearResponseBuffer();

			writeLoggedFlushedResponse(responseString);
		}
	}

	private void doEHLO(String argument) {
		String responseString = null;
		if (argument == null) {
			responseString = "501 Domain address required: " + COMMAND_EHLO;
			writeLoggedFlushedResponse(responseString);
		} else {
			// resetState();
			this.state.put(CURRENT_HELO_MODE, COMMAND_EHLO);
			// Extension defined in RFC 1870

			this.responseBuffer.append("250 ");

			this.responseBuffer.append(this.helloName).append(" Hello ")
					.append(argument).append(" (").append(this.remoteHost)
					.append(" [").append(this.remoteIP).append("])");
			responseString = clearResponseBuffer();

			writeLoggedFlushedResponse(responseString);
		}
	}

	private String clearResponseBuffer() {
		String responseString = this.responseBuffer.toString();
		this.responseBuffer.delete(0, this.responseBuffer.length());
		return responseString;
	}

	private boolean doMailSize(String mailOptionValue) {
		int size = 0;
		try {
			size = Integer.parseInt(mailOptionValue);
		} catch (NumberFormatException pe) {
			// This is a malformed option value. We return an error
			String responseString = "501 Syntactically incorrect value for SIZE parameter";
			writeLoggedFlushedResponse(responseString);
			logger
					.error("Rejected syntactically incorrect value for SIZE parameter.");
			return false;
		}
		if (logger.isDebugEnabled()) {
			StringBuffer debugBuffer = new StringBuffer(128).append(
					"MAIL command option SIZE received with value ").append(
					size).append(".");
			logger.info(debugBuffer.toString());
		}
		long maxMessageSize = -1;
		if ((maxMessageSize > 0) && (size > maxMessageSize)) {
			// Let the client know that the size limit has been hit.
			String responseString = "552 Message size exceeds fixed maximum message size";
			writeLoggedFlushedResponse(responseString);
			StringBuffer errorBuffer = new StringBuffer(256).append(
					"Rejected message from ").append(
					this.state.get(SENDER).toString()).append(" from host ")
					.append(this.remoteHost).append(" (").append(this.remoteIP)
					.append(") of size ").append(size).append(
							" exceeding system maximum message size of ")
					.append(maxMessageSize).append("based on SIZE option.");
			logger.error(errorBuffer.toString());
			return false;
		} else {
			// put the message size in the message state so it can be used
			// later to restrict messages for user quotas, etc.
			this.state.put(MESG_SIZE, new Integer(size));
		}
		return true;
	}

	private MailHeaders processMailHeaders(MailHeaders headers)
			throws MessagingException {

		// If headers do not contains minimum REQUIRED headers fields,
		// add them
		if (!headers.isSet(RFC2822Headers.DATE)) {
			headers.setHeader(RFC2822Headers.DATE, rfc822DateFormat
					.format(new Date()));
		}
		if (!headers.isSet(RFC2822Headers.FROM)
				&& this.state.get(SENDER) != null) {
			headers.setHeader(RFC2822Headers.FROM, this.state.get(SENDER)
					.toString());
		}

		// Determine the Return-Path
		String returnPath = headers.getHeader(RFC2822Headers.RETURN_PATH,
				"\r\n");
		headers.removeHeader(RFC2822Headers.RETURN_PATH);
		StringBuffer headerLineBuffer = new StringBuffer(512);
		if (returnPath == null) {
			if (this.state.get(SENDER) == null) {
				returnPath = "<>";
			} else {
				headerLineBuffer.append("<").append(this.state.get(SENDER))
						.append(">");
				returnPath = headerLineBuffer.toString();
				headerLineBuffer.delete(0, headerLineBuffer.length());
			}
		}

		// We will rebuild the header object to put Return-Path and our
		// Received header at the top
		Enumeration headerLines = headers.getAllHeaderLines();
		MailHeaders newHeaders = new MailHeaders();
		// Put the Return-Path first
		// JAMES-281 fix for messages that improperly have multiple
		// Return-Path headers
		StringTokenizer tokenizer = new StringTokenizer(returnPath, "\r\n");
		while (tokenizer.hasMoreTokens()) {
			String path = tokenizer.nextToken();
			newHeaders.addHeaderLine(RFC2822Headers.RETURN_PATH + ": " + path);
		}

		// Put our Received header next
		headerLineBuffer.append(RFC2822Headers.RECEIVED + ": from ").append(
				this.remoteHost).append(" ([").append(this.remoteIP).append(
				"])");

		newHeaders.addHeaderLine(headerLineBuffer.toString());
		headerLineBuffer.delete(0, headerLineBuffer.length());

		headerLineBuffer.append("          by ").append(this.helloName).append(
				" (").append("SupraSphere").append(") with SMTP ID ").append(
				this.smtpID);

		if (((Collection) this.state.get(RCPT_LIST)).size() == 1) {
			// Only indicate a recipient if they're the only recipient
			// (prevents email address harvesting and large headers in
			// bulk email)
			newHeaders.addHeaderLine(headerLineBuffer.toString());
			headerLineBuffer.delete(0, headerLineBuffer.length());
			headerLineBuffer.append("          for <").append(
					((List) this.state.get(RCPT_LIST)).get(0).toString())
					.append(">;");
			newHeaders.addHeaderLine(headerLineBuffer.toString());
			headerLineBuffer.delete(0, headerLineBuffer.length());
		} else {
			// Put the ; on the end of the 'by' line
			headerLineBuffer.append(";");
			newHeaders.addHeaderLine(headerLineBuffer.toString());
			headerLineBuffer.delete(0, headerLineBuffer.length());
		}
		headerLineBuffer = null;
		newHeaders.addHeaderLine("          "
				+ rfc822DateFormat.format(new Date()));

		// Add all the original message headers back in next
		while (headerLines.hasMoreElements()) {
			newHeaders.addHeaderLine((String) headerLines.nextElement());
		}
		return newHeaders;
	}

	private void processMail(MailHeaders headers, InputStream msgIn)
			throws MessagingException {
		ByteArrayInputStream headersIn = null;
		MailImpl mail = null;
		List recipientCollection = null;

		try {
			headersIn = new ByteArrayInputStream(headers.toByteArray());
			recipientCollection = (List) this.state.get(RCPT_LIST);
			mail = new MailImpl("SupraSphere", (MailAddress) this.state
					.get(SENDER), recipientCollection, new SequenceInputStream(
					headersIn, msgIn));

			// Call mail.getSize() to force the message to be
			// loaded. Need to do this to enforce the size limit

			// System.out.println("mail.getMessageSize():
			// "+mail.getMessageSize());
			try {
				new EmailProcessor(this.state).processEmail(mail);
			} catch (IOException e) {
				logger.error("email processing failed", e);
			}

			mail.setRemoteHost(this.remoteHost);
			mail.setRemoteAddr(this.remoteIP);
			// if (getUser() != null) {
			// mail.setAttribute(SMTP_AUTH_USER_ATTRIBUTE_NAME, getUser());
			// }
			// theConfigData.getMailServer().sendMail(mail);
			Collection theRecipients = mail.getRecipients();
			String recipientString = "";
			if (theRecipients != null) {
				recipientString = theRecipients.toString();
			}
			if (logger.isInfoEnabled()) {
				StringBuffer infoBuffer = new StringBuffer(256).append(
						"Successfully spooled mail ").append(mail.getName())
						.append(" from ").append(mail.getSender()).append(
								" for ").append(recipientString);
				logger.info(infoBuffer.toString());

			}
		} finally {
			if (recipientCollection != null) {
				recipientCollection.clear();
			}
			recipientCollection = null;
			if (mail != null) {
				mail.dispose();
			}
			mail = null;
			if (headersIn != null) {
				try {
					headersIn.close();
				} catch (IOException ioe) {
					// Ignore exception on close.
				}
			}
			headersIn = null;
		}
	}

}
