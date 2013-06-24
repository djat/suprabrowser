/**
 * 
 */
package ss.client.ui.preferences;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author zobo
 *
 */
public class PreferencesSimpleUserComboShellUnit extends
		PreferencesComboShellUnit {

	/**
	 * @param parent
	 * @param style
	 * @param labelString
	 * @param data
	 * @param allowModify
	 */
	public PreferencesSimpleUserComboShellUnit(Composite parent, int style, String labelString, List<String> data, boolean allowModify) {
		super(parent, style, labelString, data, allowModify);
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
        layout.numColumns = 2;
        parent.setLayout(layout);

        GridData data;

        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.BEGINNING;
        data.widthHint=150; 
        this.combo = new Combo(parent, SWT.SINGLE | SWT.READ_ONLY);
        this.combo.setLayoutData(data);
        fillCombo(stringData);
        this.combo.setEnabled(allowModify);
        
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.CENTER;
        data.horizontalIndent=10;
        data.widthHint = LABEL_WIDTH;
        this.label = new Label(parent, SWT.LEFT);
        this.label.setText(labelString);
        this.label.setLayoutData(data);
	}

}
