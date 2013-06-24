/**
 * 
 */
package ss.client.ui.sphereopen;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;

/**
 * @author zobo
 *
 */
public class SphereCreationContext {
	private final Hashtable update;
	
	private final DialogsMainCli client;

	public SphereCreationContext(final Hashtable update, final DialogsMainCli client) {
		super();
		this.update = update;
		this.client = client;
	}

	public DialogsMainCli getClient() {
		return this.client;
	}

	public Hashtable getUpdate() {
		return this.update;
	}
}
