package ss.lab.dm3.orm.mapper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.ReferenceHolder;
import ss.lab.dm3.orm.ReferenceHolderList;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.managed.CollectionAccessorList;
import ss.lab.dm3.orm.managed.ICollectionAccessor;
import ss.lab.dm3.orm.mapper.map.BeanMap;
import ss.lab.dm3.orm.mapper.property.IAccessor;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.orm.mapper.property.accessor.MethodAccessor;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.utils.ConverterUtils;

/**
 * @author Dmitry Goncharov
 */
public class BeanMapper<T extends MappedObject> {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private static final String IdPropertyName = "id";

	private final BeanMap map;

	private final Class<T> beanClass;

	private final List<Property<?>> properties;

	private final Property<?> idProperty;

	private final Hashtable<String, Property<?>> nameToProperty = new Hashtable<String, Property<?>>();
	
	private final CollectionAccessorList collectionAccessors;

	private List<Property<?>> referenceProperties = null; 
	
	private List<Property<?>> collectionProperties = null;

	private final BeanWrapper<T> beanWrapper;
	
	private final HashMap<Method, Property<?>> methodToPropery = new HashMap<Method, Property<?>>();
	
	/**
	 */
	@SuppressWarnings("unchecked")
	public BeanMapper(BeanMap map, BeanWrapper<T> beanWrapper) {
		super();
		this.map = map;
		this.beanWrapper = beanWrapper;
		this.beanClass = (Class) map.getEntityClazz();
		this.properties = new ArrayList<Property<?>>();
		final PropertyDescriptor<?>[] propertyDescriptors = this.map
				.getPropertyDescriptors();
		for (int n = 0; n < propertyDescriptors.length; ++n) {
			PropertyDescriptor<?> propertyDescriptor = propertyDescriptors[n];
			final Property property = new Property(propertyDescriptor, this.map.getPropertySerializableIndex( propertyDescriptor ) );
			this.properties.add(property);
			this.nameToProperty.put(normalizePropertyName(propertyDescriptor
					.getName()), property);
			final IAccessor accessor = property.getAccessor();
			if ( accessor instanceof MethodAccessor ) {
				this.methodToPropery.put( ((MethodAccessor) accessor).getSetter(), property);				
			}
		}
		this.idProperty = getProperty(IdPropertyName);
		this.collectionAccessors = new CollectionAccessorList();
		for (Property<?> property : this.properties) {
			if (property.isCollection()) {
				ICollectionAccessor collectionAccessor = (ICollectionAccessor) property
						.getAccessor();
				this.collectionAccessors.add(collectionAccessor);
			}
		}
		
	}

	public void load(T toBean, Serializable[] fromValues) {
		for (Property<?> property : this.properties) {
			property.load(toBean, fromValues);
		}
	}

	public boolean loadOnlyIfNew(T toBean, Serializable[] fromValues) {
		int updatesCount = 0;
		for (Property<?> property : this.properties) {
			if ( property.loadOnlyIfNew(toBean, fromValues) ) {
				++ updatesCount; 
			}
		}
		return updatesCount > 0;
	}
	
	public void save(T fromBean, Serializable[] toValues) {
		for (Property<?> property : this.properties) {
			property.save(fromBean, toValues);
		}
	}

	/**
	 * @return the properties
	 */
	public List<Property<?>> getProperties() {
		return this.properties;
	}

	/**
	 * @param owner
	 * @return
	 */
	public ReferenceHolderList createReferenceHolderList() {
		ReferenceHolderList referenceHolders = new ReferenceHolderList();
		for (Property<?> property : getReferenceProperties() ) {
			ReferenceHolder holder = property.createReferenceHolder();
			referenceHolders.add(holder);
		}
		return referenceHolders;
	}

	/**
	 * @param collectionOwner
	 * @return
	 */
	public CollectionAccessorList getCollectionAccessors() {
		return this.collectionAccessors;
	}

	public Long getMapId() {
		return this.map.getId();
	}

	public Class<T> getObjectClass() {
		return this.beanClass;
	}

	/**
	 * @param entity
	 * @return
	 */
	public QualifiedObjectId<T> getObjectId(Entity entity) {
		final int idIndex = this.idProperty.getSerializationIndex();
		Serializable idValue = entity.getValues()[idIndex];
		QualifiedObjectId<T> id = new QualifiedObjectId<T>(this.beanClass,
				ConverterUtils.getLongValue(idValue));
		return id;
	}

	/**
	 * @param entity
	 * @return
	 */
	public T toObject(Entity entity, boolean setUpManagedFeatures ) {
		T object = createObject( setUpManagedFeatures );
		load(object, entity.getValues());
		return object;
	}

	/**
	 * @param object
	 */
	public void setUpManagedFeatures(T object) {
		try {
			for( Property<?> property : this.properties ) {
				property.setUpManagedFeatures(object);
			}
		}
		catch( RuntimeException ex ) {
			this.log.error( "Properties is " + this.properties );
			throw ex;
		}
	}

	/**
	 * @param entity
	 * @return
	 */
	public boolean toObject(T targetObject, Entity entity) {
		final Class<? extends Object> targetObjectClazz = this.beanWrapper.getBeanClass( targetObject );
		if ( this.beanClass != targetObjectClazz ) {
			throw new IllegalArgumentException("Unexpected object class "
					+ targetObjectClazz);
		}
		return loadOnlyIfNew(targetObject, entity.getValues());
	}

	public boolean toObject(T targetObject, T source) {
		Entity entity = toEntity(source);
		return toObject( targetObject, entity );
	}
	
	/**
	 * @return
	 */
	public T createObject( boolean setUpManagedFeatures ) {
		T object = this.beanWrapper.newInstance( this.beanClass );
		if ( setUpManagedFeatures ) {
			setUpManagedFeatures( object );
		}
		return object;
	}

	/**
	 * @param object
	 * @return
	 */
	public Entity toEntity(T object) {
		Serializable[] values = new Serializable[ this.map.getSerializeblePropertiesCount() ];
		save(object, values);
		return new Entity(getMapId(), values);
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		return tsb.append("map", this.map).append("beanClass", this.beanClass)
				.append("properties", this.properties).toString();
	}

	public Property<?> getProperty(String propertyName) {
		final String normalizedName = normalizePropertyName(propertyName);
		Property<?> accessor = this.nameToProperty.get(normalizedName);
		if (accessor == null) {
			throw new AccessorNotFoundException(this, propertyName);
		}
		return accessor;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private static String normalizePropertyName(String propertyName) {
		return propertyName.toLowerCase();
	}

	/**
	 * @return
	 */
	public BeanMap getMap() {
		return this.map;
	}

	/**
	 * @return
	 */
	public String getEntityName() {
		return this.map.getEntityName();
	}
	
	/**
	 * 
	 */
	public synchronized List<Property<?>> getReferenceProperties() {
		if ( this.referenceProperties == null ) {
			this.referenceProperties = new ArrayList<Property<?>>();
			for( Property<?> property : this.properties ) {
				if ( property.isReference() ) {
					this.referenceProperties.add( property );
				}
			}
		}
		return this.referenceProperties;		
	}
	
	public synchronized List<Property<?>> getCollectionProperties() {
		if ( this.collectionProperties == null ) {
			this.collectionProperties = new ArrayList<Property<?>>();
			for ( Property<?> property : this.properties ) {
				if ( property.isCollection() ) {
					this.collectionProperties.add( property );
				}
			}
		}
		return this.collectionProperties;
	}

	/**
	 * @param bean
	 */
	public void refreshReferences(T bean) {
		for( Property<?> property : getReferenceProperties() ) {
			property.refresh( bean );
		}
		
	}

	public boolean isInheritanceBase() {
		return this.map.isInheritanceBase();
	}

	/**
	 * @param method
	 */
	public Property<?> getPropertyBySettedMethod(Method method) {
		return this.methodToPropery.get( method );
	}
	
}
