/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 *
 */
public class ExternalEmailWithContactStatement extends ExternalEmailStatement {

    private final ISimpleEntityProperty contactMessageId = super
   		.createAttributeProperty( "contactMessageId/@value" );
    
    private final ISimpleEntityProperty contactSphereId = super
    	.createAttributeProperty( "contactSphereId/@value" );

    
	public ExternalEmailWithContactStatement() {
	}
	
    /**
     * Create message object that wraps xml
     */
    @SuppressWarnings("unchecked")
    public static ExternalEmailWithContactStatement wrap(org.dom4j.Document data) {
        return XmlEntityObject.wrap(data, ExternalEmailWithContactStatement.class);
    }

    /**
     * Create message object that wraps xml
     */
    @SuppressWarnings("unchecked")
    public static ExternalEmailWithContactStatement wrap(org.dom4j.Element data) {
        return XmlEntityObject.wrap(data, ExternalEmailWithContactStatement.class);
    }

	public String getContactMessageId() {
		return this.contactMessageId.getValue();
	}

	public void setContactMessageId( final String value ) {
		this.contactMessageId.setValue( value );
	}
	
	public String getContactSphereId() {
		return this.contactSphereId.getValue();
	}

	public void setContactSphereId( final String value ) {
		this.contactSphereId.setValue( value );
	}
}
