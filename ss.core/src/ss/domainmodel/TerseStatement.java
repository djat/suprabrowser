package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlEntityObject;

public class TerseStatement extends SystemPossobilityStatement {
	

	public TerseStatement() {
		super( "email");
	}
	
	/**
	 * Create Terse object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static TerseStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, TerseStatement.class);
	}

	/**
	 * Create Terse object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static TerseStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, TerseStatement.class);
	}
	
    /**
     * Set the "terse" type to the statement
     *
     */
    public void setCurrentType(){
    	setType("terse");
    }
}