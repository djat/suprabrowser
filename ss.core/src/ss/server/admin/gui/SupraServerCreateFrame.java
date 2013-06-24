/**
 * 
 */
package ss.server.admin.gui;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.ListUtils;
import ss.common.StringUtils;
import ss.server.admin.SupraServerCreator;

/**
 * @author zobo
 *
 */
public class SupraServerCreateFrame extends ApplicationWindow {

	private static final String FRAME_TITLE = "Create Database";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraServerCreateFrame.class);
	
	private Text databaseName;
	private Text contactName;
	private Text loginName;
	private Text supraSphereName;
	private Text mainDomain;
	private Text password;
	private Text port;
	private Text databaseUserName;
	private Text databasePassword;

	/**
	 * @param arg0
	 */
	public SupraServerCreateFrame() {
		super(null);	
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setSize(320, 610);
		shell.setText(FRAME_TITLE);
		shell.layout();
		centerComponent(shell);
		
	}
	
	private void centerComponent(Composite comp) {
		Monitor primary = Display.getDefault().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = comp.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		comp.setLocation(x, y);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1,false));
		GridData data;
		Composite c = createTextFieldsComposite(composite);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		c.setLayoutData(data);
		c = createButtonsComposite(composite);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		c.setLayoutData(data);
		return composite;
	}

	/**
	 * @param composite
	 */
	private Composite createTextFieldsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1,false));
		this.contactName = createElement(composite, "Contact Name", "", getNewData());
		this.loginName = createElement(composite, "Login", "", getNewData());
		this.password = createElement(composite, "Password", "", getNewData() );
		this.supraSphereName = createElement(composite, "SupraSphere name", "", getNewData());
		this.mainDomain = createElement(composite, "SupraSphere main domain", "", getNewData());
		this.port = createElement(composite, "Server port", "3000", getNewData());
		this.databaseName = createElement(composite, "Database name", "", getNewData());
		this.databaseUserName = createElement(composite, "Database user name", "root", getNewData());
		this.databasePassword = createElement(composite, "Database password", "", getNewData());
		setDefaultValues();
		return composite;
	}
	
	private void setDefaultValues() {
		
		this.supraSphereName.setText("SupraDevelopment");
		this.mainDomain.setText("127.0.0.1");
		this.databaseName.setText("SupraDevelopment");
		
	}
	
	private GridData getNewData(){
		GridData data;

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;

		return data;
	}
	
	private Text createElement( final Composite parent, final String title, String initalValue, GridData compData ){
		return createElement( parent, title, initalValue, compData, SWT.BORDER );
	}
	
	private Text createElement( final Composite parent, final String title, String initalValue, GridData compData, int style ){
		Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout gridLayout = new GridLayout(1,false);
		gridLayout.marginTop = 5;
		gridLayout.marginBottom = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		composite.setLayoutData( compData );
		Label label; 
		GridData data;
		label = new Label(composite, SWT.LEFT);
		label.setText( title );
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.BEGINNING;
		data.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(data);
		Text text = new Text(composite, style);
		text.setText(initalValue);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.BEGINNING;
		data.horizontalAlignment = GridData.FILL;
		text.setLayoutData(data);
		return text;
	}

	/**
	 * @param composite
	 */
	private Composite createButtonsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2,false));
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.BEGINNING;
		data.horizontalAlignment = GridData.BEGINNING;
		Button ok = new Button(composite, SWT.PUSH);
		ok.setText("Create");
		ok.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				buttonCreateClicked();
			}
			
		});
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.BEGINNING;
		data.horizontalAlignment = GridData.BEGINNING;
		Button cancel = new Button(composite, SWT.PUSH);
		cancel.setText("Close");
		cancel.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				close();
			}
			
		});
		return composite;
	}

	private void buttonCreateClicked(){
		if (!check()){
			return;
		}
		SupraServerCreateStartGUIParameters params = new SupraServerCreateStartGUIParameters(
				this.contactName.getText(),
				this.loginName.getText(), this.supraSphereName.getText(),
				this.mainDomain.getText(), Integer.parseInt(this.port.getText()), 
				this.password.getText(),
				this.databaseName.getText(), this.databaseUserName.getText(), this.databasePassword.getText()
				);
		try {
			new SupraServerCreator().createDatabase( null, true, params );
			UserMessageDialogCreator.info("Database created successfully");
			close();
		} catch (Throwable ex) {
			logger.fatal("SupraServerCreator failed. Args "
					+ ListUtils.allValuesToString(params.getArgs()), ex);
			UserMessageDialogCreator.error("Database creation failed");
			close();
		}
	}

	/**
	 * @return
	 */
	private boolean check() {
		if (!checkOne(this.contactName, "Contact name is empty")){
			return false;
		}
		if (!checkOne(this.loginName, "Login name is empty")){
			return false;
		}
		if (!checkOne(this.supraSphereName, "supraSphereName is empty")){
			return false;
		}
		if (!checkOne(this.databaseName, "databaseName is empty")){
			return false;
		}
		if (!checkOne(this.mainDomain, "mainDomain is empty")){
			return false;
		}
		if (!checkOne(this.password, "First password field is empty")){
			return false;
		}
		return true;
	}
	
	private boolean checkOne(Text text, String message) {
		if (text == null) {
			UserMessageDialogCreator.error("No GUI element");
			return false;
		}
		if (StringUtils.isBlank(text.getText())){
			UserMessageDialogCreator.warning(message);
			return false;
		}
		return true;
	}
}
