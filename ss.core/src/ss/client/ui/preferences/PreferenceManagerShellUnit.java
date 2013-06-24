/**
 * 
 */
package ss.client.ui.preferences;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.preferences.changesdetector.IChangesDetector;

/**
 * @author zobo
 *
 */
public class PreferenceManagerShellUnit extends PreferenceAbstractShellUnit {

	private static final String ALLOW_MODIFY_STRING = "PREFERENCEMANAGERSHELLUNIT.ALLOW_MODIFY_STRING";
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_PREFERENCEMANAGERSHELLUNIT);

	private Button allowOverride;
	
	private IChangesDetector detector;
	/**
	 * @param parent
	 * @param style
	 * @param labelString
	 * @param selection
	 * @param allowModify
	 */
	public PreferenceManagerShellUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify, IChangesDetector detector) {
		super(parent, style, labelString, selection, allowModify);
		this.detector = detector;
	}

	@Override
	protected void createContent(Composite parent, String labelString,
			boolean selection, boolean allowModify) {
		GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
    	layout.marginHeight = 1;
    	layout.marginTop = 1;
    	layout.marginBottom = 1;
        layout.numColumns = 4;
        parent.setLayout(layout);

        GridData data;

        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.BEGINNING;
        this.checkButton = new Button(parent, SWT.CHECK);
        this.checkButton.setLayoutData(data);
        setValue(selection);
        this.checkButton.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent se) {
        		PreferenceManagerShellUnit.this.detector.setChanged(true);
        	}
        });
        
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.CENTER;
        this.label = new Label(parent, SWT.LEFT);
        this.label.setText(labelString);
        this.label.setLayoutData(data);
        
        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.FILL;
        this.allowOverride = new Button(parent, SWT.CHECK);
        this.allowOverride.setLayoutData(data);
        setPermittion(allowModify);
        this.allowOverride.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent se) {
        		PreferenceManagerShellUnit.this.detector.setChanged(true);
        	}
        });
        
        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.CENTER;
        Label overrideLabel = new Label(parent, SWT.LEFT);
        overrideLabel.setText(bundle.getString(ALLOW_MODIFY_STRING));
        overrideLabel.setLayoutData(data);
	}
	
	public void setPermittion(boolean allow){
		this.allowOverride.setSelection(allow);
	}

	public boolean getPermittion(){
		return this.allowOverride.getSelection();
	}
	
	public void setAllValues(boolean checked, boolean enabled){
		setValue(checked);
		setPermittion(enabled);
	}
}
