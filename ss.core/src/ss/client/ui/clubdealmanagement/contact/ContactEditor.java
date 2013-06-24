/**
 * 
 */
package ss.client.ui.clubdealmanagement.contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.common.UiUtils;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SupraSphereMember;
import ss.util.ImagesPaths;
import ss.util.SessionConstants;

/**
 * @author zobo
 * 
 */
public class ContactEditor {
	private static final Point TEXT_SIZE = new Point(135, 17);

	private static final Point LABEL_SIZE = new Point(100, 17);

	private static final Point LABEL_WIDE_SIZE = new Point(117, 17);

	private static final int firstlabelCol = 15;

	private static final int seclabelCol = 274;

	private static final int firstTextCol = 115;

	private static final int secTextCol = 391;

	enum LocationStyle {
		FIRST_COLUMN(firstlabelCol, LABEL_SIZE, true, 0), SECOND_COLUMN(
				seclabelCol, LABEL_WIDE_SIZE, false, 0), LABEL_FOR_COMBO_IN_SECOND_COLUMN(
				seclabelCol, LABEL_WIDE_SIZE, false, 6);

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

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ContactEditor.class);

	private static boolean fromMain = false;

	private int firstColLabelCount = 0;

	private int secColLabelCount = 0;

	private int firstColTextCount = 0;

	private int secColTextCount = 0;

	private org.eclipse.swt.widgets.Shell sShell = null;

	private Text textFirstName = null;

	private Text textTitle = null;

	private Text textStreet = null;

	private Text textStreetCont = null;

	private Text textCity = null;

	private Text textState = null;

	private Text textZipCode = null;

	private Text textCountry = null;

	private Text textEmail = null;

	private Text textURL = null;

	private Text textArea = null;

	private Text textLastName = null;

	private Text textDepartment = null;

	private Text textMobile = null;

	private Text textVoice1 = null;

	private Text textVoice2 = null;

	private Text textFax = null;
	
	private Text textFaxSecond = null;

	private Text textLogin = null;

	private Text textPassword = null;

	private Text textOrganization = null;

	private Combo timeZone = null;

	private Text textLocation = null;

	private Text textNamePrefix = null;

	private Text textNameSuffix = null;

	private Text textMiddleName = null;

	private Composite toolBarNew;

	private Text textHomeSphere = null;

	private static ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_VIEWERS_NEWCONTACT);

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

	// private static final String
	// YOU_HAVE_NOT_SAVED_THIS_CONTACT_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW
	// =
	// "NEWCONTACT.YOU_HAVE_NOT_SAVED_THIS_CONTACT_ARE_YOU_SURE_YOU_WANT_TO_CLOSE_THIS_WINDOW";

	private static final String SAVE = "NEWCONTACT.SAVE";

	// private static final String CHANGE_PASSPHRASE_NEXT_LOGIN =
	// "NEWCONTACT.CHANGE_PASSPHRASE_NEXT_LOGIN";

	// private static final String ENTITLE_FOR_CURRENT_SPHERE =
	// "NEWCONTACT.ENTITLE_FOR_CURRENT_SPHERE";

	// private static final String MAKE_LOGIN_SPHERE =
	// "NEWCONTACT.MAKE_LOGIN_SPHERE";

	// private static final String INVITE = "NEWCONTACT.INVITE";

	// private static final String DELETE = "NEWCONTACT.DELETE";

	private static final String NAME_PREFIX = "NEWCONTACT.NAME_PREFIX";

	private static final String NAME_SUFFIX = "NEWCONTACT.NAME_SUFFIX";

	private static final String MIDDLE_NAME = "NEWCONTACT.MIDDLE_NAME";

	private static final String LOCATION = "NEWCONTACT.LOCATION";

	private static final String TIME_ZONE = "NEWCONTACT.TIME_ZONE";

	private final ContactStatement originalContact;

	private final String editorsContactName;

	private final DialogsMainCli client;
	
	private final String sphereId;
	
	private final List<ContactEditedListener> listeners = new ArrayList<ContactEditedListener>();

	public ContactEditor(final ContactStatement contact, final DialogsMainCli client, final String sphereId) {
		this.originalContact = contact;
		this.client = client;
		this.editorsContactName = this.client.getContact();
		this.sphereId = sphereId;
	}
	
	public boolean addListener( final ContactEditedListener listener ){
		if ( listener == null ) {
			logger.error("Listener is null");
			return false;
		}
		return this.listeners.add( listener );
	}
	
	public boolean removeListener( final ContactEditedListener listener ){
		if ( listener == null ) {
			logger.error("Listener is null");
			return false;
		}
		return this.listeners.remove( listener );
	}

	public void open() {
		Thread t = new Thread() {
			@Override
			public void run() {
				createSShell();
			}
		};
		if (fromMain) {
			UiUtils.swtInvoke(t);
		} else {
			UiUtils.swtBeginInvoke(t);
		}

	}

	/**
	 * This method initializes sShell
	 */
	public void createSShell() {

		this.sShell = new Shell(SWT.BORDER | SWT.SHELL_TRIM | SWT.LEFT_TO_RIGHT);
		Image im;
		try {
			im = new Image(Display.getDefault(), getClass().getResource(
					ImagesPaths.SUPRA_ICON).openStream());
			this.sShell.setImage(im);
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

		Label labelLocation = null;

		this.textFirstName = new Text(this.sShell, SWT.BORDER);
		labelFirstName = new Label(this.sShell, SWT.NONE);
		labelTitle = new Label(this.sShell, SWT.NONE);
		labelStreet = new Label(this.sShell, SWT.NONE);
		labelStreetCont = new Label(this.sShell, SWT.NONE);
		labelCity = new Label(this.sShell, SWT.NONE);
		labelState = new Label(this.sShell, SWT.NONE);
		labelZipCode = new Label(this.sShell, SWT.NONE);
		labelCountry = new Label(this.sShell, SWT.NONE);
		labelEmail = new Label(this.sShell, SWT.NONE);
		labelURL = new Label(this.sShell, SWT.NONE);

		this.textTitle = new Text(this.sShell, SWT.BORDER);
		this.textStreet = new Text(this.sShell, SWT.BORDER);
		this.textStreetCont = new Text(this.sShell, SWT.BORDER);
		this.textCity = new Text(this.sShell, SWT.BORDER);
		this.textState = new Text(this.sShell, SWT.BORDER);
		this.textZipCode = new Text(this.sShell, SWT.BORDER);
		this.textCountry = new Text(this.sShell, SWT.BORDER);
		this.textEmail = new Text(this.sShell, SWT.BORDER);
		this.textURL = new Text(this.sShell, SWT.BORDER);
		labelOriginalNote = new Label(this.sShell, SWT.NONE);

		this.textArea = new Text(this.sShell, SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		labelLastName = new Label(this.sShell, SWT.NONE);
		this.textLastName = new Text(this.sShell, SWT.BORDER
				| SWT.LEFT_TO_RIGHT);
		labelDepartment = new Label(this.sShell, SWT.NONE);
		labelVoice1 = new Label(this.sShell, SWT.NONE);
		labelVoice2 = new Label(this.sShell, SWT.NONE);
		labelMobile = new Label(this.sShell, SWT.NONE);
		labelFax = new Label(this.sShell, SWT.NONE);
		labelFaxSecond = new Label(this.sShell, SWT.NONE);
		labelLogin = new Label(this.sShell, SWT.NONE);
		labelPassphrase = new Label(this.sShell, SWT.NONE);
		this.textDepartment = new Text(this.sShell, SWT.BORDER);
		this.textMobile = new Text(this.sShell, SWT.BORDER);
		this.textVoice1 = new Text(this.sShell, SWT.BORDER);
		this.textVoice2 = new Text(this.sShell, SWT.BORDER);
		this.textFax = new Text(this.sShell, SWT.BORDER);
		this.textFaxSecond = new Text(this.sShell, SWT.BORDER);
		this.textLogin = new Text(this.sShell, SWT.BORDER);
		this.textPassword = new Text(this.sShell, SWT.BORDER);
		labelHomeSphere = new Label(this.sShell, SWT.NONE);
		this.textHomeSphere = new Text(this.sShell, SWT.BORDER);
		labelOrganization = new Label(this.sShell, SWT.NONE);
		this.textOrganization = new Text(this.sShell, SWT.BORDER);

		labelTimeZone = new Label(this.sShell, SWT.NONE);

		this.timeZone = new Combo(this.sShell, SWT.BORDER | SWT.READ_ONLY);

		labelNamePrefix = new Label(this.sShell, SWT.NONE);
		this.textNamePrefix = new Text(this.sShell, SWT.BORDER);

		labelNameSuffix = new Label(this.sShell, SWT.NONE);
		this.textNameSuffix = new Text(this.sShell, SWT.BORDER);

		labelLocation = new Label(this.sShell, SWT.BOTTOM);
		this.textLocation = new Text(this.sShell, SWT.BORDER);

		labelMiddleName = new Label(this.sShell, SWT.NONE);
		this.textMiddleName = new Text(this.sShell, SWT.BORDER);

		// createNewToolBar();
		// createNewToolBar();

		Control[] control = { this.textFirstName, this.textLastName,
				this.textTitle, this.textDepartment, this.textStreet,
				this.textStreetCont, this.textCity, this.textState,
				this.textZipCode, this.textCountry, this.textMobile,
				this.textVoice1, this.textVoice2, this.textFax, this.textFaxSecond, this.textEmail,
				this.textURL, this.textMiddleName, this.textLocation,
				this.timeZone, this.textNamePrefix, this.textNameSuffix,
				this.textArea };
		this.sShell.setTabList(control);

		this.sShell.setText(bundle.getString(NEW_CONTACT));

		addControl(labelFirstName, bundle.getString(FIRST_NAME), true);
		addControl(labelNamePrefix, bundle.getString(NAME_PREFIX), true);
		addControl(labelNameSuffix, bundle.getString(NAME_SUFFIX), true);
		addControl(labelTitle, bundle.getString(TITLE), true);
		addControl(labelStreet, bundle.getString(STREET), true);
		addControl(labelStreetCont, bundle.getString(STREET_CONT), true);
		addControl(labelCity, bundle.getString(CITY), true);
		addControl(labelState, bundle.getString(STATE), true);
		addControl(labelZipCode, bundle.getString(ZIP_CODE), true);
		addControl(labelCountry, bundle.getString(COUNTRY), true);
		addControl(labelEmail, bundle.getString(EMAIL), true);
		addControl(labelURL, bundle.getString(URL), true);
		addControl(labelHomeSphere, bundle.getString(HOME_SPHERE), true);

		addControl(labelLastName, bundle.getString(LAST_NAME), false);
		addControl(labelMiddleName, bundle.getString(MIDDLE_NAME), false);
		addControl(labelOrganization, bundle.getString(ORGANIZATION), false);
		addControl(labelDepartment, bundle.getString(DEPARTMENT), false);
		addControl(labelVoice1, bundle.getString(VOICE1), false);
		addControl(labelVoice2, bundle.getString(VOICE2), false);
		addControl(labelMobile, bundle.getString(MOBILE), false);
		addControl(labelFax, bundle.getString(FAX), false);
		addControl(labelFaxSecond, bundle.getString(FAXSECOND), false);
		addControl(labelLogin, bundle.getString(LOGIN), false);
		addControl(labelPassphrase, bundle.getString(PASSPHRASE), false);
		addControl(labelLocation, bundle.getString(LOCATION), false);
		addControl(labelTimeZone, bundle.getString(TIME_ZONE),
				LocationStyle.LABEL_FOR_COMBO_IN_SECOND_COLUMN);

		addControl(this.textFirstName, true);
		addControl(this.textNamePrefix, true);
		addControl(this.textNameSuffix, true);
		addControl(this.textTitle, true);
		addControl(this.textStreet, true);
		addControl(this.textStreetCont, true);
		addControl(this.textCity, true);
		addControl(this.textState, true);
		addControl(this.textZipCode, true);
		addControl(this.textCountry, true);
		addControl(this.textEmail, true);
		addControl(this.textURL, true);
		addControl(this.textHomeSphere, true);

		addControl(this.textLastName, false);
		addControl(this.textMiddleName, false);
		addControl(this.textOrganization, false);
		addControl(this.textVoice1, false);
		addControl(this.textVoice2, false);
		addControl(this.textMobile, false);
		addControl(this.textDepartment, false);
		addControl(this.textFax, false);
		addControl(this.textFaxSecond, false);
		addControl(this.textLogin, false);
		addControl(this.textPassword, false);
		this.textPassword.setEchoChar('*');
		addControl(this.textLocation, false);
		addControl(this.timeZone, false);
		fillTimeZoneCombo();

		labelOriginalNote.setBounds(new org.eclipse.swt.graphics.Rectangle(13,
				362, 100, 16));
		labelOriginalNote.setText(bundle.getString(ORIGINAL_NOTE));
		this.textArea.setBounds(new org.eclipse.swt.graphics.Rectangle(12, 386,
				575, 111));

		this.textFirstName.setEnabled(false);
		this.textLastName.setEnabled(false);
		this.textLogin.setEnabled(false);

		this.sShell.setSize(new org.eclipse.swt.graphics.Point(622, 585));

		Rectangle parentBounds = SupraSphereFrame.INSTANCE.getShell()
				.getBounds();
		Rectangle childBounds = this.sShell.getBounds();
		int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
		int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
		this.sShell.setLocation(x, y);

		createNewToolBar();
		fillDoc(ContactEditor.this.originalContact);

		getSShell().layout();
		getSShell().open();
	}

	private void fillTimeZoneCombo() {
		this.timeZone.add("UTC-12");
		this.timeZone.add("UTC-11");
		this.timeZone.add("UTC-10");
		this.timeZone.add("UTC-9:30");
		this.timeZone.add("UTC-9");
		this.timeZone.add("UTC-8");
		this.timeZone.add("UTC-7");
		this.timeZone.add("UTC-6");
		this.timeZone.add("UTC-5");
		this.timeZone.add("UTC-4:30");
		this.timeZone.add("UTC-4");
		this.timeZone.add("UTC-3:30");
		this.timeZone.add("UTC-3");
		this.timeZone.add("UTC-2");
		this.timeZone.add("UTC-1");
		this.timeZone.add("UTC");
		this.timeZone.add("UTC+1");
		this.timeZone.add("UTC+2");
		this.timeZone.add("UTC+3");
		this.timeZone.add("UTC+3:30");
		this.timeZone.add("UTC+4");
		this.timeZone.add("UTC+4:30");
		this.timeZone.add("UTC+5");
		this.timeZone.add("UTC+5:30");
		this.timeZone.add("UTC+5:45");
		this.timeZone.add("UTC+6");
		this.timeZone.add("UTC+6:30");
		this.timeZone.add("UTC+7");
		this.timeZone.add("UTC+8");
		this.timeZone.add("UTC+8:45");
		this.timeZone.add("UTC+9");
		this.timeZone.add("UTC+9:30");
		this.timeZone.add("UTC+10");
		this.timeZone.add("UTC+10:30");
		this.timeZone.add("UTC+11");
		this.timeZone.add("UTC+11:30");
		this.timeZone.add("UTC+12");
		this.timeZone.add("UTC+12:45");
		this.timeZone.add("UTC+13");
		this.timeZone.add("UTC+14");
		this.timeZone.select(this.timeZone.indexOf("UTC"));
	}

	private void fillDoc(final ContactStatement contact) {

		try {
			this.textFirstName.setText(notNull(contact.getFirstName()));
			this.textLastName.setText(notNull(contact.getLastName()));
			this.textTitle.setText(notNull(contact.getMessageTitle()));
			this.textDepartment.setText(notNull(contact.getDepartment()));
			this.textOrganization.setText(notNull(contact.getOrganization()));
			this.textLogin.setText(notNull(contact.getLogin()));

			this.textEmail.setText(notNull(contact.getEmailAddress()));
			this.textVoice1.setText(notNull(contact.getWorkTelephone()));

			this.textVoice2.setText(notNull(contact.getHomeTelephone()));
			this.textMobile.setText(notNull(contact.getMobile()));

			this.textFax.setText(notNull(contact.getFax()));
			this.textFaxSecond.setText(notNull(contact.getFaxSecond()));

			this.textURL.setText(notNull(contact.getURL()));

			this.textStreet.setText(notNull(contact.getStreet()));
			this.textStreetCont.setText(notNull(contact.getStreetCont()));

			this.textCity.setText(notNull(contact.getCity()));
			this.textState.setText(notNull(contact.getState()));

			this.textZipCode.setText(notNull(contact.getZipCode()));

			this.textCountry.setText(notNull(contact.getCountry()));

			this.textHomeSphere.setText(notNull(contact.getHomeSphere()));
			this.textNamePrefix.setText(notNull(contact.getNamePrefix()));
			this.textNameSuffix.setText(notNull(contact.getNameSuffix()));
			this.textMiddleName.setText(notNull(contact.getMiddleName()));
			this.textLocation.setText(notNull(contact.getLocation()));
			this.timeZone.select(this.timeZone.indexOf(contact.getTimeZone()));

		} catch (Exception ex) {
			logger.error("Error in filling contact", ex);
		}
	}
	
	private String notNull( final String str ){
		return ( (str != null) ? str : "" );
	}

	private void createNewToolBar() {
		this.toolBarNew = new Composite(getSShell(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.makeColumnsEqualWidth = false/* true */;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 10;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.numColumns = 5;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 5;
		this.toolBarNew.setLayout(layout);

		final Button pubItem = new Button(this.toolBarNew, SWT.PUSH);
		pubItem.setText(bundle.getString(ContactEditor.SAVE));
		pubItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			public void widgetSelected(SelectionEvent e) {
				save();
			}

		});
		final Button cancel = new Button(this.toolBarNew, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				close();
			}

		});

		this.toolBarNew.setBounds(new org.eclipse.swt.graphics.Rectangle(9,
				503, 573, 42));
	}

	private void close() {
		this.sShell.close();
	}

	private void save() {
		UpdateContact(this.originalContact);
		saveContact();
		close();
		for ( ContactEditedListener listener : this.listeners ) {
			listener.contactEdited( this.originalContact );
		}
	}

	@SuppressWarnings("unchecked")
	private void saveContact() {
		final Hashtable hash = (Hashtable) this.client.session.clone();
		hash.put( SessionConstants.SPHERE_ID2, this.sphereId );
		this.client.replaceDoc(hash, this.originalContact.getBindedDocument() );
	}

	private void UpdateContact(final ContactStatement contact) {

		// contact.setLastName(this.textLastName.getText());
		// contact.setFirstName(this.textFirstName.getText());
		// contact.setLogin(this.textLogin.getText());
		contact.setEmailAddress(this.textEmail.getText());
		contact.setWorkTelephone(this.textVoice1.getText());
		contact.setHomeTelephone(this.textVoice2.getText());
		contact.setMobile(this.textMobile.getText());
		contact.setFax(this.textFax.getText());
		contact.setFaxSecond(this.textFaxSecond.getText());
		contact.setOrganization(this.textOrganization.getText());
		contact.setDepartment(this.textDepartment.getText());
		contact.setURL(this.textURL.getText());
		contact.setHomeSphere(this.textHomeSphere.getText());
		contact.setStreet(this.textStreet.getText());
		contact.setStreetCont(this.textStreetCont.getText());
		contact.setCity(this.textCity.getText());
		contact.setState(this.textState.getText());
		contact.setZipCode(this.textZipCode.getText());
		contact.setCountry(this.textCountry.getText());
		contact.setTitle(this.textTitle.getText());
		// contact.setSubject(this.textFirstName.getText() + " " +
		// this.textLastName.getText());
		// contact.setType("contact");
		// contact.setThreadType("contact");
		// contact.setGiver((String)
		// this.session.get(SessionConstants.REAL_NAME));
		// contact.setGiverUsername((String)
		// this.session.get(SessionConstants.USERNAME));
		contact.setLastUpdatedBy(this.editorsContactName);
		contact.setBody(this.textArea.getText());
		contact.setOrigBody(this.textArea.getText());
		contact.setLocation(this.textLocation.getText());
		contact.setTimeZone(this.timeZone.getText());
		contact.setMiddleName(this.textMiddleName.getText());
		contact.setNamePrefix(this.textNamePrefix.getText());
		contact.setNameSuffix(this.textNameSuffix.getText());
	}

	private org.eclipse.swt.widgets.Shell getSShell() {
		return this.sShell;
	}

	private void addControl(Label label, String text, boolean firstCollumn) {
		addControl(label, text, firstCollumn ? LocationStyle.FIRST_COLUMN
				: LocationStyle.SECOND_COLUMN);
	}

	private void addControl(Label label, String text, LocationStyle controlStyle) {
		Point size = controlStyle.getSize();
		int left = controlStyle.getLeftOffset();
		int columnCount;
		if (controlStyle.isFirstColumn()) {
			columnCount = this.firstColLabelCount;
			++this.firstColLabelCount;
		} else {
			columnCount = this.secColLabelCount;
			++this.secColLabelCount;
		}
		int top = controlStyle.getTopOffset(columnCount);
		label.setSize(size);
		label.setText(text);
		label.setLocation(left, top);
	}

	private void addControl(Control text, boolean inFirstCol) {
		int left = inFirstCol ? firstTextCol : secTextCol;
		int top = inFirstCol ? 10 + (this.firstColTextCount++) * 26
				: 10 + (this.secColTextCount++) * 26;
		if (text instanceof Combo) {
			FontData[] data = SDisplay.display.get().getSystemFont()
					.getFontData();
			if (data.length > 0) {
				data[0].setHeight(8);
				text.setFont(new Font(SDisplay.display.get(), data[0]));
			}
		}
		text.setSize(TEXT_SIZE);
		text.setLocation(left, top);
	}

	/**
	 * @return
	 */
	public static boolean isOwnContact( final ContactStatement contact, final DialogsMainCli client ) {
		try {
			final String ownContactName = client.getContact();
			final String contactName = contact.getContactNameByFirstAndLastNames();
			if ( ownContactName.equals( contactName ) ) {
				return true;
			}
			for (SupraSphereMember member : client.getVerifyAuth().getAllMembers()) {
				String name = member.getContactName();
				if ( contactName.equals(name) ) {
					return false;
				}
			}
			if ( ownContactName.equals( contact.getGiver() ) ){
				return true;
			}
		} catch ( Exception ex ) {
			logger.error("Error in detecting if can modify contact",ex);
		}
		return false;
	}
}
