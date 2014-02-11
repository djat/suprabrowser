package ss.lab.dm3.persist.wrap;

import java.io.Serializable;

import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov
 */
public interface IWrappedDomainObject extends Serializable {
	
	/**
	 * Perform serialization-time write-replacement of this wrapped object.
	 *
	 * @return The serializable wrapped object replacement.
	 */
	Object writeReplace();
	
	Class<? extends DomainObject> getWrappedEntityClazz();
	
}
