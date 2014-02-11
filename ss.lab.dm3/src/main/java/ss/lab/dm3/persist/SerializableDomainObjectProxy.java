package ss.lab.dm3.persist;

import java.io.ObjectStreamException;
import java.io.Serializable;

import ss.lab.dm3.orm.QualifiedObjectId;

/**  
 * @author Dmitry Goncharov
 */
final class SerializableDomainObjectProxy implements Serializable {

	private static final long serialVersionUID = 459425626942716121L;

	private final QualifiedObjectId<? extends DomainObject> qualifiedId;

	/**
	 * @param qualifiedId
	 */
	public SerializableDomainObjectProxy(QualifiedObjectId<? extends DomainObject> qualifiedId) {
		super();
		this.qualifiedId = qualifiedId;
	}
	
	Object readResolve() throws ObjectStreamException {
		Domain domain = DomainResolverHelper.getCurrentDomain();
		return domain.resolve( this.qualifiedId );
	}
	
}
