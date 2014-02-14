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
import java.io.*;
import javax.mail.MessagingException;

/**
 * Takes an input stream and creates a repeatable input stream source for a
 * MimeMessageWrapper. It does this by completely reading the input stream and
 * saving that to a temporary file that should delete on exit, or when this
 * object is GC'd.
 * 
 * @see MimeMessageWrapper
 * 
 * 
 */
public class MimeMessageInputStreamSource extends MimeMessageSource {

	/**
	 * A temporary file used to hold the message stream
	 */
	File file = null;

	/**
	 * The full path of the temporary file
	 */
	String sourceId = null;

	/**
	 * Construct a new MimeMessageInputStreamSource from an
	 * <code>InputStream</code> that contains the bytes of a MimeMessage.
	 * 
	 * @param key
	 *            the prefix for the name of the temp file
	 * @param in
	 *            the stream containing the MimeMessage
	 * 
	 * @throws MessagingException
	 *             if an error occurs while trying to store the stream
	 */
	public MimeMessageInputStreamSource(String key, InputStream in)
			throws MessagingException {
		// We want to immediately read this into a temporary file
		// Create a temp file and channel the input stream into it
		OutputStream fout = null;
		try {
			this.file = File.createTempFile(key, ".m64");
			fout = new BufferedOutputStream(new FileOutputStream(this.file));
			int b = -1;
			while ((b = in.read()) != -1) {
				fout.write(b);
			}
			fout.flush();

			this.sourceId = this.file.getCanonicalPath();
		} catch (IOException ioe) {
			throw new MessagingException("Unable to retrieve the data: "
					+ ioe.getMessage(), ioe);
		} finally {
			try {
				if (fout != null) {
					fout.close();
				}
			} catch (IOException ioe) {
				// Ignored - logging unavailable to log this non-fatal error.
			}

			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ioe) {
				// Ignored - logging unavailable to log this non-fatal error.
			}
		}
	}

	/**
	 * Returns the unique identifier of this input stream source
	 * 
	 * @return the unique identifier for this MimeMessageInputStreamSource
	 */
	public String getSourceId() {
		return this.sourceId;
	}

	/**
	 * Get an input stream to retrieve the data stored in the temporary file
	 * 
	 * @return a <code>BufferedInputStream</code> containing the data
	 */
	public synchronized InputStream getInputStream() throws IOException {
		return new BufferedInputStream(new FileInputStream(this.file));
	}

	/**
	 * Get the size of the temp file
	 * 
	 * @return the size of the temp file
	 * 
	 * @throws IOException
	 *             if an error is encoutered while computing the size of the
	 *             message
	 */
	public long getMessageSize() throws IOException {
		return this.file.length();
	}

	/**
	 * @see org.apache.avalon.framework.activity.Disposable#dispose()
	 */
	public void dispose() {
		try {
			if (this.file != null && this.file.exists()) {
				this.file.delete();
			}
		} catch (Exception e) {
			// ignore
		}
		this.file = null;
	}

	/**
	 * <p>
	 * Finalizer that closes and deletes the temp file. Very bad.
	 * </p>
	 * We're leaving this in temporarily, while also establishing a more formal
	 * mechanism for cleanup through use of the dispose() method.
	 * 
	 */
	public void finalize() {
		dispose();
	}
}
