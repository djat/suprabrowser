package ss.lab.dm3.persist.workers;

import ss.lab.dm3.persist.Domain;

/**
 * @author Dmitry Goncharov
 */
public class DomainWorkerContext {

	private final Domain domain;
		
	/**
	 * @param domain
	 */
	public DomainWorkerContext(Domain domain) {
		super();
		this.domain = domain;
	}

	/**
	 * @return
	 */
	public Domain getDomain() {
		return this.domain;
	}

}
