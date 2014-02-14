/**
 * 
 */
package ss.client.ui.email;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.models.autocomplete.ResultListener;

/**
 * @author zobo
 * 
 */
public class EmailSimpleShellCompositeUnit extends
        EmailAbstractShellCompositeUnit {

    public EmailSimpleShellCompositeUnit(Composite parent, int style,
            final String labelText, final String defaultText, boolean enabled) {
        super(parent, style);
        this.label.setText(labelText);
        this.text.setText(defaultText);
        if (!enabled){
        	this.text.setEditable(false);
        }
        layout();
    }
    
    @Override
    protected void createContent(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
    	layout.marginHeight = 1;
    	layout.marginTop = 1;
    	layout.marginBottom = 1;
        layout.numColumns = 2;
        setLayout(layout);
        
        GridData data;
        
        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.FILL;
        data.widthHint = LABEL_WIDTH;
        this.label = new Label(this,SWT.LEFT);
        this.label.setLayoutData(data);
        
  
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
        this.text = new Text(this, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        this.text.setLayoutData(data);
    }

    @Override
    protected ResultListener<String> getResultListener() {
        return null;
    }
}
