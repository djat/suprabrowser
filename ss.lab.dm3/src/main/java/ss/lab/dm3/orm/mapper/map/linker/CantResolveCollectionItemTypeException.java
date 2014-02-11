/**
 * 
 */
package ss.lab.dm3.orm.mapper.map.linker;

import ss.lab.dm3.orm.OrmException;

/**
 *
 */
public class CantResolveCollectionItemTypeException extends OrmException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7165853629524528894L;

	/**
	 * @param message
	 */
	public CantResolveCollectionItemTypeException(String message) {
		super(message);
	}

}
