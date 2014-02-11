/**
 * 
 */
package ss.lab.dm3.connection.service.backend;

import ss.lab.dm3.blob.backend.BlobManagerBackEnd;
import ss.lab.dm3.blob.backend.IBlobManagerBackEnd;
import ss.lab.dm3.connection.configuration.Configuration;
import ss.lab.dm3.events.backend.EventManagerBackEnd;
import ss.lab.dm3.events.backend.IEventManagerBackEnd;
import ss.lab.dm3.persist.backend.DataManagerBackEnd;
import ss.lab.dm3.persist.backend.IDataManagerBackEnd;
import ss.lab.dm3.security2.backend.ISecurityManagerBackEnd;
import ss.lab.dm3.security2.backend.SecurityManagerBackEnd;
import ss.lab.dm3.security2.backend.configuration.SecurityConfiguration;

/**
 * @author Dmitry Goncharov
 */
public class BackEndFeatures {

	private final Configuration configuration;
	
	private IEventManagerBackEnd eventManagerBackEnd;

	private ISecurityManagerBackEnd securityManagerBackEnd;

	private IDataManagerBackEnd dataManagerBackEnd;

	private IBlobManagerBackEnd blobManagerBackEnd;

	/**
	 * @param configuation
	 */
	public BackEndFeatures(Configuration configuation) {
		this.configuration = configuation;
	}

	/**
	 * 
	 */
	public void initialize() {
		this.setEventManagerBackEnd(new EventManagerBackEnd());
		final SecurityConfiguration securityConfiguration = new SecurityConfiguration();
		securityConfiguration.setDbUrl( this.configuration.getDbUrl() );
		securityConfiguration.setDbUser( this.configuration.getDbUser() );
		securityConfiguration.setDbPassword( this.configuration.getDbPassword() );
		final SecurityManagerBackEnd securityManagerBackEnd = new SecurityManagerBackEnd( securityConfiguration );
		this.setSecurityManagerBackEnd(securityManagerBackEnd);
		final DataManagerBackEnd dataManagerBackEnd = new DataManagerBackEnd( this.configuration,
			this.getEventManagerBackEnd());
		this.setDataManagerBackEnd(dataManagerBackEnd);
		this.setBlobManagerBackEnd( new BlobManagerBackEnd( this.configuration.getBlobConfiguration(), this.dataManagerBackEnd.getBlobInformationProvider() ) );
	}
	
	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	public IEventManagerBackEnd getEventManagerBackEnd() {
		return this.eventManagerBackEnd;
	}

	public ISecurityManagerBackEnd getSecurityManagerBackEnd() {
		return this.securityManagerBackEnd;
	}

	public IDataManagerBackEnd getDataManagerBackEnd() {
		return this.dataManagerBackEnd;
	}

	public IBlobManagerBackEnd getBlobManagerBackEnd() {
		return this.blobManagerBackEnd;
	}

	public void setEventManagerBackEnd(IEventManagerBackEnd eventManagerBackEnd) {
		this.eventManagerBackEnd = eventManagerBackEnd;
	}

	public void setSecurityManagerBackEnd(ISecurityManagerBackEnd securityManagerBackEnd) {
		this.securityManagerBackEnd = securityManagerBackEnd;
	}

	public void setDataManagerBackEnd(IDataManagerBackEnd dataManagerBackEnd) {
		this.dataManagerBackEnd = dataManagerBackEnd;
	}

	public void setBlobManagerBackEnd(IBlobManagerBackEnd blobManagerBackEnd) {
		this.blobManagerBackEnd = blobManagerBackEnd;
	}

}
