package ss.domainmodel;

import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class ObjectRelation extends XmlEntityObject {

	public static final String ROOT_ELEMENT_NAME = "relation";
	
	private final ISimpleEntityProperty displayName = super
			.createAttributeProperty("displayName/@value");

	private final ISimpleEntityProperty sphereId = super
			.createAttributeProperty("sphereId/@value");

	/**
	 * 
	 */
	public ObjectRelation() {
		super( ROOT_ELEMENT_NAME );
	}
	
	public ObjectRelation( String sphereId,String displayName) {
		this();
		setSphereId( sphereId );
		setDisplayName( displayName);
	}

	/**
	 * @param managedSphere
	 */
	public ObjectRelation(SphereStatement sphere) {
		this( sphere.getSystemName(), sphere.getDisplayName() );
	}

	/**
	 * Gets the displayName
	 */
	public final String getDisplayName() {
		return this.displayName.getValue();
	}

	/**
	 * Sets the displayName
	 */
	public final void setDisplayName(String value) {
		this.displayName.setValue(value);
	}
	
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

	
	
}
