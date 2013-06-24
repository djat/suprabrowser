/**
 * 
 */
package ss.server.functions.setmark.global;

import java.util.List;

import ss.server.functions.setmark.SetMarkProcedure;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public abstract class SetGlobalMarkProcedure<T extends SetGlobalMarkData> extends SetMarkProcedure<T>{

	private List<String> allAvailableSpheres;
	
	public SetGlobalMarkProcedure(T data, DialogsMainPeer peer) {
		super(data, peer);
		// TODO Auto-generated constructor stub
	}

	protected List<String> getAllAvailableSpheres(){
		if (this.allAvailableSpheres == null) {
			this.allAvailableSpheres = getPeer().getVerifyAuth().getAvailableSpheres(); 
		}
		return this.allAvailableSpheres;
	}
}
