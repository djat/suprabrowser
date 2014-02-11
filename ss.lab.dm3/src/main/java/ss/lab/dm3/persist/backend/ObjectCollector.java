package ss.lab.dm3.persist.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.orm.IBeanMapperProvider;
import ss.lab.dm3.orm.OrmManagerResolveHelper;
import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.orm.mapper.property.descriptor.impl.ManagedCollectionDescriptor;
import ss.lab.dm3.persist.CascadeFetchedList;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.FetchedDomainObjectLists;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.backend.hibernate.ObjectSelector;

/**
 * @author Dmitry Goncharov
 */
public class ObjectCollector implements IObjectCollector {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private static final DomainObject EVICTED_OBJECT = new DomainObject( new Long(-1L) ) {
	};
	
	private final HashMap<QualifiedObjectId<? extends DomainObject>, DomainObject> idToObject = new HashMap<QualifiedObjectId<? extends DomainObject>, DomainObject>();
	
	private final List<DomainObject> selected = new ArrayList<DomainObject>();
	
	private int selectedTotalCount = -1;
	
	private final List<DomainObject> cascaded = new ArrayList<DomainObject>();
	
	private final List<FetchedDomainObjectLists> fetchedLists = new ArrayList<FetchedDomainObjectLists>();
	
	private final ObjectSelector selector;
	
	private final IBeanMapperProvider beanMapperProvider;
	
	public ObjectCollector(ObjectSelector selector) {
		super();
		if ( selector == null ) {
			throw new NullPointerException("selector");
		}
		this.selector = selector;
		this.beanMapperProvider = OrmManagerResolveHelper.resolve().getBeanMapperProvider();
	}

	public boolean add( DomainObject object ) {
		return add(this.selected, object);
	}

	private boolean add(final List<DomainObject> targetList, DomainObject object) {
		QualifiedObjectId<? extends DomainObject> id = object.getQualifiedId();
		final boolean shouldAdd = !contains( id );
		if (this.log.isDebugEnabled()) {
			this.log.debug("Collecting object with id " + id + " = " + shouldAdd );
		}
		if ( shouldAdd ) {
			this.idToObject.put( id, object );
			targetList.add( object );
			processCascade(object);			
			return true;
		}
		else {
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void processCascade(DomainObject object) {
		final BeanMapper<DomainObject> beanMapper = (BeanMapper<DomainObject>) this.beanMapperProvider.get(object);
		for (Property<?> property : beanMapper.getReferenceProperties()) {
			final PropertyDescriptor<?> descriptor = property.getDescriptor();
			if (descriptor.isCascadeFetch()) {
				final Object value = property.getValue(object);
				if (value != null) {
					final DomainObject cascaded = this.selector
							.select(((DomainObject) value).getQualifiedId());
					addCasscaded(cascaded);					
				}
			}
		}	
		// Collection properties
		List<CascadeFetchedList> cascadeFetchedLists = new ArrayList<CascadeFetchedList>();
		for (Property<?> property : beanMapper.getCollectionProperties() ) {
			final PropertyDescriptor<?> descriptor = property.getDescriptor();
			if (descriptor.isCascadeFetch()) {
				final Object value = property.getValue(object);
				if (value != null) {
					ManagedCollectionDescriptor<?> collectionDescriptor = (ManagedCollectionDescriptor<?>) descriptor;
					Class<? extends DomainObject> clazz = (Class<? extends DomainObject>) collectionDescriptor.getItemType();
					final String propertyName = collectionDescriptor.getMappedByName();
					final TypedQuery<? extends DomainObject> query = QueryHelper.eq( clazz, propertyName, object.getId() );
					final CascadeFetchedList cascadeFetchedList = new CascadeFetchedList( property.getName(), query );
					//
					List<QualifiedObjectId<DomainObject>> listQualifiedObjs = new ArrayList<QualifiedObjectId<DomainObject>>();
					for (DomainObject domainObject : this.selector.select( query ) ) {
						addCasscaded( domainObject );
						cascadeFetchedList.add( (QualifiedObjectId<DomainObject>) domainObject.getQualifiedId() );
						listQualifiedObjs.add( (QualifiedObjectId<DomainObject>) domainObject.getQualifiedId() );
					}
					
					cascadeFetchedLists.add( cascadeFetchedList );	
				}
			}
		}
		if ( !cascadeFetchedLists.isEmpty() ) {
			this.fetchedLists.add( new FetchedDomainObjectLists((QualifiedObjectId<DomainObject>) object.getQualifiedId(), cascadeFetchedLists) );
		}
	}

	/**
	 * @param select
	 */
	private void addCasscaded(DomainObject object) {
		add(this.cascaded, object);
	}

	/**
	 * @param qualifiedObjectId
	 * @return
	 */
	public boolean contains(
			QualifiedObjectId<? extends DomainObject> qualifiedObjectId) {
		return this.idToObject.containsKey( qualifiedObjectId );
	}

	public boolean contains(DomainObject object) {
		return object != null && contains( object.getQualifiedId() );
	}
	
	/**
	 */
	public void block(QualifiedObjectId<? extends DomainObject> qualifiedId) {
		DomainObject object = this.idToObject.get( qualifiedId );
		if ( object != null ) {
			//TODO solve what to do
		}
		else {
			this.idToObject.put( qualifiedId, EVICTED_OBJECT );
		}
	}

	/**
	 * @param select
	 */
	public int add(Iterable<DomainObject> objects) {
		int count = 0;
		int nullCount = 0;
		for( DomainObject object : objects ) {
			if ( object == null ) {
				++ nullCount; 
			}
			else {
				if ( add( object ) ) {
					++ count;
				}
			}
		}
		if ( nullCount > 0 ) {
			if ( count != 0 || nullCount != 1 ) {
				throw new IllegalArgumentException( "Invalid objects. Objects has " + nullCount + " null objects and " + count + " not null objects. Details: " + objects );
			}
		}
		return count;
	}

	/**
	 * @return
	 */
	public List<DomainObject> getSelected() {
		return this.selected;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "object.size", this.selected.size() );
		return tsb.toString();
	}

	public List<DomainObject> getCascsded() {
		return this.cascaded;
	}

	public List<FetchedDomainObjectLists> getFetchedLists() {
		return this.fetchedLists;
	}

	public ObjectSelector getSelector() {
		return this.selector;
	}

	public int getSelectedTotalCount() {
		return Math.max( this.selected.size(), this.selectedTotalCount );
	}

	public void setSelectedTotalCount(int selectedTotalCount) {
		this.selectedTotalCount = selectedTotalCount;
	}
	
}
