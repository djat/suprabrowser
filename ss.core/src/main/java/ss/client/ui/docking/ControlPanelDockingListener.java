/**
 * 
 */
package ss.client.ui.docking;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

import ss.client.ui.AbstractControlPanel;
import ss.client.ui.ControlPanel;

/**
 * @author zobo
 *
 */
public class ControlPanelDockingListener implements ControlListener{

    private AbstractControlPanel controlPanel;   
    
    /**
     * 
     */
    public ControlPanelDockingListener(AbstractControlPanel controlPanel) {
        super();
        this.controlPanel = controlPanel;
    }

    public void controlMoved(ControlEvent arg0) {
        
    }

    public void controlResized(ControlEvent arg0) {
    	if(this.controlPanel instanceof ControlPanel)
    		((ControlPanel)this.controlPanel).moveSendFieldOnBottom();
    }

}
