/**
 * 
 */
package ss.framework.install.update;

import ss.framework.install.QualifiedVersion;

/**
 *
 */
public interface IFilesDownloader {
	
	void initialize(QualifiedVersion targetApplicationVersion );
	
	/**
	 * @param destinationPath
	 * @param resourceName 
	 * @param resourceHash
	 */
	void addToQueue(String destinationPath, String resourceName, String resourceHash);
	
	/**
	 * 
	 */
	void downloadAll() throws CantUpdateApplicationException;
}
