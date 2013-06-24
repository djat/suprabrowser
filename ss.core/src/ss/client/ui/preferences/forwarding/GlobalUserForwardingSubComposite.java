/**
 * 
 */
package ss.client.ui.preferences.forwarding;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
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
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.domainmodel.preferences.emailforwarding.CommonEmailForwardingPreferences.ForwardingModes;

/**
 * @author zobo
 *
 */
public class GlobalUserForwardingSubComposite extends Composite {

	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_FORWARDING_GLOBALUSERFORWARDINGSUBCOMPOSITE);

	private static final String OVERRIDE_ALL_SPHERES = "GLOBALUSERFORWARDINGSUBCOMPOSITE.OVERRIDE_ALL_SPHERES";

	private static final String EMAIL_ADDRESS_TO_FORWARD = "GLOBALUSERFORWARDINGSUBCOMPOSITE.EMAIL_ADDRESS_TO_FORWARD";

	private static final String OFF = "GLOBALUSERFORWARDINGSUBCOMPOSITE.OFF";

	private static final String AUTOMATIC = "GLOBALUSERFORWARDINGSUBCOMPOSITE.AUTOMATIC";

	private static final String FORCED = "GLOBALUSERFORWARDINGSUBCOMPOSITE.FORCED";

	private static final String MODE_OF_EMAIL_FORWARDING = "GLOBALUSERFORWARDINGSUBCOMPOSITE.MODE_OF_EMAIL_FORWARDING";

	private static final String APPLY = "GLOBALUSERFORWARDINGSUBCOMPOSITE.APPLY";

	private static final String GLOBAL_FORWARDING_RULE = "GLOBALUSERFORWARDINGSUBCOMPOSITE.GLOBAL_FORWARDING_RULE";
	
	private ForwardingController controller;

	private Button mode1;

	private Button mode2;

	private Button mode3;

	private Text text;

	/**
	 * @param parent
	 * @param style
	 */
	public GlobalUserForwardingSubComposite(Composite parent, int style, ForwardingController controller) {
		super(parent, style);
		this.controller = controller;
		createGUI(this);
	}

	/**
	 * @param parent
	 */
	private void createGUI(Composite parent) {
		parent.setLayout(new GridLayout(2,false));		
		GridData data;
		
		Label label = new Label(parent, SWT.LEFT);
		label.setText(bundle.getString(GLOBAL_FORWARDING_RULE));
		data = new GridData(SWT.LEFT, SWT.TOP, false, false,2,1);
		label.setLayoutData(data);
		
		Composite email = createEmailComposite(parent);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		email.setLayoutData(data);
		
		Composite modes = createModesComposite(parent);
		data = new GridData(SWT.FILL, SWT.FILL, false, false);
		modes.setLayoutData(data);
		
		Composite buttonPanel = new Composite(parent, SWT.NONE);
		buttonPanel.setLayout(new GridLayout(1,false));
		Button button;
		Button buttonApply;
		
		buttonApply = new Button(buttonPanel, SWT.PUSH);
		buttonApply.setText(bundle.getString(APPLY));
		buttonApply.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				applyPreformed();
			}
		});
		data = new GridData(SWT.RIGHT, SWT.TOP, true, false);
		buttonApply.setLayoutData(data);
		
		button = new Button(buttonPanel, SWT.PUSH);
		button.setText(bundle.getString(OVERRIDE_ALL_SPHERES));
		button.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				overridePreformed();
			}
		});
		data = new GridData(SWT.RIGHT, SWT.TOP, false, false);
		button.setLayoutData(data);
		
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		buttonPanel.setLayoutData(data);
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
		setMode();
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
		setEmailAddress();
		return comp;
	}

	private void setEmailAddress() {
		this.text.setText(this.controller.getGlobalEmailAdress());
	}

	private void applyPreformed(){
		ForwardingModes mode = getMode();
		String email = this.text.getText();
		this.controller.applyGlobalForwarding(email, mode);
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
	
	private void setMode(){
		ForwardingModes mode = this.controller.getGlobalMode();
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
	
	private void overridePreformed(){
		ForwardingModes mode = getMode();
		String email = this.text.getText();
		this.controller.overrideForwarding(email, mode);
	}
}
