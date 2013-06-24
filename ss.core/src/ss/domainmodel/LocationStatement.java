package ss.domainmodel;

import ss.framework.entities.xmlentities.IXmlElementDataProvider;
import ss.framework.entities.xmlentities.XmlEntityObject;

public class LocationStatement extends XmlEntityObject {

	private final SphereLocationCollection entities = super.bindListProperty( new SphereLocationCollection() );
	
	/**
	  Create bookmark object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static LocationStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, LocationStatement.class);
	}

	/**
	 * @param desiredRootElementName
	 */
	public LocationStatement(IXmlElementDataProvider stable) {
		super("locations");
	}
	
	/**
	 * @return Returns list of entities
	 */
	public SphereLocationCollection getEntities() {
		return this.entities;
	}
		
}
