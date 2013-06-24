package ss.client.hotkeys;

import ss.client.ui.SupraSphereFrame;

public abstract class AbstractAction implements IAction {
	
	private SupraSphereFrame supraFrame;
	/* (non-Javadoc)
	 * @see hotkeys.IAction#execute()
	 */
	public final void execute() {
		this.supraFrame = HotKeysManager.getInstance().getSupraFrame();
		performExecute();
	}

	protected abstract void performExecute();

	/**
	 * @return the sF
	 */
	protected final SupraSphereFrame getSupraFrame() {
		return this.supraFrame;
	}


}
