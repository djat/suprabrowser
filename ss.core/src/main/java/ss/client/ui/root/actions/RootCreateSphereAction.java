/**
 * 
 */
package ss.client.ui.root.actions;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.ISphereView;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.root.RootTab;
import ss.client.ui.tempComponents.DropDownItemAbstractAction;
import ss.client.ui.viewers.NewSphere;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.domainmodel.MemberReference;
import ss.util.ImagesPaths;
import ss.util.SessionConstants;

/**
 * @author zobo
 * 
 */
public class RootCreateSphereAction implements DropDownItemAbstractAction {

	public static final String SPHERE_TITLE = "Sphere";

	private Image image;

	private final RootTab rootTab;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootCreateSphereAction.class);

	private final SupraSphereFrame sF;

	public RootCreateSphereAction(final RootTab rootTab) {
		super();
		this.rootTab = rootTab;
		this.sF = SupraSphereFrame.INSTANCE;
		try {
			this.image = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public void perform() {

		if (logger.isDebugEnabled()) {
			logger.debug("Create Sphere action in Root Tab performed");
		}

		Hashtable session = (Hashtable) SupraSphereFrame.INSTANCE.client.session
				.clone();

		String sphereId = this.rootTab.getSelectedSphere();
		if (sphereId == null) {
			UserMessageDialogCreator.warning("Please select sphere to be parent to the new one");
			logger.info("Selected sphere is null, returning");
			return;
		}
		session.put(SessionConstants.SPHERE_ID2, sphereId);

		List<MemberReference> memberReferences = this.sF.client.getVerifyAuth().getMembersForSphere(sphereId);
		final Vector<String> selectedMembersNames = new Vector<String>(memberReferences.size());
		for( MemberReference ref : memberReferences){
			selectedMembersNames.add(ref.getContactName());
		}
				

		final ISphereView selectedSphereView = new RootSphereView(session,
				sphereId);
		if (selectedSphereView == null) {
			logger.warn("Selected sphere view is null");
			return;
		}

		if (selectedMembersNames.size() > 0) {

			new NewSphere(session, selectedSphereView, null, null,
					selectedMembersNames, null);
		} else {
			new NewSphere(session, selectedSphereView, null, null, null,
					null);
		}
	}

	public String getName() {
		return SPHERE_TITLE;
	}

	public Image getImage() {
		return this.image;
	}
}
