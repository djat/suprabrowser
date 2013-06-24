/**
 * 
 */
package ss.client.ui.preferences.forwarding;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.preferences.ForwardingController;
import ss.client.preferences.PreferencesAdminController;
import ss.client.ui.preferences.ManagePreferencesCommonShell;
import ss.client.ui.preferences.changesdetector.ForwardingRulesChangesDetector;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionAdaptor;
import ss.client.ui.spheremanagement.SphereManager;
import ss.client.ui.spheremanagement.SphereTreeComposite;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesSphere.SphereForwardingModes;

/**
 *
 */
public class SphereForwardingSubComposite extends Composite implements IChangable {
	
	/**
	 * @author zobo
	 *
	 */
	private final class ModesSelectionListener implements SelectionListener {
		
		private SphereForwardingModes mode;
		
		public ModesSelectionListener(SphereForwardingModes mode) {
			super();
			this.mode = mode;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			
		}

		public void widgetSelected(SelectionEvent e) {
			if ((this.mode == SphereForwardingModes.CONTACTS) || (this.mode == SphereForwardingModes.MEMBERS)){
				enableAdditional(true, true, true);
			}
			if (this.mode == SphereForwardingModes.ADDITIONAL){
				enableAdditional(false, true, false);
			}
			if (this.mode == SphereForwardingModes.OFF){
				enableAdditional(false, false, true);
			}
			getDetector().setChanged(true);
		}
	}

	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_FORWARDING_SPHEREFORWARDINGADMINSUBCOMPOSITE);
	
	private static final String APPLY = "SPHEREFORWARDINGADMINSUBCOMPOSITE.APPLY";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereForwardingSubComposite.class);

	private static final String MODE_OF_EMAIL_FORWARDING = "SPHEREFORWARDINGADMINSUBCOMPOSITE.MODE_OF_EMAIL_FORWARDING";

	private static final String CONTACTS = "SPHEREFORWARDINGADMINSUBCOMPOSITE.CONTACTS";

	private static final String MEMBERS = "SPHEREFORWARDINGADMINSUBCOMPOSITE.MEMBERS";
	
	private static final String ADDITIONAL = "SPHEREFORWARDINGADMINSUBCOMPOSITE.ADDITIONAL";
	
	private static final String ADD_ADDITIONAL = "SPHEREFORWARDINGADMINSUBCOMPOSITE.ADD_ADDITIONAL";

	private static final String OFF = "SPHEREFORWARDINGADMINSUBCOMPOSITE.OFF";

	private static final String EMAIL_ADDRESS_TO_FORWARD = "SPHEREFORWARDINGADMINSUBCOMPOSITE.EMAIL_ADDRESS_TO_FORWARD";
	
	private final ForwardingController controller;
	
	private SphereTreeComposite tree;

	private Text text;

	private Button mode1;

	private Button mode2;

	private Button mode3;
	
	private Button mode4; 
	
	private Button addButton;
	
	private Button applyButton;

	private String sphereId;

	private boolean additionalValue;
	
	private final PreferencesAdminController adminController;
	
	private ManagePreferencesCommonShell commonShell;
	
	private IChangesDetector detector;
	
	private ManagedSphere viewSphere;
	
	private ManagedSphere newSelection;

	public SphereForwardingSubComposite(/*Composite parentComposite, */ManagePreferencesCommonShell commonShell) {
		super(commonShell.getTabFolder(), SWT.NONE);
		this.commonShell = commonShell;
		this.controller = commonShell.getForwardingController();
		this.adminController = commonShell.getController();
		this.detector = new ForwardingRulesChangesDetector(this);
		createGUI(this);
	}
	
	/**
	 * @param composite
	 */
	private void createGUI(Composite composite) {
		composite.setLayout(new GridLayout(2, false));
		
		SphereManager sphereManager = new SphereManager(this.adminController.getSphereDefinitionProvider());
		sphereManager.addSelectedSphereChangedListener(new SphereActionAdaptor(){
			@Override
			public void selectedSphereChanged(ManagedSphere selectedSphere) {
				setNewSelection(selectedSphere);
				if(getViewSphere()!=null && !getNewSelection().equals(getViewSphere()) && getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(true);
					getDetector().showDialog(SphereForwardingSubComposite.this);
				} else {
					performFinalAction();
				}
				//sphereSelected(selectedSphere);
			}
		});
		this.tree = new SphereTreeComposite(composite, sphereManager);	
		GridData gridData;
		gridData = new GridData(0, SWT.FILL, false, true);
		gridData.minimumWidth = 200;
		gridData.widthHint = 200;
		this.tree.setLayoutData(gridData);
		this.tree.addLabel();
		
		Composite forwarding = createForwardingComposite(composite); 
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		forwarding.setLayoutData(gridData);
		
		enableControls(false);
		
		if (logger.isDebugEnabled()){
			logger.debug("Email Forwarding Admin composite created");
		}
	}
	
	private Composite createForwardingComposite(Composite parent){
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2,false));
		
		GridData gridData;
		
		Composite c = createEmailComposite(comp);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		c = createModesComposite(comp);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		
		Composite buttonPanel = new Composite(comp, SWT.NONE);
		buttonPanel.setLayoutData(new GridData(0, SWT.TOP, true, false, 2, 1));
		buttonPanel.setLayout(LayoutUtils.createNoMarginGridLayout(3));
		LayoutUtils.addSpacer(buttonPanel);

		this.applyButton = new Button(buttonPanel, SWT.PUSH);
		this.applyButton.setText(bundle.getString(APPLY));
		gridData = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		this.applyButton.setLayoutData(gridData);
		this.applyButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				applyPerformed();
			}
		});
		
		return comp;
	}
	
	private Composite createModesComposite(Composite parent) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout());
		GridData data;
		Label label = new Label(comp, SWT.LEFT);
		label.setText(bundle.getString(MODE_OF_EMAIL_FORWARDING));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		label.setLayoutData(data);
		this.mode1 = new Button(comp, SWT.RADIO);
		this.mode1.setText(bundle.getString(CONTACTS));
		this.mode1.addSelectionListener(new ModesSelectionListener(SphereForwardingModes.CONTACTS));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode1.setLayoutData(data);
		this.mode2 = new Button(comp, SWT.RADIO);
		this.mode2.setText(bundle.getString(MEMBERS));
		this.mode2.addSelectionListener(new ModesSelectionListener(SphereForwardingModes.MEMBERS));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode2.setLayoutData(data);
		this.mode3 = new Button(comp, SWT.RADIO);
		this.mode3.setText(bundle.getString(ADDITIONAL));
		this.mode3.addSelectionListener(new ModesSelectionListener(SphereForwardingModes.ADDITIONAL));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode3.setLayoutData(data);
		this.mode4 = new Button(comp, SWT.RADIO);
		this.mode4.setText(bundle.getString(OFF));
		this.mode4.addSelectionListener(new ModesSelectionListener(SphereForwardingModes.OFF));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false);
		this.mode4.setLayoutData(data);

		return comp;
	}

	private Composite createEmailComposite(Composite parent) {
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
		this.text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				getDetector().setChanged(true);
			}
		});
		
		this.addButton = new Button(comp, SWT.CHECK);
		this.addButton.setText(bundle.getString(ADD_ADDITIONAL));
		this.addButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getDetector().setChanged(true);
				SphereForwardingSubComposite.this.additionalValue = SphereForwardingSubComposite.this.addButton.getSelection();
			}
		});
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		this.addButton.setLayoutData(data);

		return comp;
	}

	private void setMode(SphereForwardingModes mode) {
		if (mode == SphereForwardingModes.CONTACTS){
			this.mode1.setSelection(true);
			this.mode2.setSelection(false);
			this.mode3.setSelection(false);
			this.mode4.setSelection(false);
			enableAdditional(true, true, true);
		} else if (mode == SphereForwardingModes.MEMBERS){
			this.mode1.setSelection(false);
			this.mode2.setSelection(true);
			this.mode3.setSelection(false);
			this.mode4.setSelection(false);
			enableAdditional(true, true, true);
		} else if (mode == SphereForwardingModes.ADDITIONAL){
			this.mode1.setSelection(false);
			this.mode2.setSelection(false);
			this.mode3.setSelection(true);
			this.mode4.setSelection(false);
			enableAdditional(false, true, false);
		} else {
			this.mode1.setSelection(false);
			this.mode2.setSelection(false);
			this.mode3.setSelection(false);
			this.mode4.setSelection(true);
			enableAdditional(false, false, true);
		}
	}
	
	private void enableAdditional(boolean enableButton, boolean enableText, boolean setValue) {
		this.text.setEnabled(enableText);
		this.addButton.setEnabled(enableButton);
		if (setValue){
			this.addButton.setSelection(this.additionalValue);
		} else {
			this.addButton.setSelection(true);
		}
	}

	private SphereForwardingModes getMode(){
		if (this.mode1.getSelection()){
			return SphereForwardingModes.CONTACTS;
		}
		if (this.mode2.getSelection()){
			return SphereForwardingModes.MEMBERS;
		}
		if (this.mode3.getSelection()){
			return SphereForwardingModes.ADDITIONAL;
		}
		return SphereForwardingModes.OFF;
	}
	
	private void setAdditionalEmailAddress() {
		this.text.setText(this.controller.getSphereEmailAdress(this.sphereId));
	}
	
	private String getAdditionalEmailAddress(){
		return this.text.getText();
	}
	
	private boolean isAddAdditional(){
		return this.addButton.getSelection();
	}
	
	/*private void sphereSelected(ManagedSphere selectedSphere) {
//		if (selectedSphere.isRoot()){
//			enableControls(false);
//			this.text.setText("");
//			this.addButton.setSelection(false);
//			setMode(SphereForwardingModes.OFF);
//			return;
//		}
		if (selectedSphere == null) {
			throw new NullPointerException("sphereId is null");
		}
		this.sphereId = selectedSphere.getId();
		this.additionalValue = this.controller.getAddAdditional(this.sphereId);
		enableControls(true);
		setAdditionalEmailAddress();
		setMode(this.controller.getSphereMode(this.sphereId));
	}*/
	
	private void enableControls(boolean enabled){
		if (this.applyButton != null){
			this.applyButton.setEnabled(enabled);
		}
		if ((this.mode1 != null) && (this.mode2 != null) && (this.mode3 != null) && (this.mode4 != null)){
			this.mode1.setEnabled(enabled);
			this.mode2.setEnabled(enabled);
			this.mode3.setEnabled(enabled);
			this.mode4.setEnabled(enabled);
		}
		if ((this.text != null)&&(!enabled)){
			this.text.setEnabled(enabled);
		}
		if ((this.addButton != null)&&(!enabled)){
			this.addButton.setEnabled(enabled);
		}
	}
	
	public void applyPerformed(){
		String emails = getAdditionalEmailAddress();
		SphereForwardingModes mode = getMode();
		boolean additional = isAddAdditional();
		this.controller.applySphereForwarding(emails, mode, this.sphereId, additional);
		getDetector().setChanged(false);
	}

	public IChangesDetector getDetector() {
		return this.detector;
	}

	public void jumpToNextItem() {
		this.commonShell.jumpToNextItem();
	}

	public void performFinalAction() {
		ManagedSphere selectedSphere = this.tree.getSelected();
		if (selectedSphere == null) {
			throw new NullPointerException("sphereId is null");
		}
		this.sphereId = selectedSphere.getId();
		this.additionalValue = this.controller.getAddAdditional(this.sphereId);
		enableControls(true);
		setAdditionalEmailAddress();
		setMode(this.controller.getSphereMode(this.sphereId));
		
		setViewSphere(selectedSphere);
		getDetector().setChanged(false);
	}

	public void revertSelection() {
		this.tree.selectSphere(getViewSphere());
	}
	
	public ManagedSphere getViewSphere() {
		return this.viewSphere;
	}
	
	protected void setViewSphere(ManagedSphere newSelection) {
		this.viewSphere = newSelection;
	}
	
	private ManagedSphere getNewSelection() {
		return this.newSelection;
	}
	
	protected void setNewSelection(ManagedSphere sphere) {
		this.newSelection = sphere;
	}
}
