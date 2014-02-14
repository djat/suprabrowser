/**
 * 
 */
package ss.server.functions.setmark.message;

import ss.server.functions.setmark.SetMarkProcedure;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public abstract class SetMessageMarkProcedure<T extends SetMessageMarkData> extends SetMarkProcedure<T>{

	/**
	 * @param data
	 * @param peer
	 */
	public SetMessageMarkProcedure(T data, DialogsMainPeer peer) {
		super(data, peer);
		// TODO Auto-generated constructor stub
	}
}
