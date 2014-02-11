package ss.lab.dm3.persist;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.persist.query.GenericQuery;

/**
 * 
 * @author Dmitry Goncharov
 * 
 */
public class LazyObjectLoader {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	public <T extends DomainObject> T find( Context context, Class<T> objectClazz, Long id ) {
		final T result = find( context, QueryHelper.eq(objectClazz, id) ).getSingle();
		if (this.log.isDebugEnabled()) {
			this.log.debug("Loaded data for object " + objectClazz.getSimpleName() + "#" + id );
		}
		return result;
	}
	
	public <T extends DomainObject> DomainObjectCollector<T> find( Context context, TypedQuery<T> query) {
		DomainLoader loader = new DomainLoader( query, context.getSpace() );
		CallbackResultWaiter waiter = new CallbackResultWaiter();
		loader.beginLoad( context.getDomain(), waiter );
		SelectResult selectResult = waiter.waitToResult( SelectResult.class );
		DomainObjectCollector<T> collector = new DomainObjectCollector<T>( query );
		collector.add( selectResult.getResultList() );
		collector.setTotalCount( selectResult.getResultTotalCount() );
		return collector;
	}

	public <T> T evaluate( Context context, GenericQuery<T> query) {
		DomainLoader loader = new DomainLoader( query );
		CallbackResultWaiter waiter = new CallbackResultWaiter();
		loader.beginLoad( context.getDomain(), waiter );
		SelectResult selectResult = waiter.waitToResult( SelectResult.class );
		Object rawObject = selectResult.getGeneric();
		return query.getGenericClazz().cast( rawObject );
	}
}
