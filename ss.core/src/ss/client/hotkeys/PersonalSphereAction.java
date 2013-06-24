package ss.client.hotkeys;

import ss.client.event.MemberPresenceListenerSWT;
import ss.client.ui.MessagesPane;
import ss.client.ui.PeopleTableOwner;
import ss.common.UiUtils;
import ss.util.SessionConstants;

public class PersonalSphereAction extends AbstractAction {

	public void performExecute() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				String name = (String) getSupraFrame().client.session
						.get(SessionConstants.REAL_NAME);
				MessagesPane mp = getSupraFrame().getMainMessagesPane();
				(new MemberPresenceListenerSWT(new PeopleTableOwner(mp), mp.getPeopleTable()))
						.openPersonalSphere(name);
				getSupraFrame().tabbedPane.setFocusToCurrentSendField();
			}
		});
	}

}
