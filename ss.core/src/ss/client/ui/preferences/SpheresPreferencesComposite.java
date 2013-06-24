/**
 * 
 */
package ss.client.ui.preferences;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.preferences.PreferencesController;
import ss.client.ui.SupraSphereFrame;
import ss.domainmodel.SphereItem;
import ss.util.ImagesPaths;

/**
 * @author zobo
 * 
 */
public abstract class SpheresPreferencesComposite extends Composite implements
		ShellUnitInterface {

	private static final String APPLY = "SPHERESPREFERENCESCOMPOSITE.APPLY";

	private static final String CHOOSE_SPHERE = "SPHERESPREFERENCESCOMPOSITE.CHOOSE_SPHERE";

	private static final String SHOULD_REPLY_TO_MY_MESSAGES_NOTIFY_SYSTEM_TRAY = "SPHERESPREFERENCESCOMPOSITE.SHOULD_REPLY_TO_MY_MESSAGES_NOTIFY_SYSTEM_TRAY";

	private static final String SHOULD_FIRST_OPENING_OF_SPHERE_NOTIFY_SYSTEM_TRAY = "SPHERESPREFERENCESCOMPOSITE.SHOULD_FIRST_OPENING_OF_SPHERE_NOTIFY_SYSTEM_TRAY";

	private static final String SHOULD_REPLY_TO_POP_UP_MESSAGE_ALSO_POP_UP = "SPHERESPREFERENCESCOMPOSITE.SHOULD_REPLY_TO_POP_UP_MESSAGE_ALSO_POP_UP";

	private static final String SHOULD_NEW_MESSAGE_OPEN_CURRENT_SPHERE = "SPHERESPREFERENCESCOMPOSITE.SHOULD_NEW_MESSAGE_OPEN_CURRENT_SPHERE";

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_SPHERESPREFERENCESCOMPOSITE);

	private PreferencesController controller;

	private Combo spheres;

	private SupraSphereFrame sF;

	private PreferenceAbstractShellUnit newMessageShouldOpenTab;

	private PreferenceAbstractShellUnit replyIsAlsoAPopUpToPopUp;

	private PreferenceAbstractShellUnit systemTrayNotificationOfFirstTimeSphere;

	private PreferenceAbstractShellUnit systemTrayNotificationOfReply;

	private List<SphereItem> spheresItems;

	private static Image sphereImage;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpheresPreferencesComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public SpheresPreferencesComposite(Composite parent, int style,
			PreferencesController controller, SupraSphereFrame sF) {
		super(parent, style);
		this.controller = controller;
		this.sF = sF;
		this.spheresItems = this.controller.getGroupSpheres();
		initIcons();
		createGUI(this);
	}

	private void initIcons() {
		try {
			sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	private void createGUI(Composite parent) {
		parent.setLayout(new GridLayout());

		String initialSphereId = createSpheresList(parent);
		createPreferencesComposite(parent, initialSphereId);

		Button apply = new Button(parent, SWT.PUSH);
		apply.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true));
		apply.setText(bundle.getString(APPLY));
		apply.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				applyPerformed();
			}
		});
	}

	/**
	 * 
	 */
	protected void applyPerformed() {
		this.controller.applySphere(this, getCurrentSphereId().getSystemName());
	}

	private SphereItem getCurrentSphereId() {
		String sphereName = this.spheres.getText();
		for (SphereItem sItem : this.spheresItems) {
			if (sItem.getDisplayName().equals(sphereName)) {
				return sItem;
			}
		}
		return this.spheresItems.get(0);
	}

	/**
	 * @param parent
	 */
	private String createSpheresList(Composite parent) {
		Composite comp = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		comp.setLayout(layout);
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(layoutData);

		Label label = new Label(comp, SWT.LEFT);
		label.setImage(sphereImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(layoutData);

		label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(CHOOSE_SPHERE));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(layoutData);

		this.spheres = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 2;
		this.spheres.setLayoutData(layoutData);
		String supraSphere = this.sF.client.getVerifyAuth()
				.getSupraSphereName();

		String startingSphereId = null;
		for (SphereItem sItem : this.spheresItems) {
			String sphereDisplayName = sItem.getDisplayName();
			if (!(supraSphere.equals(sphereDisplayName))) {
				if (startingSphereId == null){
					startingSphereId = sItem.getSystemName();
				}
				this.spheres.add(sphereDisplayName);
			}
		}
		if (startingSphereId == null){
			startingSphereId = supraSphere;
		}
		setSelection(0);

		this.spheres.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				String displayName = SpheresPreferencesComposite.this.spheres
						.getText();
				logger.info("Selected sphere name: " + displayName);
				processSelection(getSystemOnDisplay(displayName));
			}

		});
		return startingSphereId;
	}

	protected Composite createPreferencesComposite(Composite parent,
			String sphereId) {
		if (logger.isDebugEnabled()){
			logger.debug("Creating SpheresPreferencesComposite starting with sphereId: " + sphereId);
		}
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(layoutData);

		this.newMessageShouldOpenTab = getNewUnit(comp, SWT.NONE, bundle
				.getString(SHOULD_NEW_MESSAGE_OPEN_CURRENT_SPHERE),
				this.controller.isNewMessageShouldOpenTab(sphereId),
				this.controller.isNewMessageShouldOpenTabModify(sphereId));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.newMessageShouldOpenTab.setLayoutData(layoutData);

		this.replyIsAlsoAPopUpToPopUp = getNewUnit(comp, SWT.NONE, bundle
				.getString(SHOULD_REPLY_TO_POP_UP_MESSAGE_ALSO_POP_UP),
				this.controller.isReplyIsAlsoAPopUpToPopUp(sphereId),
				this.controller.isReplyIsAlsoAPopUpToPopUpModify(sphereId));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.replyIsAlsoAPopUpToPopUp.setLayoutData(layoutData);

		this.systemTrayNotificationOfFirstTimeSphere = getNewUnit(
				comp,
				SWT.NONE,
				bundle
						.getString(SHOULD_FIRST_OPENING_OF_SPHERE_NOTIFY_SYSTEM_TRAY),
				this.controller
						.isSystemTrayNotificationOfFirstTimeSphere(sphereId),
				this.controller
						.isSystemTrayNotificationOfFirstTimeSphereModify(sphereId));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.systemTrayNotificationOfFirstTimeSphere.setLayoutData(layoutData);

		this.systemTrayNotificationOfReply = getNewUnit(comp, SWT.NONE, bundle
				.getString(SHOULD_REPLY_TO_MY_MESSAGES_NOTIFY_SYSTEM_TRAY),
				this.controller.isSystemTrayNotificationOfReply(sphereId),
				this.controller.isSystemTrayNotificationOfReplyModify(sphereId));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.systemTrayNotificationOfReply.setLayoutData(layoutData);

		return comp;
	}

	/**
	 * @param sphereId
	 */
	public void setSelection(String sphereId) {
		String displayName = getDisplayOnSystem(sphereId);
		String[] items = this.spheres.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(displayName)) {
				setSelection(i);
				processSelection(sphereId);
				return;
			}
		}
	}

	private void setSelection(int index) {
		this.spheres.select(index);
	}

	private String getSystemOnDisplay(String displayName) {
		for (SphereItem item : this.spheresItems) {
			if (item.getDisplayName().equals(displayName)) {
				return item.getSystemName();
			}
		}
		return displayName;
	}

	private String getDisplayOnSystem(String systemName) {
		for (SphereItem item : this.spheresItems) {
			if (item.getSystemName().equals(systemName)) {
				return item.getDisplayName();
			}
		}
		return systemName;
	}

	protected void processSelection(String sphereId) {
		if (logger.isDebugEnabled()){
			logger.debug("Processing selection for sphereId: " + sphereId);
		}
		this.newMessageShouldOpenTab.setAllValues(this.controller
				.isNewMessageShouldOpenTab(sphereId), this.controller
				.isNewMessageShouldOpenTabModify(sphereId));
		this.replyIsAlsoAPopUpToPopUp.setAllValues(this.controller
				.isReplyIsAlsoAPopUpToPopUp(sphereId), this.controller
				.isReplyIsAlsoAPopUpToPopUpModify(sphereId));
		this.systemTrayNotificationOfReply.setAllValues(this.controller
				.isSystemTrayNotificationOfReply(sphereId), this.controller
				.isSystemTrayNotificationOfReplyModify(sphereId));
		this.systemTrayNotificationOfFirstTimeSphere
				.setAllValues(
						this.controller
								.isSystemTrayNotificationOfFirstTimeSphere(sphereId),
						this.controller
								.isSystemTrayNotificationOfReplyModify(sphereId));
	}

	/**
	 * @return the controller
	 */
	protected PreferencesController getController() {
		return this.controller;
	}

	public String getCurrentSphereSystemName() {
		return getCurrentSphereId().getSystemName();
	}

	/**
	 * @return the newMessageShouldOpenTab
	 */
	public boolean getNewMessageShouldOpenTab() {
		return this.newMessageShouldOpenTab.getValue();
	}

	/**
	 * @return the replyIsAlsoAPopUpToPopUp
	 */
	public boolean getReplyIsAlsoAPopUpToPopUp() {
		return this.replyIsAlsoAPopUpToPopUp.getValue();
	}

	/**
	 * @return the systemTrayNotificationOfFirstTimeSphere
	 */
	public boolean getSystemTrayNotificationOfFirstTimeSphere() {
		return this.systemTrayNotificationOfFirstTimeSphere.getValue();
	}

	/**
	 * @return the systemTrayNotificationOfReply
	 */
	public boolean getSystemTrayNotificationOfReply() {
		return this.systemTrayNotificationOfReply.getValue();
	}

	/**
	 * @return
	 */
	public boolean getNewMessageShouldOpenTabModify() {
		return this.newMessageShouldOpenTab.getEnabled();
	}

	/**
	 * @return
	 */
	public boolean getReplyIsAlsoAPopUpToPopUpModify() {
		return this.replyIsAlsoAPopUpToPopUp.getEnabled();
	}

	/**
	 * @return
	 */
	public boolean getSystemTrayNotificationOfFirstTimeSphereModify() {
		return this.systemTrayNotificationOfFirstTimeSphere.getEnabled();
	}

	/**
	 * @return
	 */
	public boolean getSystemTrayNotificationOfReplyModify() {
		return this.systemTrayNotificationOfReply.getEnabled();
	}
}
