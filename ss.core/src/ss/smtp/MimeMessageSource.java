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
import java.io.IOException;
import java.io.InputStream;

/**
 * This defines a reusable datasource that can supply an input stream with
 * MimeMessage data. This allows a MimeMessageWrapper or other classes to grab
 * the underlying data.
 * 
 * @see MimeMessageWrapper
 */
public abstract class MimeMessageSource {
	/**
	 * Returns a unique String ID that represents the location from where this
	 * file is loaded. This will be used to identify where the data is,
	 * primarily to avoid situations where this data would get overwritten.
	 * 
	 * @return the String ID
	 */
	public abstract String getSourceId();

	/**
	 * Get an input stream to retrieve the data stored in the datasource
	 * 
	 * @return a <code>InputStream</code> containing the data
	 * 
	 * @throws IOException
	 *             if an error occurs while generating the InputStream
	 */
	public abstract InputStream getInputStream() throws IOException;

	/**
	 * Return the size of all the data. Default implementation... others can
	 * override to do this much faster
	 * 
	 * @return the size of the data represented by this source
	 * @throws IOException
	 *             if an error is encountered while computing the message size
	 */
	public long getMessageSize() throws IOException {
		int size = 0;
		InputStream in = null;
		try {
			in = getInputStream();
			int read = 0;
			byte[] data = new byte[1024];
			while ((read = in.read(data)) > 0) {
				size += read;
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ioe) {
				// Exception ignored because logging is
				// unavailable
			}
		}
		return size;
	}

}
