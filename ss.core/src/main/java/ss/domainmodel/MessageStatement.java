package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class MessageStatement extends SystemPossobilityStatement {

	private final ISimpleEntityProperty receiver = super
	.createAttributeProperty("receiver/@value" );
	
	public MessageStatement() {
		super( "email");
	}
	
	/**
	 * Create message object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static MessageStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, MessageStatement.class);
	}

	/**
	 * Create message object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static MessageStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, MessageStatement.class);
	}
	
	/**
	 * Gets the body text
	 */
	public String getReceiver() {
		return this.receiver.getValue();
	}
	
	public void setReceiver(String value) {
		this.receiver.setValue(value);
	}

	/**
	 * 
	 */
	public void setCurrentType() {
		setType("message");
	}

 
}