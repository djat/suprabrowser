/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.admin.EmailAliasesManageComposite;
import ss.domainmodel.SphereEmail;

/**
 * @author roman
 *
 */
public class EmailAliasesChangesDetector extends AbstractChangesDetector {
	
	private EmailAliasesManageComposite emailComposite;
	
	public EmailAliasesChangesDetector(EmailAliasesManageComposite emailComposite) {
		this.emailComposite = emailComposite;
	}
	
	public void collectChangesAndUpdate() {
		SphereEmail sphereEmail = this.emailComposite.getInnerEmailAliasesComposite().createSphereEmail();
		SupraSphereFrame.INSTANCE.client.saveNewSpheresEmails(sphereEmail);
	}

	public void rollbackChanges() {
		this.emailComposite.performFinalAction();
	}

	@Override
	protected String getWarningString() {
		return this.bundle.getString(EMAIL_ALIASES);
	}
}
