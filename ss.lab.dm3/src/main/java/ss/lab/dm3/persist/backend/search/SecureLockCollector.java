package ss.lab.dm3.persist.backend.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ss.lab.dm3.orm.MappedObject;

public class SecureLockCollector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7624201226568995797L;
	
	private final List<String> locks = new ArrayList<String>();
	
	public void add( String lock ) {
		this.locks.add( lock );
	}
	
	public void add( Class<? extends MappedObject> clazz, Long id ) {
		this.locks.add( SearchHelper.toLock( clazz, id ) );
	}

	public boolean isEmpty() {
		return this.locks.isEmpty();
	}

	public void flushTo(StringBuilder target) {
		for( String lock : this.locks ) {
			target.append( lock );
			target.append( " " );
		}		
	}
	
	
}
