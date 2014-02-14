/**
 * 
 */
package ss.client.ui.preferences;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.preferences.PreferencesController;
import ss.client.preferences.PreferencesSupporter;
import ss.util.ImagesPaths;

/**
 * @author zobo
 * 
 */
public class GlobalPreferencesSimpleUserComposite extends Composite {

	private static final String APPLY = "GLOBALPREFERENCESSIMPLEUSERCOMPOSITE.APPLY";

	private static final String SHOULD_SOUND_BE_PLAYED_FOR_CONFIRM_RECIEPT_MESSAGE = "GLOBALPREFERENCESSIMPLEUSERCOMPOSITE.SHOULD_SOUND_BE_PLAYED_FOR_CONFIRM_RECIEPT_MESSAGE";

	private static final String SHOULD_SOUND_BE_PLAYED_FOR_NORMAL_MESSAGE = "GLOBALPREFERENCESSIMPLEUSERCOMPOSITE.SHOULD_SOUND_BE_PLAYED_FOR_NORMAL_MESSAGE";

	private static final String ON_TOP_BEHAVIOR_OF_POP_UP_WINDOWS = "GLOBALPREFERENCESSIMPLEUSERCOMPOSITE.ON_TOP_BEHAVIOR_OF_POP_UP_WINDOWS";

	private static final String DEFAULT_DELIVERY_TYPE_FOR_PERSON_TO_PERSON_SPHERES = "GLOBALPREFERENCESSIMPLEUSERCOMPOSITE.DEFAULT_DELIVERY_TYPE_FOR_PERSON_TO_PERSON_SPHERES";

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_GLOBALPREFERENCESSIMPLEUSERCOMPOSITE);

	private PreferenceAbstractShellUnit normalMessageSoundPlay;

	private PreferenceAbstractShellUnit confirmRecieptMessageSoundPlay;

	private PreferencesComboShellUnit deliveryType;

	private PreferencesComboShellUnit onTopBehavior;

	private PreferencesController controller;

	private static Image soundImage;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GlobalPreferencesSimpleUserComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public GlobalPreferencesSimpleUserComposite(Composite parent, int style,
			PreferencesController controller) {
		super(parent, style);
		this.controller = controller;
		initIcons();
		createGUI(this);
	}

	private void createGUI(Composite parent) {
		parent.setLayout(new GridLayout());

		CreateSoundComposite(parent);
		CreateOptionsComposite(parent);
		CreateButtonsComposite(parent);
	}

	private void CreateOptionsComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(layoutData);

		this.deliveryType = new PreferencesSimpleUserComboShellUnit(comp,
				SWT.NONE,bundle.getString(DEFAULT_DELIVERY_TYPE_FOR_PERSON_TO_PERSON_SPHERES),
				PreferencesSupporter.getDeliveryTypes(), this.controller
						.isCanChangeDefaultDeliveryForP2PSphere());
		this.deliveryType.setValue(this.controller
				.getDefaultDeliveryTypeForP2PSphere());
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.deliveryType.setLayoutData(layoutData);

		this.onTopBehavior = new PreferencesSimpleUserComboShellUnit(comp,
				SWT.NONE, bundle.getString(ON_TOP_BEHAVIOR_OF_POP_UP_WINDOWS),
				PreferencesSupporter.getPopUpBehaviorStrings(), this.controller
						.isPopUpBehaviorModify());
		this.onTopBehavior
				.setValue(PreferencesSupporter
						.getPopUpBehaviourName(this.controller
								.getPopUpBehaviorValue()));
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.onTopBehavior.setLayoutData(layoutData);
	}

	private void CreateButtonsComposite(Composite parent) {

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

	private void initIcons() {
		try {
			soundImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SOUND_ICON).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	/**
	 * @param parent
	 */
	protected void CreateSoundComposite(Composite parent) {
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

		this.normalMessageSoundPlay = getNewUnit(comp, SWT.NONE, bundle
				.getString(SHOULD_SOUND_BE_PLAYED_FOR_NORMAL_MESSAGE),
				this.controller.isNormalMessageSoundPlay(), this.controller
						.isNormalMessageSoundPlayModify());
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.normalMessageSoundPlay.setLayoutData(layoutData);

		this.confirmRecieptMessageSoundPlay = getNewUnit(comp, SWT.NONE, bundle
				.getString(SHOULD_SOUND_BE_PLAYED_FOR_CONFIRM_RECIEPT_MESSAGE),
				this.controller.isConfirmRecieptMessageSoundPlay(),
				this.controller.isConfirmRecieptMessageSoundPlayModify());
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		this.confirmRecieptMessageSoundPlay.setLayoutData(layoutData);
	}

	protected void applyPerformed() {
		this.controller.applyGlobal(this);
	}

	public boolean getNormalMessageSoundPlay() {
		return this.normalMessageSoundPlay.getValue();
	}

	public boolean getConfirmRecieptMessageSoundPlay() {
		return this.confirmRecieptMessageSoundPlay.getValue();
	}

	/**
	 * @return
	 */
	public boolean getNormalMessageSoundPlayModify() {
		return this.normalMessageSoundPlay.getEnabled();
	}

	/**
	 * @return
	 */
	public boolean getConfirmRecieptMessageSoundPlayModify() {
		return this.confirmRecieptMessageSoundPlay.getEnabled();
	}

	/**
	 * @return
	 */
	public String getDefaultDeliveryTypeForP2P() {
		return this.deliveryType.getValue();
	}

	/**
	 * @return
	 */
	public boolean getDefaultDeliveryTypeForP2PModify() {
		return this.deliveryType.getPermittion();
	}

	/**
	 * @return
	 */
	public boolean getPopUpOnTop() {
		return PreferencesSupporter.getPopUpBehaviourValue(this.onTopBehavior
				.getValue());
	}

	/**
	 * @return
	 */
	public boolean getPopUpOnTopModify() {
		return this.onTopBehavior.getPermittion();
	}

	public PreferenceAbstractShellUnit getNewUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify) {
		return new PreferenceSimpleShellUnit(parent, style, labelString,
				selection, allowModify);
	}
}
