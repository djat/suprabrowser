/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.ui.spheremanagement.memberaccess.MemberAccessManager;

/**
 * @author roman
 *
 */
public class MemberAccessChangesDetector extends AbstractChangesDetector {
	
	private MemberAccessManager manager;
	
	public MemberAccessChangesDetector( MemberAccessManager manager ) {
		this.manager = manager;
	}
	
	public void collectChangesAndUpdate() {
		this.manager.collectChangesAndUpdate();
		setChanged(false);
	}

	public void rollbackChanges() {
		this.manager.rollbackChanges();
		setChanged(false);
	}

	@Override
	protected String getWarningString() {
		return this.bundle.getString(MEMBER_ACCESS);
	}
}
