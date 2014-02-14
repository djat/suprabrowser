package ss.client.ui.docking;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public abstract class DockingLabelMouseListener extends MouseAdapter {

	protected AbstractDockingComponent docking;
	
	public DockingLabelMouseListener(AbstractDockingComponent docking) {
		this.docking = docking;
	}
	
	public AbstractDockingComponent getDocking() {
		return this.docking;
	}
	
	public abstract void mouseDown(MouseEvent me);
}
