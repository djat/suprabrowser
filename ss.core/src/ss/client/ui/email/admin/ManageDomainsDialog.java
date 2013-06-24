/**
 * 
 */
package ss.client.ui.email.admin;

import java.awt.Dimension;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.BaseDialog;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.DomainProvider;
import ss.domainmodel.configuration.EmailDomain;
import ss.util.ImagesPaths;

/**
 * @author zobo
 *
 */
public class ManageDomainsDialog extends BaseDialog {
	private static final String EXISTED_DOMAINS = "Existed domains:";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AddEmailAliasDialog.class);

	private Button ok;

	private Button cancel;

	//private EmailAliasesComposite parentDialog;

//	private final ResourceBundle bundle = ResourceBundle
//			.getBundle(LocalizationLinks.CLIENT_UI_EMAIL_ADMIN_ADDEMAILALIASDIALOG);

	private List domains;

	private Text newDomainText;

	public ManageDomainsDialog() {

	}

	@Override
	protected void initializeControls() {
		super.initializeControls();

		initIcons();

		GridLayout gridLayout = new GridLayout(2, true);
		GridData data;
		getShell().setLayout(gridLayout);

		final Composite existed = new Composite(getShell(), SWT.NONE);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		existed.setLayoutData(data);
		existed.setLayout(new GridLayout(1, false));
		
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		Label label;
		label = new Label(existed, SWT.LEFT);
		label.setText(EXISTED_DOMAINS);
		label.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		this.domains = new List(existed, SWT.MULTI);
		this.domains.setItems(DomainProvider.getDomains());
		this.domains.setLayoutData(data);
		
		final Composite buttons = new Composite(getShell(), SWT.NONE);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		buttons.setLayoutData(data);
		buttons.setLayout(new GridLayout(1, false));
		
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		label = new Label(buttons, SWT.LEFT);
		label.setText("New domain");
		label.setLayoutData(data);
		
		
		final SelectionListener addListener = new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				String text = ManageDomainsDialog.this.newDomainText.getText();
				if (StringUtils.isBlank(text)){
					UserMessageDialogCreator.error("Blank domain is not allowed", "Domains error");
					return;
				}
				String[] existedDomains = ManageDomainsDialog.this.domains.getItems();
				for (String s : existedDomains) {
					if (s.equals(text)){
						UserMessageDialogCreator.error("Such domain already specified", "Domains error");
						return;
					}
				}
				ManageDomainsDialog.this.domains.add(text);
				ManageDomainsDialog.this.newDomainText.setText("");
			}
			
		};
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		this.newDomainText = new Text(buttons, SWT.BORDER);
		this.newDomainText.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == '\r'){
					addListener.widgetSelected(null);
				}
			}

			public void keyReleased(KeyEvent e) {
			}
			
		});
		this.newDomainText.setText("");
		this.newDomainText.setLayoutData(data);
		
		Button addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("Add");
		addButton.addSelectionListener(addListener);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		addButton.setLayoutData(data);
		
		Button removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				String[] toDelete = ManageDomainsDialog.this.domains.getSelection();
				String[] items = ManageDomainsDialog.this.domains.getItems();
				if (toDelete.length <= 0){
					return;
				}
				if (items.length <= toDelete.length) {
					UserMessageDialogCreator.error("Can not delete all domains", "Domains error");
					return;
				}
				for (String s : toDelete){
					ManageDomainsDialog.this.domains.remove(s);
				}
			}
			
		});
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		removeButton.setLayoutData(data);
		
		Button setPrimaryButton = new Button(buttons, SWT.PUSH);
		setPrimaryButton.setText("Set primary");
		setPrimaryButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				int[] selection = ManageDomainsDialog.this.domains.getSelectionIndices();
				if ((selection == null)||(selection.length <= 0)||(selection.length > 1)){
					UserMessageDialogCreator.error("Select one domain to make it primary", "Domains error");
					return;
				}
				String main = ManageDomainsDialog.this.domains.getItem(selection[0]);
				String[] items = ManageDomainsDialog.this.domains.getItems();
				for (int i = 0; i < items.length; i++){
					if (items[i].equals(main)){
						items[i] = items[0];
						items[0] = main;
						break;
					}
				}
				ManageDomainsDialog.this.domains.removeAll();
				ManageDomainsDialog.this.domains.setItems(items);
			}
			
		});
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		setPrimaryButton.setLayoutData(data);

		Composite toolBar = new Composite(getShell(), SWT.NONE);
		toolBar.setLayout(new GridLayout(2, false));

		this.ok = new Button(toolBar, SWT.PUSH);
		this.ok.setText("Apply");
		addOkActionListener();

		this.cancel = new Button(toolBar, SWT.PUSH);
		this.cancel.setText("Cancel");
		addCancelActionListener();

		data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		toolBar.setLayoutData(data);
	}

	@Override
	protected Dimension getStartUpDialogSize() {
		return new Dimension(500, 300);
	}

	@Override
	protected String getStartUpTitle() {
		return "Manage Domains";
	}

	private void addCancelActionListener() {

		this.cancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				ManageDomainsDialog.this.close();
			}
		});
	}

	private void addOkActionListener() {

		this.ok.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				okPerformed();
			}
		});
	}

	protected void okPerformed() {
		String[] domainsMassive = this.domains.getItems();
		if (domainsMassive.length <= 0){
			UserMessageDialogCreator.error("No domains is not allowed", "Domains error");
			return;
		}
		final ConfigurationValue configuration = new ConfigurationValue();
		for (String domain : domainsMassive){
			EmailDomain domainElem = new EmailDomain();
			domainElem.setDomain(domain);
			configuration.getDomains().put(domainElem );
		}
		SsDomain.CONFIGURATION.setMainConfigurationValue(configuration);
		close();
	}

	private void initIcons() {
		try {
			Image image = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.EMAIL_IN_ICON).openStream());
			getShell().setImage(image);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void show(final Shell parentShell) {
		super.show(parentShell);
	}
}
