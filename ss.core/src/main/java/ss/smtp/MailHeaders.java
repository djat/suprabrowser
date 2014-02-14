/*
 * Created on Feb 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.smtp;

/**
 * @author david
 * 
 */
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import java.io.*;
import java.util.Enumeration;

/**
 * This interface defines a container for mail headers. Each header must use
 * MIME format:
 * 
 * <pre>
 * name: value
 * </pre>.
 * 
 */
public class MailHeaders extends InternetHeaders implements Serializable,
		Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3066453570705986622L;

	/**
	 * No argument constructor
	 * 
	 * @throws MessagingException
	 *             if the super class cannot be properly instantiated
	 */
	public MailHeaders() throws MessagingException {
		super();
	}

	/**
	 * Constructor that takes an InputStream containing the contents of the set
	 * of mail headers.
	 * 
	 * @param in
	 *            the InputStream containing the header data
	 * 
	 * @throws MessagingException
	 *             if the super class cannot be properly instantiated based on
	 *             the stream
	 */
	public MailHeaders(InputStream in) throws MessagingException {
		super(in);
	}

	/**
	 * Write the headers to an output stream
	 * 
	 * @param writer
	 *            the stream to which to write the headers
	 */
	public void writeTo(OutputStream out) {
		PrintStream pout;
		if (out instanceof PrintStream) {
			pout = (PrintStream) out;
		} else {
			pout = new PrintStream(out);
		}
		for (Enumeration e = super.getAllHeaderLines(); e.hasMoreElements();) {
			pout.print((String) e.nextElement());
			pout.print("\r\n");
		}
		// Print trailing CRLF
		pout.print("\r\n");
	}

	/**
	 * Generate a representation of the headers as a series of bytes.
	 * 
	 * @return the byte array containing the headers
	 */
	public byte[] toByteArray() {
		ByteArrayOutputStream headersBytes = new ByteArrayOutputStream();
		writeTo(headersBytes);
		return headersBytes.toByteArray();
	}

	/**
	 * Check if a particular header is present.
	 * 
	 * @return true if the header is present, false otherwise
	 */
	public boolean isSet(String name) {
		String[] value = super.getHeader(name);
		return (value != null && value.length != 0);
	}

	/**
	 * Check if all REQUIRED headers fields as specified in RFC 822 are present.
	 * 
	 * @return true if the headers are present, false otherwise
	 */
	public boolean isValid() {
		return (isSet(RFC2822Headers.DATE) && isSet(RFC2822Headers.TO) && isSet(RFC2822Headers.FROM));
	}
}
