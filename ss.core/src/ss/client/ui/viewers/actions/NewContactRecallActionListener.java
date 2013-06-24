/**
 * 
 */
package ss.client.ui.viewers.actions;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.event.messagedeleters.SingleMessageDeleter;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.viewers.NewContact;
import ss.global.SSLogger;
import ss.util.XMLSchemaTransform;

/**
 * @author zobo
 *
 */
public class NewContactRecallActionListener implements Listener {
	private NewContact newContact;

	private static final Logger logger = SSLogger
			.getLogger(NewContactRecallActionListener.class);

	private static final String ARE_YOU_SURE = "NEWCONTACTRECALLACTIONLISTENER.ARE_YOU_SURE";

	private final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_ACTIONS_NEWCONTACTRECALLACTIONLISTENER);

	/**
	 * @param newContact
	 */
	public NewContactRecallActionListener(NewContact newContact) {
		super();
		this.newContact = newContact;
	}

	public void handleEvent(Event event) {

		try {

			if (this.newContact.getOrigDoc().getRootElement().element(
					"current_sphere") == null) {
				this.newContact.getOrigDoc().getRootElement().addElement(
						"current_sphere").addAttribute("value",
						(String) this.newContact.getSession().get("sphere_id"));

			}
			if (this.newContact.getOrigDoc().getRootElement().element(
					"thread_id") == null) {
				this.newContact.setOrigDoc(XMLSchemaTransform
						.setNewMoment(this.newContact.getOrigDoc()));
			}

			Thread main = new Thread() { 
				// Need to do it inside a
				// thread because of the
				// threading issues between
				// Swing and SWT
				private NewContact newContact = NewContactRecallActionListener.this.newContact;

				public void run() {			
					//TODO: fix this casting
					MessagesPane messagePane = (MessagesPane) this.newContact.getSphereViewOwner();
					(new SingleMessageDeleter( messagePane, true, NewContactRecallActionListener.this.bundle
							.getString(ARE_YOU_SURE), NewContact.getSShell())).executeDeliting(this.newContact.getOrigDoc());
				}

			};
			main.start();

		} catch (NullPointerException npe) {
			logger.error(npe.getMessage(), npe);
		}
	}
}
