/**
 * 
 */
package ss.client.ui.forward;

import java.util.Hashtable;

import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public abstract class AbstractDoneEmailListener extends AbstractDoneSelectionListener {

	public AbstractDoneEmailListener(final CurrentMessageForwardingDialog dialog) {
		super(dialog);
	}

	@SuppressWarnings("unchecked")
	abstract void performSpecificEmailAction(final Hashtable send_session, final StringBuffer buffer, final String subject, final String sphere);
	
	@Override
	final public void performSpecificAction() {
		for(String sphere : getSelection()) {
			performEmailAction(sphere);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected final void performEmailAction(final String sphere) {
		final DialogsMainCli client = SupraSphereFrame.INSTANCE.client;

		final Hashtable send_session = (Hashtable) client.session
		.clone();

		send_session.put("delivery_type", "normal");
		
		Element orig_current = getDoc().getRootElement()
		.element("current_sphere");

		Element orig_message_id = getDoc().getRootElement()
		.element("message_id");
		Element orig_date = getDoc().getRootElement()
		.element("moment");
		Element last_updated = getDoc().getRootElement()
		.element("last_updated");
		Element orig = getDoc().getRootElement().element(
		"original_id");

		Element response = getDoc().getRootElement()
		.element("response_id");
		if (response != null) {
			getDoc().getRootElement().remove(response);
		}

		if (orig_current != null) {
			getDoc().getRootElement().remove(orig_current);
		}

		Element old = getDoc().getRootElement().element(
		"forwarded_by");
		if (old == null) {
			getDoc().getRootElement().addElement(
			"forwarded_by").addAttribute("value",
					(String) send_session.get("real_name"));
		} else {
			getDoc().getRootElement()
			.element("forwarded_by").addAttribute(
					"value",
					(String) send_session
					.get("real_name"));
		}
		String type = getDoc().getRootElement().element(
		"type").attributeValue("value");

		StringBuffer sb = new StringBuffer();
		String subject = "";
		if (type.equals("terse")) {
			subject = getDoc().getRootElement().element(
			"subject").attributeValue("value");
		} else if (type.equals("message")) {
			sb.append(getDoc().getRootElement().element(
			"body").getText());
			subject = getDoc().getRootElement().element(
			"subject").attributeValue("value");
		} else if (type.equals("reply")) {

			if (getDoc().getRootElement().element("body")
					.element("comment") != null) {
				sb.append(getDoc().getRootElement().element(
				"body").element("comment")
				.getText());
			} else {
				sb.append(getDoc().getRootElement().element(
				"body").getText());
			}

			subject = getDoc().getRootElement().element(
			"subject").attributeValue("value");
		} else if (type.equals("bookmark")) {

			subject = getDoc().getRootElement().element(
			"subject").attributeValue("value");
			sb.append("Bookmark: "
					+ getDoc().getRootElement().element(
					"address").attributeValue(
					"value") + "\n\n");
			sb.append(getDoc().getRootElement().element(
			"body").getText());

		}

		performSpecificEmailAction(send_session, sb, subject, sphere);
		
		getDoc().getRootElement().remove(orig_message_id);
		getDoc().getRootElement().remove(orig_date);
		getDoc().getRootElement().remove(last_updated);
		getDoc().getRootElement().remove(orig);
	}

	/**
	 * @param sphere
	 * @return
	 */
	protected String getEmailChopSphere(final String sphere) {
		String chop_sphere = null;
		if (sphere.lastIndexOf('*') != -1) {
			StringBuffer new_mp = new StringBuffer(
					sphere);
			chop_sphere = new_mp.substring(1);
			new_mp = new StringBuffer(chop_sphere);
			chop_sphere = new_mp.substring(0, new_mp
					.length() - 1);

			Element conf = getDoc().getRootElement()
			.element("confirmed");
			if (conf == null) {
				getDoc().getRootElement().addElement(
				"confirmed").addAttribute("value",
				"false");
			} else {
				getDoc().getRootElement().element(
				"confirmed").addAttribute("value",
				"false");
			}
		} else {
			chop_sphere = sphere;
		}
		return chop_sphere;
	}
}
