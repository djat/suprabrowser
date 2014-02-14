/**
 * 
 */
package ss.framework.install.update;

import java.io.InputStream;

/**
 *
 */
public class UpdateResult {

	private final InputStream installerLock;

	/**
	 * @param installerLock
	 */
	public UpdateResult(final InputStream installerLock) {
		super();
		this.installerLock = installerLock;
	}

	/**
	 * @return the shouldExit
	 */
	public boolean isShouldExit() {
		return true;
	}

	/**
	 * @return the installerLock
	 */
	public InputStream getInstallerLock() {
		return this.installerLock;
	}
	
}