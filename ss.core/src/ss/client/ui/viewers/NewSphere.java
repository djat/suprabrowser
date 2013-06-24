/**
 * 
 */
package ss.client.ui.viewers;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;

import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import ss.client.event.NewSphereSaveSelectionListener;
import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.ControlPanel;
import ss.client.ui.ISphereView;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.FilteredDataSource;
import ss.client.ui.models.autocomplete.FilteredModel;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.UiUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.MembershipEntityObject;
import ss.domainmodel.SphereMember;
import ss.domainmodel.SpherePhisicalLocationItem;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SphereItem.SphereType;
import ss.domainmodel.configuration.SphereRoleList;
import ss.domainmodel.configuration.SphereRoleObject;
import ss.util.ImagesPaths;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class NewSphere extends Dialog implements ISphereLocationEditor {

	private ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWSPHERE);

	private static final String YOU_HAVE_NOT_SAVED_YOUR_SPHERE_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW = "NEWSPHERE.YOU_HAVE_NOT_SAVED_YOUR_SPHERE_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW";

	private static final String SAVE = "NEWSPHERE.SAVE";

	public static final String YOU_MUST_CHOOSE_A_NAME_FOR_THE_SPHERE = "NEWSPHERE.YOU_MUST_CHOOSE_A_NAME_FOR_THE_SPHERE";
	
	public static final String THE_SPHERE_WITH_SUCH_NAME_ALREADY_EXISTS = "NEWSPHERE.THE_SPHERE_WITH_SUCH_NAME_ALREADY_EXISTS";

	public static final String ALERT = "NEWSPHERE.ALERT";

	public static final String YOU_MUST_CHOOSE_AT_LEAST_ONE_MEMBER_FOR_THE_SPHERE = "NEWSPHERE.YOU_MUST_CHOOSE_AT_LEAST_ONE_MEMBER_FOR_THE_SPHERE";

	public static final String YOU_MUST_CHOOSE_OWN_CONTACT_NAME = "NEWSPHERE.YOU_MUST_CHOOSE_OWN_CONTACT_NAME";

	private static final String NONE = "NEWSPHERE.NONE";

	private static final String DEFAULTS = "NEWSPHERE.DEFAULTS";

	private static final String INITIAL_MEMBERS = "NEWSPHERE.INITIAL_MEMBERS";

	private static final String DEFAULT_DELIVERY = "NEWSPHERE.DEFAULT_DELIVERY";

	private static final String CONFIRM_RECEIPT = "NEWSPHERE.CONFIRM_RECEIPT";

	private static final String NORMAL = "NEWSPHERE.NORMAL";

	private static final String DEFAULT_TYPE = "NEWSPHERE.DEFAULT_TYPE";

	private static final String DATE_RANGE = "NEWSPHERE.DATE_RANGE";

	private static final String HOUR1 = "NEWSPHERE.1HOUR";

	private static final String HOURS2 = "NEWSPHERE.2HOURS";

	private static final String HOURS3 = "NEWSPHERE.3HOURS";

	private static final String HOURS6 = "NEWSPHERE.6HOURS";

	private static final String DAY1 = "NEWSPHERE.1DAY";

	private static final String DAYS2 = "NEWSPHERE.2DAYS";

	private static final String DAYS3 = "NEWSPHERE.3DAYS";

	private static final String DAYS4 = "NEWSPHERE.4DAYS";

	private static final String DAYS5 = "NEWSPHERE.5DAYS";

	private static final String WEEK1 = "NEWSPHERE.1WEEK";

	private static final String WEEKS2 = "NEWSPHERE.2WEEKS";

	private static final String WEEKS4 = "NEWSPHERE.4WEEKS";

	private static final String ALL = "NEWSPHERE.ALL";

	private static final String NAME = "NEWSPHERE.NAME";
	
	private static final String CANCEL = "NEWSPHERE.CANCEL";
	
	private static final String NEWSPHERE = "NEWSPHERE.NEWSPHERE";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(NewSphere.class);
	
	private SupraSphereFrame sF = null;
	
	private ISphereView sphereView = null;
	
	private String login_name = null;

	private String contact_name = null;
	
	private List<String> preselected = null;
	
	private String response_id = null;

	private String thread_id = null;
	
	private String title = null;
	
	private Vector<Document> registerDocs = null;

	private List<String> registerNames = null;
	
	private Vector<Button> checkButtons = null;
	
	private Hashtable session = null;
	
	private Vector chooseFromMembers = null;
	
	private Font boldFont = null;
	
	private Combo comboDate = null;
	
	private Combo comboType = null;
	
	private Combo comboDel = null;

	private Text nameText;

	private Table table;
	
	private Combo typeCombo;

//	private Text regionText;
//
//	private Text addressText;
//
//	private Text phoneText;
	
	private final SpherePhisicalLocationItem locationItem = new SpherePhisicalLocationItem();
	
	private static final int COMBO_WIDTH = 150;
	
	public NewSphere(Hashtable session, ISphereView sphereView, String response_id,
			String thread_id, List<String> preselected, String title) {
		super(sphereView.getSupraSphereFrame().getShell());

		this.response_id = response_id;
		this.thread_id = thread_id;

		this.session = session;
		this.sphereView = sphereView;
		this.title = title;
		this.sF = sphereView.getSupraSphereFrame();
		
		this.boldFont = new Font(Display.getDefault(), "Verdana", 10, SWT.NONE);

		this.initUser();
		initPreselected(preselected);

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				setBlockOnOpen(true);
				open();
			}
		});
	}


	private void initPreselected(List<String> preselected) {
		if (!this.isAdmin()) {
			if (preselected == null) {
				this.preselected = new Vector<String>();
			} else {
				this.preselected = preselected;
				if(preselected.contains(this.contact_name)) {
					this.preselected.add(this.contact_name);
				}
			}		
		}
	}

	
	@Override
	protected void configureShell(final Shell shell) {
		shell.setSize(640, 500);
		shell.setText(this.bundle.getString(NEWSPHERE));
		try {
			Image sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
			shell.setImage(sphereImage);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	@Override
	protected ShellListener getShellListener() { 
		return new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent se) {
				se.doit = false;
				UserMessageDialogCreator.warningYesCancelButton(NewSphere.this.bundle.getString(
						YOU_HAVE_NOT_SAVED_YOUR_SPHERE_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW));
			}
		};
	}


	@Override
	protected Control createContents(Composite parent) {
		getShell().setLayout(new GridLayout());
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		mainComp.setLayoutData(data);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 2;
		mainComp.setLayout(layout);
		
		createSphereNamePane(mainComp);
		createLabelPane(mainComp);
		createMembersPane(mainComp);
		createDefaultDeliveryCombo(mainComp);
		createDefaultTypeCombo(mainComp);
		createDateRangePane(mainComp);
		createButtonPane(mainComp);
		
		mainComp.layout();
		parent.layout();
		
		return mainComp;
	}


	/**
	 * @param mainComp
	 */
	private void createButtonPane(Composite mainComp) {
		Composite comp = new Composite(mainComp, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		
		Button saveButton = new Button(comp, SWT.PUSH | SWT.CENTER);
		saveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		saveButton.setText(this.bundle.getString(SAVE));
		saveButton.addSelectionListener(new NewSphereSaveSelectionListener(this));
		
		Button cancelButton = new Button(comp, SWT.PUSH | SWT.CENTER);
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.setText(this.bundle.getString(CANCEL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				getShell().dispose();
			}
		});
	}


	/**
	 * @param mainComp
	 */
	private void createDateRangePane(Composite mainComp) {
		Label label = new Label(mainComp, SWT.NONE);
		label.setText(this.bundle.getString(DATE_RANGE));
		label.setFont(this.boldFont);
		
		this.comboDate = new Combo(mainComp, SWT.READ_ONLY);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.widthHint = COMBO_WIDTH;
		this.comboDate.setLayoutData(data);
		
		this.comboDate.add(this.bundle.getString(NONE));
		this.comboDate.add(this.bundle.getString(HOUR1));
		this.comboDate.add(this.bundle.getString(HOURS2));
		this.comboDate.add(this.bundle.getString(HOURS3));
		this.comboDate.add(this.bundle.getString(HOURS6));
		this.comboDate.add(this.bundle.getString(DAY1));
		this.comboDate.add(this.bundle.getString(DAYS2));
		this.comboDate.add(this.bundle.getString(DAYS3));
		this.comboDate.add(this.bundle.getString(DAYS4));
		this.comboDate.add(this.bundle.getString(DAYS5));
		this.comboDate.add(this.bundle.getString(WEEK1));
		this.comboDate.add(this.bundle.getString(WEEKS2));
		this.comboDate.add(this.bundle.getString(WEEKS4));
		this.comboDate.add(this.bundle.getString(ALL));
		
		this.comboDate.setText(this.bundle.getString(WEEK1));
	}


	/**
	 * @param mainComp
	 */
	private void createDefaultTypeCombo(Composite mainComp) {
		Label label = new Label(mainComp, SWT.NONE);
		label.setText(this.bundle.getString(DEFAULT_TYPE));
		label.setFont(this.boldFont);
		
		this.comboType = new Combo(mainComp, SWT.READ_ONLY);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.widthHint = COMBO_WIDTH;
		this.comboType.setLayoutData(data);
		
		this.comboType.add(ControlPanel.getTypeTerse());
		this.comboType.add(ControlPanel.getTypeBookmark());
		this.comboType.add(ControlPanel.getTypeContact());
		this.comboType.add(ControlPanel.getTypeEmail());
		this.comboType.add(ControlPanel.getTypeFile());
		this.comboType.add(ControlPanel.getTypeMessage());
		this.comboType.add(ControlPanel.getTypeRss());
		this.comboType.add(ControlPanel.getTypeSphere());
		
		this.comboType.setText(ControlPanel.getTypeTerse());
	}


	/**
	 * @param mainComp
	 */
	private void createDefaultDeliveryCombo(Composite mainComp) {
		Label label = new Label(mainComp, SWT.NONE);
		label.setText(this.bundle.getString(DEFAULT_DELIVERY));
		label.setFont(this.boldFont);
		
		this.comboDel = new Combo(mainComp, SWT.READ_ONLY);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.widthHint = COMBO_WIDTH;
		this.comboDel.setLayoutData(data);
		
		this.comboDel.add(this.bundle.getString(NORMAL));
		this.comboDel.add(this.bundle.getString(CONFIRM_RECEIPT));
		
		this.comboDel.setText(this.bundle.getString(NORMAL));
	}

	/**
	 * @param mainComp
	 */
	private void createMembersPane(Composite mainComp) {		
		this.table = new Table(mainComp, SWT.CHECK | SWT.BORDER);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		this.table.setLayoutData(data);
		
		this.chooseFromMembers = this.sF.client.getMembersFor(this.session);
		
		this.checkButtons = new Vector<Button>();
		
		for (int i = 0; i < this.chooseFromMembers.size(); i++) {
			MembershipEntityObject membership = MembershipEntityObject.wrap((Document) this.chooseFromMembers
					.get(i));
			String name = membership.getContactName();
			boolean selected = (this.preselected!=null && this.preselected.contains(name));

			TableItem item = new TableItem(this.table, SWT.NONE);
			item.setChecked(selected);
			item.setText(name);
		}
	}


	/**
	 * @param mainComp
	 */
	private void createLabelPane(Composite mainComp) {
		Label defaultsLabel = new Label(mainComp, SWT.NONE);
		defaultsLabel.setText(this.bundle.getString(DEFAULTS));
		GridData data = new GridData();
		defaultsLabel.setLayoutData(data);
		
		Label initialLabel = new Label(mainComp, SWT.NONE);
		initialLabel.setText(this.bundle.getString(INITIAL_MEMBERS));
		initialLabel.setLayoutData(data);
		
		defaultsLabel.setFont(this.boldFont);
		initialLabel.setFont(this.boldFont);
		
	}

	/**
	 * @param mainComp
	 */
	private void createSphereNamePane(Composite mainComp) {
		Composite sphereNameComp = new Composite(mainComp, SWT.NONE);
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		sphereNameComp.setLayoutData(data);
		
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		sphereNameComp.setLayout(layout);
		
		Label nameLabel = new Label(sphereNameComp, SWT.LEFT);
		nameLabel.setText(this.bundle.getString(NAME));
		data = new GridData();
		data.verticalIndent = 3;
		nameLabel.setLayoutData(data);
		nameLabel.setFont(this.boldFont);
		
		this.nameText = new Text(sphereNameComp, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
		this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		if(this.title!=null) {
			this.nameText.setText(this.title);
		}
		
		Label comboLabel = new Label(sphereNameComp, SWT.LEFT);
		comboLabel.setText("Type");
		data = new GridData();
		data.verticalIndent = 3;
		comboLabel.setLayoutData(data);
		comboLabel.setFont(this.boldFont);
		
		this.typeCombo = new Combo(sphereNameComp, SWT.BORDER | SWT.READ_ONLY | SWT.LEFT);
		this.typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		SphereRoleList list = SsDomain.CONFIGURATION.getMainConfigurationValue().getSphereRoleList();
		for(SphereRoleObject roleObject : list) {
			this.typeCombo.add(roleObject.getRoleName());
		}
		if(!list.contains(SphereRoleObject.getDefaultName())) {
			this.typeCombo.add(SphereRoleObject.getDefaultName(), 0);
		}
		this.typeCombo.select(this.typeCombo.indexOf(SphereRoleObject.getDefaultName()));
		
		Button editLocationButton = new Button(sphereNameComp, SWT.PUSH);
		editLocationButton.setText("Edit Location...");
		data = new GridData();
		data.horizontalSpan = 2;
		editLocationButton.setLayoutData(data);
		editLocationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				startEditLocation();
			}
		});
		
		
		sphereNameComp.layout();
	}
	
	/**
	 * 
	 */
	public void startEditLocation() {
		SphereLocationDialog dialog = new SphereLocationDialog(this, true);
		dialog.setBlockOnOpen(true);
		dialog.open();
	}


	private void addTypeAhead( final Text field, final List<String> recievedData ){
		if ((recievedData == null) || (recievedData.isEmpty())) {
			return;
		}
		final TreeSet<String> set = new TreeSet<String>();
		set.addAll(recievedData);
		new TypeAheadComponent<String>(field,
				new FilteredModel<String>(new FilteredDataSource<String>() {

					public Vector<String> getData(String filter) {
						return processDataFiltered(set, filter);
					}
				}, 200, BaseDataModel.FilterType.NoFilter,
						new DataSourceLabeler<String>() {

							public String getDataLabel(String data) {
								return data;
							}
						}), new ResultAdapter<String>() {
					@Override
					public void processListSelection(String selection,
							String realData) {
						field.setText(realData);
					}
				});
	}
	
	private Vector<String> processDataFiltered(final TreeSet<String> data,
			String filter) {
		if ((filter == null) || (filter.trim().equals("")))
			return new Vector<String>(data);
		final Vector<String> out = new Vector<String>();
		for (String s : data) {
			if ( (s != null)&&(s.startsWith(filter)) ){
				out.add(s);
			}
		}
		return out;
	}
	
	public void initUser() {
		this.login_name = this.sphereView.getVerbosedSession().getUserLogin();
		this.contact_name = this.sphereView.getVerbosedSession().getUserContactName();
	}
	
	public boolean isAdmin() {
		return this.sF.client.getVerifyAuth().isAdmin(this.contact_name,
				this.login_name);
	}
	
	public long getNextTableId() {
		synchronized (MessagesPane.getTableIdGenerator()) {
			return Math.abs(MessagesPane.getTableIdGenerator().nextLong());
		}
	}
	
	public SphereStatement createSphere() {

		SphereStatement sphere = new SphereStatement();

		this.registerDocs = new Vector<Document>();
		this.registerNames = new Vector<String>();

		String delivery = this.comboDel.getText();

		String default_delivery = "normal";

		if (delivery.equals(this.bundle.getString(CONFIRM_RECEIPT))) {
			default_delivery = "confirm_receipt";
		}

		String system_id = (Long.toString(getNextTableId()));

		sphere.setVotingModelDesc("Absolute without qualification");
		sphere.setVotingModelType("absolute");
		sphere.setSpecificMemberContactName("__NOBODY__");
		sphere.setTallyNumber("0.0");
		sphere.setTallyValue("0.0");
		sphere.setThreadType("sphere");
		sphere.setType("sphere");
		
		if (this.response_id != null) {
			sphere.setResponseId(this.response_id);
			sphere.setThreadId(this.thread_id);
		}

		for(TableItem item : this.table.getItems()) {
			if(item.getChecked()) {
				String login = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getLoginForContact(item.getText());
				
				SphereMember member = new SphereMember();
				member.setContactName(item.getText());
				member.setLoginName(login);
				
				sphere.addMember(member);
				
				for (int j = 0; j < this.chooseFromMembers.size(); j++) {
					MembershipEntityObject membership = MembershipEntityObject.wrap((Document) this.chooseFromMembers
							.get(j));
					String name = item.getText();
					String contactName = membership.getContactName();
					if (name.equals(contactName)) {
						this.registerNames.add(name);
						this.registerDocs.add(membership.getBindedDocument());
					}
				}
			}
		}

		sphere.setVersion("3000");		

		sphere.setDisplayName(this.nameText.getText());
		sphere.setSystemName(system_id);
		sphere.setSphereType(SphereType.GROUP);
		sphere.setSubject(this.nameText.getText());
		String role = this.typeCombo.getText();
		if (StringUtils.isNotBlank(role) && !role.equals(SphereRoleObject.getDefaultName())) {
			sphere.setRole(role);
		}
		sphere.getPhisicalLocation().copyAll(this.locationItem);
		
//		if(StringUtils.isNotBlank(this.addressText.getText())) {
//			sphere.getPhisicalLocation().setAddress(StringUtils.getTrimmedString(this.addressText.getText()));
//		}
//		if(StringUtils.isNotBlank(this.regionText.getText())) {
//			sphere.getPhisicalLocation().setRegion(StringUtils.getTrimmedString(this.regionText.getText()));
//		}
//		if(StringUtils.isNotBlank(this.phoneText.getText())) {
//			sphere.getPhisicalLocation().setTelephone(StringUtils.getTrimmedString(this.phoneText.getText()));
//		}

		sphere.setGiver((String) this.session.get(SessionConstants.REAL_NAME));
		sphere.setGiverUsername(SupraSphereFrame.INSTANCE.client.getVerifyAuth().getLoginForContact(this.contact_name));

		sphere.setDefaultDelivery(default_delivery);
		sphere.setDefaultType(this.comboType.getText().toLowerCase());

		sphere.setTerseEnabled(true);
		sphere.setTerseModify("own");
		sphere.setMessageEnabled(true);
		sphere.setMessageModify("own");
		sphere.setBookmarkEnabled(true);
		sphere.setBookmarkModify("own");
		sphere.setExternalEmailEnabled(true);
		sphere.setExternalEmailModify("own");
		sphere.setContactEnabled(true);
		sphere.setContactModify("own");
		sphere.setRssEnabled(true);
		sphere.setRssModify("own");
		sphere.setKeywordsEnabled(true);
		sphere.setKeywordsModify("own");
		sphere.setFileEnabled(true);
		sphere.setFileModify("own");
		sphere.setSphereEnabled(true);
		sphere.setSphereModify("own");
		
		sphere.setExpiration(this.comboDate.getText());

		return sphere;

	}
	
	public Text getNameField() {
		return this.nameText;
	}
	
	public Vector<Button> getCheckButtons() {
		return this.checkButtons;
	}
	
	public ResourceBundle getBundle() {
		return this.bundle;
	}
	
	public Vector<Document> getRegisterDocs() {
		return this.registerDocs;
	}
	
	public List<String> getRegisterNames() {
		return this.registerNames;
	}
	
	public String getContactName() {
		return this.contact_name;
	}
	
	public Hashtable getSession() {
		return this.session;
	}

	/**
	 * @return 
	 * @return
	 */
	public DialogsMainCli getClient() {
		return this.sF.client;
	}
	
	public SpherePhisicalLocationItem getPhisicalLocationItem() {
		return this.locationItem;
	}
	
	public void setPhisicalLocation(final SpherePhisicalLocationItem locationItem) {
		this.locationItem.copyAll(locationItem);
	}
}
