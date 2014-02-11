package ss.lab.dm3.persist;

import org.apache.commons.lang.builder.ToStringBuilder;

import ss.lab.dm3.connection.CallbackHandlerException;
import ss.lab.dm3.connection.CallbackTypedHandler;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.persist.backend.EntitiesSelectResult;
import ss.lab.dm3.persist.service.DataProviderAsync;
import ss.lab.dm3.persist.space.Space;

/**
 * @author Dmitry Goncharov
 */
public class DomainLoader {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(DomainLoader.class);
	
	private final Query query;
	
	private final Space space;
	
	public DomainLoader(Query query) {
		this( query, null );
	}
	
	/**
	 * @param query
	 */
	public DomainLoader(Query query, Space space) {
		super();
		if ( query == null ) {
			throw new NullPointerException( "query" );
		}
		this.query = query;
		this.space = space;
	}

	/**
	 * @param domain
	 * @param dataLoaderHandler
	 */
	public void beginLoad(final Domain domain, final ICallbackHandler handler) {
		if (log.isDebugEnabled()) {
			log.debug("Begin load " + this );
		}
		DataProviderAsync dataProvider = domain.getDataProvider();
		ICallbackHandler selectDataHandler = domain.createDomainSafeProxy( new SelectDataHandler( handler, domain) );
		dataProvider.selectData( this.query, selectDataHandler );
	}
	
	
	
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		tsb.append( "query", this.query ); 
		return tsb.toString();
	}




	/**
	 *
	 */
	private final class SelectDataHandler extends
			CallbackTypedHandler<EntitiesSelectResult> {
		
		/**
		 * 
		 */
		private final ICallbackHandler handler;
		/**
		 * 
		 */
		private final Domain domain;

		/**
		 * @param expectedResult
		 * @param handler
		 * @param domain
		 */
		private SelectDataHandler(ICallbackHandler handler, Domain domain) {
			super(EntitiesSelectResult.class);
			this.handler = handler;
			this.domain = domain;
		}

		@Override
		protected void typedOnSuccess(final EntitiesSelectResult resultEntities) throws CallbackHandlerException {
			super.typedOnSuccess(resultEntities);
			if (DomainLoader.log.isDebugEnabled()) {
				DomainLoader.log.debug("Received data " + resultEntities );
			}	
			SelectResult loaded = this.domain.onDataLoaded( DomainLoader.this.space, resultEntities );
			//TODG work with collections
			if ( this.handler != null ) { 
				this.handler.onSuccess( loaded );
			}
		}

		@Override
		public void onFail(final Throwable ex) {
			if ( this.handler != null ) {
				this.handler.onFail( ex );
			}
			else {
				super.onFail(ex);
			}
		}
	}

}
