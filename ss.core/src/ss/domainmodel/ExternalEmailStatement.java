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
public class ExternalEmailStatement extends Statement {

    public static final String BASE_ELEMENT = "email";
    
    private final ISimpleEntityProperty mailingthread = super
    .createAttributeProperty( "mailingthread/@value" );    
    
    private final ISimpleEntityProperty reciever = super
    .createAttributeProperty( "reciever/@value" );
    
    private final ISimpleEntityProperty ccrecievers = super
    .createAttributeProperty( "cc/@value" );
    
    private final ISimpleEntityProperty bccrecievers = super
    .createAttributeProperty( "bcc/@value" );
    
    private final ISimpleEntityProperty body = super
    .createTextProperty( "body" );
    
    private final ISimpleEntityProperty status = super
    .createAttributeProperty( "status/@value" );
    
    private final ISimpleEntityProperty input = super
    .createAttributeProperty( "input/@value" );
    
    private final ISimpleEntityProperty emailmessageId = super
    .createAttributeProperty( "emailmessageId/@value" );
    
    public ExternalEmailStatement() {
        super(BASE_ELEMENT);
        
    }

    /**
     * Create message object that wraps xml
     */
    @SuppressWarnings("unchecked")
    public static ExternalEmailStatement wrap(org.dom4j.Document data) {
        return XmlEntityObject.wrap(data, ExternalEmailStatement.class);
    }

    /**
     * Create message object that wraps xml
     */
    @SuppressWarnings("unchecked")
    public static ExternalEmailStatement wrap(org.dom4j.Element data) {
        return XmlEntityObject.wrap(data, ExternalEmailStatement.class);
    }

    public String getBody() {
        return this.body.getValue();
    }

    public void setBody(String value) {
        this.body.setValue(value);
    }

    public String getReciever() {
        return this.reciever.getValue();
    }

    public void setReciever(String value) {
        this.reciever.setValue(value);
    }

    /**
     * @return the input
     */
    public boolean isInput() {
        return this.input.getBooleanValue();
    }

    /**
     * @param input the input to set
     */
    public void setInput(boolean input) {
        this.input.setBooleanValue(input);
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return this.status.getValue();
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status.setValue(status);
    }

    /**
     * @return the bccrecievers
     */
    public String getBccrecievers() {
        return this.bccrecievers.getValue();
    }

    /**
     * @param bccrecievers the bccrecievers to set
     */
    public void setBccrecievers(String bccrecievers) {
        this.bccrecievers.setValue(bccrecievers);
    }

    /**
     * @return the ccrecievers
     */
    public String getCcrecievers() {
        return this.ccrecievers.getValue();
    }

    /**
     * @param ccrecievers the ccrecievers to set
     */
    public void setCcrecievers(String ccrecievers) {
        this.ccrecievers.setValue(ccrecievers);
    }

	public String getEmailmessageId() {
		return this.emailmessageId.getValue();
	}

	public void setEmailmessageId(final String emailmessageId) {
		this.emailmessageId.setValue( emailmessageId );
	}

	public boolean isMailingThread() {
		return this.mailingthread.getBooleanValue( false );
	}
	
	public void setMailingThread( final boolean value ) {
		this.mailingthread.setBooleanValue( value );
	}
}