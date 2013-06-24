/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.domainmodel.MemberReference;

/**
 * @author roman
 * 
 */
public class HierarchyPeopleTableMenuListener implements SelectionListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HierarchyPeopleTableMenuListener.class);

	private final MemberReference memberOwner;

	private final MemberReference memberMenuItem;

	/**
	 * @param memberOwner
	 * @param memberMenuItem
	 */
	public HierarchyPeopleTableMenuListener(MemberReference memberOwner,
			MemberReference memberMenuItem) {
		this.memberOwner = memberOwner;
		this.memberMenuItem = memberMenuItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent se) {

		if (this.memberMenuItem != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("member login in menu: "
						+ this.memberMenuItem.getLoginName());
			}
		} else {
			logger.error("member in menu is null");
			return;
		}
		if (this.memberOwner != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("memberOwner login: "
						+ this.memberOwner.getLoginName());
			}
		} else {
			logger.error("memberOwner is null");
			return;
		}
		String systemName = SupraSphereFrame.INSTANCE.client.getVerifyAuth()
				.getSphereSystemNameByContactAndDisplayName(this.memberMenuItem.getContactName(),
						this.memberOwner.getContactName());
		if (systemName != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("systemName is: " + systemName);
			}
		} else {
			logger.error("systemName is null");
			return;
		}

		SphereOpenManager.INSTANCE.requestP2P(this.memberOwner, systemName);

	}

}
