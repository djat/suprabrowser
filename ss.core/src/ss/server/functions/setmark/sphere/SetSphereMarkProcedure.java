/**
 * 
 */
package ss.server.functions.setmark.sphere;

import ss.server.functions.setmark.SetMarkProcedure;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public abstract class SetSphereMarkProcedure<T extends SetSphereMarkData> extends SetMarkProcedure<T> {

	/**
	 * @param data
	 * @param peer
	 */
	public SetSphereMarkProcedure(T data, DialogsMainPeer peer) {
		super(data, peer);
	}

}
