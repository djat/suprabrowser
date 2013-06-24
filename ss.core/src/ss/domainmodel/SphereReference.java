package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SphereReference extends XmlEntityObject {

	private static final long serialVersionUID = 1L;
    /**
	 * 
	 */

	private final ISimpleEntityProperty systemName = super
    .createAttributeProperty("@system_name");

    private final ISimpleEntityProperty displayName = super
    .createAttributeProperty("@display_name");
    
    private final ISimpleEntityProperty sphere_type = super
    .createAttributeProperty("@sphere_type");

    /**
     * Create SphereDefinition object that wraps xml
     */
    @SuppressWarnings("unchecked")
    public static SphereReference wrap(org.dom4j.Document data) {
        return XmlEntityObject.wrap(data, SphereReference.class);
    }

    /**
     * Create SphereDefinition object that wraps xml
     */
    @SuppressWarnings("unchecked")
    public static SphereReference wrap(org.dom4j.Element data) {
        return XmlEntityObject.wrap(data, SphereReference.class);
    }
    
    /**
     * Gets system name
     */
    public final String getSystemName() {
        return this.systemName.getValue();
    }
    
    /**
     * Gets system name
     */
    public final String getDisplayName() {
        return this.displayName.getValueOrEmpty();
    }
    
    private String getSphereType() {
    	return this.sphere_type.getValue();
    }
    
    public boolean isMember() {
    	return getSphereType().equals("member");
    }

	/**
	 * @return
	 */
	public boolean isEmailBox() {
		return SphereStatement.isDisplayNameLikeEmailBox( getDisplayName() );
	}

	/**
	 * @param value
	 */
	public void setDisplayName(String value) {
		this.displayName.setValue(value);
	}
	
	/**
     * Gets system name
     */
    public final void setSystemName(final String systemName) {
        this.systemName.setValue(systemName);
    }
   
}
