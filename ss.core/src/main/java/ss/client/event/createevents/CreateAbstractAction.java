package ss.client.event.createevents;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;

import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.DropDownItemAbstractAction;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.workflow.AbstractDelivery;

public abstract class CreateAbstractAction implements DropDownItemAbstractAction {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateAbstractAction.class);
	
	private boolean isSetLastAction = true;

	abstract public Image getImage();

	abstract public String getName();

	public void perform() {
		performImpl();
		performed();
	}
	
	public void perform(final boolean isSetLastAction) {
		this.isSetLastAction = isSetLastAction; 
		performImpl();
		performed();
	}
	
	public void performImpl(){
		this.checkIsTagBoxSelected();
	}
		
	public void checkIsTagBoxSelected() {
		final MessagesPane messPane = SupraSphereFrame.INSTANCE.tabbedPane.getSelectedMessagesPane();
		if( messPane != null && !messPane.isRootView()) {
			ControlPanel controlPanel = (ControlPanel)messPane.getControlPanel();
			if(this instanceof CreateKeywordsAction) {	
				controlPanel.getTagBox().setSelection(true);				
				controlPanel.getReplyBox().setSelection(false);
			} else {
				if ((this.isSetLastAction)&&(!controlPanel.isTypeLocked())){
					messPane.getControlPanel().setPreviousType(this.getName());
				}
				controlPanel.getTagBox().setSelection(false);
			}
		}
	}
	
	public SupraSphereFrame getSupraFrame() {
		return SupraSphereFrame.INSTANCE;
	}

	public static void performed(){
		final MessagesPane messPane = SupraSphereFrame.INSTANCE.tabbedPane.getSelectedMessagesPane();
		if ( messPane != null ) {
			final String systemName = messPane.getSystemName();
			final AbstractDelivery defaultDelivery = SsDomain.SPHERE_HELPER.getSpherePreferences( systemName ).getWorkflowConfiguration().getDefaultDelivery();
			final String name = defaultDelivery.getDisplayName();
			if (name == null) {
				logger.error("Default delivery name is null");
				return;
			}
			final Combo deliveryCombo = messPane.getControlPanel().getDelivery();
			final String[] items = deliveryCombo.getItems();
			if (items == null) {
				logger.error("List of delivery in delivery combo for " + systemName + " is null");
				return;
			}
			for (int i = 0; i < items.length; i++){
				if (items[i].equals(name)){
					if (!deliveryCombo.getText().equals( name )){
						deliveryCombo.select( i );
						if (logger.isDebugEnabled()) {
							logger.debug("Set selection to: " + items[i] + ", with index: " + i);
						}
					}
					break;
				}
			}
		}
	}
}
