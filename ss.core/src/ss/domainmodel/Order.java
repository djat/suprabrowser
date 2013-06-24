package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class Order extends XmlEntityObject {
	
	public static final String ITEM_ROOT_ELEMENT_NAME = "order";
	
	private final ISimpleEntityProperty value = super
		.createAttributeProperty( "@value" );
	
	private final ISimpleEntityProperty displayName = super
		.createAttributeProperty( "@display_name" );
	
	private final ISimpleEntityProperty systemName = super
		.createAttributeProperty( "@system_name" );
	
	@SuppressWarnings("unchecked")
	public static Order wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, Order.class);
	}

	@SuppressWarnings("unchecked")
	public static Order wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, Order.class);
	}
	
	public Order() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public String getDisplayName() {
		return this.displayName.getValue();
	}
	
	public void setDisplayName(String value) {
		this.displayName.setValue(value);
	}
	
	public String getSystemName() {
		return this.systemName.getValue();
	}
	
	public void setSystemName(String value) {
		this.systemName.setValue(value);
	}
	
	public String getValue() {
		return this.value.getValue();
	}
	
	public void setValue(String value) {
		this.value.setValue(value);
	}
}
