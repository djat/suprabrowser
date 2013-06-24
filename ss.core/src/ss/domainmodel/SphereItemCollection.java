package ss.domainmodel;

import java.util.ArrayList;
import java.util.List;

import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;

public class SphereItemCollection extends XmlListEntityObject<SphereItem> {

	public SphereItemCollection() {
		super( SphereItem.class, SphereItem.ITEM_ROOT_ELEMENT_NAME);
	}
	
	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( SphereItem item ) {
		super.internalAdd(item);
	}

	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(SphereItem entity) {
		super.internalRemove(entity);
	}
	
	
	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public SphereItem get(int index) {
		return super.internalGet(index);
	}

	/**
	 * @param sphereSystemId
	 * @return
	 */
	public boolean isEnabled(final String sphereSystemId) {
		SphereItem sphereItem = findSphere(sphereSystemId);
		return sphereItem != null && sphereItem.isEnabled();
	}

	/**
	 * @param sphereId
	 */
	private SphereItem findSphere(final String sphereId) {
		if ( sphereId == null ) {
			return null;
		}
		return findFirst( new IXmlEntityObjectFindCondition<SphereItem>()  {
			public boolean macth(SphereItem entityObject) {
				return sphereId.equals( entityObject.getSystemName() );				
			}
		});
	}

	/**
	 * 
	 */
	public List<SphereItem> getEnabledSpheres() {
		List<SphereItem> enabledSpheres = new ArrayList<SphereItem>();
		for( SphereItem sphereItem : this ) {
			if ( sphereItem.isEnabled() ) {
				enabledSpheres.add( sphereItem );
			}
		}
		return enabledSpheres;
	}
	
	
	
}
