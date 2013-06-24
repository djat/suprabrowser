/**
 * 
 */
package ss.client.ui.forward;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class DoneNotifyTrayListener extends SelectionAdapter {

	private final NotifyTrayDialog dialog;
	
	public DoneNotifyTrayListener(final NotifyTrayDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		performSpecificAction();
		this.dialog.close();
	}

	@SuppressWarnings("unchecked")
	private void handleNotifyTray(final Document doc, final Vector memberList,
			String contactName) {
		String chop_sphere = getChopSphere(doc, contactName, false);
		String member = SupraSphereFrame.INSTANCE.client.getVerifyAuth()
				.getLoginForContact(chop_sphere);
		memberList.add(member);
	}
	
	@SuppressWarnings("unchecked")
	void performSpecificAction() {
		Vector notifyedMembers = new Vector();
		for (String sphere : this.dialog.getSelection()) {
			handleNotifyTray(this.dialog.getDoc(), notifyedMembers, sphere);

		}
		Hashtable session = SupraSphereFrame.INSTANCE.client.session; 
		SupraSphereFrame.INSTANCE.client.notifySystemTray(
				(Hashtable)session.clone(), notifyedMembers, this.dialog.getDoc());
	}
	
	protected String getChopSphere(final Document doc, final String sphere_name,
			boolean skipConfirmed) {
		int lastCharIndex = sphere_name.length() - 1;
		if ((sphere_name.charAt(0) == '*')
				&& (sphere_name.charAt(lastCharIndex) == '*')) {
			if (!skipConfirmed) {
				Element confirmed = doc.getRootElement().element("confirmed");
				if (confirmed == null) {
					doc.getRootElement().addElement("confirmed");
					confirmed = doc.getRootElement().element("confirmed");
				}
				confirmed.addAttribute("value", "true");
			}
			return sphere_name.substring(1, lastCharIndex);

		}
		return sphere_name;
	}
}
