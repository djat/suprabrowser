package ss.lab.dm3.persist;

public abstract class QueryMatcher implements IObjectMatcher {

	public abstract boolean match( DomainObject obj );
	
	// TODO [dg] implement collect that use indexes
	//public abstract boolean collect( DomainObject obj );
	
}
