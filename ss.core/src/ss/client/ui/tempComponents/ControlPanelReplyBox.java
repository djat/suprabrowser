/**
 * 
 */
package ss.client.ui.tempComponents;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import ss.client.ui.ControlPanel;

/**
 * @author roman
 *
 */
public class ControlPanelReplyBox {
	
	private ControlPanel cp;
	
	private Button replyBox;
	
	public ControlPanelReplyBox(ControlPanel cp) {
		this.cp = cp;
		this.replyBox = new Button(cp.getButtonComposite(), SWT.CHECK);
	}
	
	public boolean getSelection() {
		return this.replyBox.getSelection();
	}
	
	public void setSelection(boolean selection) {
		this.replyBox.setSelection(selection);
		if(selection) {
			this.cp.getMP().setYellowParentMessage();
		} else {
			this.cp.getMP().revertParentMessageColor();
		}
	}
	
	public void addSelectionListener(SelectionListener listener) {
		this.replyBox.addSelectionListener(listener);
	}
	
	public void setText(String text) {
		this.replyBox.setText(text);
	}
	
	public void setLayoutData(GridData data) {
		this.replyBox.setLayoutData(data);
	}
	
	public void setVisible(boolean visible) {
		this.replyBox.setVisible(visible);
	}
	
	public void setEnabled(boolean enabled) {
		this.replyBox.setEnabled(enabled);
	}
	
	public boolean isEnabled() {
		return this.replyBox.isEnabled();
	}
}
