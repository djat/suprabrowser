/**
 * 
 */
package ss.client.ui.preferences;

import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.preferences.changesdetector.IChangesDetector;

/**
 * @author zobo
 *
 */
public class PreferencesManageComboShellUnit extends PreferencesComboShellUnit {

	private static final String ALLOW_MODIFY_STRING = "PREFERENCEMANAGERCOMBOSHELLUNIT.ALLOW_MODIFY_STRING";
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_PREFERENCEMANAGERCOMBOSHELLUNIT);

	
	private IChangesDetector detector;
	
	private Button allowOverride;
	/**
	 * @param parent
	 * @param style
	 * @param labelString
	 * @param data
	 * @param allowModify
	 */
	public PreferencesManageComboShellUnit(Composite parent, int style, String labelString, List<String> data, boolean allowModify, IChangesDetector detector) {
		super(parent, style, labelString, data, allowModify);
		this.detector = detector;
	}


	@Override
	protected void createContent(Composite parent, String labelString,
			List<String> stringData, boolean allowModify) {
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
        this.combo = new Combo(parent, SWT.SINGLE);
        this.combo.setLayoutData(data);
        fillCombo(stringData);
        this.combo.setEnabled(true);
        this.combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				PreferencesManageComboShellUnit.this.detector.setChanged(true);
			}
        });
        
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.CENTER;
        data.widthHint = LABEL_WIDTH;
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
        		PreferencesManageComboShellUnit.this.detector.setChanged(true);
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

	@Override
	public boolean getPermittion(){
		return this.allowOverride.getSelection();
	}
}
