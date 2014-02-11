/**
 * 
 */
package ss.lab.dm3.orm.mapper.property.descriptor.impl;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmException;
import ss.lab.dm3.orm.mapper.property.descriptor.ICollectionDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 * @author Dmitry Goncharov
 */
public abstract class CollectionDescriptor<T> extends PropertyDescriptor<T> implements ICollectionDescriptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7042589269071980395L;

	protected String mappedByName;
	
	protected Class<? extends MappedObject> itemType;
	
	/**
	 * @param beanClazz
	 * @param name
	 * @param valueClazz
	 * @param mappedByName
	 */
	public CollectionDescriptor(Class<? extends MappedObject> beanClazz,
			String name, Class<T> valueClazz, String mappedByName) {
		super(beanClazz, name, valueClazz);
		this.mappedByName = mappedByName;
	}

	public String getMappedByName() {
		return this.mappedByName;
	}

	public Class<? extends MappedObject> getItemType() {
		return this.itemType;
	}

	public void setItemType(Class<? extends MappedObject> entityClazz) {
		this.itemType = entityClazz;
	}

	@Override
	public void validate() {
		super.validate();
		if ( this.itemType == null ) {
			throw new OrmException( "Item type of collection property " + this + " is null");
		}
		if ( this.mappedByName == null ) {
			throw new OrmException( "MappedByName by of collection property " + this + " is null");
		}
	}

	public boolean isMappedByDefined() {
		return this.mappedByName != null && this.mappedByName.length() > 0;
	}

	public void setMappedByName(String mappedByName) {
		this.mappedByName = mappedByName;
	}
	
	
	
}
