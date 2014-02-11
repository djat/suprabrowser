package ss.lab.dm3.persist.backend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.IObjectResolver;
import ss.lab.dm3.orm.ObjectResolver;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.entity.EntityList;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.persist.DomainObject;

public class CreatedObjectResolver extends ObjectResolver {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
			
	private final IObjectResolver defaultResolver;

	private final Mapper<DomainObject> mapper;

	private final HashMap<QualifiedObjectId<?>, DomainObject> idToObject = new HashMap<QualifiedObjectId<?>, DomainObject>();
	private final HashMap<Entity, DomainObject> entityToObject = new HashMap<Entity, DomainObject>();
	private final Set<DomainObject> objects = new HashSet<DomainObject>();

	/**
	 * @param defaultResolver
	 * @param mapper
	 * @param entities
	 */
	public CreatedObjectResolver(IObjectResolver defaultResolver, Mapper<DomainObject> mapper,
			EntityList entities) {
		super();
		this.defaultResolver = defaultResolver;
		this.mapper = mapper;
		for (Entity entity : entities) {
			final BeanMapper<DomainObject> beanMapper = this.mapper.get(entity);
			final DomainObject createdObject = beanMapper.createObject( false );
			final QualifiedObjectId<DomainObject> objectId = beanMapper.getObjectId(entity);
			createdObject.setId(objectId.getId());
			this.entityToObject.put(entity, createdObject);
			this.idToObject.put(objectId, createdObject);
			this.objects.add(createdObject);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.orm.ObjectResolver#resolve(java.lang.Class,
	 *      java.lang.Long)
	 */
	@Override
	public <T extends MappedObject> T resolve(Class<T> entityClass, Long id) {
		DomainObject object = this.idToObject.get(new QualifiedObjectId<T>(entityClass, id));
		if (object == null) {
			return this.defaultResolver.resolve(entityClass, id);
		}
		else {
			return entityClass.cast(object);
		}
	}

	public void loadCreatedObjectDataFromEntities() {
		for (Entity entity : this.entityToObject.keySet()) {
			DomainObject object = this.entityToObject.get(entity);
			this.mapper.toObject(object, entity);
		}
	}
	
	/**
	 * @return
	 */
	public Iterable<DomainObject> getCreatedObjects() {
		return this.idToObject.values();
	}

	@Override
	public boolean isObjectManagedByOrm(MappedObject object) {
		return this.defaultResolver.isObjectManagedByOrm(object);
	}

	@Override
	public <T extends MappedObject> QualifiedObjectId<? extends T> getQualifiedObjectId(
			T bean) {
		return this.defaultResolver.getQualifiedObjectId(bean);
	}
	
	

}
