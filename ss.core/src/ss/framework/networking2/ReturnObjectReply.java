/**
 * 
 */
package ss.framework.networking2;

import java.io.Serializable;

/**
 *
 */
public final class ReturnObjectReply<T extends Serializable> extends SuccessReply {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2756037230957894044L;
		
	private final T object;

	/**
	 * @param object
	 */
	public ReturnObjectReply(T object) {
		super();
		this.object = object;
	}

	/**
	 * @return the object
	 */
	public final T getObject( Class expectedClass ) throws IllegalStateException {
		if ( this.object == null ) {
			return null;
		}
		else if ( expectedClass.isInstance( this.object ) ) {
			return (T)this.object; 
		}
		else {
			throw new IllegalStateException( "Invalid object type " + this.object.getClass() + " in command " + this );
		}
	}
	
	/**
	 * @return the object
	 */
	public final T requireObject( Class expectedClass ) {
		T ret = getObject(expectedClass);
		if ( ret == null ) {
			throw new NullPointerException( "Returned object is null in command " + this );
		}
		else {
			return ret;
		}		
	}
	
}
