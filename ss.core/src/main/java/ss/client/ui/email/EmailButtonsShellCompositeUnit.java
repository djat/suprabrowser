/**
 * 
 */
package ss.client.ui.email;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ss.client.localization.LocalizationLinks;

/**
 * @author zobo
 *
 */
public class EmailButtonsShellCompositeUnit extends Composite {
    private static final String OK_BUTTON_STRING = "EMAILBUTTONSSHELLCOMPOSITEUNIT.SEND";
    
    private static final String CANCEL_BUTTON_STRING = "EMAILBUTTONSSHELLCOMPOSITEUNIT.CANCEL";
    
    private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_EMAIL_EMAILBUTTONSSHELLCOMPOSITEUNIT);
    
    private Button buttonOK;
    
    private Button buttonCancel;

    public EmailButtonsShellCompositeUnit(Composite parent, int style, final EmailShellButtonsActions actionsResiever) {
        super(parent, style);
        init(actionsResiever);
    }
    
    private void init(final EmailShellButtonsActions actionsResiever) {
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 3;
        layout.verticalSpacing = 3;
        layout.numColumns = 2;
        setLayout(layout);
        
        GridData data;
        
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.BEGINNING;
        this.buttonOK = new Button(this, SWT.PUSH);
        this.buttonOK.setText(this.bundle.getString(OK_BUTTON_STRING));
        this.buttonOK.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(SelectionEvent arg0) {        
            }

            public void widgetSelected(SelectionEvent arg0) {
                actionsResiever.buttonOKPerformed();
            }
            
        });
        
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.BEGINNING;
        this.buttonCancel = new Button(this, SWT.PUSH);
        this.buttonCancel.setText(this.bundle.getString(CANCEL_BUTTON_STRING));
        this.buttonCancel.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(SelectionEvent arg0) {        
            }

            public void widgetSelected(SelectionEvent arg0) {
                actionsResiever.buttonCancelPerformed();
            }
            
        });
        
        layout();
    }

    /**
     * turn buttons enabled or disabled
     * @param lock true makes buttons disabled, false makes enabled
     */
    public void lockButtons(boolean lock) {
        this.buttonOK.setEnabled(!lock);
        this.buttonCancel.setEnabled(!lock);
    }
}
