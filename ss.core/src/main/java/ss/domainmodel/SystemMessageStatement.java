/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.util.SupraXMLConstants;

/**
 * @author roman
 *
 */
public class SystemMessageStatement extends SystemPossobilityStatement {
	
	public SystemMessageStatement() {
		super( "email");
	}
	
	/**
	 * Create Terse object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SystemMessageStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SystemMessageStatement.class);
	}

	/**
	 * Create Terse object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SystemMessageStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SystemMessageStatement.class);
	}
	
    /**
     * Set the "terse" type to the statement
     *
     */
    public void setCurrentType(){
    	setType(SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE);
    }

}
