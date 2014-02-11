package ss.lab.dm3.persist.script;

import java.io.Serializable;

import ss.lab.dm3.orm.QualifiedObjectId;
import ss.lab.dm3.persist.DomainObject;

/**
 * Not used because wrapped domain object is serializable  
 */
@Deprecated
public class ScriptDomainObjectReference<T extends DomainObject> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5633425235060328538L;

	/**
	 * @param owner
	 */
	@Deprecated
	public ScriptDomainObjectReference(IScript owner) {
		super();
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public T get() {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	public void set(T object) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 */
	@Deprecated
	public QualifiedObjectId<? extends DomainObject> getObjectId() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return
	 */
	@Deprecated
	public Long getId() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @param class1
	 * @param id
	 */
	@Deprecated
	public void set(Class<T> clazz, Long id) {
		throw new UnsupportedOperationException();
	}
	
}
