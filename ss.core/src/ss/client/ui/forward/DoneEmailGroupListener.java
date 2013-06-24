/**
 * 
 */
package ss.client.ui.forward;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.EmailAddressesContainer;


/**
 * @author roman
 *
 */
public class DoneEmailGroupListener extends AbstractDoneEmailListener {

	public DoneEmailGroupListener(final CurrentMessageForwardingDialog dialog) {
		super(dialog);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	void performSpecificEmailAction(final Hashtable send_session,
			final StringBuffer sb, final String subject, final String sphere) {
		final DialogsMainCli client = SupraSphereFrame.INSTANCE.client;

		Vector contacts = client.getAllContacts(
				send_session, client.getVerifyAuth()
				.getSystemName(getChopSphere(sphere)));

		for (int j = 0; j < contacts.size(); j++) {

			Document doc = (Document) contacts.get(j);

			try {
				final String to_email = doc
				.getRootElement().element(
						"email_address")
						.attributeValue("value");
				if (to_email != null) {
					if (to_email.length() > 0) {

						final String from_email = (String) (client
								.getEmailInfo(
										send_session,
										(String) send_session
										.get("real_name")))
										.get("email_address");
						final String fromDomain = (String) (client
								.getEmailInfo(
										send_session,
										(String) send_session
										.get("real_name")))
										.get("sphere_domain");
						final String replySphere = (String) send_session
						.get("sphere_id")
						+ "."
						+ doc
						.getRootElement()
						.element(
						"message_id")
						.attributeValue(
						"value")
						+ "@" + fromDomain;

						final StringBuffer to_send = sb;
						final String sub_send = subject;
						if (!to_email
								.equals("__NOBODY__")
								&& !from_email
								.equals("__NOBODY__")) {
							Thread t = new Thread() {
								public void run() {

									client
									.sendEmailFromServer(
											send_session,
											new EmailAddressesContainer(to_email, from_email),
											null,
											to_send,
											sub_send,
											replySphere);

								}
							};
							t.start();
						}
					}
				}
			} catch (NullPointerException npe) {
			}
		}
	}

}
