/**
 * 
 */
package ss.client.ui.messagedeliver.popup;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ss.client.localization.LocalizationLinks;

/**
 * @author roman
 *
 */
public class SOptionPaneChoicePane extends Composite {
	
	private Button yes_button;
	private Button no_button;
	private Button dont_know_button;
	private GridData gridData;
	
	private final static ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_SOPTIONPANECHOICEPANE);
	
	public static final String KEY_YES = "SOPTIONPANECHOICEPANE.YES";
	public static final String KEY_NO = "SOPTIONPANECHOICEPANE.NO";
	public static final String KEY_UNSURE = "SOPTIONPANECHOICEPANE.UNSURE";
	
	public static final String YES = bundle.getString(KEY_YES);
	public static final String NO = bundle.getString(KEY_NO);
	public static final String UNSURE = bundle.getString(KEY_UNSURE);
	
	public SOptionPaneChoicePane(Composite parent){
		super(parent, SWT.NONE);

		this.setLayout(new GridLayout(1, true));

		this.yes_button = new Button(this, SWT.RADIO);
		this.yes_button.setText(YES);

		this.no_button = new Button(this, SWT.RADIO);
		this.no_button.setText(NO);

		this.dont_know_button = new Button(this, SWT.RADIO);
		this.dont_know_button.setText(UNSURE);

		this.gridData = new GridData();

		this.gridData.verticalAlignment = GridData.FILL;
		this.gridData.horizontalAlignment = GridData.FILL;

		this.gridData.horizontalSpan = 2;
		this.gridData.grabExcessHorizontalSpace = true;
		this.gridData.grabExcessVerticalSpace = false;
		this.setLayoutData(this.gridData);

		this.yes_button.setSelection(true);
	}
	
	public String getChoice() {
		if(this.yes_button.getSelection()) {
			return YES;
		}	
		if(this.no_button.getSelection()) {
			return NO;
		}
		return UNSURE;
	}
}
