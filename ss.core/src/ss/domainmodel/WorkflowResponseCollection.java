/**
 * 
 */
package ss.domainmodel;

import ss.framework.entities.xmlentities.XmlListEntityObject;

/**
 * @author roman
 *
 */
public class WorkflowResponseCollection extends XmlListEntityObject<WorkflowResponse>{

	public WorkflowResponseCollection(){
		super(WorkflowResponse.class, WorkflowResponse.ITEM_ROOT_ELEMENT_NAME);
	}

	/**
	 * Adds item to the collection
	 * @param item not null object 
	 */
	public void add( WorkflowResponse item ) {
		super.internalAdd(item);
	}

	/**
	 * Remove item from collection
	 * @param entity
	 */
	public void remove(WorkflowResponse entity) {
		super.internalRemove(entity);
	}


	/**
	 * @param index from 0 to Count-1  
	 * @return Returns item by index 
	 */
	public WorkflowResponse get(int index) {
		return super.internalGet(index);
	}

	public void removeAll() {
		for(WorkflowResponse fav: this) {
			this.remove(fav);
		}
	}
}
