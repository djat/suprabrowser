/**
 * 
 */
package ss.server.networking.protocol.actions;

import ss.client.networking.protocol.actions.SetMarkAction;
import ss.server.functions.setmark.SetMarkCenter;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SetMarkActionHandler extends AbstractActionHandler<SetMarkAction>{

	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public SetMarkActionHandler( DialogsMainPeer peer ) {
		super(SetMarkAction.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.server.networking.protocol.actions.AbstractActionHandler#execute(ss.client.networking.protocol.actions.AbstractAction)
	 */
	@Override
	protected void execute(SetMarkAction action) {		
		SetMarkCenter.INSTANCE.process( action.getData(), this.peer );
	}

}
