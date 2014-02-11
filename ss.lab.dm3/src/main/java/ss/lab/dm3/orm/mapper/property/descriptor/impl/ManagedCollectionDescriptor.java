/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.managed.IManagedCollection;
import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.accessor.ManagerCollectionAccessor;

/**
 * @author Dmitry Goncharov
 */
public class ManagedCollectionDescriptor<T extends IManagedCollection> extends CollectionDescriptor<T> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2355529764116793629L;
	
	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 * @param mappedByName
	 */
	public ManagedCollectionDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz, String mappedByName) {
		super(beanClazz, name, valueClazz, mappedByName);
	}

	/**
	 * @param translateObjectType
	 * @param name
	 * @param collectionClazz
	 * @param mappedByName
	 * @param itemClazz
	 */
	public ManagedCollectionDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz, String mappedByName, Class<? extends MappedObject> itemClazz) {
		this( beanClazz, name,valueClazz, mappedByName);
		setItemType(itemClazz);
	}

	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor#createPropertyAccessor()
	 */
	@Override
	public IAccessor createPropertyAccessor() {
		return new ManagerCollectionAccessor(super.createFieldAccessor(), this.mappedByName, this.itemType );
	}

}
