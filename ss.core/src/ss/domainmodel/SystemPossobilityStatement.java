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
public class SystemPossobilityStatement extends Statement {
	
	private final ISimpleEntityProperty sysmessage = super
    .createAttributeProperty( "systemmessage/@value" );
	
	private final ISimpleEntityProperty systype = super
    .createAttributeProperty( "systemtype/@value" );
	
	public static String SYSTEM_TYPE_INFO = "info";
	
	public static String SYSTEM_TYPE_WARNING = "warning";
	
	public static String SYSTEM_TYPE_ERROR = "error";

	public SystemPossobilityStatement(String desiredRootElementName) {
		super(desiredRootElementName);
	}
	
	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static Statement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SystemPossobilityStatement.class);
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static Statement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SystemPossobilityStatement.class);
	}
	
	   /**
     * @return the sysmessage
     */
    public boolean isServerSystemMessage() {
        return this.sysmessage.getBooleanValue();
    }

    /**
     * @param input the sysmessage to set
     */
    public void setServerSystemMessage(boolean sysmessage) {
        this.sysmessage.setBooleanValue(sysmessage);
    }
    
    /**
     * @return the sysmessage
     */
    public String getSystemType() {
        return this.systype.getValue();
    }

    /**
     * @param input the sysmessage to set
     */
    public void setSystemType(String systype) {
        this.systype.setValue(systype);
    }
}
