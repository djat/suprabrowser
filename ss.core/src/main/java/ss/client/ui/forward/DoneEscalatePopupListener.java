/**
 * 
 */
package ss.client.ui.forward;

import java.util.Hashtable;

import ss.client.ui.SupraSphereFrame;

/**
 * @author roman
 *
 */
public class DoneEscalatePopupListener extends AbstractDoneSelectionListener {

	public DoneEscalatePopupListener(final CurrentMessageForwardingDialog dialog) {
		super(dialog);
	}
	
	@SuppressWarnings("unchecked")
	private void handleEscalatePopup(final String selectedSphere) {
		Hashtable sendSession = (Hashtable) SupraSphereFrame.INSTANCE.client.session.clone();
		String chop_sphere = getChopSphere(getDoc(),
				selectedSphere, true);

		String member = SupraSphereFrame.INSTANCE.client.getVerifyAuth()
		.getLoginForContact(chop_sphere);
		getMemberList().add(member);

		SupraSphereFrame.INSTANCE.client.sendPopupNotification(sendSession,
				getMemberList(), getDoc());
	}

	
	@Override
	void performSpecificAction() {
		for(String sphere : getDialog().getCheckedSpheres()) {
			handleEscalatePopup(sphere);
		}
	}
}
