/**
 * 
 */
package ss.server.install.update;

import ss.framework.install.InstallationDescription;
import ss.framework.install.QualifiedVersion;
import ss.framework.install.update.UpdateResponse;
import ss.framework.install.update.loader.IFilePathResolver;

/**
 *
 */
public class ClientUpdate {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientUpdate.class);
	
	private final InstallationDescription blankInstallationDescription;

	private final QualifiedVersion sourceClientVersion;

	/**
	 * @param blankInstallationDescription
	 * @param sourceClientVersion
	 */
	public ClientUpdate(final InstallationDescription blankInstallationDescription, final QualifiedVersion sourceClientVersion) {
		super();
		this.blankInstallationDescription = blankInstallationDescription;
		this.sourceClientVersion = sourceClientVersion;
	}

	/**
	 * @return
	 */
	public IFilePathResolver createFilePathResolver() {
		return new FilePathResolver( this.blankInstallationDescription.getRootEntry().getLocalBase() );
	}

	/**
	 * @return
	 */
	public UpdateResponse createResponse() {
		final QualifiedVersion blankVersion = this.blankInstallationDescription.getApplicationVersionObj();
		final int result = this.sourceClientVersion.compareTo( blankVersion );
		if ( result == 0 ) {
			return UpdateResponse.formatUpToDate();
		}
		else if ( result > 0 ) {
			final String message = "Client has newest version. Client " + this.sourceClientVersion + ", blank " + blankVersion;
			logger.warn( message );
			return UpdateResponse.formatUpToDate( message );			
		}
		else {
			//FIXME cleaver way to select actualClientCanWorkWithServer 
			return UpdateResponse.formatOutOfDate(this.blankInstallationDescription, true );
		}
	}
	
	

}
