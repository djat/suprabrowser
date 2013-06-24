/**
 * 
 */
package ss.client.ui.forward;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.EmailAddressesContainer;


/**
 * @author roman
 *
 */
public class DoneEmailContactListener extends AbstractDoneEmailListener {

	public DoneEmailContactListener(final CurrentMessageForwardingDialog dialog) {
		super(dialog);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.forward.AbstractEmailListener#performSpecificEmailAction()
	 */
	@SuppressWarnings("unchecked")
	@Override
	void performSpecificEmailAction(final Hashtable send_session, final StringBuffer sb, final String subject, final String sphere) {
		final DialogsMainCli client = SupraSphereFrame.INSTANCE.client;
		String toEmail = null;
		for (Enumeration enumer = getDialog().getAddresses().keys(); enumer
		.hasMoreElements();) {
			String key = (String) enumer.nextElement();
			String name = (String) getDialog().getAddresses().get(key);
			if (name.equals(sphere)) {
				toEmail = key;
			}
		}
		final String to_email = toEmail;

		final String from_email = (String) (client
				.getEmailInfo(send_session, (String) send_session
						.get("real_name")))
						.get("email_address");
		final String fromDomain = (String) (client
				.getEmailInfo(send_session, (String) send_session
						.get("real_name")))
						.get("sphere_domain");
		final String replySphere = (String) send_session
		.get("sphere_id")
		+ "."
		+ getDoc().getRootElement().element(
		"message_id").attributeValue(
		"value") + "@" + fromDomain;

		String moment = null;
		if (moment == null) {
			Date currentDate = new Date();
			moment = DateFormat.getTimeInstance(
					DateFormat.LONG)
					.format(currentDate)
					+ " "
					+ DateFormat.getDateInstance(
							DateFormat.MEDIUM).format(
									currentDate);
		}
		Element emailed = new DefaultElement("emailed")
		.addAttribute("description",
				"Emailed to " + sphere)
				.addAttribute("moment", moment)
				.addAttribute(
						"giver",
						(String) send_session
						.get("real_name"))
						.addAttribute("status", "completed");

		client.addEventToMessage(send_session, getDoc()
				.getRootElement().element("message_id")
				.attributeValue("value"), emailed);

		final StringBuffer to_send = sb;
		final String sub_send = subject;
		if (!to_email.equals("__NOBODY__")
				&& !from_email.equals("__NOBODY__")) {
			Thread t = new Thread() {
				public void run() {
					client.sendEmailFromServer(
							send_session, new EmailAddressesContainer(to_email, from_email), null, to_send,
							sub_send, replySphere);

				}
			};
			t.start();
		}
	}
}
