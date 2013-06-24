/**
 * 
 */
package ss.framework.install.update;

import java.io.IOException;

import ss.client.networking2.ClientProtocolManager;
import ss.common.ArgumentNullPointerException;
import ss.common.FolderUtils;
import ss.common.ThreadUtils;
import ss.common.debug.DebugUtils;
import ss.framework.install.CantLoadInstallationDescriptionException;
import ss.framework.install.CantSaveInstallationDescriptionException;
import ss.framework.install.InstallationDescription;
import ss.framework.install.InstallationDescriptionManager;
import ss.framework.install.QualifiedVersion;
import ss.framework.networking2.CommandExecuteException;
import ss.framework.networking2.Protocol;

/**
 *
 */
public final class ApplicationUpdater {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ApplicationUpdater.class);

	private final InstallationDescriptionManager installationDescriptionManager = InstallationDescriptionManager.INSTANCE;
		
	private final String applicationFolder;
	
	private final UpdateProcessListener updateProcessListener; 
	
	private final IUpdateProtocolFactory protocolFactory;
	
	private final IFilesDownloader filesDownloader;
	
	private boolean actualClientCanWorkWithServer = false;
	
	/**
	 * @param updateResultListener
	 * @param protocolFactory
	 * @param filesDownloader
	 */
	public ApplicationUpdater(UpdateProcessListener updateProcessListener, IUpdateProtocolFactory protocolFactory, IFilesDownloader filesDownloader) {
		super();
		if (updateProcessListener == null) {
			throw new ArgumentNullPointerException("updateProcessListener");
		}
		if (protocolFactory == null) {
			throw new ArgumentNullPointerException("protocolFactory");
		}
		if (filesDownloader == null) {
			throw new ArgumentNullPointerException("filesDownloader");
		}
		this.updateProcessListener = updateProcessListener;
		this.protocolFactory = protocolFactory;
		this.filesDownloader = filesDownloader;
		this.applicationFolder = FolderUtils.getApplicationFolder();	
		if ( this.applicationFolder == null ) {
			throw new NullPointerException( "Application folder is null" );
		}
		
	}

	public void start() {
		ThreadUtils.start( new Runnable() {
			public void run() {
				safeUpdate();
			} 
		}, getClass() );
	}
	
	void safeUpdate() {
		try {
			update();
		}
		catch( CantCreateUpdateProtocolException ex ) {
			logger.error("Cant update application. Continue normal run", ex);
			continueApplictionIsUpToDate();
		}
		catch( CantUpdateApplicationException ex ) {
			final String message = DebugUtils.toSignificantMessage( ex );
			logger.error( message, ex);
			cantUpdate( message );
		}
		catch( Throwable ex ) {
			final String message = "Unexpected update error: " + DebugUtils.toSignificantMessage( ex );
			logger.error(message, ex);
			cantUpdate( message );
		}
	}
	/**
	 * @param runnable
	 * @throws IOException 
	 * @throws CommandExecuteException 
	 * @throws CantUpdateApplicationException 
	 */
	void update() throws CantUpdateApplicationException {
		final InstallationDescription localInstallationDescription = getLocalInstallationDescription();
		final UpdateResponse serverResponse = getServerResponse( localInstallationDescription.getApplicationVersionObj() );
		this.actualClientCanWorkWithServer = serverResponse.isActualClientCanWorkWithServer(); 
		if ( serverResponse.getInstallationStatus() == InstallationStatus.UP_TO_DATE ) {
			continueApplictionIsUpToDate();
		}
		else if ( serverResponse.getInstallationStatus() == InstallationStatus.OUT_OF_DATE ) {
			runUpdate( localInstallationDescription, serverResponse.getInstallationDescription() );
		}
		else if ( serverResponse.getInstallationStatus() == InstallationStatus.UNKNOWN ) {
			logger.warn( "Cant update application server response is unknown. Cause: " + serverResponse.getMessage()  );
			if ( serverResponse.isActualClientCanWorkWithServer() ) {
				continueApplictionIsUpToDate();
			}
			else {
				cantUpdate( serverResponse.getMessage() );
			}
		}
		else {
			cantUpdate( serverResponse.getMessage() );
		}
	}

	/**
	 * @param message
	 */
	private void cantUpdate(String message) {
		try {
			this.updateProcessListener.cantUpdate(message, this.actualClientCanWorkWithServer );
		}
		catch( Throwable ex ) {
			logger.error( "Can't notify about update fails: " + message, ex );
		}
	}

	/**
	 * @param message
	 * @param installationDescription
	 * @param description 
	 * @throws CantUpdateApplicationException 
	 */
	private void runUpdate(InstallationDescription current, InstallationDescription target) throws CantUpdateApplicationException {
		if ( !askUserToProceed() ) {
			this.cantUpdate( "You can't login with old application version." );
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug( "Create update builder from " + current.getApplicationVersionObj() + " to " + target.getApplicationVersionObj() );
			}
			final UpdateBuilder builder = new UpdateBuilder( current, target );
			final Update update = builder.getResult();
			if (logger.isDebugEnabled()) {
				logger.debug( "Update created " + update );
			}
			UpdateResult result = update.perform( this.filesDownloader );
			this.updateProcessListener.updated( result );			
		}
	}

	/**
	 * @return
	 */
	private boolean askUserToProceed() {
		return this.updateProcessListener.askUserToProceedUpdate();		
	}

	/**
	 * 
	 */
	private void continueApplictionIsUpToDate() {
		this.updateProcessListener.applicationIsUpToDate();
	}

	/**
	 * @param applicationVersionObj
	 * @return
	 */
	private UpdateResponse getServerResponse( QualifiedVersion applicationVersionObj) throws CantUpdateApplicationException {
		final Protocol protocol = this.protocolFactory.create();
		try {
			protocol.start( ClientProtocolManager.INSTANCE );
			final UpdateHelloCommand helloCommand = new UpdateHelloCommand( applicationVersionObj ); 
			final UpdateResponse response = helloCommand.execute( protocol, UpdateResponse.class );
			if ( response == null ) {
				throw new CantUpdateApplicationException( "Update response is null" );
			}
			return response;
		} catch (CommandExecuteException ex) {
			throw new CantUpdateApplicationException( "Can't fetch update response",  ex );
		}
		finally {
			protocol.beginClose();
		}
	}

	/**
	 * 
	 */
	private InstallationDescription getLocalInstallationDescription() {
		if ( this.installationDescriptionManager.hasInstallationDescription(this.applicationFolder)) {
			try {
				final InstallationDescription description = this.installationDescriptionManager.loadFromApplicationFolder( this.applicationFolder );
				description.verifyAndFixOsName();
				return description;
			} catch (CantLoadInstallationDescriptionException ex) {
				logger.error( "Can't load application description. Try to generate it",  ex );
			}
		}
		else {
			logger.warn( "Can' find application description. Try to generate it" );
		}
		final InstallationDescription blank = this.installationDescriptionManager.createBlank();
		try {
			this.installationDescriptionManager.saveToApplicationFolder(this.applicationFolder, blank);
		} catch (CantSaveInstallationDescriptionException ex) {
			logger.error( "Can't save application installation description",  ex );
		}
		return blank;
	}
	
	
	
	
}
