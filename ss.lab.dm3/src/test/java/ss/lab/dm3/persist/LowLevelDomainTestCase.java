package ss.lab.dm3.persist;


import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.persist.workers.DomainWorker;
import ss.lab.dm3.persist.workers.DomainWorkerContext;
import ss.lab.dm3.persist.workers.DomainWorkerExecutor;
import ss.lab.dm3.persist.workers.DomainWorkerHost;
import ss.lab.dm3.testsupport.DeadlineGuard;

public abstract class LowLevelDomainTestCase extends AbstractDomainTestCase implements DomainWorkerHost {

	private DeadlineGuard guard = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.guard = new DeadlineGuard( "Domain test case guard" );
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if ( this.guard != null ) {
			this.guard.waitToAllCheckpoints( 5000 );
		}
	}
	
	public WorkersBuilder createWorkersBuilder() {
		return createWorkersBuilder( getDomain() );
	}

	public WorkersBuilder createWorkersBuilder( Domain domain ) {
		this.guard.addCheckpoint( "WorkersBuilder.run" );
		return new WorkersBuilder( domain );
	}
			
	public class WorkersBuilder {
		
		final DomainWorkerExecutor<DomainWorkerContext> executor;
		final DomainWorkerHost host = LowLevelDomainTestCase.this;
		
		/**
		 * @param domain
		 */
		public WorkersBuilder(Domain domain) {
			this.executor = DomainWorkerExecutor.create(domain);
		}

		public WorkersBuilder add( Class<? extends DomainWorker<DomainWorkerContext>> workerClazz ) {
			this.executor.add(workerClazz);
			return this;
		}
		
		public WorkersBuilder add( DomainWorker<DomainWorkerContext> worker ) {
			this.executor.add(worker);
			return this;
		}
		
		@Deprecated		
		public WorkersBuilder add( DomainWorkerHost notUsed, String methodName ) {
			this.executor.add( this.host, methodName);
			return this;
		}
		
		public WorkersBuilder add( String ... methodNames ) {
			for( String methodName : methodNames ) {
				this.executor.add( this.host, methodName);
			}
			return this;
		}
		
		public WorkersBuilder addInTransaction(String methodName ) { 
			this.executor.addInTransaction(this.host, methodName);
			return this;
		}

		
		public void run() {
			LowLevelDomainTestCase.this.guard.passCheckpoint( "WorkersBuilder.run" );
			CallbackResultWaiter waiter = new CallbackResultWaiter();
			this.executor.execute(waiter);
			waiter.waitToResult();
		}

		/**
		 * @return
		 */
		public WorkersBuilder addEvictDomainObjects() {
			return add( "evictDomainObjects" );
		}

	
	}
	
	
	
}
