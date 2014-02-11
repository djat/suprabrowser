package ss.lab.dm3.orm.mapper.property.accessor;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.managed.ICollectionAccessor;
import ss.lab.dm3.orm.mapper.property.CantGetObjectValueException;
import ss.lab.dm3.orm.mapper.property.CantSetObjectValueException;
import ss.lab.dm3.orm.mapper.property.IAccessor;

/**
 * @author Dmitry Goncharov
 */
public abstract class CollectionAccessor extends AbstractAccessor implements ICollectionAccessor {
	
	/**
	 * 
	 */
	private final IAccessor collectionPropertyAccessor;
	
	private final String mappedByName;
	
	private final Class<? extends MappedObject> itemType;
	/**
	 * @param collectionDescriptor
	 */
	public CollectionAccessor(IAccessor collectionPropertyAccessor, String mappedByName, Class<? extends MappedObject> itemType) {
		this.collectionPropertyAccessor = collectionPropertyAccessor;
		this.mappedByName = mappedByName; 
		this.itemType = itemType;
	}
	

	public final String getMappedByName() {
		return this.mappedByName;
	}
	
	
	public Class<? extends MappedObject> getItemType() {
		return this.itemType;
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.mapper.property.IAccessor#getValue(java.lang.Object)
	 */
	public final Object getValue(Object bean) throws CantGetObjectValueException {
		return this.collectionPropertyAccessor.getValue(bean);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.mapper.property.IAccessor#setValue(java.lang.Object, java.lang.Object)
	 */
	public final void setValue(Object bean, Object value)
			throws CantSetObjectValueException {
		this.collectionPropertyAccessor.setValue(bean, value);
	}


}