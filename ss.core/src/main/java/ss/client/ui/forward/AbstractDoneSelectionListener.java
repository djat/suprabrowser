/**
 * 
 */
package ss.client.ui.forward;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public abstract class AbstractDoneSelectionListener implements Listener {

	private final CurrentMessageForwardingDialog dialog;
	protected Hashtable session;
	protected Document doc;
	protected Vector memberList;
	
	public AbstractDoneSelectionListener(final CurrentMessageForwardingDialog dialog) {
		this.dialog = dialog;
		initialize();
	}

	abstract void performSpecificAction();
	
	public final void handleEvent(Event event) {
		performSpecificAction();
		getDialog().close();
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

	/**
	 * 
	 */
	private void initialize() {
		this.session = SupraSphereFrame.INSTANCE.client.session;
		this.doc = this.dialog.getDocsToForward().iterator().next();
	}

	protected String getChopSphere(final String sphere_name) {
		int lastCharIndex = sphere_name.length() - 1;
		if ((sphere_name.charAt(0) == '*')
				&& (sphere_name.charAt(lastCharIndex) == '*')) {
			return sphere_name.substring(1, lastCharIndex);

		}
		return sphere_name;
	}
	
	/**
	 * @return the selector
	 */
	public CurrentMessageForwardingDialog getDialog() {
		return this.dialog;
	}
	
	/**
	 * @return the session
	 */
	public Hashtable getSession() {
		return this.session;
	}
	
	/**
	 * @return the selection
	 */
	public List<String> getSelection() {
		return this.dialog.getCheckedSpheres();
	}
	
	/**
	 * @return the doc
	 */
	public Document getDoc() {
		return this.doc;
	}
	
	/**
	 * @return the memberList
	 */
	public Vector getMemberList() {
		return this.memberList;
	}
}
