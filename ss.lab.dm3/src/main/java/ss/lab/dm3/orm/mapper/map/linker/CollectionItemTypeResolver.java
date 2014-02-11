/**
 * 
 */
package ss.lab.dm3.orm.mapper.map.linker;

import java.util.HashMap;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.mapper.map.BeanMap;
import ss.lab.dm3.orm.mapper.map.BeanSpace;
import ss.lab.dm3.orm.mapper.property.descriptor.IReferenceDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

/**
 *
 */
public class CollectionItemTypeResolver {

	private final BeanSpace space;

	private final HashMap<Class<? extends MappedObject>, ReferenceFromList> targetToFromList = new HashMap<Class<? extends MappedObject>, ReferenceFromList>(); 
	
	/**
	 * @param space
	 */
	public CollectionItemTypeResolver(BeanSpace space) {
		super();
		this.space = space;
		biuldRerefernceFromMap();
	}
	
	/**
	 * 
	 */
	private void biuldRerefernceFromMap() {
		for( BeanMap beanMap : this.space ) {
			for( PropertyDescriptor<?> property : beanMap.getPropertyDescriptors() ) {
				if ( property instanceof IReferenceDescriptor ) {
					final IReferenceDescriptor referenceProperty = (IReferenceDescriptor) property;
					final Class<? extends MappedObject> targetEntityClass = referenceProperty.getTargetEntityClass();
					ReferenceFromList froms = this.targetToFromList.get( targetEntityClass );
					if ( froms == null ) {
						froms = new ReferenceFromList();
						this.targetToFromList.put(targetEntityClass, froms);
					}
					froms.add( referenceProperty );
				}
			}
		}
	}

	/**
	 * @param entityClazz
	 * @param propertyName
	 * @return
	 */
	public Class<? extends MappedObject> resolve( Class<? extends MappedObject> entityClazz, String mappedByName) {
		ReferenceFromList froms = this.targetToFromList.get( entityClazz );
		if ( froms == null ) {
			throw new CantResolveCollectionItemTypeException( "Can't find any entity that refer to " + entityClazz );
		}
		return froms.resolve( mappedByName );
	}

	/**
	 * @param entityClazz
	 * @param itemType
	 * @return
	 */
	public String resolve(final Class<? extends MappedObject> entityClazz,
			Class<? extends MappedObject> itemType) {
		ReferenceFromList froms = this.targetToFromList.get( entityClazz );
		Class<?> iter = entityClazz;
		while ( froms == null ) {
			final Class<?> superClazz = iter.getSuperclass();
			if ( superClazz != null && 
				 MappedObject.class.isAssignableFrom( superClazz ) ) {
				iter = superClazz;
				froms = this.targetToFromList.get( iter );
			}
			else {
				break;
			}
		}
		if ( froms == null ) {
			throw new CantResolveCollectionItemTypeException( "Can't find any entity that refer to " + entityClazz );
		}
		return froms.resolve( itemType );
	}
	
}
