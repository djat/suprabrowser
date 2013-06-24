/**
 * Jul 4, 2006 : 5:06:19 PM
 */
package ss.common;

import java.util.Hashtable;


/**
 * @author dankosedin
 * 
 */
public interface ProtocolHandler {

	public String getProtocol();

	public void handle(final Hashtable update);

}
