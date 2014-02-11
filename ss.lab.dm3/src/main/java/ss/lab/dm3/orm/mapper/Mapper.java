package ss.lab.dm3.orm.mapper;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.entity.Entity;
import ss.lab.dm3.orm.mapper.map.BeanMap;
import ss.lab.dm3.orm.mapper.map.BeanSpace;

/**
 * TODO implement object class resolving strategy for proxy objects support.
 * Currently hibernate proxy objects generated for data objects cause problem with data object class checking/getting  
 * 
 * @author Dmitry Goncharov
 */
public class Mapper<T extends MappedObject> implements Iterable<BeanMapper<T>> {

	private final Class<T> baseBeanClass;

	private final BeanSpace beanSpace;

	private final Hashtable<Long, BeanMapper<T>> mapIdToMapper = new Hashtable<Long, BeanMapper<T>>();

	private final Hashtable<Class<?>, BeanMapper<T>> classToMapper = new Hashtable<Class<?>, BeanMapper<T>>();
	private final Hashtable<Class<?>, List<Class<? extends T>>> classToSubclasses = new Hashtable<Class<?>, List<Class<? extends T>>>();

	private final BeanWrapper<T> beanWrapper;
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Mapper(BeanSpace beanSpace, BeanWrapper<T> beanWrapper) {
		super();
		this.baseBeanClass = (Class<T>) beanSpace.getBaseBeanClazz();
		this.beanSpace = beanSpace;
		this.beanWrapper = beanWrapper;
		for (BeanMap map : beanSpace) {
			BeanMapper<T> mapper = new BeanMapper<T>(map, this.beanWrapper);
			add(mapper);
		}
	}

	private void add(BeanMapper<T> mapper) {
		this.mapIdToMapper.put(mapper.getMapId(), mapper);
		this.classToMapper.put(mapper.getObjectClass(), mapper);
	}

	/**
	 * @param entity
	 */
	public QualifiedObjectId<T> getObjectId(Entity entity) {
		BeanMapper<T> mapper = get(entity.getMapId());
		return mapper.getObjectId(entity);
	}

	/**
	 * @param entity
	 * @return
	 */
	public T toObject(Entity entity, boolean setUpManagedFeatures) {
		BeanMapper<T> mapper = get(entity.getMapId());
		return mapper.toObject(entity, setUpManagedFeatures );
	}

	public void toObject(T obj, Entity entity) {
		BeanMapper<T> mapper = get(entity.getMapId());
		mapper.toObject(obj, entity);
	}

	public BeanMapper<T> get(Entity entity) {
		return get(entity.getMapId());
	}

	/**
	 * @param mapId
	 * @return
	 */
	public BeanMapper<T> get(Long mapId) {
		BeanMapper<T> mapper = this.mapIdToMapper.get(mapId);
		if (mapper == null) {
			throw new CantFindBeanMapperException(mapId);
		}
		return mapper;
	}

	public Long getMapId(Class<? extends T> objClazz) {
		return get(objClazz).getMapId();
	}

	/**
	 * @param mapId
	 * @return
	 */
	public BeanMapper<T> get(Class<? extends T> objClazz) {
		BeanMapper<T> mapper = this.classToMapper.get(objClazz);
		if (mapper == null) {
			throw new CantFindBeanMapperException(objClazz);
		}
		return mapper;
	}

	/**
	 * @param message
	 * @return
	 */
	public Entity toEntity(T object) {
		BeanMapper<T> mapper = get(object);
		return mapper.toEntity(object);
	}

	public BeanMapper<T> get(T object) {
		Class<? extends T> objClazz = this.beanWrapper.getBeanClass( object );
		return get(objClazz);
	}

	public Class<T> getBaseObjectClass() {
		return this.baseBeanClass;
	}

	/**
	 * @return
	 */
	public BeanSpace getBeanSpace() {
		return this.beanSpace;
	}

	/**
	 * @param toObject
	 * @param fromObject
	 */
	public void objectToObject(T toObject, T fromObject) {
		BeanMapper<T> mapper = get(toObject);
		mapper.toObject( toObject, fromObject);
	}

	/**
	 * @param string
	 * @return
	 */
	public BeanMapper<T> get(String entityName) {
		final BeanMap beanMap = this.beanSpace.get(entityName);
		return get( beanMap.getId() );
	}
	
	/**
	 * @param domainObject
	 */
	public void setUpManagedFeatures(T object) {
		get(object).setUpManagedFeatures(object);
	}

	public Iterator<BeanMapper<T>> iterator() {
		return this.mapIdToMapper.values().iterator();
	}

	/**
	 * @return
	 */
	public Object[] toArray() {
		return this.mapIdToMapper.values().toArray();
	}

	/**
	 * 
	 * 
	 * @param objectClazz
	 */
	public List<Class<? extends T>> getKnownSubclasses(Class<? extends T> clazz) {
		List<Class<? extends T>> subClasses = this.classToSubclasses.get( clazz );
		if ( subClasses == null) {
			subClasses = new ArrayList<Class<? extends T>>(); 
			for( BeanMapper<T> subMapper : this.classToMapper.values() ) {
				if ( !subMapper.isInheritanceBase() 
				    && clazz.isAssignableFrom( subMapper.getObjectClass() ) ) {
					subClasses.add( subMapper.getObjectClass() );
				}
			}
			this.classToSubclasses.put( (Class<?>)clazz, subClasses);
		}
		return subClasses;
	}

}
