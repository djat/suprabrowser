/**
 * 
 */
package ss.client.ui.Listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public class ControlCompositeReplyBoxMouseListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ControlCompositeReplyBoxMouseListener.class);
	
	private final ControlPanel comp;
    
    public ControlCompositeReplyBoxMouseListener(ControlPanel comp) {
        super();
        this.comp = comp;
    }

    public void widgetSelected(SelectionEvent se) {
    	if (logger.isDebugEnabled()) {
			logger.debug("selection event : "+((Button)se.widget).getSelection());
		}
    	checkIsSelected((Button)se.widget);
    	if(!((Button)se.widget).getSelection()) {
    		return;
    	}
    	this.comp.getTagBox().setSelection(false);
        this.comp.setIsTagSelected();
        MessagesPane mp = (MessagesPane) this.comp.getSF().tabbedPane
                .getSelectedMessagesPane();
        if ( mp != null ) {
        	mp.getControlPanelDocking().setFocusToTextField();
        	logger.info("reply box selected in mouse clicked");
        }
    }

    /**
	 * @param button
	 */
	private void checkIsSelected(Button button) {
		boolean selected = button.getSelection();
		
		if(selected) {
			this.comp.getMP().setYellowParentMessage();
		} else {
			this.comp.getMP().revertParentMessageColor();
		}
		
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {
        
    }
}
