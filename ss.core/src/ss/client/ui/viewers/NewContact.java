/*
 * Created on Aug 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui.viewers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.ISphereView;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.FilteredDataSource;
import ss.client.ui.models.autocomplete.FilteredModel;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.client.ui.viewers.actions.NewContactChangePassphraseActionListener;
import ss.client.ui.viewers.actions.NewContactConnectActionListener;
import ss.client.ui.viewers.actions.NewContactEntitleActionListener;
import ss.client.ui.viewers.actions.NewContactInviteActionListener;
import ss.client.ui.viewers.actions.NewContactLockContactActionListener;
import ss.client.ui.viewers.actions.NewContactMakeSphereCoreActionListener;
import ss.client.ui.viewers.actions.NewContactOpenSphereActionListener;
import ss.client.ui.viewers.actions.NewContactPublishActionListener;
import ss.client.ui.viewers.actions.NewContactRecallActionListener;
import ss.client.ui.viewers.actions.NewContactUnlockActionListener;
import ss.client.ui.viewers.actions.NewContactUpdateActionListener;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.TimeZoneUtils;
import ss.common.UiUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereMember;
import ss.domainmodel.configuration.ClubdealContactType;
import ss.domainmodel.configuration.ClubdealContactTypeCollection;
import ss.global.SSLogger;
import ss.util.ImagesPaths;
import ss.util.NameTranslation;
import ss.util.SessionConstants;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class NewContact {
	
	private static final Point TEXT_SIZE = new Point(135, 17);
	private static final Point LABEL_SIZE = new Point(100, 17);
	private static final Point LABEL_WIDE_SIZE = new Point(117, 17);
	private static final int firstlabelCol = 15;
	private static final int seclabelCol = 274;
	private static final int firstTextCol = 115;
	private static final int secTextCol = 391;

	enum LocationStyle {
		FIRST_COLUMN( firstlabelCol, LABEL_SIZE, true, 3 ),
		SECOND_COLUMN( seclabelCol, LABEL_WIDE_SIZE, false, 3 ),
		LABEL_FOR_COMBO_IN_SECOND_COLUMN( seclabelCol, LABEL_WIDE_SIZE, false, 3 ),
		LABEL_FOR_COMBO_IN_FIRST_COLUMN( firstlabelCol, LABEL_SIZE, true, 3 );
		
		private final int leftOffset;
		
		private final Point size;
		
		private final boolean firstColumn;
		
		private final int vertialOffset;

		
		/**
		 * @param leftOffset
		 * @param size
		 * @param firstColumn
		 * @param vertialOffset
		 */
		private LocationStyle(int leftOffset, Point size, boolean firstColumn,
				int vertialOffset) {
			this.leftOffset = leftOffset;
			this.size = size;
			this.firstColumn = firstColumn;
			this.vertialOffset = vertialOffset;
		}

		/**
		 * @return
		 */
		public int getLeftOffset() {
			return this.leftOffset;
		}

		/**
		 * @return
		 */
		public Point getSize() {
			return this.size;
		}
		
		public boolean isFirstColumn() {
			return this.firstColumn;
		}

		/**
		 * @param firstColLabelCount
		 * @param secColLabelCount
		 * @return
		 */
		public int getTopOffset(int firstColLabelCount) {
			return 10 + firstColLabelCount * 26 + this.vertialOffset;
		}		
	}
	
	private static boolean fromMain = false;
	private int firstColLabelCount = 0;
	private int secColLabelCount = 0;
	private int firstColTextCount = 0;
	private int secColTextCount = 0;

	private static final Logger logger = SSLogger.getLogger(NewContact.class);

	private static org.eclipse.swt.widgets.Shell sShell = null; // @jve:decl-index=0:visual-constraint="116,16"
	
	private final ISphereView sphereViewOwner;

	private Document origDoc = null;

	private boolean isFillMessage = false;
	
	private boolean isContactLocked = false;

	private final Hashtable session;

	private Text textFirstName = null;

	private Display display = null;

	private Text textTitle = null;

	private Text textStreet = null;

	private Text textStreetCont = null;

	private Text textCity = null;

	private Text textState = null;

	private Text textZipCode = null;

	private Text textCountry = null;

	private Text textEmail = null;
	
	private Text textSecondEmail = null;

	private Text textURL = null;

	private Text textArea = null;

	private Text textLastName = null;

	private Text textDepartment = null;
	
	private Text account = null;
	
	private Text ownerContact = null;

	private Text textMobile = null;

	private Text textVoice1 = null;

	private Text textVoice2 = null;

	private Text textFax = null;
	
	private Text textFaxSecond = null;

	private Text textLogin = null;

	private Text textPassword = null;

	private Text textOrganization = null;
	
	private Combo timeZone = null;
	
	private Combo contactType = null;
	
	private Text textLocation = null;
	
	private Text textNamePrefix = null;
	
	private Text textNameSuffix = null;
	
	private Text textMiddleName = null;
	
	/******************************************************/
//	private Composite buttonComposite;
	/******************************************************/

	private Composite toolBarNew;

	private Button pubItem = null;

	private Button updateItem = null;

	private Button connectItem = null;

	private Button entitleItem = null;

	private Button inviteItem = null;

	private Button recallItem = null;

	private Button changePassphraseNextItem = null;

	private Text textHomeSphere = null;

	private Button openSphereItem;

	@SuppressWarnings("unused")
	private String bdir = System.getProperty("user.dir");

	@SuppressWarnings("unused")
	private String fsep = System.getProperty("file.separator");

	private Button makeSphereCore;

	private boolean isAdmin = false;

	private boolean isCreator = false;

	private boolean isUserContact = false;
	
	private List<String> contactNamesList = null;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWCONTACT);
	protected Button lockContactItem;
	private Button unlockContactItem;

	private static final String NEW_CONTACT = "NEWCONTACT.NEW_CONTACT";

	private static final String FIRST_NAME = "NEWCONTACT.FIRST_NAME";

	private static final String TITLE = "NEWCONTACT.TITLE";

	private static final String STREET = "NEWCONTACT.STREET";

	private static final String STREET_CONT = "NEWCONTACT.STREET_CONT";

	private static final String CITY = "NEWCONTACT.CITY";

	private static final String STATE = "NEWCONTACT.STATE";

	private static final String ZIP_CODE = "NEWCONTACT.ZIP_CODE";

	private static final String COUNTRY = "NEWCONTACT.COUNTRY";

	private static final String EMAIL = "NEWCONTACT.EMAIL";

	private static final String URL = "NEWCONTACT.URL";

	private static final String ORIGINAL_NOTE = "NEWCONTACT.ORIGINAL_NOTE";

	private static final String LAST_NAME = "NEWCONTACT.LAST_NAME";

	private static final String ORGANIZATION = "NEWCONTACT.ORGANIZATION";

	private static final String DEPARTMENT = "NEWCONTACT.DEPARTMENT";

	private static final String VOICE1 = "NEWCONTACT.VOICE1";

	private static final String VOICE2 = "NEWCONTACT.VOICE2";

	private static final String MOBILE = "NEWCONTACT.MOBILE";

	private static final String FAX = "NEWCONTACT.FAX";
	
	private static final String FAXSECOND = "NEWCONTACT.FAXSECOND";

	private static final String LOGIN = "NEWCONTACT.LOGIN";

	private static final String PASSPHRASE = "NEWCONTACT.PASSPHRASE";

	private static final String HOME_SPHERE = "NEWCONTACT.HOME_SPHERE";

	private static final String YOU_HAVE_NOT_SAVED_THIS_CONTACT_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW = "NEWCONTACT.YOU_HAVE_NOT_SAVED_THIS_CONTACT_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW";

	private static final String SAVE = "NEWCONTACT.SAVE";

	private static final String CHANGE_PASSPHRASE_NEXT_LOGIN = "NEWCONTACT.CHANGE_PASSPHRASE_NEXT_LOGIN";

	private static final String ENTITLE_FOR_CURRENT_SPHERE = "NEWCONTACT.ENTITLE_FOR_CURRENT_SPHERE";

	private static final String MAKE_LOGIN_SPHERE = "NEWCONTACT.MAKE_LOGIN_SPHERE";

	private static final String INVITE = "NEWCONTACT.INVITE";

	private static final String DELETE = "NEWCONTACT.DELETE";
	
	private static final String NAME_PREFIX = "NEWCONTACT.NAME_PREFIX";
	
	private static final String NAME_SUFFIX = "NEWCONTACT.NAME_SUFFIX";
	
	private static final String MIDDLE_NAME = "NEWCONTACT.MIDDLE_NAME";
	
	private static final String LOCATION = "NEWCONTACT.LOCATION";
	
	private static final String TIME_ZONE = "NEWCONTACT.TIME_ZONE";

	public NewContact() {
		this( new Hashtable(), null );
	}

	public NewContact(Hashtable session, ISphereView mP) {
		this(session, mP, null);
	}

	public NewContact(Hashtable session, ISphereView sphereViewOwner, String response_id) {
		this.session = session;
		// Not used, commented:
		// this.response_id = response_id;
		this.sphereViewOwner = sphereViewOwner;
		createSShell();

	}

	/**
	 * This method initializes sShell
	 */
	public void createSShell() {

		if (SupraSphereFrame.INSTANCE.client.getVerifyAuth().isAdmin(
				(String) this.session.get(SessionConstants.REAL_NAME),
				(String) this.session.get(SessionConstants.USERNAME))) {
			this.isAdmin = true;
		}

		this.display = Display.getDefault();
		Thread t = new Thread() {
			private NewContact newContact = NewContact.this;

			@Override
			public void run() {
				
				loadContactNamesList();

				sShell = new Shell(SWT.BORDER | SWT.SHELL_TRIM
						| SWT.LEFT_TO_RIGHT);
				Image im;
				try {
					im = new Image(Display.getDefault(), getClass()
							.getResource(ImagesPaths.SUPRA_ICON).openStream());
					sShell.setImage(im);
				} catch (IOException ex) {
					logger.error("can't create supra icon", ex);
				}


				Label labelFirstName = null;

				Label labelTitle = null;

				Label labelStreet = null;

				Label labelStreetCont = null;

				Label labelCity = null;

				Label labelState = null;

				Label labelZipCode = null;

				Label labelCountry = null;

				Label labelEmail = null;

				Label labelURL = null;

				Label labelOriginalNote = null;

				Label labelLastName = null;

				Label labelDepartment = null;
				
				Label labelAccount = null;
				
				Label labelOwnerContact = null;

				Label labelVoice1 = null;

				Label labelVoice2 = null;

				Label labelMobile = null;

				Label labelFax = null;
				
				Label labelFaxSecond = null;

				Label labelLogin = null;

				Label labelPassphrase = null;

				Label labelHomeSphere = null;

				Label labelOrganization = null;
				
				Label labelMiddleName = null;
				
				Label labelNamePrefix = null;
				
				Label labelNameSuffix = null;
				
				Label labelTimeZone = null;
				
				Label labelSecondEmail = null;
				
				Label labelContactType = null;
				
				Label labelLocation = null;

				this.newContact.textFirstName = new Text(sShell, SWT.BORDER);
				labelFirstName = new Label(sShell, SWT.NONE);
				labelTitle = new Label(sShell, SWT.NONE);
				labelStreet = new Label(sShell, SWT.NONE);
				labelStreetCont = new Label(sShell, SWT.NONE);
				labelCity = new Label(sShell, SWT.NONE);
				labelState = new Label(sShell, SWT.NONE);
				labelZipCode = new Label(sShell, SWT.NONE);
				labelCountry = new Label(sShell, SWT.NONE);
				labelEmail = new Label(sShell, SWT.NONE);
				labelURL = new Label(sShell, SWT.NONE);
				labelAccount = new Label(sShell, SWT.NONE);
				labelSecondEmail = new Label(sShell, SWT.NONE);

				this.newContact.textTitle = new Text(sShell, SWT.BORDER);
				this.newContact.textStreet = new Text(sShell, SWT.BORDER);
				this.newContact.textStreetCont = new Text(sShell, SWT.BORDER);
				this.newContact.textCity = new Text(sShell, SWT.BORDER);
				this.newContact.textState = new Text(sShell, SWT.BORDER);
				this.newContact.textZipCode = new Text(sShell, SWT.BORDER);
				this.newContact.textCountry = new Text(sShell, SWT.BORDER);
				this.newContact.textEmail = new Text(sShell, SWT.BORDER);
				this.newContact.textSecondEmail = new Text(sShell, SWT.BORDER);
				this.newContact.textURL = new Text(sShell, SWT.BORDER);
				labelOriginalNote = new Label(sShell, SWT.NONE);

				this.newContact.textArea = new Text(sShell, SWT.MULTI
						| SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
				labelLastName = new Label(sShell, SWT.NONE);
				this.newContact.textLastName = new Text(sShell, SWT.BORDER
						| SWT.LEFT_TO_RIGHT);
				labelDepartment = new Label(sShell, SWT.BOTTOM);
				labelVoice1 = new Label(sShell, SWT.BOTTOM);
				labelVoice2 = new Label(sShell, SWT.BOTTOM);
				labelMobile = new Label(sShell, SWT.BOTTOM);
				labelFax = new Label(sShell, SWT.BOTTOM);
				labelFaxSecond = new Label(sShell, SWT.BOTTOM);
				labelLogin = new Label(sShell, SWT.BOTTOM);
				labelPassphrase = new Label(sShell, SWT.BOTTOM);
				labelHomeSphere = new Label(sShell, SWT.BOTTOM);
				labelOrganization = new Label(sShell, SWT.BOTTOM);
				labelTimeZone = new Label(sShell, SWT.BOTTOM);
				labelNamePrefix = new Label(sShell, SWT.BOTTOM);
				labelNameSuffix = new Label(sShell, SWT.BOTTOM);
				labelLocation = new Label(sShell, SWT.BOTTOM);
				labelMiddleName = new Label(sShell, SWT.BOTTOM);
				labelContactType = new Label(sShell, SWT.BOTTOM);
				labelOwnerContact = new Label(sShell, SWT.BOTTOM);
				
				this.newContact.textDepartment = new Text(sShell, SWT.BORDER);
				this.newContact.account = new Text(sShell, SWT.BORDER);
				this.newContact.ownerContact = new Text(sShell, SWT.BORDER);
				this.newContact.textMobile = new Text(sShell, SWT.BORDER);
				this.newContact.textVoice1 = new Text(sShell, SWT.BORDER);
				this.newContact.textVoice2 = new Text(sShell, SWT.BORDER);
				this.newContact.textFax = new Text(sShell, SWT.BORDER);
				this.newContact.textFaxSecond = new Text(sShell, SWT.BORDER);
				this.newContact.textLogin = new Text(sShell, SWT.BORDER);
				this.newContact.textPassword = new Text(sShell, SWT.BORDER);
				this.newContact.textHomeSphere = new Text(sShell, SWT.BORDER);
				this.newContact.textOrganization = new Text(sShell, SWT.BORDER);
				this.newContact.timeZone = new Combo(sShell, SWT.BORDER | SWT.READ_ONLY);
				this.newContact.textNamePrefix = new Text(sShell, SWT.BORDER);
				this.newContact.textNameSuffix = new Text(sShell, SWT.BORDER);
				this.newContact.textLocation = new Text(sShell, SWT.BORDER);
				this.newContact.textMiddleName = new Text(sShell, SWT.BORDER);
				this.newContact.contactType = new Combo(sShell, SWT.READ_ONLY | SWT.LEFT);
				
				// createNewToolBar();
				// createNewToolBar();

				Control[] control = { this.newContact.textFirstName,
						this.newContact.textLastName, this.newContact.textTitle,
						this.newContact.textDepartment, this.newContact.account,  
						this.newContact.ownerContact, this.newContact.textStreet,
						this.newContact.textStreetCont, this.newContact.textCity,
						this.newContact.textState, this.newContact.textZipCode,
						this.newContact.textCountry, this.newContact.textMobile,
						this.newContact.textVoice1, this.newContact.textVoice2,
						this.newContact.textFax, this.newContact.textFaxSecond, 
						this.newContact.textEmail, this.newContact.textURL, 
						this.newContact.textMiddleName,	this.newContact.textLocation, 
						this.newContact.timeZone, this.newContact.textNamePrefix, 
						this.newContact.textNameSuffix,	this.newContact.textArea 
					};
				sShell.setTabList(control);

				sShell.setText(NewContact.this.bundle
						.getString(NewContact.NEW_CONTACT));

				addControl(labelFirstName, NewContact.this.bundle
						.getString(NewContact.FIRST_NAME), true);
				addControl(labelNamePrefix, NewContact.this.bundle
						.getString(NewContact.NAME_PREFIX), true);
				addControl(labelNameSuffix, NewContact.this.bundle
						.getString(NewContact.NAME_SUFFIX), true);
				addControl(labelTitle, NewContact.this.bundle
						.getString(NewContact.TITLE), true);
				addControl(labelStreet, NewContact.this.bundle
						.getString(NewContact.STREET), true);
				addControl(labelStreetCont, NewContact.this.bundle
						.getString(NewContact.STREET_CONT), true);
				addControl(labelCity, NewContact.this.bundle
						.getString(NewContact.CITY), true);
				addControl(labelState, NewContact.this.bundle
						.getString(NewContact.STATE), true);
				addControl(labelZipCode, NewContact.this.bundle
						.getString(NewContact.ZIP_CODE), true);
				addControl(labelCountry, NewContact.this.bundle
						.getString(NewContact.COUNTRY), true);
				addControl(labelEmail, NewContact.this.bundle
						.getString(NewContact.EMAIL), true);
				addControl(labelSecondEmail, "Second Email", true);
				addControl(labelURL, NewContact.this.bundle
						.getString(NewContact.URL), true);
				addControl(labelHomeSphere, NewContact.this.bundle
						.getString(HOME_SPHERE), true);
				addControl(labelContactType, "Contact Type ", LocationStyle.LABEL_FOR_COMBO_IN_FIRST_COLUMN);
				
				
				addControl(labelLastName, NewContact.this.bundle
						.getString(NewContact.LAST_NAME), false);
				addControl(labelMiddleName, NewContact.this.bundle
						.getString(NewContact.MIDDLE_NAME), false);
				addControl(labelOrganization, NewContact.this.bundle
						.getString(NewContact.ORGANIZATION), false);
				addControl(labelDepartment, NewContact.this.bundle
						.getString(NewContact.DEPARTMENT), false);
				addControl(labelAccount, "Account", false);
				addControl(labelOwnerContact, "Contact Owner", false);
				addControl(labelVoice1, NewContact.this.bundle
						.getString(NewContact.VOICE1), false);
				addControl(labelVoice2, NewContact.this.bundle
						.getString(NewContact.VOICE2), false);
				addControl(labelMobile, NewContact.this.bundle
						.getString(NewContact.MOBILE), false);
				addControl(labelFax, NewContact.this.bundle
						.getString(NewContact.FAX), false);
				addControl(labelFaxSecond, NewContact.this.bundle
						.getString(NewContact.FAXSECOND), false);
				addControl(labelLogin, NewContact.this.bundle
						.getString(NewContact.LOGIN), false);
				addControl(labelPassphrase, NewContact.this.bundle
						.getString(NewContact.PASSPHRASE), false);
				addControl(labelLocation, NewContact.this.bundle
						.getString(NewContact.LOCATION), false);
				addControl(labelTimeZone, NewContact.this.bundle
						.getString(NewContact.TIME_ZONE), LocationStyle.LABEL_FOR_COMBO_IN_SECOND_COLUMN);
				
				addControl(this.newContact.textFirstName, true);
				addControl(this.newContact.textNamePrefix, true);
				addControl(this.newContact.textNameSuffix, true);
				addControl(this.newContact.textTitle, true);
				addControl(this.newContact.textStreet, true);
				addControl(this.newContact.textStreetCont, true);
				addControl(this.newContact.textCity, true);
				addControl(this.newContact.textState, true);
				addControl(this.newContact.textZipCode, true);
				addControl(this.newContact.textCountry, true);
				addControl(this.newContact.textEmail, true);
				addControl(this.newContact.textSecondEmail, true);
				addControl(this.newContact.textURL, true);
				addControl(this.newContact.textHomeSphere, true);
				addControl(this.newContact.contactType, true);
				
				addControl(this.newContact.textLastName, false);
				addControl(this.newContact.textMiddleName, false);
				addControl(this.newContact.textOrganization, false);
				addControl(this.newContact.textDepartment, false);
				addControl(this.newContact.account, false);
				addControl(this.newContact.ownerContact, false);
				addControl(this.newContact.textVoice1, false);
				addControl(this.newContact.textVoice2, false);
				addControl(this.newContact.textMobile, false);
				addControl(this.newContact.textFax, false);
				addControl(this.newContact.textFaxSecond, false);
				addControl(this.newContact.textLogin, false);
				addControl(this.newContact.textPassword, false);
				this.newContact.textPassword.setEchoChar('*');
				addControl(this.newContact.textLocation, false);
				addControl(this.newContact.timeZone, false);
				
				fillTimeZoneCombo();
				fillContactTypeCombo();
				
				addTypeAhead( this.newContact.ownerContact, this.newContact.contactNamesList );
				
				labelOriginalNote.setBounds(new org.eclipse.swt.graphics.Rectangle(13,
						400, 100, 16));
				labelOriginalNote.setText(NewContact.this.bundle
						.getString(NewContact.ORIGINAL_NOTE));
				this.newContact.textArea
						.setBounds(new org.eclipse.swt.graphics.Rectangle(12,
								425, 575, 111));
				
				sShell.setSize(new org.eclipse.swt.graphics.Point(622, 610));

				if (SupraSphereFrame.INSTANCE != null) {
					Rectangle parentBounds = SupraSphereFrame.INSTANCE
							.getShell().getBounds();

					Rectangle childBounds = sShell.getBounds();
					int x = parentBounds.x
							+ (parentBounds.width - childBounds.width) / 2;

					int y = parentBounds.y
							+ (parentBounds.height - childBounds.height) / 2;

					sShell.setLocation(x, y);
				} else {
					logger.info("laying out");
					sShell.layout();
					sShell.open();
					runDisplay();
				}

				sShell.addShellListener(new ShellAdapter() {
					private NewContact newContact = NewContact.this;

					@Override
					public void shellClosed(ShellEvent e) {

						if (!this.newContact.isFillMessage) {
							e.doit = false;
							Thread main = new Thread() { // Need to do it
								// inside a thread
								// because of the
								// threading issues
								// between Swing and
								// SWT
								private NewContact newContact = NewContact.this;

								@Override
								public void run() {
									UserMessageDialogCreator
											.warningYesCancelButton(this.newContact.bundle
													.getString(YOU_HAVE_NOT_SAVED_THIS_CONTACT_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW));

								}
							};
							main.start();
						}

					}
				});

			}
		};
		if (fromMain) {
			UiUtils.swtInvoke(t);
		} else {
			UiUtils.swtBeginInvoke(t);
		}
	}
	
	@SuppressWarnings("unused")
	private void addTypeAhead( final Text field, final List<String> recievedData ){
		if ((this.contactNamesList == null) || (this.contactNamesList.isEmpty())) {
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
	
	private void loadContactNamesList(){
		this.contactNamesList = SupraSphereFrame.INSTANCE.client.getAllVisibleContactNames();
		if ( this.contactNamesList == null ) {
			logger.error("Contact Names list returned is null");
			this.contactNamesList = new ArrayList<String>();
		}
	}
	
	private boolean checkSpecificFields(){
		final String owner = this.ownerContact.getText();
		if ( (this.contactNamesList == null) || (this.contactNamesList.isEmpty()) ) {
			if ( StringUtils.isNotBlank( owner ) ) {
				return false;
			}
		}
		if ( StringUtils.isNotBlank( owner ) ) {
			if ( !this.contactNamesList.contains( owner ) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	protected void fillContactTypeCombo() {
		ClubdealContactTypeCollection types = SsDomain.CONFIGURATION.getMainConfigurationValue().getClubdealContactTypes();
		for(ClubdealContactType type : types) {
			this.contactType.add(type.getName());
		}
		if(this.contactType.indexOf(SphereMember.NO_TYPE)<0) {
			this.contactType.add(SphereMember.NO_TYPE);
		}
		this.contactType.select(this.contactType.indexOf(SphereMember.NO_TYPE));
	}

	/**
	 * 
	 */
	protected void fillTimeZoneCombo() {
		for(String zone : TimeZoneUtils.INSTANCE.getZoneList()) {
			this.timeZone.add(zone);
		}
		this.timeZone.select(this.timeZone.indexOf(TimeZoneUtils.UTC));
	}
	
	private boolean isContactLocked() {
		return this.isContactLocked;
	}

	public void fillDoc(final Document xmlDoc) {

		this.isFillMessage = true;

		Thread t = new Thread() {
			private NewContact newContact = NewContact.this;

			@Override
			public void run() {

				this.newContact.setOrigDoc(xmlDoc);
				ContactStatement contact = this.newContact.getOrigContact();
				
				NewContact.this.isContactLocked = SupraSphereFrame.INSTANCE.client.isContactLocked(contact.getLogin());
				
				String creatorName = contact.getGiver();

				if (creatorName.equals((String) this.newContact.getSession()
						.get(SessionConstants.REAL_NAME))) {
					this.newContact.isCreator = true;

				}

				String name = NameTranslation
						.createContactNameFromContactDoc(this.newContact
								.getOrigDoc());

				if (name.equals((String) this.newContact.getSession().get(
						SessionConstants.REAL_NAME))) {
					this.newContact.isUserContact = true;
				}

				this.newContact.textFirstName.setText(contact.getFirstName());
				this.newContact.textLastName.setText(contact.getLastName());

				this.newContact.setLogin(StringUtils.getTrimmedString(contact.getLogin()));
				if (contact.getMessageTitle() != null && !contact.getMessageTitle().trim().equals("")) {

					this.newContact.textTitle.setText(contact.getMessageTitle());
				}
				if (contact.getDepartment() != null) {
					this.newContact.textDepartment.setText(contact.getDepartment());
				}
				this.newContact.account.setText( StringUtils.getNotNullString(contact.getAccount()));
				this.newContact.ownerContact.setText( StringUtils.getNotNullString(contact.getOwnerContact()));
				if (contact.getOrganization() != null) {
					this.newContact.textOrganization.setText(contact.getOrganization());
				}

				try {
					this.newContact.textEmail.setText(StringUtils.getTrimmedString(contact.getEmailAddress()));
					this.newContact.textSecondEmail.setText(StringUtils.getTrimmedString(contact.getSecondEmailAddress()));
					this.newContact.textVoice1.setText(StringUtils.getTrimmedString(contact.getWorkTelephone()));

					this.newContact.textVoice2.setText(StringUtils.getTrimmedString(contact.getHomeTelephone()));
					this.newContact.textMobile.setText(StringUtils.getTrimmedString(contact.getMobile()));

					this.newContact.textFax.setText(StringUtils.getTrimmedString(contact.getFax()));
					
					this.newContact.textFaxSecond.setText(StringUtils.getTrimmedString(contact.getFaxSecond()));

					this.newContact.textURL.setText(StringUtils.getTrimmedString(contact.getURL()));

					this.newContact.textStreet.setText(StringUtils.getTrimmedString(contact.getStreet()));
					this.newContact.textStreetCont.setText(StringUtils.getTrimmedString(contact.getStreetCont()));

					this.newContact.textCity.setText(StringUtils.getTrimmedString(contact.getCity()));
					this.newContact.textState.setText(StringUtils.getTrimmedString(contact.getState()));

					this.newContact.textZipCode.setText(StringUtils.getTrimmedString(contact.getZipCode()));

					this.newContact.textCountry.setText(StringUtils.getTrimmedString(contact.getCountry()));

					this.newContact.textHomeSphere.setText(StringUtils.getTrimmedString(contact.getHomeSphere()));
					this.newContact.textNamePrefix.setText(StringUtils.getTrimmedString(contact.getNamePrefix()));
					this.newContact.textNameSuffix.setText(StringUtils.getTrimmedString(contact.getNameSuffix()));
					this.newContact.textMiddleName.setText(StringUtils.getTrimmedString(contact.getMiddleName()));
					if(contact.getLocation()!=null) {
						this.newContact.textLocation.setText(StringUtils.getTrimmedString(contact.getLocation()));
					}
					this.newContact.timeZone.select(this.newContact.timeZone.indexOf(contact.getTimeZone()));
					this.newContact.contactType.select(this.newContact.contactType.indexOf(contact.getRole()));

				} catch (NullPointerException npe) {
				}

			}
		};
		UiUtils.swtBeginInvoke(t);

	}

	public void addPublishActionListener() {

		this.pubItem.addListener(SWT.Selection,
				new NewContactPublishActionListener(this));
	}

	/**
	 * This method initializes toolBar
	 * 
	 */
	public void createNewToolBar() {
		UiUtils.swtBeginInvoke(new Runnable() {
			private NewContact newContact = NewContact.this;

			public void run() {
				this.newContact.toolBarNew = new Composite(NewContact.getSShell(),SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.horizontalSpacing = 0;
				layout.makeColumnsEqualWidth = false/*true*/;
				layout.marginBottom = 0;
				layout.marginHeight = 0;
				layout.marginLeft = 10;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.marginWidth = 0;
				layout.numColumns = 5;
				layout.verticalSpacing = 0;
				layout.horizontalSpacing = 5;
				this.newContact.toolBarNew.setLayout(layout);

				// button = new Button(toolBar, SWT.NONE);
				// button.setText("Publish");
				// button.setBounds(new
				// org.eclipse.swt.graphics.Rectangle(115,9,84,23));

				this.newContact.pubItem = new Button(this.newContact.toolBarNew,
						SWT.PUSH);
				this.newContact.pubItem.setText(NewContact.this.bundle
						.getString(NewContact.SAVE));
				this.newContact.toolBarNew
						.setBounds(new org.eclipse.swt.graphics.Rectangle(9,
								545, 573, 42));
				addPublishActionListener();
			}
		});
	}

	@ss.refactor.Refactoring(classify=ss.refactor.supraspheredoc.SupraSphereRefactor.class)
	public void createViewToolBar() {
		UiUtils.swtBeginInvoke(new Runnable() {
			private NewContact newContact = NewContact.this;

			public void run() {

				logger.info("creating view toolbar here");
				ContactStatement contact = getOrigContact();
				
				this.newContact.toolBarNew = new Composite(NewContact.getSShell(),SWT.NONE);
				GridLayout layout = new GridLayout();
				layout.horizontalSpacing = 0;
				layout.makeColumnsEqualWidth = false/*true*/;
				layout.marginBottom = 0;
				layout.marginHeight = 0;
				layout.marginLeft = 10;
				layout.marginRight = 0;
				layout.marginTop = 0;
				layout.marginWidth = 0;
				layout.numColumns = 6;
				layout.verticalSpacing = 0;
				layout.horizontalSpacing = 5;
				this.newContact.toolBarNew.setLayout(layout);

				// button1 = new Button(toolBar, SWT.NONE);
				// button1.setBounds(new
				// org.eclipse.swt.graphics.Rectangle(100,10,89,22));
				// button1.setText("Update");

				if (this.newContact.isAdmin()) {
					this.newContact.changePassphraseNextItem = new Button(
							this.newContact.toolBarNew, SWT.PUSH);
					this.newContact.changePassphraseNextItem
							.setText(NewContact.this.bundle
									.getString(NewContact.CHANGE_PASSPHRASE_NEXT_LOGIN));
					
					if(StringUtils.isNotBlank(this.newContact.getOrigContact().getLogin()) && !isContactLocked()) {
						this.newContact.lockContactItem = new Button(
								this.newContact.toolBarNew, SWT.PUSH);
						this.newContact.lockContactItem
								.setText("Lock Contact");
					} else if(StringUtils.isNotBlank(this.newContact.getOrigContact().getLogin()) && isContactLocked()) {
						this.newContact.unlockContactItem = new Button(
								this.newContact.toolBarNew, SWT.PUSH);
						this.newContact.unlockContactItem
								.setText("Unlock Contact");
					}
				}

				final String sphereId = (String) this.newContact.getSession().get(
						SessionConstants.SPHERE_ID2);
				if (this.newContact.isAdmin()
						|| this.newContact.getClient()
								.getVerifyAuth().isPersonal(
										sphereId,
										(String) this.newContact.getSession()
												.get(SessionConstants.USERNAME),
										(String) this.newContact.getSession()
												.get(SessionConstants.REAL_NAME))) {
					// if (false) {

					String contactLogin = contact.getLogin();
					if (!getClient()
							.getVerifyAuth().isSphereEnabledForMember(sphereId, contactLogin)) {

						this.newContact.entitleItem = new Button(
								this.newContact.toolBarNew, SWT.PUSH);

						this.newContact.entitleItem
								.setText(NewContact.this.bundle
										.getString(NewContact.ENTITLE_FOR_CURRENT_SPHERE));
					}

				} else {
					logger.warn("RESULT: :"
							+ getClient()
									.getVerifyAuth().isPersonal(
											sphereId,
											(String) this.newContact
													.getSession().get(
															SessionConstants.USERNAME),
											(String) this.newContact
													.getSession().get(
															SessionConstants.REAL_NAME)));
				}

				if (this.newContact.isAdmin()) {
					String contactLogin = contact.getLogin();
					if (getClient()
							.getVerifyAuth().isSphereEnabledForMember(sphereId, contactLogin)) {

						this.newContact.makeSphereCore = new Button(
								this.newContact.toolBarNew, SWT.PUSH);
						this.newContact.makeSphereCore
								.setText(NewContact.this.bundle
										.getString(NewContact.MAKE_LOGIN_SPHERE));
					}
				}

				this.newContact.inviteItem = new Button(
						this.newContact.toolBarNew, SWT.PUSH);
				this.newContact.inviteItem.setText(NewContact.this.bundle
						.getString(NewContact.INVITE));

				if (this.newContact.isCreator || this.newContact.isAdmin()
						|| this.newContact.isUserContact) {
					this.newContact.updateItem = new Button(
							this.newContact.toolBarNew, SWT.PUSH);
					this.newContact.updateItem.setText(NewContact.this.bundle
							.getString(NewContact.SAVE));
				}

				if (this.newContact.isAdmin() || this.newContact.isCreator) {
					this.newContact.recallItem = new Button(
							this.newContact.toolBarNew, SWT.PUSH);
					this.newContact.recallItem.setText(NewContact.this.bundle
							.getString(NewContact.DELETE));
				}

				// String cpath =
				// "//sphere/thread_types/contact/member[@contact_name=\""+(String)session.get("real_name")+"\"]/entitle";

				// if
				// (mP.client.verifyAuth.checkAuth(cpath,mP.getSphereDefinition()))
				// {

				// logger.info("already entitled");
				// }

				if (contact.getHomeSphere() != null) {
					if (contact.getHomeSphere().length() == 0) {
						// connectItem = new ToolItem(toolBar, SWT.PUSH);
						// connectItem.setText("Connect");
					}
				} else {
					this.newContact.getOrigDoc().getRootElement().addElement(
							"home_sphere");
				}

				// openSphereItem = new ToolItem(toolBar, SWT.PUSH);
				// openSphereItem.setText("Open Sphere");
				// addOpenSphereActionListener();

				this.newContact.toolBarNew
						.setBounds(new org.eclipse.swt.graphics.Rectangle(9,
								545, 573, 42));

				if (this.newContact.isAdmin() || this.newContact.isCreator) {
					addRecallActionListener();
				}
				if (this.newContact.isAdmin() || this.newContact.isCreator
						|| this.newContact.isUserContact) {
					addUpdateActionListener();
				}

				addInviteActionListener();

//				if (contact.getHomeSphere().length() == 0) {
//					// addConnectActionListener();
//				}
				if (this.newContact.isAdmin()
						|| getClient()
								.getVerifyAuth().isPersonal(
										sphereId,
										(String) this.newContact.getSession()
												.get(SessionConstants.USERNAME),
										(String) this.newContact.getSession()
												.get(SessionConstants.REAL_NAME))) {

					if (this.newContact.entitleItem != null) {
						addEntitleActionListener();
					}

				}
				if (this.newContact.isAdmin()) {
					addSphereCoreActionListener();
				}
				if (this.newContact.isAdmin()) {
					addChangePassphraseNextActionListener();
					addLockContactActionListener();
					addUnlockContactActionListener();
				}

				getSShell().layout();
			}
		});

	}

	public void runDisplay() {

		while (!sShell.isDisposed()) {
			try {
				if (!this.display.readAndDispatch()) {
					this.display.sleep();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	public void addEntitleActionListener() {

		this.entitleItem.addListener(SWT.Selection,
				new NewContactEntitleActionListener(this));

	}

	public void addSphereCoreActionListener() {
		if (this.makeSphereCore != null) {
			this.makeSphereCore.addListener(SWT.Selection,
					new NewContactMakeSphereCoreActionListener(this));
		}
	}

	public void addChangePassphraseNextActionListener() {
		this.changePassphraseNextItem.addListener(SWT.Selection,
				new NewContactChangePassphraseActionListener(this));
	}
	
	public void addLockContactActionListener() {
		if(this.lockContactItem==null) {
			return;
		}
		this.lockContactItem.addListener(SWT.Selection,
				new NewContactLockContactActionListener(this));
	}
	
	public void addUnlockContactActionListener() {
		if(this.unlockContactItem==null) {
			return;
		}
		this.unlockContactItem.addListener(SWT.Selection,
				new NewContactUnlockActionListener(this));
	}

	public void addOpenSphereActionListener() {
		this.openSphereItem.addListener(SWT.Selection,
				new NewContactOpenSphereActionListener(this));
	}

	public void addConnectActionListener() {
		this.connectItem.addListener(SWT.Selection,
				new NewContactConnectActionListener(this));

	}

	public void addInviteActionListener() {
		this.inviteItem.addListener(SWT.Selection,
				new NewContactInviteActionListener(this));
	}

	public void addUpdateActionListener() {
		this.updateItem.addListener(SWT.Selection,
				new NewContactUpdateActionListener(this));
	}

	public void addRecallActionListener() {
		this.recallItem.addListener(SWT.Selection,
				new NewContactRecallActionListener(this));
	}

	public static void main(String[] args) {

		fromMain = true;
		// Display display = new Display();

		NewContact contact = new NewContact();
		contact.createSShell();

	}

	public static Document XMLContactDoc(final String loginName, final String contactName, final String firstName, final String lastName) {

		final ContactStatement contact = new ContactStatement();
		contact.setConfirmed(true);
		contact.setVotingModelDesc("Absolute without qualification");
		contact.setVotingModelType("absolute");
		contact.setTallyNumber("0.0");
		contact.setTallyValue("0.0");
		contact.setLastName( lastName );
		contact.setFirstName( firstName );
		contact.setLogin( StringUtils.normalizeName(  loginName ) );
		contact.setEmailAddress("");
		contact.setWorkTelephone("");
		contact.setHomeTelephone("");
		contact.setMobile("");
		contact.setFax("");
		contact.setFaxSecond("");
		contact.setURL("");
		contact.setHomeSphere("");
		contact.setStreet("");
		contact.setStreetCont("");
		contact.setCity("");
		contact.setState("");
		contact.setZipCode("");
		contact.setCountry("");
		contact.setType("contact");
		contact.setThreadType("contact");
		contact.setSubject(firstName + " " + lastName);
		contact.setGiver(contactName);
		contact.setBody("");
		contact.setLastUpdatedBy(contactName);
		contact.setLocation("");
		contact.setTimeZone("");
		contact.setMiddleName("");
		contact.setNamePrefix("");
		contact.setNameSuffix("");

		return contact.getBindedDocument();

	}

	public Document XMLDoc() {
		
		if (!checkSpecificFields()) {
			UserMessageDialogCreator.warning("Contact Owner could be only one of existing or blank", "Filling fields error");
			return null;
		}

		final ContactStatement contact = new ContactStatement();
		contact.setConfirmed(true);
		contact.setVotingModelDesc("Absolute without qualification");
		contact.setVotingModelType("absolute");
		contact.setTallyNumber("0.0");
		contact.setTallyValue("0.0");
		contact.setLastName( this.textLastName.getText() );
		contact.setFirstName( this.textFirstName.getText() );
		contact.setLogin( StringUtils.normalizeName( this.textLogin.getText() ) );
		contact.setEmailAddress(this.textEmail.getText());
		contact.setSecondEmailAddress(this.textSecondEmail.getText());
		contact.setWorkTelephone(this.textVoice1.getText());
		contact.setHomeTelephone(this.textVoice2.getText());
		contact.setMobile(this.textMobile.getText());
		contact.setFax(this.textFax.getText());
		contact.setFaxSecond(this.textFaxSecond.getText());
		contact.setOrganization(this.textOrganization.getText());
		contact.setDepartment(this.textDepartment.getText());
		contact.setAccount(this.account.getText());
		contact.setOwnerContact(this.ownerContact.getText());
		contact.setURL(this.textURL.getText());
		contact.setHomeSphere(this.textHomeSphere.getText());
		contact.setStreet(this.textStreet.getText());
		contact.setStreetCont(this.textStreetCont.getText());
		contact.setCity(this.textCity.getText());
		contact.setState(this.textState.getText());
		contact.setZipCode(this.textZipCode.getText());
		contact.setCountry(this.textCountry.getText());
		contact.setTitle(this.textTitle.getText());
		contact.setSubject(this.textFirstName.getText() + " " + this.textLastName.getText());
		contact.setType("contact");
		contact.setThreadType("contact");
		contact.setGiver((String) this.session.get(SessionConstants.REAL_NAME));
		contact.setGiverUsername((String) this.session.get(SessionConstants.USERNAME));
		contact.setLastUpdatedBy((String) this.session.get(SessionConstants.REAL_NAME));
		contact.setBody(this.textArea.getText());
		contact.setLocation(this.textLocation.getText());
		contact.setTimeZone(this.timeZone.getText());
		contact.setMiddleName(this.textMiddleName.getText());
		contact.setNamePrefix(this.textNamePrefix.getText());
		contact.setNameSuffix(this.textNameSuffix.getText());
		
		contact.setRole(this.contactType.getText());

		return contact.getBindedDocument();

	}

	public void runEventLoop() {
		UiUtils.swtBeginInvoke(new Runnable() {
			private NewContact newContact = NewContact.this;

			public void run() {
				NewContact.getSShell().layout();
				NewContact.getSShell().open();

				while (!NewContact.getSShell().isDisposed()) {
					if (!this.newContact.display.readAndDispatch()) {
						this.newContact.display.sleep();
					}
				}
			}
		});
	}

	public void setTextArea(final String text) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				NewContact.this.textArea.setText(text);
			}
		});
	}

	public ISphereView getSphereViewOwner() {
		return this.sphereViewOwner;
	}

	public void setOrigDoc(Document origDoc) {
		this.origDoc = origDoc;
	}

	public Document getOrigDoc() {
		return this.origDoc;
	}
	
	public ContactStatement getOrigContact() {
		if(this.origDoc==null) {
			return null;
		}
		return ContactStatement.wrap(this.origDoc);
	}

	public Hashtable getSession() {
		return this.session;
	}

	public String getLogin() {
		return this.textLogin.getText();
	}

	/**
	 * @param string
	 */
	protected void setLogin(String value) {
		this.textLogin.setText(value);
	}

	public String getPassword() {
		return this.textPassword.getText();
	}

	public static org.eclipse.swt.widgets.Shell getSShell() {
		return sShell;
	}

	public Display getDisplay() {
		return this.display;
	}

	public boolean isAdmin() {
		return this.isAdmin;
	}
	
	public DialogsMainCli getClient(){
		return SupraSphereFrame.INSTANCE.client;
	}
	
	private void addControl(Label label, String text, boolean firstCollumn) {
		addControl(label, text,  firstCollumn ? LocationStyle.FIRST_COLUMN : LocationStyle.SECOND_COLUMN );
	}
	
	private void addControl(Label label, String text, LocationStyle controlStyle) {
		Point size = controlStyle.getSize();
		int left = controlStyle.getLeftOffset();
		int columnCount;
		if ( controlStyle.isFirstColumn() ) {
			columnCount = this.firstColLabelCount;
			++ this.firstColLabelCount;
		}
		else {
			columnCount = this.secColLabelCount; 
			++ this.secColLabelCount;
		}
		int top = controlStyle.getTopOffset( columnCount );   
		label.setSize(size);
		label.setText(text);
		label.setLocation(left, top);
	}
	
	private void addControl(Control text, boolean inFirstCol) {
		int left = inFirstCol ? firstTextCol : secTextCol;
		int top = inFirstCol ? 10+(this.firstColTextCount++)*26 : 10+(this.secColTextCount++)*26; 
		if(text instanceof Combo) {
			FontData[] data = SDisplay.display.get().getSystemFont().getFontData();
			if(data.length>0) {
				data[0].setHeight(8);
				text.setFont(new Font(SDisplay.display.get(), data[0]));
			}
		}
		text.setSize(TEXT_SIZE);
		text.setLocation(left, top);
	}

} // @jve:decl-index=0:visual-constraint="55,60"
