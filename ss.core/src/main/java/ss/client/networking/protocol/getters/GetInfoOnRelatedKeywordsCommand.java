/**
 * 
 */
package ss.client.networking.protocol.getters;

import java.util.Hashtable;

/**
 * @author zobo
 *
 */
public class GetInfoOnRelatedKeywordsCommand extends AbstractGetterCommand {

	private static final String DATA = "data";
	private static final long serialVersionUID = 4401227729735733511L;

	/**
	 * key - unique_id
	 * value - sphereId
	 */
	public void setKeywordsHash( final Hashtable<String, String> data ) {
		putArg(DATA, data);
	}

	/**
	 * key - unique_id
	 * value - sphereId
	 */
	public Hashtable<String, String> getKeywordsHash() {
		return (Hashtable<String, String>) getObjectArg(DATA);
	}
}
