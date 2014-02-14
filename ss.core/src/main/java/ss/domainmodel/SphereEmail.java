package ss.domainmodel;

import java.io.Serializable;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

@SuppressWarnings("serial")
public class SphereEmail extends XmlEntityObject implements Serializable{

	
	private final ISimpleEntityProperty sphereId = super
			.createAttributeProperty("@sphere-id");

	private final ISimpleEntityProperty emailNames = super
		.createAttributeProperty("@email-name");
	
	private final ISimpleEntityProperty enabled = super
			.createAttributeProperty("@enabled");

    private final ISimpleEntityProperty messageIdAdd = super
        .createAttributeProperty("@message_id_add");

	/**
	 * Gets the sphereId
	 */
	public final String getSphereId() {
		return this.sphereId.getValue();
	}

	/**
	 * Sets the sphereId
	 */
	public final void setSphereId(String value) {
		this.sphereId.setValue(value);
	}
	
	

	/**
	 * Gets the emailName
	 */
	public final SpherePossibleEmailsSet getEmailNames() {
        if (this.emailNames.getValue() == null)
            return null;
		return new SpherePossibleEmailsSet(this.emailNames.getValue());
	}

	/**
	 * Sets the emailName
	 */
	public final void setEmailNames(SpherePossibleEmailsSet value) {
		this.emailNames.setValue(value.getSingleStringEmails());
	}

	/**
	 * Sets the emailName
	 */
	public final void setEmailNames(String emails) {
		setEmailNames( new SpherePossibleEmailsSet( emails ) );
	}
	
	/**
	 * Gets the enabled
	 */
	public final boolean getEnabled() {
		return this.enabled.getBooleanValue( true );
	}

	/**
	 * Sets the enabled
	 */
	public final void setEnabled(boolean value) {
		this.enabled.setBooleanValue(value);
	} 
    
    /**
     * Gets the enabled
     */
    public final boolean getIsMessageIdAdd() {
        return this.messageIdAdd.getBooleanValue( true );
    }

    /**
     * Sets the enabled
     */
    public final void setIsMessageIdAdd(boolean value) {
        this.messageIdAdd.setBooleanValue(value);
    } 
    
    //private get
}
