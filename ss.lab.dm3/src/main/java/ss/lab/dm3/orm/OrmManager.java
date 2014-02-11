package ss.lab.dm3.orm;

import java.util.WeakHashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.annotation.PropertyInjection;
import ss.lab.dm3.orm.managed.CollectionAccessorList;
import ss.lab.dm3.orm.managed.ICollectionAccessor;
import ss.lab.dm3.orm.managed.ManagedReference;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.property.Property;

/**
 * @author Dmitry Goncharov
 */
public class OrmManager {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	private final WeakHashMap<MappedObject, ReferenceHolderList> beanToReferences = new WeakHashMap<MappedObject, ReferenceHolderList>();

	private IObjectResolver objectResolver;
	
	private IBeanMapperProvider beanMapperProvider;
		
	public IObjectResolver getObjectResolver() {
		return this.objectResolver;
	}

	@PropertyInjection
	public void setObjectResolver(IObjectResolver objectResolver) {
		this.objectResolver = objectResolver;
	}
	
	/**
	 * TODG Bad design. Mapper.get( object ) fail for proxy objects.
	 * It's very unclear that users should get mapper via IBeanMapperProvider 
	 *  
	 * @return
	 */
	public IBeanMapperProvider getBeanMapperProvider() {
		return this.beanMapperProvider;
	}

	public void setBeanMapperProvider(IBeanMapperProvider beanMapperProvider) {
		this.beanMapperProvider = beanMapperProvider;
	}

//	public <T extends MappedObject> T resolve(Class<T> entityClass, Long id) {
//		if (id == null) {
//			return null;
//		}
//		return this.objectResolver.resolve(entityClass, id);
//	}

	public void referenceChanged(ManagedReference<?> reference) {
		referenceChanged(reference.getOwner(), reference.getName(),
			reference.getId());
	}

	/**
	 * @param fromBean
	 * @param referenceName
	 * @param targetBeanId
	 */
	public void referenceChanged(final MappedObject fromBean,
			final String referenceName, final Long targetBeanId) {
		ReferenceHolderList referenceHolders = getReferenceHolders(fromBean);
		ReferenceHolder referenceHolder = referenceHolders.get(referenceName);
		breakBackReference(fromBean, referenceHolder);
		referenceHolder.setTargetEntityId(targetBeanId);
		bindBackReference(fromBean, referenceHolder);
	}

	/**
	 * @param owner
	 * @return
	 */
	private ReferenceHolderList getReferenceHolders(MappedObject owner) {
		ReferenceHolderList holders = this.beanToReferences.get(owner);
		if ( holders == null ) {
			if (this.log.isDebugEnabled() ) {
				this.log.debug( "Create reference holders for " + owner );
			}
			holders = createReferenceHolders(owner);
			this.beanToReferences.put(owner, holders);
		}
		return holders;
	}

	/**
	 * @param mappedObjectClazz
	 * @return
	 */
	private ReferenceHolderList createReferenceHolders(
			MappedObject owner) {
		BeanMapper<?> beanMapper = getBeanMapper( owner );
		return beanMapper.createReferenceHolderList();
	}

	/**
	 * @param referenceHolder
	 */
	private void bindBackReference(MappedObject owner, ReferenceHolder referenceHolder) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Bind reference " + referenceHolder + " for " + owner );
		}
		MappedObject targetObject = this.objectResolver.resolveManagedOrNull(
			referenceHolder.getTargetEntityType(),
			referenceHolder.getTargetEntityId());
		if (targetObject != null) {
			ICollectionAccessor accessor = getCollectionAccessor(
				targetObject, referenceHolder.getPropertyName(), referenceHolder.getOwnerEntityType() );
			if (accessor != null) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Call accessor to add " + owner + " to " + targetObject );
				}
				accessor.addByOrm(targetObject, owner);
			}
			else {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Related collection not found to " + referenceHolder );
				}
			}
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Target not found for " + referenceHolder );
			}
		}
	}

	/**
	 * @param collectionOwner
	 * @param mappedByName
	 */
	private ICollectionAccessor getCollectionAccessor(
			MappedObject collectionOwner, String mappedByName, Class<? extends MappedObject> itemType) {
		CollectionAccessorList collectionAccessors = getCollectionAccessors( collectionOwner );
		return collectionAccessors.get( mappedByName, itemType );
	}

	/**
	 * @param collectionOwner
	 * @return
	 */
	private CollectionAccessorList getCollectionAccessors(
			MappedObject collectionOwner) {
		BeanMapper<?> beanMapper = getBeanMapper(collectionOwner);
		return beanMapper.getCollectionAccessors();
	}

	/**
	 * @param collectionOwner
	 * @return
	 */
	private BeanMapper<?> getBeanMapper(MappedObject bean) {
		return this.beanMapperProvider.get(bean);
	}
	
	/**
	 * @param referenceHolder
	 */
	private void breakBackReference(MappedObject owner, ReferenceHolder referenceHolder) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Break back references " + referenceHolder + " for " + owner );
		}
		MappedObject mappedObject = this.objectResolver.resolveManagedOrNull(
			referenceHolder.getTargetEntityType(),
			referenceHolder.getTargetEntityId());
		if (mappedObject != null) {
			ICollectionAccessor accessor = getCollectionAccessor(
				mappedObject, referenceHolder.getPropertyName(), referenceHolder.getOwnerEntityType() );
			if (accessor != null) {
				accessor.removeByOrm(mappedObject, owner);
			}
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug( "Target of reference " + referenceHolder +" not found ");
			}
		}
	}

	/**
	 * @param item
	 * @param mappedByName
	 * @param id
	 */
	public void changeReference(MappedObject referenceOwner, String refereceName, QualifiedObjectId<?> targetBeanId) {
		BeanMapper<?> beanMapper = getBeanMapper(referenceOwner);
		Property<?> property = beanMapper.getProperty(refereceName);
		property.setValue( referenceOwner, targetBeanId );
	}

	/**
	 * @param object
	 */
	public void remove(MappedObject bean) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Remove " + bean );
		}
		ReferenceHolderList holders = this.beanToReferences.get(bean);
		if ( holders != null ) {
			for( ReferenceHolder referenceHolder : holders ) {
				breakBackReference(bean, referenceHolder);
			}
		}
		else {
			this.log.debug( "Reference holder list is null for " + bean );
		}
		this.beanToReferences.remove(bean);
	}
	
	public void remove(MappedObject bean, boolean clearReferences) {
		remove(bean);
		if ( clearReferences ) {
			for( Property<?> property : getBeanMapper(bean).getProperties() ) {
				property.resetToDefault( bean );
			}
		}
	}

	/**
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(MappedObject bean) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Add " + bean );
		}
		// TODO: think about what to do if isObjectManaged return false
		if ( !isObjectManaged(bean) ) {
			throw new IllegalArgumentException( "Object is not managed " + bean );
		}
		ReferenceHolderList references = this.beanToReferences.get( bean );
		if ( references != null ) {
			this.log.error( "Already known object " + bean );
		}
		else {
			references = getReferenceHolders(bean);
			BeanMapper<?> mapper = getBeanMapper(bean);
			((BeanMapper)mapper).refreshReferences( bean );
		}
	}

	public boolean isObjectManaged(MappedObject object) {
		return this.objectResolver.isObjectManagedByOrm(object);
	}

	/**
	 * Returns null if entityClass OR id is null.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param id
	 * @return
	 */
	public <T extends MappedObject> T resolve( Class<T> clazz, Long id ) {
		return clazz != null && id != null ? this.objectResolver.resolve(clazz, id) : null;
	}
	
	/**
	 * 
	 */
	public void clear() {
		if (this.log.isInfoEnabled() )  {
			this.log.info("Clear orm" );
		}
		this.beanToReferences.clear();
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "bean to reference size", this.beanToReferences.size() );
		tsb.append( "beanMapperProvider", this.beanMapperProvider );
		tsb.append( "objectResolver", this.objectResolver );
		return tsb.toString();
	}

	/**
	 * TODO check performance / memory usage 
	 * @param owner
	 * @return
	 */
	public <T extends MappedObject> QualifiedObjectId<? extends T> getQualifiedObjectId(T bean) {
		return this.objectResolver.getQualifiedObjectId(bean);
	}

	
}
