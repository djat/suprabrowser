/**
 * 
 */
package ss.framework.install.update;

import java.io.Serializable;


import ss.common.XmlDocumentUtils;
import ss.framework.install.InstallationDescription;

/**
 *
 */
public class UpdateResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5545056335336078211L;

	private final String installationDescription;

	private final InstallationStatus installationStatus;
		
	private final boolean actualClientCanWorkWithServer;
	
	private final String message;


	/**
	 * @param installationDescription
	 * @param installationStatus
	 * @param actualClientCanWorkWithServer
	 * @param message
	 */
	private UpdateResponse(final String installationDescription, final InstallationStatus installationStatus, final boolean actualClientCanWorkWithServer, final String message) {
		super();
		this.installationDescription = installationDescription;
		this.installationStatus = installationStatus;
		this.actualClientCanWorkWithServer = actualClientCanWorkWithServer;
		this.message = message;
	}
	
	public static UpdateResponse formatUpToDate() {
		return formatUpToDate( null );
	}
	
	/**
	 * @param message2
	 */
	public static UpdateResponse formatUpToDate(String message) {
		return new UpdateResponse( null, InstallationStatus.UP_TO_DATE, true, message );
	}

	public static UpdateResponse formatOutOfDate( InstallationDescription description, boolean actualClientCanWorkWithServer ) {
		return new UpdateResponse( XmlDocumentUtils.toPrettyString( description.getDocumentCopy() ), InstallationStatus.OUT_OF_DATE, actualClientCanWorkWithServer, null );
	}
	
	public static UpdateResponse formatUnusable( String message ) {
		return new UpdateResponse( null, InstallationStatus.UNUSABLE, false, message );
	}
	
	public static UpdateResponse formatUnknown(String message ) {
		return new UpdateResponse( null, InstallationStatus.UNKNOWN, true, message );
	}
	
	/**
	 * @return the installationDescription
	 */
	public InstallationDescription getInstallationDescription() {
		return InstallationDescription.wrap( XmlDocumentUtils.parse( this.installationDescription ) );
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the versionStatus
	 */
	public InstallationStatus getInstallationStatus() {
		return this.installationStatus;
	}

	/**
	 * @return the actualClientCanWorkWithServer
	 */
	public boolean isActualClientCanWorkWithServer() {
		return this.actualClientCanWorkWithServer;
	}

	

	
	
}
