/**
 * 
 */
package ss.framework.install.update.loader;

import java.io.Serializable;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public class DownloadFilesReply implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7217093553285007061L;

	private final boolean downloadAllowed;
	
	private final String message;

	/**
	 * @param downloadAllowed
	 * @param message
	 */
	private DownloadFilesReply(final boolean downloadAllowed, final String message) {
		super();
		this.downloadAllowed = downloadAllowed;
		this.message = message;
	}

	/**
	 * @return the downloadAllowed
	 */
	public boolean isDownloadAllowed() {
		return this.downloadAllowed;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param b
	 * @return
	 */
	public static DownloadFilesReply createSucceededReply() {
		return new DownloadFilesReply( true, null );
	}

	/**
	 * @param string
	 * @return
	 */
	public static DownloadFilesReply createFailedReply(String message) {
		if (message == null) {
			throw new ArgumentNullPointerException("message");
		}
		return new DownloadFilesReply( false, message );
	}

}
