/**
 * 
 */
package ss.client.ui.email;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.models.autocomplete.ResultListener;
import ss.common.StringUtils;
import ss.global.SSLogger;
import ss.util.StringProcessor;

/**
 * @author zobo
 * 
 */
public class EmailCheckedShellCompositeUnit extends
        EmailAbstractShellCompositeUnit {

    @SuppressWarnings("unused")
    private static final Logger logger = SSLogger
            .getLogger(EmailCheckedShellCompositeUnit.class);

    private Button check;

    private String routingNumber;

	private EmailAbstractShellCompositeUnit conragentUnit = null;

    public EmailCheckedShellCompositeUnit(Composite parent, int style,
            String labelText, String checkButtonText, String defaultText, boolean enabled) {
        super(parent, style);
        this.label.setText(labelText);
        this.check.setText(checkButtonText);
        this.text.setText(defaultText);
        if (!enabled){
        	this.check.setSelection(true);
        	this.check.setEnabled(false);
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
        parent.setLayout(layout);

        GridData data;

        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.BEGINNING;
        data.widthHint = LABEL_WIDTH;
        this.label = new Label(parent, SWT.LEFT);
        this.label.setLayoutData(data);
        
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        data.horizontalAlignment = SWT.FILL;
        data.verticalAlignment = SWT.FILL;
        this.text = new Text(parent, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        this.text.setLayoutData(data);

        data = new GridData();
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        data.horizontalAlignment = SWT.BEGINNING;
        data.verticalAlignment = SWT.BEGINNING;
        data.horizontalSpan = 2;
        this.check = new Button(parent, SWT.CHECK);
        this.check.setSelection(true);
        this.check.setLayoutData(data);
        this.check.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
            	if (!updateText()){
            		supplyTextWithRotingNumber();
            	}
            }
        });
    }

    public boolean isChecked() {
        return this.check.getSelection();
    }

    @Override
    protected ResultListener<String> getResultListener() {
        return new ResultAdapter<String>() {

            private EmailCheckedShellCompositeUnit unit = EmailCheckedShellCompositeUnit.this;

            /*@Override
            public void processListSelection(String selection, String realData) {
                logger.info("completting by " + realData);
                String str = this.unit.text.getText();
                String toSet = realData;
                if (!str.trim().equals("")) {
                    SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
                            str);
                    if (this.unit.check.getSelection()) {
                        String dataWithNumber = SpherePossibleEmailsSet
                                .supplySingleAddressWithRoutingNumber(realData,
                                        this.unit.routingNumber,
                                        this.unit.check.getSelection());
                        if (!set.getParsedEmailNames().contains(
                                SpherePossibleEmailsSet
                                        .parseSingleAddress(dataWithNumber))) {
                            set.addAddresses(dataWithNumber, false);
                        }
                    } else {
                        if (!set.getParsedEmailNames().contains(
                                SpherePossibleEmailsSet
                                        .parseSingleAddress(realData))) {
                            set.addAddresses(realData, false);
                        }
                    }
                    toSet = set.getSingleStringEmails();
                }
                logger.info(toSet);
                EmailCheckedShellCompositeUnit.this.text.setText(toSet);
            }*/
            
            @Override
            public void processListSelection(String selection, String realData) {
            	String str = realData;
                if (this.unit.check.getSelection()) {
                	str = SpherePossibleEmailsSet.supplyAddressesWithRoutingNumber(
                    		realData, EmailCheckedShellCompositeUnit.this.routingNumber, true);
                }
                EmailCheckedShellCompositeUnit.this.text.setText(str);
            }
        };
    }


    public void setCheckEnabled(boolean checked, boolean enabled) {
        this.check.setSelection(checked);
        this.check.setEnabled(enabled);
    }

    private void supplyTextWithRotingNumber() {
    	boolean selection = this.check.getSelection();
    	String str;
    	if (selection){
    		str = SpherePossibleEmailsSet.supplyAddressesWithRoutingNumber(
    				this.text.getText(), this.routingNumber, selection);
    	} else {
    		str = "";
    	}
        this.text.setText(str);
    }

    /**
     * @param routingNumber
     *            the routingNumber to set
     */
    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    @Override
    public void setText(String text) {
        if (this.routingNumber != null) {
            text = SpherePossibleEmailsSet.supplyAddressesWithRoutingNumber(
                    text, this.routingNumber, this.check.getSelection());
        }
        super.setText(text);
    }

	/**
	 * @param b
	 */
	public void setCheckSelected(boolean selected) {
		this.check.setSelection(selected);
		try {
			supplyTextWithRotingNumber();
		} catch (Exception e) {
		}
	}

	@Override
	protected Vector<String> processDataFiltered(List<String> data, String filter) {
		return new Vector<String>(data);
	}
	
	public void setContragentUnit(EmailAbstractShellCompositeUnit unit){
		this.conragentUnit  = unit;
	}
	
	private boolean updateText(){
		if (this.conragentUnit == null)
			return false;
		String remoteText = this.conragentUnit.getText();
		if (StringUtils.isBlank(remoteText))
			return false;
		if (StringUtils.isNotBlank(getText()))
			return false;
		setText(remoteText);
		return true;
	}
}
