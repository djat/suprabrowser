/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 * @author Dmitry Goncharov
 */
public class TransientDescriptor<T> extends PropertyDescriptor<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5446057583172442421L;

	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 */
	public TransientDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz) {
		super(beanClazz, name, valueClazz);
	}

}
