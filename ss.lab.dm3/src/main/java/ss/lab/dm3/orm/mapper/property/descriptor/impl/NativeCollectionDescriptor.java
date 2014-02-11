/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import java.util.Collection;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.NativeCollectionAccessor;

/**
 * @author Dmitry Goncharov
 */
public class NativeCollectionDescriptor<T extends Collection<?>> extends CollectionDescriptor<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3221044317353329363L;
	
	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 * @param mappedByName
	 */
	public NativeCollectionDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz, String mappedByName) {
		super(beanClazz, name, valueClazz, mappedByName);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor#createPropertyAccessor()
	 */
	@Override
	public IAccessor createPropertyAccessor() {
		return new NativeCollectionAccessor( super.createPropertyAccessor(), this.mappedByName, this.itemType );
	}

	
}
