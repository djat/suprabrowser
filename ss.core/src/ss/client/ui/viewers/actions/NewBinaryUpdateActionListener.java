package ss.client.ui.viewers.actions;

import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.viewers.NewBinarySWT;

/**
 * @author zobo
 *
 */
public class NewBinaryUpdateActionListener implements SelectionListener {
    private NewBinarySWT newBinary;
    /**
     * 
     */
    public NewBinaryUpdateActionListener(NewBinarySWT newBinary) {
        super();
        this.newBinary = newBinary;
    }

	
	public void widgetDefaultSelected(SelectionEvent arg0) {
		
	}

	
	public void widgetSelected(SelectionEvent arg0) {
		String messageId = this.newBinary.getViewDoc().getRootElement()
				.element("message_id").attributeValue("value");

		NewBinarySWT nb = new NewBinarySWT(this.newBinary.getSession(),
				this.newBinary.getMessagesPane(), messageId, false);
		Element current_sphere = this.newBinary.getViewDoc().getRootElement()
				.element("current_sphere");

		if (current_sphere != null) {

			this.newBinary.getViewDoc().remove(current_sphere);
		}
		nb.setViewDoc(this.newBinary.getViewDoc());
		nb.addButtons();
		this.newBinary.getShell().dispose();
	}
}