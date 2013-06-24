/**
 * 
 */
package ss.client.ui.email.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.email.EmailController;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.domainmodel.SphereEmail;

/**
 * @author zobo
 * 
 */
public class EmailAliasesComposite extends Composite {

	/**
	 * 
	 */
	private static final String SUCH_ALIASES_ALREADY_IN_USE = "EMAILALIASESCOMPOSITE.SUCH_ALIASES_ALREADY_IN_USE";
	
	private static final String ALLOW_SPHERE_TO_BE_EMAIL_ADDRESSABLE = "EMAILALIASESCOMPOSITE.ALLOW_SPHERE_TO_BE_EMAIL_ADDRESSABLE";
	
	private static final String EMAIL_ADDRESSABLE = "EMAILALIASESCOMPOSITE.EMAIL_ADDRESSABLE";
	
	private static final String EMAIL_ALIASES_FOR_SPHERE = "EMAILALIASESCOMPOSITE.EMAIL_ALIASES_FOR_SPHERE";
	
	private static final String ADD = "EMAILALIASESCOMPOSITE.ADD";
	
	private static final String REMOVE = "EMAILALIASESCOMPOSITE.REMOVE";
	
	private static final String SET_PRIMARY = "EMAILALIASESCOMPOSITE.SET_PRIMARY";
	
	private static final String ONLY_ONE_ALIAS_COULD_BE_PRIMARY = "EMAILALIASESCOMPOSITE.ONLY_ONE_ALIAS_COULD_BE_PRIMARY";
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_EMAIL_ADMIN_EMAILALIASESCOMPOSITE);

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailAliasesComposite.class);

	private Button check = null;

	private Button ok = null;

	private SpherePossibleEmailsSet existingAliases = null;

	private SpherePossibleEmailsSet currentSphereEmails = null;

	private String sphere_id;

	private EmailController controller;

	private boolean enabled = true;

	private ListViewer listView;

	private Button removeButton;

	private Button setPrimaryButton;

	private Button attachButton;
	
	private IChangesDetector detector;

	public EmailAliasesComposite(Composite parent ,EmailController controller, IChangesDetector detector) {
		super(parent, SWT.NONE);
		this.controller = controller;
		this.detector = detector;
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.numColumns = 4;
		setLayout(gridLayout);
		createContent(this);
	}
	
	public void callSphere(ManagedSphere selectedSphere){
//		if (selectedSphere.isRoot()){
//			enableControls(false);
//			this.listView.setInput(null);
//			this.listView.refresh();
//			return;
//		}
		if (selectedSphere == null) {
			throw new NullPointerException("sphereId is null");
		}
		this.sphere_id = selectedSphere.getId();
		this.currentSphereEmails = this.controller
			.getSphereEmailsList(this.sphere_id);
		this.enabled = this.controller.isSphereEmailAddressable(this.sphere_id);
		
		this.existingAliases = this.controller.getAllAliasesOfAllSpheres();
		this.existingAliases.deleteAddresses(this.currentSphereEmails
				.getSingleStringEmails());
		this.listView.setInput(this.currentSphereEmails);
		enableControls(true);
		this.listView.refresh();
	}

	protected void createContent(Composite parent) {
		GridData data;
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.CENTER;
		data.verticalIndent=10;
		Label label;
		label = new Label(parent, SWT.LEFT);
		label.setText(this.bundle.getString(ALLOW_SPHERE_TO_BE_EMAIL_ADDRESSABLE));
		label.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.verticalIndent=10;
		data.horizontalSpan = 2;
		this.check = new Button(parent, SWT.CHECK);
		this.check.setSelection(this.enabled);
		this.check.setText(this.bundle.getString(EMAIL_ADDRESSABLE));
		this.check.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		data.verticalIndent = 5;
		label = new Label(parent, SWT.LEFT);
		label.setText(this.bundle.getString(EMAIL_ALIASES_FOR_SPHERE));
		label.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		data.verticalAlignment = SWT.FILL;
		data.horizontalSpan = 4;

		this.listView = new ListViewer(parent, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.HORIZONTAL);
		this.listView.getList().setLayoutData(data);
		this.listView.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((SpherePossibleEmailsSet) inputElement)
						.getParsedEmailAddresses().toArray();
			}

			public void dispose() {
				// do nothing
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// do nothing
			}
		});
		this.listView.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				return (String) element;
			}
		});

		this.listView
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						EmailAliasesComposite.this.removeButton
								.setEnabled(!event.getSelection().isEmpty());
						EmailAliasesComposite.this.setPrimaryButton
								.setEnabled(!event.getSelection().isEmpty());
					}
				});
		this.listView.getList().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					removeSelectedAlias();
				}
			}

		});

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = SWT.LEFT;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 4;

		Composite buttonsComposite = new Composite(parent, SWT.NONE);
		buttonsComposite.setLayoutData(data);
		createButtons(buttonsComposite);

		ToolBar toolBar = new ToolBar(parent, SWT.NONE);

		data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.horizontalSpan = 4;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		toolBar.setLayoutData(data);
		
		enableControls(false);

	}
	
	private void enableControls(boolean enable){
		if (this.ok != null){
			this.ok.setEnabled(enable);
		}
		this.check.setEnabled(enable);
		if (!enable){
			this.removeButton.setEnabled(enable);
			this.setPrimaryButton.setEnabled(enable);
		}
		this.attachButton.setEnabled(enable);
	}
	
	private void createButtons(Composite parent){
		parent.setLayout(new GridLayout(4,false));
		
		this.attachButton = new Button(parent, SWT.PUSH);
		this.attachButton.setText(this.bundle.getString(ADD));
		this.attachButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				(new AddEmailAliasDialog(EmailAliasesComposite.this))
						.show(EmailAliasesComposite.this.getShell());
			}
		});

		this.removeButton = new Button(parent, SWT.PUSH);
		this.removeButton.setText(this.bundle.getString(REMOVE));
		this.removeButton.setEnabled(false);
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSelectedAlias();
			}
		});

		this.setPrimaryButton = new Button(parent, SWT.PUSH);
		this.setPrimaryButton.setText(this.bundle.getString(SET_PRIMARY));
		this.setPrimaryButton.setEnabled(false);
		this.setPrimaryButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPrimarySelectedAlias();
			}
		});
		
		Button domainsButton = new Button(parent, SWT.PUSH);
		domainsButton.setText("Manage domains...");
		domainsButton.setEnabled(true);
		domainsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				(new ManageDomainsDialog()).show(getShell());
			}
		});
	}
	
	/**
	 * 
	 */
	protected void setPrimarySelectedAlias() {
		IStructuredSelection selection = (IStructuredSelection) this.listView
				.getSelection();
		if (selection.size() > 1) {
			UserMessageDialogCreator.error(this.bundle.getString(ONLY_ONE_ALIAS_COULD_BE_PRIMARY));
			return;
		}
		final String alias = (String) selection.getFirstElement();
		
		this.currentSphereEmails.setPrimaryAlias(alias);

		getDetector().setChanged(true);
		
		this.listView.refresh(false);
	}

	public void applyOkActionListener(Button button) {
		this.ok = button;
		this.ok.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				okPerformed();
			}
		});
	}

	private void okPerformed() {
		getDetector().collectChangesAndUpdate();
		getDetector().setChanged(false);
		//SupraSphereFrame.INSTANCE.client.saveNewSpheresEmails(sphereEmail);
		//this.controller.saveNewSphereEmails(sphereEmail);
	}
	
	public SphereEmail createSphereEmail() {
		SphereEmail sphereEmail = new SphereEmail();

		sphereEmail.setSphereId(this.sphere_id);
		sphereEmail.setEmailNames(this.currentSphereEmails);
		sphereEmail.setEnabled(this.check.getSelection());
		sphereEmail.setIsMessageIdAdd(true);
		
		return sphereEmail;
	}

	public boolean addAliases(final String aliases, final String description, final String domain) {
		if (StringUtils.isBlank(domain)){
			throw new NullPointerException("Domain is blank");
		}
		if (StringUtils.isBlank(aliases)){
			return false;
		}
		if (!SpherePossibleEmailsSet.isValidEmailAlias(aliases))
			return false;

		SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(aliases);
		List<String> addresses = set.getParsedEmailAddresses();
		List<String> existingAddresses = this.existingAliases
				.getParsedEmailNames();
		String existingError = "";
		List<String> toSave = new ArrayList<String>();
		for (String s : addresses) {
			String emailName = SpherePossibleEmailsSet
					.parseSingleAddress(s);
			String emailNameToCheck = emailName + "@" + domain;
			if (existingAddresses.contains(emailNameToCheck)) {
				existingError += " " + emailNameToCheck;
			}
			toSave.add(SpherePossibleEmailsSet.createAddressString(
					StringUtils.isBlank(description) ? SpherePossibleEmailsSet.getDescriptionFromAddress(s) : description,
					emailName, domain));
		}
		if (!existingError.equals("")) {
			UserMessageDialogCreator.error(this.bundle.getString(SUCH_ALIASES_ALREADY_IN_USE)
					+ existingError);
			return false;
		}

		this.currentSphereEmails.addAddresses(SpherePossibleEmailsSet
				.generateSingleStringEmailAddresses(toSave));

		this.listView.refresh(false);

		return true;
	}

	private void removeSelectedAlias() {
		IStructuredSelection selection = (IStructuredSelection) this.listView
				.getSelection();
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			final String alias = (String) iterator.next();
			this.currentSphereEmails.deleteAddresses(alias);
		}
		getDetector().setChanged(true);
		this.listView.refresh(false);
	}
	
	public IChangesDetector getDetector() {
		return this.detector;
	}
}
