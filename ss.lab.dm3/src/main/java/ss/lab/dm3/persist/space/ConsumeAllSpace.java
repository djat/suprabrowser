package ss.lab.dm3.persist.space;

import ss.lab.dm3.persist.DomainObject;

public class ConsumeAllSpace extends Space {

	@Override
	public boolean shouldExpandBy(DomainObject object) {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null ) {
			return false;
		}
		return getClass().equals( obj.getClass() );
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}	
	
}
