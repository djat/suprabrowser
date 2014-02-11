package ss.lab.dm3.persist.backend.search;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import ss.lab.dm3.orm.IBeanMapperProvider;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.OrmManagerResolveHelper;
import ss.lab.dm3.orm.QualifiedUtils;
import ss.lab.dm3.orm.mapper.BeanMapper;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.property.Property;
import ss.lab.dm3.orm.mapper.property.descriptor.PropertyDescriptor;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.search.ISearchable;

public class SearchHelper {

	protected final static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(SearchHelper.class);
	
	public static final String SECURE_LOCK_FIELD_NAME = "secureLock";

	public static final String LOCKED = "locked";

	public static final String PUBLIC = "public";
	
	private static Field createField(String lock ) {
		return new Field( SECURE_LOCK_FIELD_NAME, lock, Field.Store.YES, Field.Index.TOKENIZED );
	}
	
	public static void collectSecureField( ISearchable searchable, Document collector, boolean publicAsDefault ) {
		if ( searchable instanceof ISearchableSecureProvider ) {
			collectSecureField(collector,(ISearchableSecureProvider) searchable );
		}
		else {
			collector.add( createField( publicAsDefault ? PUBLIC : LOCKED ) );
		}
	}


	public static void collectSecureField(Document collector,ISearchableSecureProvider secureLockProvider) {
		StringBuilder lockBuilders = new StringBuilder();
		if ( secureLockProvider.isPublicForSearch()  ) {
			if (log.isDebugEnabled()) {
				log.debug(secureLockProvider + " is public");
			}
			lockBuilders.append( PUBLIC );
		}
		else {
			SecureLockCollector secureLockCollector = new SecureLockCollector();
			secureLockProvider.collectSecureLocks(secureLockCollector);
			if ( secureLockCollector.isEmpty() ) {
				lockBuilders.append( LOCKED );
			}
			else {
				secureLockCollector.flushTo( lockBuilders );
			}
		}	
		collector.add( createField(lockBuilders.toString()));
	}

	public static void addSecureRestriction( StringBuilder query, SecureLockCollector keys ) {
		addAndIfRequired(query);
		query.append( SECURE_LOCK_FIELD_NAME );
		query.append( ":( ");
		if ( keys != null ) {
			keys.flushTo(query);
		}
		query.append( " " );
		query.append( PUBLIC );
		query.append( " )");
	}

	public static void addAndIfRequired(StringBuilder query) {
		if (query.length() > 0 ) {
			query.insert( 0, "( " );
			query.append( " ) AND " );
		}
	}
		
	public static void collectSearchableFields(ISearchable object, Document collector) {
		IBeanMapperProvider beanMapperProvider = OrmManagerResolveHelper.resolve().getBeanMapperProvider();
		final BeanMapper<?> beanMapper = beanMapperProvider.get( object );
		for (Property<?> property : beanMapper.getProperties()) {
			final PropertyDescriptor<?> descriptor = property.getDescriptor();
			if (descriptor.isSearchableField()) {
				final Object value = property.getValue(object);
				collector.add( new Field(property.getName(), String.valueOf( value ), Field.Store.YES,
						Field.Index.TOKENIZED) );
			}
		}
	}
	
	public static void collectByDefault(ISearchable object, Document collector) {
		collectSearchableFields(object, collector);
		collectSecureField(object, collector, true );
	}
	
	/**
	 * TODO move it out to SearchHelper
	 * @param itemClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String createQualifierRestriction(Class<? extends DomainObject> itemClass) {
		StringBuilder sb = new StringBuilder();
		Mapper<DomainObject> mapper = (Mapper<DomainObject>) OrmManagerResolveHelper.resolve().getBeanMapperProvider().get();
		List<Class<? extends DomainObject>> listItemClasses = mapper.getKnownSubclasses(itemClass);
		if (!listItemClasses.isEmpty()) {
			sb.append("qualifier:( ");
			for (Class<?> clazz : listItemClasses) {
				sb.append(clazz.getSimpleName());
				sb.append(" ");
			}
			sb.append(")");
		}
		else {
			log.error( "Qualified list is empty for " + itemClass );
		}
		return sb.toString();
	}

	public static String toLock(Class<? extends MappedObject> clazz, Long id) {
		return QualifiedUtils.resolveQualifier(clazz) + "#" + id;
	}

	public static void addQualifierRestriction(StringBuilder queryText,
			Class<? extends DomainObject> clazz) {
		SearchHelper.addAndIfRequired(queryText);
		queryText.append(createQualifierRestriction(clazz));		
	}
}
