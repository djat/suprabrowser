/**
 * 
 */
package ss.server.install.update;


import ss.common.FileUtils;
import ss.common.FolderUtils;
import ss.common.PathUtils;
import ss.framework.install.CantLoadInstallationDescriptionException;
import ss.framework.install.InstallationDescription;
import ss.framework.install.InstallationDescriptionManager;
import ss.framework.install.QualifiedVersion;

/**
 *
 */
public class ClientUpdateManager {

	/**
	 * 
	 */
	private static final String CLIENT_INSTALLATION_FOLDER = "client-installation";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientUpdateManager.class); 
	
	/**
	 * Singleton instance
	 */
	public final static ClientUpdateManager INSTANCE = new ClientUpdateManager();

	private ClientUpdateManager() {
	}
	
	public ClientUpdate create( QualifiedVersion clientVersion ) {
		final String serverApplicationFolder = FolderUtils.getApplicationFolder();
		if ( serverApplicationFolder == null ) {
			logger.error( "Server application folder is null");
			return null;
		}
		final String operationSystemFamily = clientVersion.getOperationSystem().getFamily().toString();
		final String clientInstallationFolder = PathUtils.combinePath( serverApplicationFolder, CLIENT_INSTALLATION_FOLDER, 
					operationSystemFamily );
		if ( !FileUtils.isFolderExist( clientInstallationFolder ) ) {
			logger.error( "Blank client installation folder not found: " + clientInstallationFolder );
			return null;
		}
		try {
			InstallationDescription blankInstallationDesctription = InstallationDescriptionManager.INSTANCE.loadFromApplicationFolder( clientInstallationFolder );
			return new ClientUpdate( blankInstallationDesctription, clientVersion );
		} catch (CantLoadInstallationDescriptionException ex) {
			logger.error( "Can't load blank installation description",  ex );
			return null;
		}
	}
	
}
