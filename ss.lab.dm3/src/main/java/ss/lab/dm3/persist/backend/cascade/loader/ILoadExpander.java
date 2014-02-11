package ss.lab.dm3.persist.backend.cascade.loader;

import ss.lab.dm3.persist.DomainObject;

/**
 * @author Dmitry Goncharov
 */
public interface ILoadExpander {

	public abstract void expand(CascadeLoader loader, DomainObject object);

}