/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.accessor;

import ss.lab.dm3.orm.mapper.property.IAccessor;

/**
 * @author Dmitry Goncharov
 */
public abstract class AbstractAccessor implements IAccessor {

	public void refresh(Object bean) {
	}	
	
	public void resetToDefault(Object bean) {		
	}
	
}
