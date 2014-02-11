package ss.lab.dm3.persist.workers;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.persist.Domain;

public class SimpleDomainWorkerRunner {

	final DomainWorkerExecutor<DomainWorkerContext> executor;
	final DomainWorkerHost host;
	
	public SimpleDomainWorkerRunner(Domain domain) {
		this( domain, null );
	}
	/**
	 * @param domain
	 */
	public SimpleDomainWorkerRunner(Domain domain, DomainWorkerHost host ) {
		this.executor = DomainWorkerExecutor.create(domain);
		this.host = host;
	}

	public SimpleDomainWorkerRunner add( Class<? extends DomainWorker<DomainWorkerContext>> workerClazz ) {
		this.executor.add(workerClazz);
		return this;
	}
	
	public SimpleDomainWorkerRunner add( DomainWorker<DomainWorkerContext> worker ) {
		this.executor.add(worker);
		return this;
	}
	
	public SimpleDomainWorkerRunner add( DomainWorkerHost host, String ... methodNames ) {
		for( String methodName : methodNames ) {
			this.executor.add( host, methodName);
		}
		return this;
	}
	
	public SimpleDomainWorkerRunner add( String ... methodNames ) {
		if ( this.host == null ) {
			throw new IllegalStateException( "Can't add method. Host is null." ); 
		}
		for( String methodName : methodNames ) {
			this.executor.add( this.host, methodName);
		}
		return this;
	}
	
	public SimpleDomainWorkerRunner addInTransaction(DomainWorkerHost host, String ... methodNames ) {
		for( String methodName : methodNames ) {
			this.executor.addInTransaction(this.host, methodName);
		}
		return this;
	}
	
	public SimpleDomainWorkerRunner addInTransaction(String ... methodNames ) { 
		if ( this.host == null ) {
			throw new IllegalStateException( "Can't add method. Host is null." ); 
		}
		for( String methodName : methodNames ) {
			this.executor.addInTransaction(this.host, methodName);
		}
		return this;
	}

	
	public CallbackResultWaiter execute() {
		CallbackResultWaiter waiter = new CallbackResultWaiter();
		this.executor.execute(waiter);
		return waiter;
	}

}
