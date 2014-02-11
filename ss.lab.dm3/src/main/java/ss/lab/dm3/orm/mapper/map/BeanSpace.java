package ss.lab.dm3.orm.mapper.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmException;
import ss.lab.dm3.orm.mapper.map.linker.CollectionItemTypeResolver;
import ss.lab.dm3.orm.mapper.property.descriptor.ICollectionDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;

public class BeanSpace implements Serializable, Iterable<BeanMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -224661341369126196L;
	
	private final Class<? extends MappedObject> baseBeanClazz;
	
	private final TreeMap<String, BeanMap> nameToMap = new TreeMap<String, BeanMap>();
	
	private final HashMap<Class<? extends MappedObject>, BeanMap> classToMap = new HashMap<Class<? extends MappedObject>,BeanMap>();
	
	/**
	 * @param baseBeanClazz
	 */
	public BeanSpace(Class<? extends MappedObject> baseBeanClazz) {
		this.baseBeanClazz = baseBeanClazz;
	}

	/**
	 * @param class1
	 * @param domainMaps
	 */
	public BeanSpace(Class<? extends MappedObject> baseBeanClazz, Iterable<BeanMap> maps) {
		this(baseBeanClazz);
		for( BeanMap map : maps ) {
			add( map );
		}
	}

	public void add( BeanMap beanMap ) {
		if ( !this.baseBeanClazz.isAssignableFrom( beanMap.getEntityClazz() ) ) {
			throw new IllegalArgumentException( beanMap.getEntityClazz() + " is not assignable from " + this.baseBeanClazz );
		}
		if ( this.nameToMap.containsKey( beanMap.getEntityName() ) ) {
			throw new IllegalArgumentException( "Found two objects with same name " + beanMap );
		}
		if ( this.classToMap.containsKey( beanMap.getEntityClazz() ) ) {
			throw new IllegalArgumentException( "Found two objects with same class " + beanMap );
		}
		this.nameToMap.put( beanMap.getEntityName(), beanMap );
		this.classToMap.put( beanMap.getEntityClazz(), beanMap );
	}
	
	public BeanMap get( String entityName ) {
		BeanMap map = this.nameToMap.get(entityName);
		if ( map == null ) {
			throw new OrmException( "Entity with name " +  entityName + " not found." );
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<BeanMap> iterator() {
		return this.nameToMap.values().iterator();
	}

	/**
	 * @param targetEntityClazz
	 * @return
	 */
	public BeanMap get(Class<? extends MappedObject> targetEntityClazz) {
		BeanMap map = this.classToMap.get( targetEntityClazz );
		if ( map == null ) {
			throw new OrmException( "Entity with class " + targetEntityClazz + " not found." );
		}
		return map;
	}

	/**
	 * 
	 */
	public void validate() {
		for (BeanMap map : this ) {
			for (PropertyDescriptor<?> propertyDescriptor : map
					.getPropertyDescriptors()) {
				propertyDescriptor.validate();
			}
		}
	}

	/**
	 * 
	 */
	void resolveCollectionItemTypes() {
		final CollectionItemTypeResolver resolver = new CollectionItemTypeResolver( this );
		for (BeanMap map : this ) {
			for (PropertyDescriptor<?> propertyDescriptor : map
					.getPropertyDescriptors()) {
				if ( propertyDescriptor instanceof ICollectionDescriptor ) {
					ICollectionDescriptor collectionProperty = (ICollectionDescriptor) propertyDescriptor;
					if ( collectionProperty.getItemType() == null ) {
						collectionProperty.setItemType(	resolver.resolve( map.getEntityClazz(), collectionProperty.getMappedByName()  ) );
					}
					if ( !collectionProperty.isMappedByDefined() ) {
						collectionProperty.setMappedByName( resolver.resolve( map.getEntityClazz(), collectionProperty.getItemType() ) );
					}
				}
//				if (propertyDescriptor instanceof IReferenceDescriptor) {
//					IReferenceDescriptor referenceProperty = (IReferenceDescriptor) propertyDescriptor;
//					Class<? extends MappedObject> targetEntityClazz = referenceProperty.getTargetEntityClass();
//					BeanMap targetEntity = get(targetEntityClazz);
//					for (PropertyDescriptor<?> propertyInTarget : targetEntity
//							.getPropertyDescriptors()) {
//						if (propertyInTarget instanceof ICollectionDescriptor) {
//							ICollectionDescriptor collectionInTarget = (ICollectionDescriptor) propertyInTarget;
//							String mappedByPropertyName = collectionInTarget
//									.getMappedByName();
//							if (mappedByPropertyName.equals(propertyDescriptor
//									.getName())) {
//								collectionInTarget.setItemType(map
//										.getEntityClazz());
//							}
//						}
//					}
//				}
				
				
				
				
			}
		}
	}

	/**
	 * @return
	 */
	public int size() {
		return this.classToMap.size();
	}

	public Object[] toArray() {
		return this.nameToMap.values().toArray();
	}

	public Class<? extends MappedObject> getBaseBeanClazz() {
		return this.baseBeanClazz;
	}
	
	
}
