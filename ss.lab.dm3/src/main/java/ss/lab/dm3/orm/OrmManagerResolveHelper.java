package ss.lab.dm3.orm;

import java.util.Stack;

import ss.lab.dm3.persist.orm.DomainOrmManagerProvider;

/**
 * @author Dmitry Goncharov
 */
public class OrmManagerResolveHelper {

	private static OrmManagerProvider MANAGER_PROVIDER = new DomainOrmManagerProvider();
	
	private static final ThreadLocal<InterceptionStack> INTERCEPTION_LIST = new ThreadLocal<InterceptionStack>();
	
	public synchronized static OrmManagerProvider getOrmManagerProvider() {
		return MANAGER_PROVIDER;
	}

	public synchronized static void setOrmManagerProvider(OrmManagerProvider ormManagerProvider) {
		MANAGER_PROVIDER = ormManagerProvider;
	}
	
	/**
	 * @param owner
	 * @return
	 */
	public static OrmManager resolve(MappedObject owner) {
		final InterceptionStack interceptions = INTERCEPTION_LIST.get();
		if ( interceptions != null && interceptions.size() > 0 ) {
			return interceptions.top();
		}
		else {
			final OrmManagerProvider managerProvider = getOrmManagerProvider();
			if ( managerProvider == null ){ 
				throw new NullPointerException( "Orm manager provider is null" );
			}
			OrmManager manager = managerProvider.get();
			if ( manager == null ) {
				throw new NullPointerException( "Provider " + managerProvider + " returns null manager" );
			}
			return manager;
		}
	}

	/**
	 * @param ormManager 
	 * 
	 */
	public static void beginInterceptionForCurrentThread(OrmManager ormManager) {
		InterceptionStack interceptions = INTERCEPTION_LIST.get();
		if ( interceptions == null ) {
			interceptions = new InterceptionStack();
			INTERCEPTION_LIST.set( interceptions );
		}
		interceptions.push(ormManager);
	}

	/**
	 * 
	 */
	public static void endInterceptionForCurrentThread() {
		INTERCEPTION_LIST.get().pop();
	}
	
	static class InterceptionStack {

		private Stack<OrmManager> managers = new Stack<OrmManager>();
		
		/**
		 * @return
		 */
		public OrmManager top() {
			return this.managers.peek();
		}

		public void push( OrmManager ormManager ) {
			if ( ormManager == null ) {
				throw new NullPointerException( "ormManager" );
			}
			this.managers.push( ormManager );
		}
		
		public void pop() {
			this.managers.pop();
		}
		
		/**
		 * @return
		 */
		public int size() {
			return this.managers.size();
		}
		
	}

	/**
	 * @return
	 */
	public static OrmManager resolve() {
		return resolve( null );
	}

}
