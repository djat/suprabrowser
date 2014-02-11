/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;



import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.descriptor.ISerializableDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 * @author Dmitry Goncharov
 */
public class PlainDescriptor<T> extends PropertyDescriptor<T> implements ISerializableDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6970700339609518983L;
	
	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 */
	public PlainDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz) {
		super(beanClazz, name, valueClazz);
	}

}
