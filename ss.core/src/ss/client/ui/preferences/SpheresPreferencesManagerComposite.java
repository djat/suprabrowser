/**
 * 
 */
package ss.client.ui.preferences;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
import ss.client.preferences.PreferencesAdmin;
import ss.client.preferences.PreferencesAdminController;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.preferences.changesdetector.SpherePreferencesChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.domainmodel.SphereItem;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class SpheresPreferencesManagerComposite extends Composite implements IChangable {


	private static final String CHOOSE_SPHERE = "SPHERESPREFERENCESMANAGERCOMPOSITE.CHOOSE_SPHERE";

	private static final String APPLY = "SPHERESPREFERENCESMANAGERCOMPOSITE.APPLY";

	private static final String SHOULD_BE_SYSTEM_TRAY_NOTIFICATION_OF_REPLY = "SPHERESPREFERENCESMANAGERCOMPOSITE.SHOULD_BE_SYSTEM_TRAY_NOTIFICATION_OF_REPLY";

	private static final String SHOULD_BE_SYSTEM_TRAY_NOTIFICATION_OF_FIRST_TIME_OPENED_SPHERE = "SPHERESPREFERENCESMANAGERCOMPOSITE.SHOULD_BE_SYSTEM_TRAY_NOTIFICATION_OF_FIRST_TIME_OPENED_SPHERE";

	private static final String REPLY_IS_ALSO_A_POP_UP_TO_POP_UP_MESSAGE = "SPHERESPREFERENCESMANAGERCOMPOSITE.REPLY_IS_ALSO_A_POP_UP_TO_POP_UP_MESSAGE";

	private static final String SHOUD_NEW_MESSAGE_OPEN_CURRENT_SPHERE = "SPHERESPREFERENCESMANAGERCOMPOSITE.SHOUD_NEW_MESSAGE_OPEN_CURRENT_SPHERE";
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_SPHERESPREFERENCESMANAGERCOMPOSITE);

	private PreferenceAbstractShellUnit canChangeEmailForwardingRules;
	
	private PreferencesAdminController controller;

	private Combo spheres;
	
	private PreferenceManagerShellUnit newMessageShouldOpenTab;
	
	private PreferenceManagerShellUnit replyIsAlsoAPopUpToPopUp;
	
	private PreferenceManagerShellUnit systemTrayNotificationOfFirstTimeSphere;
	
	private PreferenceManagerShellUnit systemTrayNotificationOfReply;
	
	private List<SphereItem> spheresReferences;

	private static Image sphereImage;
	
	private IChangesDetector detector;
	
	private ManagePreferencesCommonShell commonShell;
	
	private String previousSphere;
	
	private String nextSphere;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpheresPreferencesComposite.class);
	/**
	 * @param parent
	 * @param style
	 * @param controller
	 * @param sF
	 */
	public SpheresPreferencesManagerComposite(ManagePreferencesCommonShell commonShell) {
		super(commonShell.getTabFolder(), SWT.NONE);
		this.commonShell = commonShell;
		this.controller = commonShell.getController();
		this.spheresReferences = this.controller.getGroupSpheres();
		this.detector = new SpherePreferencesChangesDetector(this);
		initIcons();
		createGUI(this);
	}
	
	private void createGUI(Composite parent){
		parent.setLayout(new GridLayout());
		
		String initialSphereId = createSpheresList(parent);
		createPreferencesComposite(parent, initialSphereId);
		
		Composite buttonPanel = new Composite(this, SWT.NONE);
		buttonPanel.setLayoutData(new GridData(0, SWT.TOP, true, false, 1, 1));
		buttonPanel.setLayout(LayoutUtils.createNoMarginGridLayout(3));
		LayoutUtils.addSpacer(buttonPanel);
		Button button;
		button = new Button(buttonPanel, SWT.PUSH);
		button.setText(bundle.getString(APPLY));
		GridData gridData = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		button.setLayoutData(gridData);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyPerformed();
			}
		});
	}

	/**
	 * 
	 */
	protected void applyPerformed() {
		getDetector().collectChangesAndUpdate();
		//this.controller.applySingleSphere(this);
	}

	public PreferenceAbstractShellUnit getNewUnit(Composite parent, int style, String labelString, boolean selection, boolean allowModify) {
		return new PreferenceManagerShellUnit(parent,  style,
				 labelString,  selection,  allowModify, this.detector);
	}
	
	private void initIcons() {
		try {
			sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}
	}
	
	private Composite createPreferencesComposite(Composite parent, String sphereId){
		if (logger.isDebugEnabled()){
			logger.debug("Creating SpheresPreferencesManagerComposite starting with sphereId: " + sphereId);
		}
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        comp.setLayoutData(layoutData);
        
		this.newMessageShouldOpenTab = new PreferenceManagerShellUnit(comp, SWT.NONE, bundle.getString(SHOUD_NEW_MESSAGE_OPEN_CURRENT_SPHERE), this.controller.isNewMessageShouldOpenTab(sphereId),
				this.controller.isNewMessageShouldOpenTabModify(sphereId), this.detector);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.verticalAlignment = GridData.BEGINNING;
        layoutData.horizontalAlignment = GridData.FILL;
        this.newMessageShouldOpenTab.setLayoutData(layoutData);
        
		this.replyIsAlsoAPopUpToPopUp = new PreferenceManagerShellUnit(comp, SWT.NONE, bundle.getString(REPLY_IS_ALSO_A_POP_UP_TO_POP_UP_MESSAGE), 
				this.controller.isReplyIsAlsoAPopUpToPopUp(sphereId), this.controller.isReplyIsAlsoAPopUpToPopUpModify(sphereId), this.detector);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.verticalAlignment = GridData.BEGINNING;
        layoutData.horizontalAlignment = GridData.FILL;
        this.replyIsAlsoAPopUpToPopUp.setLayoutData(layoutData);
        
		this.systemTrayNotificationOfFirstTimeSphere = new PreferenceManagerShellUnit(comp, SWT.NONE, bundle.getString(SHOULD_BE_SYSTEM_TRAY_NOTIFICATION_OF_FIRST_TIME_OPENED_SPHERE), 
				this.controller.isSystemTrayNotificationOfFirstTimeSphere(sphereId), this.controller.isSystemTrayNotificationOfFirstTimeSphereModify(sphereId), this.detector);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.verticalAlignment = GridData.BEGINNING;
        layoutData.horizontalAlignment = GridData.FILL;
        this.systemTrayNotificationOfFirstTimeSphere.setLayoutData(layoutData);
        
		this.systemTrayNotificationOfReply = new PreferenceManagerShellUnit(comp, SWT.NONE, bundle.getString(SHOULD_BE_SYSTEM_TRAY_NOTIFICATION_OF_REPLY), 
				this.controller.isSystemTrayNotificationOfReply(sphereId), this.controller.isSystemTrayNotificationOfReplyModify(sphereId), this.detector);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = false;
        layoutData.verticalAlignment = GridData.BEGINNING;
        layoutData.horizontalAlignment = GridData.FILL;
        this.systemTrayNotificationOfReply.setLayoutData(layoutData);
        
        return comp;
	}

	protected void processSelection(String sphereId) {
		if (logger.isDebugEnabled()){
			logger.debug("Processing selection for sphereId: " + sphereId);
		}
		this.newMessageShouldOpenTab.setAllValues(
				this.controller.isNewMessageShouldOpenTab(sphereId),
				this.controller.isNewMessageShouldOpenTabModify(sphereId));
		this.replyIsAlsoAPopUpToPopUp.setAllValues(
				this.controller.isReplyIsAlsoAPopUpToPopUp(sphereId),
				this.controller.isReplyIsAlsoAPopUpToPopUpModify(sphereId));
		this.systemTrayNotificationOfReply.setAllValues(
				this.controller.isSystemTrayNotificationOfReply(sphereId),
				this.controller.isSystemTrayNotificationOfReplyModify(sphereId));
		this.systemTrayNotificationOfFirstTimeSphere.setAllValues(
				this.controller.isSystemTrayNotificationOfFirstTimeSphere(sphereId),
				this.controller.isSystemTrayNotificationOfFirstTimeSphereModify(sphereId));
	}
	
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
        String supraSphere = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getSupraSphereName();
        this.spheres.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				setPreviousSphere(getNextSphere());
				setNextSphere(SpheresPreferencesManagerComposite.this.spheres.getText());
				if (logger.isDebugEnabled()) {
					logger.debug("previous: "+getPreviousSphere());
					logger.debug("next: "+getNextSphere());
					logger.debug("changed?: "+getDetector().hasChanges());
				}
				if((getPreviousSphere()==null || !getPreviousSphere().equals(getNextSphere())) &&
						getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(SpheresPreferencesManagerComposite.this);
				} else {
					performFinalAction();
				}
			}
        });
        
        String startingSphereId = null;
        for (SphereItem sf : this.spheresReferences){
        	String sphereDisplayName = sf.getDisplayName();
        	if (!(supraSphere.equals(sphereDisplayName))) {
        		if (startingSphereId == null){
					startingSphereId = sf.getSystemName();
				}
        		this.spheres.add(sphereDisplayName);
        	}
        }
        if (startingSphereId == null){
			startingSphereId = supraSphere;
		}
        setSelection(0);
        
        return startingSphereId;
	}
	
	/**
	 * @param text
	 */
	protected void setNextSphere(String text) {
		this.nextSphere = text;
	}
	
	/**
	 * @param text
	 */
	protected void setPreviousSphere(String text) {
		this.previousSphere = text;
	}
	
	protected String getPreviousSphere() {
		return this.previousSphere;
	}
	
	protected String getNextSphere() {
		return this.nextSphere;
	}

	public void setSelection(String sphereId) {
		String displayName = getDisplayOnSystem(sphereId);
		String[] items = this.spheres.getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(displayName)){
				setSelection(i);
				processSelection(sphereId);
				return;
			}
		}
	}
	
	private void setSelection(int index){
		this.spheres.select(index);
		setNextSphere(this.spheres.getText());
	}
	
	private String getSystemOnDisplay(String displayName){
		for (SphereItem ref : this.spheresReferences) {
			if (ref.getDisplayName().equals(displayName)){
				return ref.getSystemName();
			}
		}
		return displayName;
	}
	
	private String getDisplayOnSystem(String systemName){
		for (SphereItem ref : this.spheresReferences) {
			if (ref.getSystemName().equals(systemName)){
				return ref.getDisplayName();
			}
		}
		return systemName;
	}

	/**
	 * @return
	 */
	public boolean getCanChangeEmailForwardingRules() {
		return this.canChangeEmailForwardingRules.getValue();
	}

	/**
	 * @return
	 */
	public boolean getNewMessageShouldOpenTab() {
		return this.newMessageShouldOpenTab.getValue();
	}

	/**
	 * @return
	 */
	public boolean getNewMessageShouldOpenTabModify() {
		return this.newMessageShouldOpenTab.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getSystemTrayNotificationOfReplyModify() {
		return this.systemTrayNotificationOfReply.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getSystemTrayNotificationOfReply() {
		return this.systemTrayNotificationOfReply.getValue();
	}

	/**
	 * @return
	 */
	public boolean getSystemTrayNotificationOfFirstTimeSphereModify() {
		return this.systemTrayNotificationOfFirstTimeSphere.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getSystemTrayNotificationOfFirstTimeSphere() {
		return this.systemTrayNotificationOfFirstTimeSphere.getValue();
	}

	/**
	 * @return
	 */
	public boolean getReplyIsAlsoAPopUpToPopUpModify() {
		return this.replyIsAlsoAPopUpToPopUp.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getReplyIsAlsoAPopUpToPopUp() {
		return this.replyIsAlsoAPopUpToPopUp.getValue();
	}
	
	public PreferencesAdmin getPreferencesAdmin() {
		return this.controller.getPreferencesAdmin();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.memberaccess.IChangable#getDetector()
	 */
	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void performFinalAction() {
		String displayName = SpheresPreferencesManagerComposite.this.spheres.getText();
		logger.info("Selected sphere name: " + displayName);
		processSelection(getSystemOnDisplay(displayName));
	}

	public void revertSelection() {
		this.spheres.select(this.spheres.indexOf(getPreviousSphere()));
		setNextSphere(getPreviousSphere());
	}

	public void jumpToNextItem() {
		this.commonShell.jumpToNextItem();
	}
}
