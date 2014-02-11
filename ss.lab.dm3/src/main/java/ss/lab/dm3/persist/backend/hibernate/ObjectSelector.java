package ss.lab.dm3.persist.backend.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.persist.backend.search.SearchEngine;
import ss.lab.dm3.persist.backend.search.SearchQueryAdaptor;
import ss.lab.dm3.persist.query.QueryList;

/**
 * @author Dmitry Goncharov
 */
public class ObjectSelector {

	private final Session session;
	
	private final QueryConverter converter;
	
	private final SearchEngine searchEngine;
	
	/**
	 * @param session
	 * @param searchEngine 
	 * @param dataMapper
	 * @param domainMapper
	 */
	public ObjectSelector(Session session, Mapper<DomainObject> mapper, SearchEngine searchEngine) {
		this.session = session;
		this.converter = new QueryConverter( session, mapper  );
		this.searchEngine = searchEngine;
	}

	public <T extends DomainObject> T select( Class<T> objectClazz, Long id ) {
		return objectClazz.cast( this.session.get( objectClazz, id) );
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DomainObject> List<T> select( Class<T> objectClazz ) {
		return this.session.createCriteria( objectClazz ).list();
	}
	
	public QuerySelectResult select( ss.lab.dm3.persist.Query domQuery ) {
		if ( domQuery instanceof QueryList ) {
			List<DomainObject> objects = new ArrayList<DomainObject>(); 
			final QueryList criteriaList = (QueryList)domQuery;
			for( ss.lab.dm3.persist.Query subCriteria : criteriaList.getQueries() ) {
				objects.addAll( select( subCriteria ).getItems() );
			}
			return new QuerySelectResult( objects );
		}
		else if ( domQuery instanceof TypedQuery ) {
			TypedQuery<?> domTypedQuery = (TypedQuery<?>) domQuery;
			if ( QueryHelper.isIdQuery(domTypedQuery) ) {
				final Long id = QueryHelper.getIdQueryValue(domTypedQuery);
				ArrayList<DomainObject> items = new ArrayList<DomainObject>();
				items.add( select( domTypedQuery.getEntityClass(), id ) );
				return new QuerySelectResult( items );
			}
			else if ( this.converter.canConvertToCriterai(domTypedQuery) ) {
				Criteria hibCriteria = this.converter.convertToCriteria( domTypedQuery );
				return  new QuerySelectResult( select(hibCriteria) );
			} 
			else if ( this.converter.canConvertToLuceneCriterai(domTypedQuery)  ) {
				SearchQueryAdaptor searchQuery = new SearchQueryAdaptor( domTypedQuery );
				return this.searchEngine.search( searchQuery, this );
			} 
			else {
				Query hibQuery = this.converter.convertToQuery(domTypedQuery);
				return new QuerySelectResult( select(this.converter.getDataClass( domTypedQuery ), hibQuery ) );
			}
		}
		else { 
			throw new IllegalArgumentException( "Unsupported domain criteria " + domQuery );
		}
	}
	
	/**
	 * @param dataClass
	 * @param hibQuery
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DomainObject> select(Class<? extends DomainObject> dataClass, Query hibQuery) {
		final List<DomainObject> queryResult = hibQuery.list();
		final List<DomainObject> filteredResult = new ArrayList<DomainObject>( queryResult.size() );
		for( DomainObject object : queryResult ) {
			if ( dataClass.isInstance( object ) ) {
				filteredResult.add( object );
			}	
		}
		return filteredResult;
	}

	@SuppressWarnings("unchecked")
	public List<DomainObject> select( Criteria hibCriteria ) {
		return hibCriteria.list();
	}

	/**
	 * @param id
	 * @return
	 */
	public <T extends DomainObject> T select(QualifiedObjectId<T> id) {
		return select( id.getObjectClazz(), id.getId() );
	}

	public Session getSession() {
		return this.session;
	}

	public SearchEngine getSearchEngine() {
		return this.searchEngine;
	}
	
}
