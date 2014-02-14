/**
 * 
 */
package ss.framework.install.update;

import ss.framework.networking2.Protocol;

/**
 *
 */
public interface IUpdateProtocolFactory {

	Protocol create() throws CantCreateUpdateProtocolException;
	
}
