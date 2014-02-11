/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor;

import ss.lab.dm3.orm.MappedObject;

/**
 * @author Dmitry Goncharov
 */
public interface ICollectionDescriptor extends IPropertyDescriptor {

	/**
	 * 
	 */
	String getMappedByName();

	/**
	 * @param entityClazz
	 */
	void setItemType(Class<? extends MappedObject> entityClazz);
	
	Class<? extends MappedObject> getItemType();

	/**
	 * @return
	 */
	boolean isMappedByDefined();

	/**
	 * 
	 */
	void setMappedByName(String mappedByName);
	
}
