/**
 * 
 */
package ss.client.event;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewSphere;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class NewSphereSaveSelectionListener implements SelectionListener {

	private NewSphere viewer;
	
	private static final Logger logger = SSLogger.getLogger(NewSphereSaveSelectionListener.class);
	
	public NewSphereSaveSelectionListener(NewSphere viewer) {
		this.viewer = viewer;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}
	
	@SuppressWarnings("unchecked")
	public void widgetSelected(SelectionEvent se) {
		logger.info("try to create new sphere");
		SphereStatement sphere = this.viewer.createSphere();
		if (sphere == null){
			logger.error("Sphere statement is null in New sphere operation");
			return;
		}

		if (this.viewer.getNameField().getText().length() == 0) {
				UserMessageDialogCreator.warning(this.viewer.getBundle()
					.getString(
							NewSphere.YOU_MUST_CHOOSE_A_NAME_FOR_THE_SPHERE));
		} else 	if (SupraSphereFrame.INSTANCE.client.getVerifyAuth().isSphereExists(sphere.getDisplayName())) {
			UserMessageDialogCreator.warning(this.viewer.getBundle()
					.getString(
							NewSphere.THE_SPHERE_WITH_SUCH_NAME_ALREADY_EXISTS));
//		} else if (this.viewer.getRegisterDocs().isEmpty()) {
//			UserMessageDialogCreator.warning(this.viewer.getBundle().getString(
//					NewSphere.YOU_MUST_CHOOSE_AT_LEAST_ONE_MEMBER_FOR_THE_SPHERE));
		} else if (!this.viewer.isAdmin()
				&& !this.viewer.getRegisterNames()
						.contains(this.viewer.getContactName())) {
			UserMessageDialogCreator.warning(this.viewer
					.getBundle()
					.getString(
							NewSphere.YOU_MUST_CHOOSE_OWN_CONTACT_NAME));
		} else {
			sphere.setSphereCoreId((String) this.viewer.getSession()
					.get("sphere_id"));
						
			this.viewer.getSession().put("delivery_type", "normal");
			this.viewer.getClient().openSphereForMembers(
					this.viewer.getSession(), sphere.getBindedDocument(),
					this.viewer.getRegisterDocs(), sphere.getSystemName(), sphere.getDisplayName());
			this.viewer.getShell().dispose();
		}
	}
}
