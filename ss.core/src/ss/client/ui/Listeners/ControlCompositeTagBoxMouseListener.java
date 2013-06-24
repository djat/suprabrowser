/**
 * 
 */
package ss.client.ui.Listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.ControlPanel;


/**
 * @author zobo
 *
 */
public class ControlCompositeTagBoxMouseListener implements SelectionListener {
    private ControlPanel comp;

    public ControlCompositeTagBoxMouseListener(ControlPanel comp) {
        super();
        this.comp = comp;
    }

    public void mouseClicked() {
        this.comp.getReplyBox().setSelection(false);
        this.comp.setIsTagSelected();
    }

    public void widgetSelected(SelectionEvent arg0) {
        mouseClicked();
    }

    public void widgetDefaultSelected(SelectionEvent arg0) {
        
    }
}