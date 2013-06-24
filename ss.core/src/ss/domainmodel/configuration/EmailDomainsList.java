/**
 * 
 */
package ss.domainmodel.configuration;

import ss.common.ArgumentNullPointerException;
import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author zobo
 *
 */
public class EmailDomainsList extends XmlListEntityObject<EmailDomain> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailDomainsList.class);
	
	/**
	 * @param itemType
	 */
	public EmailDomainsList() {
		super(EmailDomain.class, EmailDomain.ROOT_ELEMENT_NAME );
	}
	
	public void put( final EmailDomain domain ) {
		if ( domain == null ) {
			throw new ArgumentNullPointerException( "emailDomains" );
		}
		for (EmailDomain existedDomain : this){
			if (existedDomain.getDomain().equals(domain.getDomain())){
				logger.warn("Trying to set existed domain: " + domain.getDomain());
				return;
			}
		}
		super.internalAdd( domain );
	}
	
	public void remove( final EmailDomain domain ) {
		if ( domain == null ) {
			throw new ArgumentNullPointerException( "emailDomains" );
		}
		super.internalRemove( domain );
	}
}
