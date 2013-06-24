/**
 * 
 */
package ss.client.ui.email;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import ss.global.SSLogger;

/**
 * @author zobo
 * 
 */
public class EmailAttachmentShellComposite extends Composite {
    
    @SuppressWarnings("unused")
    private static final Logger logger = SSLogger
            .getLogger(EmailCommonShell.class);

    private FileDialog fileDialog = null;

    EmailAttachmentShellComposite(Composite parent, int style) {
        super(parent, style);
        createContent(this);
    }

    private void createContent(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 3;
        layout.verticalSpacing = 3;
        layout.numColumns = 2;
        parent.setLayout(layout);

        GridData data;

        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.FILL;
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Attach File");
        button.setLayoutData(data);
        button.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                loadDialog();
            }
        });

        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
    }

    private void loadDialog() {
        if (this.fileDialog == null) {
            this.fileDialog = new FileDialog(getShell(), SWT.OPEN);
        }
        String file = this.fileDialog.open();
        if (file == null){
            logger.info("File Dialog Canceled or error occured");
        } else {
            
        }
    }
}
