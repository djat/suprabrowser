package ss.lab.dm3.persist;

import ss.lab.dm3.persist.space.QuerySpace;
import ss.lab.dm3.persist.space.Space;
import ss.lab.dm3.security2.Authentication;

public class Context {

	private final Domain domain;
	
	private final Space space;
	
	private boolean alive = true;
	
	private LazyObjectLoader lazyObjectLoader = new LazyObjectLoader();
	
	/**
	 * @param domain
	 */
	public Context(Domain domain) {
		super();
		this.domain = domain;
		this.space = new QuerySpace();
	}

	public LazyObjectLoader getLazyObjectLoader() {
		return this.lazyObjectLoader;
	}
	
	/**
	 * 
	 */
	public void check() {
		this.domain.checkDomain();
		if ( !this.isAlive() ) {
			throw new IllegalStateException( "Context " +  this + " is die." );
		} 
	}

	public boolean isAlive() {
		return this.alive;
	}
	
	public void release() {
		this.domain.checkDomain();
		if ( this.alive ) {
			this.alive = false;
			this.domain.release( this );
		}
	}
	
	public Space getSpace() {
		return this.space;
	}

	/**
	 * @return
	 */
	public Domain getDomain() {
		return this.domain;
	}

	/**
	 * @param object
	 */
	public void addByOrm(DomainObject object) {
		check();
		this.domain.getRepository().addObjectToSpace(this.space, object);
	}

	/**
	 * @param object
	 */
	public void removeByOrm(DomainObject object) {
		check();
		this.domain.getRepository().removeObjectFromSpace(this.space, object);
	}

	/**
	 * 
	 */
	public Authentication getAuthentication() {
		return null;
	}
	
}
