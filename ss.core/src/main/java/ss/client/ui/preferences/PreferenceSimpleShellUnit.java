/**
 * 
 */
package ss.client.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ss.client.ui.preferences.changesdetector.IChangesDetector;

/**
 * @author zobo
 *
 */
public class PreferenceSimpleShellUnit extends PreferenceAbstractShellUnit {

	private Label label;
	
    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger logger = ss.global.SSLogger
            .getLogger(PreferenceSimpleShellUnit.class);
    
    private IChangesDetector detector;
	
	public PreferenceSimpleShellUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify, IChangesDetector detector) {
		super(parent, style, labelString, selection, allowModify);
		this.detector = detector;
	}
	
	public PreferenceSimpleShellUnit(Composite parent, int style,
			String labelString, boolean selection, boolean allowModify) {
		this(parent, style, labelString, selection, allowModify, null);
	}
	
	@Override
	protected void createContent(Composite parent, String labelString,
			boolean selection, boolean allowModify){        
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
        this.checkButton = new Button(parent, SWT.CHECK);
        this.checkButton.setLayoutData(data);
        setValue(selection);
        this.checkButton.setEnabled(allowModify);
        this.checkButton.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent se) {
        		if(PreferenceSimpleShellUnit.this.detector==null) {
        			return;
        		}
        		PreferenceSimpleShellUnit.this.detector.setChanged(true);
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

	}
	
	public void setAllValues(boolean checked, boolean enabled){
		setValue(checked);
		this.checkButton.setEnabled(enabled);
	}
}
