/**
 * 
 */
package ss.client.networking.protocol.actions;

import ss.server.functions.setmark.SetMarkData;

/**
 * @author zobo
 *
 */
public class SetMarkAction extends AbstractAction {

	private static final String SET_GLOBAL_MARK_DATA = "SetGlobalMarkData";
	
	private static final long serialVersionUID = -1626214114487175209L;

	public void setData( final SetMarkData data ){
		putArg(SET_GLOBAL_MARK_DATA, data);
	}
	
	public SetMarkData getData(){
		return (SetMarkData) getObjectArg( SET_GLOBAL_MARK_DATA );
	}
}
