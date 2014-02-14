/*
 * Created on Nov 6, 2006
 *
 */
package ss.client.ui;

/**
 * This is experimental class. In future would be deleted.
 * @author dankosedin
 */

import java.awt.Dimension;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.models.autocomplete.DataModel;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.models.autocomplete.ResultListener;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.global.SSLogger;

public class AutoCompleteDialog extends BaseDialog {

    private DataModel model;

    private Text textField;

    private TypeAheadComponent typeAhead = null;
    
    private static final String START_UP_TITLE = "BASEDIALOG.TYPE_AHEAD";
    
    private static final Logger logger = SSLogger.getLogger(AutoCompleteDialog.class);
    
    private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_BASEDIALOG);

    public AutoCompleteDialog(DataModel model) {
	this.model = model;
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void initializeControls() {
	super.initializeControls();

	this.textField = new Text(getShell(), SWT.LEFT | SWT.SINGLE);
	this.textField.setSize(250, 100);

	this.typeAhead = new TypeAheadComponent(this.textField,
		this.model, getResultListener());
	this.textField.setLayoutData(getTextFieldGridData());

	GridLayout gridLayout = new GridLayout();
	gridLayout.numColumns = 1;
	getShell().setLayout(gridLayout);
	this.model.setFilter("");
	this.textField.setVisible(true);
    }

    /**
         * @return
         */
    private ResultListener getResultListener() {
	return new ResultAdapter() {

	    @SuppressWarnings("unused")
		public void processResult(String string) {
        logger.info("PROCESSING RESULT HERE");
		AutoCompleteDialog.this.textField.setText("");
		//removeAndAddTypeAhead();
	    }
	};
    }

    @SuppressWarnings("unchecked")
	protected void removeAndAddTypeAhead() {
    	if ( this.typeAhead != null ) {
    		this.typeAhead.dispose();
    	}
    	this.typeAhead = new TypeAheadComponent(this.textField,
    			this.model, getResultListener());

    }

    /**
         * Start dialog thread
         * 
         * @param parentShell parent shell
         */
    public void show(final Shell parentShell) {
	super.show(parentShell);
    }

    @Override
    protected Dimension getStartUpDialogSize() {
	return new Dimension(250, 50);
    }

    @Override
    protected String getStartUpTitle() {
	return this.bundle.getString(START_UP_TITLE);
    }

    private GridData getTextFieldGridData() {
	GridData gridData = new GridData();

	gridData.horizontalAlignment = GridData.FILL;
	gridData.verticalAlignment = GridData.FILL;

	gridData.grabExcessHorizontalSpace = true;
	// gridData.grabExcessVerticalSpace = true;
	return gridData;
    }

}