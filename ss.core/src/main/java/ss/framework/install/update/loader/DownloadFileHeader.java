/**
 * 
 */
package ss.framework.install.update.loader;

import java.io.Serializable;

/**
 *
 */
public final class DownloadFileHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -396243470867485864L;

	private final String remotePath;
	
	private final String hash;

	/**
	 * @param relativePath
	 * @param hash
	 */
	public DownloadFileHeader(final String relativePath, final String hash) {
		super();
		this.remotePath = relativePath;
		this.hash = hash;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}

	/**
	 * @return the relativePath
	 */
	public String getRemotePath() {
		return this.remotePath;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.remotePath + " #" + this.hash;
	}

}
