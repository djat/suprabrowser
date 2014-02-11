package ss.lab.dm3.persist.script;

import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.Query;
import ss.lab.dm3.persist.Domain;

/**
 * @author Dmitry Goncharov
 */
public abstract class QueryScript extends Query implements IScript {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2553879360231675202L;

	private transient Domain domain;
	
	public Domain getDomain() {
		return this.domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	@Deprecated
	public <T extends DomainObject> ScriptDomainObjectReference<T> createReference() {
		throw new UnsupportedOperationException( "see ScriptDomainObjectReference");
	}

	@Deprecated
	public <T extends DomainObject> ScriptDomainObjectReference<T> createReference(T object) { 
		throw new UnsupportedOperationException( "see ScriptDomainObjectReference");
	}
		
}