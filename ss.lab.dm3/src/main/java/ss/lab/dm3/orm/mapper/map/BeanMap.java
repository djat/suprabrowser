/**
 * 
 */
package ss.lab.dm3.orm.mapper.map;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.property.descriptor.ISerializableDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 * @author Dmitry Goncharov
 */
public class BeanMap implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -484635101254644187L;

	private final Long id;
	
	private final String entityName;
	
	private final Class<? extends MappedObject> entityClazz;
	
	private final PropertyDescriptor<?>[] propertyDescriptors;
	
	private final HashMap<PropertyDescriptor<?>, Integer> propertySerializationIndex = new HashMap<PropertyDescriptor<?>, Integer>();
	
	private final boolean inheritanceBase;  
	
	/**
	 * @param id
	 * @param entityName
	 * @param propertyDescriptors
	 */
	public BeanMap(Long id, String entityName, Class<? extends MappedObject> entityClazz,
			boolean inheritanceBase,
			PropertyDescriptor<?>[] propertyDescriptors) {
		super();
		this.id = id;
		this.entityClazz = entityClazz;
		this.entityName = entityName;
		this.propertyDescriptors = propertyDescriptors;
		this.inheritanceBase = inheritanceBase;
		int serializeblePropertiesCount = 0;
		for( PropertyDescriptor<?> propertyDescriptor : propertyDescriptors ) {
			if ( propertyDescriptor instanceof ISerializableDescriptor ) {
				this.propertySerializationIndex.put(propertyDescriptor, serializeblePropertiesCount);
				++ serializeblePropertiesCount;
			}
		}
	}
	
	public Class<? extends MappedObject> getEntityClazz() {
		return this.entityClazz;
	}

	public Long getId() {
		return this.id;
	}
	
	public String getEntityName() {
		return this.entityName;
	}

	public PropertyDescriptor<?>[] getPropertyDescriptors() {
		return this.propertyDescriptors;
	}
	
	public PropertyDescriptor<?> findProperty(String propertyName) {
		for (int n = 0; n < this.propertyDescriptors.length; ++n) {
			final PropertyDescriptor<?> propertyDescriptor = this.propertyDescriptors[n];
			if (propertyDescriptor.getName().equals(propertyName)) {
				return propertyDescriptor;
			}
		}
		return null;
	}

	public int getPropertySerializableIndex(String propertyName) {
		PropertyDescriptor<?> property = findProperty(propertyName);
		return property != null ? getPropertySerializableIndex(property) : -1;
	}

	/**
	 * @param propertyDescriptor
	 * @return
	 */
	public int getPropertySerializableIndex(
			PropertyDescriptor<?> propertyDescriptor) {
		Integer index = this.propertySerializationIndex.get( propertyDescriptor );
		return index != null ? index.intValue() : -1;
	}

	public int getSerializeblePropertiesCount() {
		return this.propertySerializationIndex.size();
	}

	public boolean isInheritanceBase() {
		return this.inheritanceBase;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "id", this.id )
		.append( "entityName", this.entityName )
		.append( "entityClazz", this.entityClazz )
		.append( "properties count", this.propertyDescriptors.length )
		.append( "serializable properties count", this.propertySerializationIndex.size() )
		.toString();
	}
	
}
