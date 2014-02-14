/**
 * 
 */
package ss.client.ui.preferences.forwarding;

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
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.preferences.ForwardingController;
import ss.client.preferences.PreferencesController;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.domainmodel.SphereItem;
import ss.domainmodel.preferences.emailforwarding.CommonEmailForwardingPreferences.ForwardingModes;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class SphereUserForwardingSubComposite extends Composite{

	private static final ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_FORWARDING_SPHEREUSERFORWARDINGSUBCOMPOSITE);
	
	private static final String CHOOSE_SPHERE = "SPHEREUSERFORWARDINGSUBCOMPOSITE.CHOOSE_SPHERE";
	
	private static final String EMAIL_ADDRESS_TO_FORWARD = "SPHEREUSERFORWARDINGSUBCOMPOSITE.EMAIL_ADDRESS_TO_FORWARD";

	private static final String OFF = "SPHEREUSERFORWARDINGSUBCOMPOSITE.OFF";

	private static final String AUTOMATIC = "SPHEREUSERFORWARDINGSUBCOMPOSITE.AUTOMATIC";

	private static final String FORCED = "SPHEREUSERFORWARDINGSUBCOMPOSITE.FORCED";

	private static final String MODE_OF_EMAIL_FORWARDING = "SPHEREUSERFORWARDINGSUBCOMPOSITE.MODE_OF_EMAIL_FORWARDING";

	private static final String APPLY = "SPHEREUSERFORWARDINGSUBCOMPOSITE.APPLY";
	
	private static final String SET_SPECIFIC_FORWARDING_RULES = "SPHEREUSERFORWARDINGSUBCOMPOSITE.SET_SPECIFIC_FORWARDING_RULES";
	
	private static final String PER_SPHERE_FORWARDING_RULE = "SPHEREUSERFORWARDINGSUBCOMPOSITE.PER_SPHERE_FORWARDING_RULE";

	private static Image sphereImage;

	private List<SphereItem> spheresReferences;

	private Combo spheres;

	private ForwardingController controller;
	
	private SupraSphereFrame sF;
	
	private Button mode1;

	private Button mode2;

	private Button mode3;

	private Text text;
	
	private Button checkOverride;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereUserForwardingSubComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public SphereUserForwardingSubComposite(Composite parent, int style, ForwardingController controller, PreferencesController prefController, SupraSphereFrame sF) {
		super(parent, style);
		this.controller = controller;
		this.spheresReferences = prefController.getGroupSpheres();
		this.sF = sF;
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
		parent.setLayout(new GridLayout(2,false));	
		
		Label label = new Label(parent, SWT.LEFT);
		label.setText(bundle.getString(PER_SPHERE_FORWARDING_RULE));
		GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false,2,1);
		label.setLayoutData(data);
		
		createSpheresList(parent);
		
		createForwardingComposite(parent, this.spheresReferences.get(0).getSystemName());
		
		processSelection(getCurrentSphereSystemName());
	}
	
	private void createForwardingComposite(Composite parent, String sphereId) {	
		GridData data;
		
		Composite activate = new Composite(parent, SWT.NONE);
		data = new GridData(SWT.LEFT, SWT.TOP, false, false,2,1);
		activate.setLayoutData(data);
		activate.setLayout(new GridLayout(2,false));
		
		this.checkOverride = new Button(activate, SWT.CHECK);
		this.checkOverride.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				checkOverridePreformed();
			}
			
		});
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.checkOverride.setLayoutData(data);
		
		Label label = new Label(activate, SWT.LEFT);
		label.setText(bundle.getString(SET_SPECIFIC_FORWARDING_RULES));
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		label.setLayoutData(data);
		
		Composite email = createEmailComposite(parent, sphereId);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		email.setLayoutData(data);
		
		Composite modes = createModesComposite(parent, sphereId);
		data = new GridData(SWT.FILL, SWT.FILL, false, true);
		modes.setLayoutData(data);
		
		Composite buttonPanel = new Composite(this, SWT.NONE);
		buttonPanel.setLayout(LayoutUtils.createNoMarginGridLayout(2));
		LayoutUtils.addSpacer(buttonPanel);
		Button applyButton = new Button(buttonPanel, SWT.PUSH);
		applyButton.setText(bundle.getString(APPLY));
		applyButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				applyPreformed();
			}
		});
		data = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		applyButton.setLayoutData(data);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		buttonPanel.setLayoutData(data);
	}
	
	private Composite createModesComposite(Composite parent, String sphereId) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData data;
		Label label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(MODE_OF_EMAIL_FORWARDING));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		label.setLayoutData(data);
		this.mode1 = new Button(comp, SWT.RADIO);
		this.mode1.setText(bundle.getString(FORCED));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode1.setLayoutData(data);
		this.mode2 = new Button(comp, SWT.RADIO);
		this.mode2.setText(bundle.getString(AUTOMATIC));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode2.setLayoutData(data);
		this.mode3 = new Button(comp, SWT.RADIO);
		this.mode3.setText(bundle.getString(OFF));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode3.setLayoutData(data);
		
		return comp;
	}
	
	private Composite createEmailComposite(Composite parent, String sphereId) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData data;
		Label label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(EMAIL_ADDRESS_TO_FORWARD));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		label.setLayoutData(data);
		this.text = new Text(comp, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		this.text.setLayoutData(data);
		
		return comp;
	}
	
	private void setEmailAddress(String sphereId) {
		this.text.setText(this.controller.getEmailAdress(sphereId));
	}

	private ForwardingModes getMode(){
		if (this.mode1.getSelection()){
			return ForwardingModes.FORCED;
		}
		if (this.mode2.getSelection()){
			return ForwardingModes.AUTOMATIC;
		}
		return ForwardingModes.OFF;
	}
	
	private void setMode(String sphereId){
		ForwardingModes mode = this.controller.getMode(sphereId);
		if (mode == ForwardingModes.FORCED){
			this.mode1.setSelection(true);
			this.mode2.setSelection(false);
			this.mode3.setSelection(false);
		} else if (mode == ForwardingModes.AUTOMATIC){
			this.mode1.setSelection(false);
			this.mode2.setSelection(true);
			this.mode3.setSelection(false);
		} else {
			this.mode1.setSelection(false);
			this.mode2.setSelection(false);
			this.mode3.setSelection(true);
		}
	}

	private void createSpheresList(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		comp.setLayout(layout);
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 2;
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

		for (SphereItem item : this.spheresReferences) {
			String sphereDisplayName = item.getDisplayName();
			if (!(supraSphere.equals(sphereDisplayName))) {
				this.spheres.add(sphereDisplayName);
			}
		}
		setSelection(0);

		this.spheres.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				String displayName = SphereUserForwardingSubComposite.this.spheres
						.getText();
				if (logger.isDebugEnabled()){
					logger.debug("Selected sphere name: " + displayName);
				}
				processSelection(getSystemOnDisplay(displayName));
			}

		});
	}
	
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
		for (SphereItem ref : this.spheresReferences) {
			if (ref.getDisplayName().equals(displayName)) {
				return ref.getSystemName();
			}
		}
		return displayName;
	}

	private String getDisplayOnSystem(String systemName) {
		for (SphereItem ref : this.spheresReferences) {
			if (ref.getSystemName().equals(systemName)) {
				return ref.getDisplayName();
			}
		}
		return systemName;
	}
	
	private SphereItem getCurrentSphereId() {
		String sphereName = this.spheres.getText();
		for (SphereItem sf : this.spheresReferences) {
			if (sf.getDisplayName().equals(sphereName)) {
				return sf;
			}
		}
		return this.spheresReferences.get(0);
	}
	
	public String getCurrentSphereSystemName() {
		return getCurrentSphereId().getSystemName();
	}
	
	private void processSelection(String sphereId) {
		setEmailAddress(sphereId);
		setMode(sphereId);
		this.checkOverride.setSelection(this.controller.getSetted(sphereId));
		checkOverridePreformed();
	}
	
	private void checkOverridePreformed(){
		boolean enabled = this.checkOverride.getSelection();
		this.mode1.setEnabled(enabled);
		this.mode2.setEnabled(enabled);
		this.mode3.setEnabled(enabled);
		this.text.setEnabled(enabled);
	}
	
	private void applyPreformed(){
		ForwardingModes mode = getMode();
		String email = this.text.getText();
		boolean setted = this.checkOverride.getSelection();
		this.controller.applySphereUserForwarding(email, mode, setted, getCurrentSphereId().getSystemName());
	}

}
