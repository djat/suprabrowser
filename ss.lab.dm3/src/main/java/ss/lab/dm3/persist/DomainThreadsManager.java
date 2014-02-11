package ss.lab.dm3.persist;

import java.util.Hashtable;

/**
 * @author Dmitry Goncharov
 */
public class DomainThreadsManager {

	public final static DomainThreadsManager INSTANCE = new DomainThreadsManager();

	private final Hashtable<Domain,Thread> domainToThread = new Hashtable<Domain, Thread>();   
	private final Hashtable<Thread, Domain> threadToDomain = new Hashtable<Thread, Domain>();
	
	private DomainThreadsManager() {
	}

	/**
	 * @return
	 */
	public synchronized Domain getCurrentDomain() {
		return this.threadToDomain.get( Thread.currentThread() );
	}

	/**
	 * @param domainThread
	 * @param object
	 */
	public void bind(Domain domain) {
		final Thread domainThread = domain.getDomainThread();
		this.domainToThread.put(domain, domainThread );
		this.threadToDomain.put(domainThread, domain );
	}

	/**
	 * @param domain
	 */
	public void unbind(Domain domain) {
		Thread domainThread = this.domainToThread.get(domain);
		if ( domainThread != null ) {
			this.threadToDomain.remove( domainThread );
		}
	}
	
}
