package ss.framework.arbitrary.change;

import java.util.HashMap;
import java.util.Map;

public class BackwardConvert<T,F> implements IObjectConverter<T,F> {

	private final Map<F,T> map = new HashMap<F,T>();
	
	/* (non-Javadoc)
	 * @see ss.framework.arbitrary.change.IObjectConverter#convert(java.lang.Object)
	 */
	public T convert(F obj) {
		final T result = convertOrNull(obj);
		if ( result == null ) {
			throw new IllegalArgumentException( "Object with key " + obj + " was not found." );
		}
		return result;
	}
	
	public T convertOrNull(F obj) {
		return this.map.get(obj);
	}

	public void add(F from, T to ) {
		if ( from == null ) {
			throw new NullPointerException( "from" );
		}
		if ( to == null ) {
			throw new NullPointerException( "to" );
		}
		if ( this.map.containsKey( from ) ) {
			throw new IllegalArgumentException( "Already contains from " + from );
		}
		this.map.put( from, to );
	}
		
}
