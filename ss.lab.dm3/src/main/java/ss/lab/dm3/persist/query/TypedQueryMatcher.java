package ss.lab.dm3.persist.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ss.lab.dm3.context.InjectionUtils;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.query.matcher.ExpressionMatcher;
import ss.lab.dm3.orm.query.matcher.ExpressionMatcherFactory;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.DomainResolverHelper;
import ss.lab.dm3.persist.QueryMatcher;
import ss.lab.dm3.persist.TypedQuery;

/**
 * @author Dmitry Goncharov
 */
public class TypedQueryMatcher extends QueryMatcher {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	public static final String DEBUG_KEY = TypedQueryMatcher.class + ".DEBUG";
	
	private final Class<? extends DomainObject> objectClazz;
	
	private final ExpressionMatcher expressionMatcher;

	public TypedQueryMatcher(TypedQuery<? extends DomainObject> criteria) {
		this.objectClazz = criteria.getEntityClass();
		ExpressionMatcherFactory factory = getExpressionMatcherFactory( this.objectClazz );
		this.expressionMatcher = factory.create(criteria.getRestriction());
	}

	/**
	 * @param objectClazz
	 */
	public TypedQueryMatcher(Class<? extends DomainObject> objectClazz) {
		super();
		this.objectClazz = objectClazz;
		this.expressionMatcher = ExpressionMatcherFactory.createMatchAll();
	}
	
	@Override
	public boolean match(DomainObject object) {
		return this.objectClazz.isInstance( object ) && this.expressionMatcher.match(object);
	}

	public void removeUnmatched(Collection<? extends DomainObject> target ) {
		Set<DomainObject> objectsToRemove = null;
		for( DomainObject object : target ) {
			if ( !match(object) ) {
				if ( objectsToRemove == null ) {
					objectsToRemove = new HashSet<DomainObject>(); 
				}					
				objectsToRemove.add( object );	
			}
			else {
//				if ( this.log.isDebugEnabled() && InjectionUtils.find( Boolean.class, DEBUG_KEY, Boolean.FALSE ) ) {
//					this.log.debug( "Object matched " + object + " to " + this );
//				}
			}
		}
		if ( objectsToRemove != null ) {
			for( DomainObject object : objectsToRemove ) {
				if ( this.log.isDebugEnabled() && InjectionUtils.find( Boolean.class, DEBUG_KEY, Boolean.FALSE ) ) {
					this.log.debug( "Object " + object + " is not matched to " + this.expressionMatcher );					
				}				
				target.remove( object );
			}
		}
	}
	
	/**
	 * @return
	 */
	private static ExpressionMatcherFactory getExpressionMatcherFactory( Class<? extends DomainObject> objectClazz ) {
		// TODO [dg] move getExpressionMatcherFactory() to domain
		final Mapper<DomainObject> mapper = DomainResolverHelper.getCurrentDomain().getMapper();
		return new ExpressionMatcherFactory( mapper.get(objectClazz) );
	}
	
}
