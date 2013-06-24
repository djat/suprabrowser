package ss.client.ui.tempComponents;

import org.eclipse.swt.widgets.Composite;

import ss.client.ui.AbstractControlPanel;
import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.RootSphereControlPanel;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.docking.ControlPanelDocking;
import ss.client.ui.docking.DockingTopTitle;

public class FactoryOfControlPanel {
	
	public static AbstractControlPanel createControlPanel(ControlPanelDocking docking, Composite parentComposite) {
		AbstractControlPanel controlPanel = null;
		SupraSphereFrame sF = docking.getSupraFrame();
		MessagesPane mP = docking.getMessagesPane();
		DockingTopTitle headComposite = docking.getHeadComposite();
		if (mP.isRootView()) {
			controlPanel = new RootSphereControlPanel(sF, parentComposite, mP, headComposite);
        } else {
        	controlPanel = new ControlPanel(sF, parentComposite, mP, headComposite);
        }
		return controlPanel;
	}

}
