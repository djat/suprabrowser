/**
 * 
 */
package ss.server.install.update;

import ss.client.networking.NetworkConnection;
import ss.framework.install.QualifiedVersion;
import ss.framework.install.update.loader.AbstractFilesUploader;
import ss.framework.install.update.loader.IFilePathResolver;

/**
 *
 */
public class FilesUploader extends AbstractFilesUploader{

	/**
	 * @param connection
	 */
	private FilesUploader(NetworkConnection connection) {
		super(connection);
	}
	
	/**
	 * @param connection
	 */
	public static void createAndStart(NetworkConnection connection) {
		new FilesUploader( connection ).start();		
	}

	/* (non-Javadoc)
	 * @see ss.framework.install.update.loader.AbstractFilesUploader#findFilesResolver(ss.framework.install.QualifiedVersion)
	 */
	@Override
	protected IFilePathResolver findFilesResolver(QualifiedVersion applicationVersion) {
		ClientUpdate clientUpdate = ClientUpdateManager.INSTANCE.create(applicationVersion);
		return clientUpdate != null ? clientUpdate.createFilePathResolver() : null;
	}
	


}
