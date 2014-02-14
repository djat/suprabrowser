/**
 * 
 */
package ss.client.ui.tempComponents.interfaces;

import ss.client.ui.tempComponents.SpheresCollectionByTypeObject;

/**
 * @author roman
 *
 */
public interface ISphereListOwner {
	
	void setCurrent(String current);
	
	void setFocusToSubjectField();
	
	SpheresCollectionByTypeObject getSphereOwner();

}
