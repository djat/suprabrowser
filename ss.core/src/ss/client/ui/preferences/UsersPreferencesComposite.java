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
import ss.client.preferences.PreferencesSupporter;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.preferences.changesdetector.UserPreferencesChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.util.ImagesPaths;

/**
 * @author zobo
 * 
 */
public class UsersPreferencesComposite extends Composite implements IChangable {

	private static final String CHOOSE_USER = "USERSPREFERENCESCOMPOSITE.CHOOSE_USER";

	private static final String NO_USERS_TO_SET_PREFERENCES = "USERSPREFERENCESCOMPOSITE.NO_USERS_TO_SET_PREFERENCES";

	private static final String APPLY = "USERSPREFERENCESCOMPOSITE.APPLY";

	private static final String COMMON_USER_PREFERENCES_TITLE = "USERSPREFERENCESCOMPOSITE.COMMON_USER_PREFERENCES_TITLE";

	private static final String SOUND_FOR_CONFIRM_RECIEPT_MESSAGE = "USERSPREFERENCESCOMPOSITE.SOUND_FOR_CONFIRM_RECIEPT_MESSAGE";

	private static final String SOUND_FOR_NORMAL_MESSAGE = "USERSPREFERENCESCOMPOSITE.SOUND_FOR_NORMAL_MESSAGE";

	private static final String DEFAULT_DELIVERY_TYPE_FOR_SPHERE = "USERSPREFERENCESCOMPOSITE.DEFAULT_DELIVERY_TYPE_FOR_SPHERE";

	private static final String DEFAULT_DELIVERY_TYPE_FOR_PERSON_TO_PERSON_SPHERES = "USERSPREFERENCESCOMPOSITE.DEFAULT_DELIVERY_TYPE_FOR_PERSON_TO_PERSON_SPHERES";
	
	private static final String ON_TOP_BEHAVIOR_OF_POP_UP_WINDOWS = "USERSPREFERENCESCOMPOSITE.ON_TOP_BEHAVIOR_OF_POP_UP_WINDOWS";
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_USERSPREFERENCESCOMPOSITE);

	private PreferencesAdminController controller;

	private Combo usersCombo;

	private PreferenceManagerShellUnit normalMessageSoundPlay;

	private PreferenceManagerShellUnit confirmRecieptMessageSoundPlay;

	private static Image soundImage;

	private List<String> users;

	private PreferenceAbstractShellUnit canChangeDefaultTypeForSphere;

	private PreferencesManageComboShellUnit canChangeDefaultDeliveryTypeForP2PSphere;

	private PreferencesManageComboShellUnit onTopBehaviour;
	
	private IChangesDetector detector;

	private static Image userImage;
	
	private String previousUser;
	
	private String nextUser;
	
	private ManagePreferencesCommonShell commonShell;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpheresPreferencesComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public UsersPreferencesComposite(ManagePreferencesCommonShell commonShell) {
		super(commonShell.getTabFolder(), SWT.NONE);
		this.commonShell = commonShell;
		this.controller = commonShell.getController();
		this.detector = new UserPreferencesChangesDetector(this);
		this.users = this.controller.getUsers();
		initIcons();
		createGUI(this);
	}

	private void initIcons() {
		try {
			soundImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SOUND_ICON).openStream());
			userImage = new Image(Display.getDefault(), getClass().getResource(
					ImagesPaths.CONTACT).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	private void createGUI(Composite parent) {
		parent.setLayout(new GridLayout());
		if (this.users.size() == 0) {
			createVoidComposite(parent);
		} else {

			createUsersList(parent);
			createPreferencesComposite(parent, this.users.get(0));

			Composite buttonPanel = new Composite(this, SWT.NONE);
			buttonPanel.setLayoutData(new GridData(0, SWT.BOTTOM, true, true, 1, 1));
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
	}
	
	private void createVoidComposite(Composite parent){
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(layoutData);
		
		Label label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(NO_USERS_TO_SET_PREFERENCES));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(layoutData);
	}

	/**
	 * @param parent
	 */
	private void createUsersList(Composite parent) {
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
		label.setImage(userImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(layoutData);

		label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(CHOOSE_USER));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(layoutData);

		this.usersCombo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 2;
		this.usersCombo.setLayoutData(layoutData);

		for (String user : this.users) {
			this.usersCombo.add(user);
		}
		setSelection(0);

		this.usersCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				setPreviousUser(getNextUser());
				setNextUser(UsersPreferencesComposite.this.usersCombo.getText());
				
				if((getPreviousUser()==null || !getPreviousUser().equals(getNextUser())) &&
						getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(UsersPreferencesComposite.this);
				} else {
					performFinalAction();
				}
			}
		});
	}

	private void createPreferencesComposite(Composite parent, String userName) {
		CreateSoundComposite(parent, userName);
		CreateCommonUserPreferencesComposite(parent, userName);
	}

	/**
	 * @param parent
	 * @param userName
	 */
	private void CreateCommonUserPreferencesComposite(Composite parent,
			String userName) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(layoutData);

		Label label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(COMMON_USER_PREFERENCES_TITLE));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(layoutData);

		this.canChangeDefaultTypeForSphere = new PreferenceSimpleShellUnit(
				comp,
				SWT.NONE,
				bundle.getString(DEFAULT_DELIVERY_TYPE_FOR_SPHERE),
				this.controller.isCanChangeDefaultTypeForSphere(userName), true, getDetector());
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.canChangeDefaultTypeForSphere.setLayoutData(layoutData);
		
		this.canChangeDefaultDeliveryTypeForP2PSphere = new PreferencesManageComboShellUnit(
				comp,
				SWT.NONE,
				bundle.getString(DEFAULT_DELIVERY_TYPE_FOR_PERSON_TO_PERSON_SPHERES),
				PreferencesSupporter.getDeliveryTypes(), this.controller.isCanChangeDefaultDeliveryForP2PSphere(userName), getDetector());
		//this.canChangeDefaultDeliveryTypeForP2PSphere.setValue(this.controller.getDefaultDeliveryTypeForP2PSphere(userName));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.canChangeDefaultDeliveryTypeForP2PSphere.setLayoutData(layoutData);
		
		this.onTopBehaviour = new PreferencesManageComboShellUnit(comp,
				SWT.NONE, bundle.getString(ON_TOP_BEHAVIOR_OF_POP_UP_WINDOWS), 
				PreferencesSupporter.getPopUpBehaviorStrings() ,this.controller.isPopUpBehaviorModify(userName), getDetector());
		//this.onTopBehaviour.setValue(PreferencesSupporter.getPopUpBehaviourName(this.controller.getPopUpBehaviorValue(userName)));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.onTopBehaviour.setLayoutData(layoutData);
		
		setInitialValuesToComboUnits();
	}

	private void applyPerformed() {
		getDetector().collectChangesAndUpdate();
		//this.controller.applySingleUser(this);
	}

	protected void CreateSoundComposite(Composite parent, String userName) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(layoutData);

		Label label = new Label(comp, SWT.LEFT);
		label.setImage(soundImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(layoutData);

		this.normalMessageSoundPlay = new PreferenceManagerShellUnit(comp, SWT.NONE,
				bundle.getString(SOUND_FOR_NORMAL_MESSAGE), this.controller
						.isNormalMessageSoundPlay(userName), this.controller
						.isNormalMessageSoundPlayModify(userName), this.detector);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.normalMessageSoundPlay.setLayoutData(layoutData);

		this.confirmRecieptMessageSoundPlay = new PreferenceManagerShellUnit(comp, SWT.NONE,
				bundle.getString(SOUND_FOR_CONFIRM_RECIEPT_MESSAGE),
				this.controller.isConfirmRecieptMessageSoundPlay(userName),
				this.controller
						.isConfirmRecieptMessageSoundPlayModify(userName), this.detector);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.confirmRecieptMessageSoundPlay.setLayoutData(layoutData);
	}

	private void setSelection(int index) {
		this.usersCombo.select(index);
		setNextUser(this.usersCombo.getText());
	}

	private void processSelection(String userName) {
		this.confirmRecieptMessageSoundPlay.setAllValues(this.controller
				.isConfirmRecieptMessageSoundPlay(userName), this.controller
				.isConfirmRecieptMessageSoundPlayModify(userName));
		this.normalMessageSoundPlay.setAllValues(this.controller
				.isNormalMessageSoundPlay(userName), this.controller
				.isNormalMessageSoundPlayModify(userName));
		this.canChangeDefaultTypeForSphere.setAllValues(this.controller
				.isCanChangeDefaultTypeForSphere(userName), true);
	}

	public PreferenceAbstractShellUnit getNewUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify) {
		return new PreferenceManagerShellUnit(parent, style, labelString,
				selection, allowModify, this.detector);
	}

	/**
	 * @return
	 */
	public boolean getCanChangeDefaultTypeForSphere() {
		return this.canChangeDefaultTypeForSphere.getValue();
	}

	/**
	 * @return
	 */
	public boolean getConfirmRecieptMessageSoundPlay() {
		return this.confirmRecieptMessageSoundPlay.getValue();
	}

	/**
	 * @return
	 */
	public boolean getConfirmRecieptMessageSoundPlayModify() {
		return this.confirmRecieptMessageSoundPlay.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getNormalMessageSoundPlay() {
		return this.normalMessageSoundPlay.getValue();
	}

	/**
	 * @return
	 */
	public boolean getNormalMessageSoundPlayModify() {
		return this.normalMessageSoundPlay.getPermittion();
	}

	/**
	 * @return
	 */
	public String getP2PSpheresDefaultDeliveryType() {
		return this.canChangeDefaultDeliveryTypeForP2PSphere.getValue();
	}

	/**
	 * @return
	 */
	public boolean getP2PSpheresDefaultDeliveryTypeModify() {
		return this.canChangeDefaultDeliveryTypeForP2PSphere.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getPopUpOnTop() {
		return PreferencesSupporter.getPopUpBehaviourValue(this.onTopBehaviour.getValue());
	}

	/**
	 * @return
	 */
	public boolean getPopUpOnTopModify() {
		return this.onTopBehaviour.getPermittion();
	}
	
	public PreferencesAdmin getPreferncesAdmin() {
		return this.controller.getPreferencesAdmin();
	}

	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void performFinalAction() {
		String userName = UsersPreferencesComposite.this.usersCombo.getText();
		logger.info("Selected User name: " + userName);
		processSelection(userName);
	}

	public void revertSelection() {
		this.usersCombo.select(this.usersCombo.indexOf(getPreviousUser()));
		setNextUser(getPreviousUser());
	}

	
	private String getNextUser() {
		return this.nextUser;
	}
	
	private void setNextUser(String nextUser) {
		this.nextUser = nextUser;
	}

	private String getPreviousUser() {
		return this.previousUser;
	}

	private void setPreviousUser(String previousUser) {
		this.previousUser = previousUser;
	}
	
	public void setInitialValuesToComboUnits() {
		String userName = this.usersCombo.getText();
		this.canChangeDefaultDeliveryTypeForP2PSphere.setValue(this.controller.getDefaultDeliveryTypeForP2PSphere(userName));
		this.onTopBehaviour.setValue(PreferencesSupporter.getPopUpBehaviourName(this.controller.getPopUpBehaviorValue(userName)));
	}

	public void jumpToNextItem() {
		this.commonShell.jumpToNextItem();
	}

	/**
	 * 
	 */
	public void rollbackChanges() {
		String username = this.usersCombo.getText();
		processSelection(username);
	}
}
