package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class SphereItem extends XmlEntityObject {


	/**
	 * 
	 */
	private static final String NORMAL_DELIVERY = "normal";

	public enum SphereType {
		GROUP,
		MEMBER,
		CLUBDEAL
	}
		
	public static final String ITEM_ROOT_ELEMENT_NAME = "sphere";
	
	private final ISimpleEntityProperty displayName = super
		.createAttributeProperty( "@display_name" );
	
	private final ISimpleEntityProperty systemName = super
		.createAttributeProperty( "@system_name" );
	
	private final ISimpleEntityProperty sphereType = super
		.createAttributeProperty( "@sphere_type" );
	
	private final ISimpleEntityProperty defaultDelivery = super
		.createAttributeProperty( "@default_delivery" );
		
	private final ISimpleEntityProperty enabled = super
		.createAttributeProperty( "@enabled" );
	
	
	public SphereItem() {
		super(ITEM_ROOT_ELEMENT_NAME);
	}
	
	public SphereItem( String sphereId, String displayName, SphereType type, boolean enabled ) {
		this();
		setDisplayName(displayName);
		setSystemName(sphereId);
		setDefaultDelivery( NORMAL_DELIVERY );
		setSphereType(type);
		setEnabled(enabled);
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
	
	public SphereType getSphereType() {
		return this.sphereType.getEnumValue( SphereType.class );
	}
		
	public void setSphereType(SphereType type) {
		this.sphereType.setEnumValue(type);
	}
	
	public String getDefaultDelivery() {
		return this.defaultDelivery.getValue();
	}
	
	public void setDefaultDelivery(String value) {
		this.defaultDelivery.setValue(value);
	}
	
	public boolean isEnabled() {
		return this.enabled.getBooleanValue();
	}
	
	public void setEnabled(boolean value) {
		this.enabled.setBooleanValue(value);
	}

	/**
	 * @return
	 */
	public boolean hasValidDisplayName() {
		return SphereStatement.isDisplayNameValid(getDisplayName());
	}
	
	public boolean isMember(){
		return SphereType.MEMBER.equals(getSphereType());
	}

	
	
	
}
