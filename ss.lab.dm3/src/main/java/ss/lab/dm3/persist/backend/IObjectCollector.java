package ss.lab.dm3.persist.backend;

import ss.lab.dm3.persist.DomainObject;

/**
 * @author dmitry
 *
 */
public interface IObjectCollector {

	boolean add( DomainObject object );
	
}
