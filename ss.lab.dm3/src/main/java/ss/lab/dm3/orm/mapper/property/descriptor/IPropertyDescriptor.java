package ss.lab.dm3.orm.mapper.property.descriptor;

import ss.lab.dm3.orm.MappedObject;

public interface IPropertyDescriptor {

	String getName();
	
	/**
	 * @return the beanClazz
	 */
	Class<? extends MappedObject> getBeanClazz();

	Class<?> getValueClazz();
	
}
