/**
 * 
 */
package ss.lab.dm3.connection.service.backend;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.blob.backend.IBlobManagerBackEnd;
import ss.lab.dm3.events.backend.IEventManagerBackEnd;
import ss.lab.dm3.persist.backend.IDataManagerBackEnd;
import ss.lab.dm3.security2.Authentication;
import ss.lab.dm3.security2.backend.ISecurityManagerBackEnd;
import ss.lab.dm3.utils.SimpleRuntimeIdGenerator;

/**
 * @author Dmitry Goncharov
 */
public class BackEndContext {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(getClass());
	
	private final static SimpleRuntimeIdGenerator ID_GENERATOR = new SimpleRuntimeIdGenerator( "BackEnd" );
	
	private final String id;
		
	private final BackEndFeatures features;
	
	private final Authentication authentication;
	

	/**
	 * @param eventManagerBackEnd
	 * @param securityManagerBackEnd
	 * @param dataManagerBackEnd
	 */
	public BackEndContext(BackEndFeatures backEndFeatures,
			Authentication authentication) {
		this( ID_GENERATOR.qualifiedNextId(), backEndFeatures, authentication );
	}
	
	/**
	 * @param eventManagerBackEnd
	 * @param securityManagerBackEnd
	 * @param dataManagerBackEnd
	 */
	public BackEndContext(String id, BackEndFeatures backEndFeatures, Authentication authentication) {
		super();
		this.id = id;
		this.features = backEndFeatures;
		this.authentication = authentication;
	}
	

	/**
	 * @return
	 */
	public IDataManagerBackEnd getDataManagerBackEnd() {
		return this.features.getDataManagerBackEnd();
	}

	/**
	 * @return
	 */
	public IEventManagerBackEnd getEventManagerBackEnd() {
		return this.features.getEventManagerBackEnd();
	}


	/**
	 * @return
	 */
	public IBlobManagerBackEnd getBlobManagerBackEnd() {
		return this.features.getBlobManagerBackEnd();
	}
	
	/**
	 * @return
	 */
	public ISecurityManagerBackEnd getSecurityManagerBackEnd() {
		return this.features.getSecurityManagerBackEnd();
	}

	/**
	 * @return the authentication
	 */
	public Authentication getAuthentication() {
		return this.authentication;
	}

	/**
	 * @return
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 
	 */
	public void dispose() {
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Backend context diposed " + this );
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "id", this.id );
		return tsb.toString();
	}


	
}
