/**
 * 
 */
package ss.client.event;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.sphereopen.SphereOpenManager;
import ss.domainmodel.MemberReference;

/**
 * @author roman
 *
 */
public class HierarchyPeopleTablePrivateMenuItemListener implements
		SelectionListener {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HierarchyPeopleTablePrivateMenuItemListener.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent se) {
		MemberReference member = (MemberReference)se.widget.getData();
		
		SphereOpenManager.INSTANCE.requestUserPrivate(member);
	}

}
