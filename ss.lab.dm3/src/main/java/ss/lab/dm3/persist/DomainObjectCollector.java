package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.context.InjectionUtils;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.property.Property;

/**
 * @author Dmitry Goncharov
 */
public class DomainObjectCollector<T extends DomainObject> implements Iterable<T>{

	public static final Object DEBUG_KEY = DomainObjectCollector.class + ".DEBUG";
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final Class<T> itemClass;
	
	private final Collection<T> items = new ArrayList<T>();
	
	private int totalCount = -1;

	private final TypedQuery<T> query;
	
	private QueryMatcher queryMatcher;
	
	/**
	 * @param entityClass
	 */
	public DomainObjectCollector(TypedQuery<T> query) {
		this.itemClass = query.getEntityClass();
		this.query = query;
	}
	
	public Class<T> getItemClass() {
		return this.itemClass;
	}

	public void add(DomainObject domainObject) {
		if ( domainObject == null ) {
			throw new NullPointerException( "domainObject" );
		}
		if ( !this.itemClass.isInstance( domainObject ) ) {
			throw new IllegalArgumentException( "Can't add domain object " + domainObject + " to " + this );
		}
		this.items.add( this.itemClass.cast( domainObject ) );
	}

	public void add(Iterable<? extends DomainObject> objects) {
		for( DomainObject object : objects ) {
			add( object );
		}
	}
	
	/**
	 * @return
	 */
	public int size() {
		return this.items.size();
	}

	/**
	 * @return
	 */
	public T getFirst() {
		if ( this.items.isEmpty() ) {
			throw new IndexOutOfBoundsException( "Can't get first element from empty set" );
		}
		if ( this.items.size() > 1 ) {
			this.log.warn( "Getting first object from " + this.items.size() + " set." );
		}
		return this.items.iterator().next();
	}

	/**
	 * @return
	 */
	public T getFirstOrNull() {
		return this.items.size() > 0 ? getFirst() : null;
	}
	
	/**
	 * @return
	 */
	public List<T> toList() {
		return new ArrayList<T>( this.items );
	}

	public <P> Map<P,T> toMap( Class<P> propertyClazz, String propertyName ) {
		Map<P,T> map = new HashMap<P, T>();
		final Mapper<DomainObject> mapper = DomainResolverHelper.getCurrentDomain().getMapper();
		final BeanMapper<DomainObject> beanMapper = mapper.get( this.itemClass );
		final Property<?> property = beanMapper.getProperty(propertyName);
		for( T item : this.items ) {
			Object rawPropertyValue = property.getValue( item );
			if ( rawPropertyValue != null ) {
				P propertyValue = propertyClazz.cast(rawPropertyValue);
				map.put(propertyValue, item);
			}
			else {
				// For now - skip 
			}
		}
		return map;
	}
	
	/**
	 * @return
	 */
	public TypedQuery<T> getQuery() {
		return this.query;
	}

	/**
	 * @return
	 */
	public QueryMatcher getQueryMatcher() {
		if ( this.queryMatcher == null ) {
			this.queryMatcher = QueryMatcherFactory.INSTANCE.create(this.query);
		}
		return this.queryMatcher;
	}

	public Iterator<T> iterator() {
		return this.items.iterator();
	}

	public Set<T> toSet() {
		return new HashSet<T>( this.items );
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "query", this.query );
		tsb.append( "size", size() );
		return tsb.toString();
	}

	public T getSingleOrNull() {
		if ( this.size() > 1 ) {
			throw new ObjectNotFoundException( "Can't get signle. Found '" + this.size() + "' items by " + this.query );
		}
		else if ( this.size() == 0 ) {
			return null;
		}
		else {
			return getFirst();
		}
	}
	
	/**
	 * @return
	 */
	public T getSingle() {
		T result = getSingleOrNull();
		if ( result == null ) {
			throw new ObjectNotFoundException( this.query );
		}
		return result;
	}

	/**
	 * @param typedQuery
	 * @return
	 */
	public static <E extends DomainObject> DomainObjectCollector<E> create(TypedQuery<E> typedQuery) {
		return new DomainObjectCollector<E>( typedQuery );
	}

	/**
	 * Skips already existed objects
	 * @param target
	 */
	@SuppressWarnings("unchecked")
	public void copyUnexistedTo(Collection<? extends DomainObject> target) {
		Collection<T> castedTarget = (Collection<T>) target;
		for( T obj : this.items ) {
			if ( !castedTarget.contains( obj ) ) {
				if (this.log.isDebugEnabled() && InjectionUtils.find( Boolean.class, DEBUG_KEY, Boolean.FALSE ) ) {
					this.log.debug("Adding " + obj + " to " + castedTarget );
				}
				castedTarget.add( obj );
			}
		}
	}

	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		for( DomainObject obj : this ) {
			if ( sb.length() > 0 ) {
				sb.append( ", " ); 
			}
			sb.append( obj.toShortString() );
		}
		return sb.toString();
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	
	
}
