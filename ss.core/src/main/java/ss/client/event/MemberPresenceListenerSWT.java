package ss.client.event;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.event.createevents.CreateContactAction;
import ss.client.event.createevents.CreateEmailAction;
import ss.client.event.createevents.CreateFileAction;
import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.event.createevents.CreateMessageAction;
import ss.client.event.createevents.CreateRssAction;
import ss.client.event.createevents.CreateSphereAction;
import ss.client.event.createevents.CreateTerseAction;
import ss.client.ui.MessagesPane;
import ss.client.ui.SearchInputWindow;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.peoplelist.IPeopleList;
import ss.client.ui.peoplelist.IPeopleListOwner;
import ss.client.ui.peoplelist.IMemberSelection;
import ss.client.ui.peoplelist.SphereMember;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.client.ui.viewers.NewContact;
import ss.common.UiUtils;
import ss.domainmodel.ContactStatement;
import ss.util.SupraXMLConstants;

/**
 * @author roman
 *
 */
public class MemberPresenceListenerSWT implements MouseListener {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MemberPresenceListenerSWT.class);

	private final SupraSphereFrame sF;
	
	private final IPeopleListOwner owner;
	
	private final IMemberSelection peopleList;

	public MemberPresenceListenerSWT(final IPeopleListOwner owner, final IMemberSelection peopleList) {
		this.owner = owner;
		this.peopleList = peopleList;
		this.sF = SupraSphereFrame.INSTANCE;
	}

	public void mouseDoubleClick(MouseEvent arg0) {
		final SphereMember member = this.peopleList.getSelectedMember();
		if (member == null) {
			return;
		}
		openPersonalSphere(member);
	}

	public void mouseDown(MouseEvent arg0) {
		final SphereMember member = this.peopleList.getSelectedMember();
		if (member == null) {
			return;
		}
		logger.info("single click on the member" + member);
		final String messageId = member.getReplyMessageId();
		if (messageId != null) {
			this.owner.selectInPane(messageId);
		}
	}
	
	public void mouseUp(MouseEvent me) {
		if ((me.count == 1 && me.button==3)
				|| me.stateMask==SWT.CTRL) {
			showPeopleListMenu();
		}
	}

	private void showSearchInputWindow() {
		final MessagesPane mP = this.owner.getMessagesPane();
		final String memberName = this.peopleList.getSelectedMember().getName();

//		final String systemName = mP.client.getVerifyAuth()
//				.getSystemName(memberName);
//		
//		final MessagesPane rootMP = mP.sF.getRootMessagePane();
		new Thread(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Document doc = null;
				if (mP != null) {
					doc = mP.getSphereDefinition();
				}
				String apath = "//sphere/thread_types/*";
				Vector results = new Vector();
				Vector<String> assetTypes = new Vector<String>();
				if (doc != null) {
					try {
						java.util.List list = (ArrayList) doc
								.selectObject(apath);
						results = new Vector(list);
					} catch (ClassCastException cce) {
						results.add((Element) doc.selectObject(apath));
					}
					for (int j = 0; j < results.size(); j++) {
						Element elem = (Element) results.get(j);
						String enabled = elem.attributeValue("enabled");
						if (enabled != null) {
							if (enabled.equals("true")) {
								String type = elem.getName();
								char originalStart = type.charAt(0);
								char upperStart = Character
										.toUpperCase(originalStart);
								type = upperStart + type.substring(1);
								if (!assetTypes.contains(type)) {
									assetTypes.add(type);
								}
							}
						}
					}
					logger.info("Doc was NOT NOT NOT null!!!");
				} else {
					logger.info("Doc was null!!!!!");
					assetTypes.add(CreateTerseAction.TERSE_TITLE);
					assetTypes.add(CreateMessageAction.MESSAGE_TITLE);
					assetTypes
							.add(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL_BIG);
					assetTypes.add(CreateBookmarkAction.BOOKMARK_TITLE);
					assetTypes.add(CreateContactAction.CONTACT_TITLE);
					assetTypes.add(CreateKeywordsAction.KEYWORD_TITLE);
					assetTypes.add(CreateRssAction.RSS_TITLE);
					assetTypes.add(CreateFileAction.FILE_TITLE);
					// assetTypes.add("Filesystem");
					assetTypes.add(CreateSphereAction.SPHERE_TITLE);
					assetTypes.add(CreateEmailAction.EMAIL_TITLE);
				}
				Vector<String> oneSphere = new Vector<String>();
				oneSphere.add( MemberPresenceListenerSWT.this.owner.getClientProtocol().getVerifyAuth().getSystemName(memberName));
				SearchInputWindow siw = new SearchInputWindow(
						SupraSphereFrame.INSTANCE,	mP,	MemberPresenceListenerSWT.this.owner.getSession(),
						oneSphere, assetTypes, false);
				siw.layoutUIAndSetFocus();

			}
		}).start();
	}
	
	/**
	 * 
	 */
	private void showPeopleListMenu() {
		Menu menu = new Menu(SupraSphereFrame.INSTANCE.getShell());//this.owner.getMessagesPane()); 
		
		MenuItem searchItem = new MenuItem(menu, SWT.PUSH);
		searchItem.setText("Search");
		searchItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				showSearchInputWindow();
			}
		});
		
		MenuItem infoItem = new MenuItem(menu, SWT.PUSH);
		infoItem.setText("Contact Info");
		infoItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				showContactInfoWindow();
			}
		});
		
		menu.setVisible(true);
		
	}

	/**
	 * 
	 */
	protected void showContactInfoWindow() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				final MessagesPane mp = MemberPresenceListenerSWT.this.owner.getMessagesPane();
				final Hashtable session = MemberPresenceListenerSWT.this.owner.getSession();//mp.getRawSession();
				
				final String contactName = MemberPresenceListenerSWT.this.peopleList.getSelectedMember().getName();
				final String loginName = MemberPresenceListenerSWT.this.owner.getClientProtocol().getVerifyAuth().getLoginForContact(contactName);
				
				final NewContact newUI = new NewContact(
						new Hashtable(), mp);
				
				new Thread() {
					public void run() {
						Document doc = MemberPresenceListenerSWT.this.owner.getClientProtocol().getContactFromLogin(session, loginName);
						newUI.fillDoc(doc);
						newUI.createViewToolBar();
					}
				}.start();
				
				newUI.runEventLoop();
			}
		});
	}

	public void openPersonalSphere(final String memberName) {
		SphereOpenManager.INSTANCE.requestUser(memberName);
		//openPersonalSphere( memberName, isOnline );
	}
	
	private void openPersonalSphere(final SphereMember member) {
		SphereOpenManager.INSTANCE.requestUser(member.getName());
		//openPersonalSphere( member.getName(), member.isOnline() );
	}

	public void openPersonalSpherePerform(final String memberContactName) {
		
		IPeopleList list = this.sF.getRootTab().getPeopleTable();
		boolean isOnline = false;
		if ( list != null ) {
			SphereMember member = list.findMember(memberContactName);
			if ( member != null ) {
				isOnline = member.isOnline();
			}
		}
		
		final boolean sphereOpened = startConnection(memberContactName, isOnline);
		if (selectAlreadyOpenedPersonalSphere(memberContactName)) {
			return;
		}
		if (sphereOpened) {
			return;
		}
		final String systemName = this.owner.getClientProtocol().getVerifyAuth().getSystemName(memberContactName);
		final boolean enabled = this.owner.getClientProtocol().getVerifyAuth().getEnabled(memberContactName);
		if (systemName == null || enabled) {
			logger.error("skip sphere opening. System name " + systemName
					+ ", enabled " + enabled);
			return;
		}
		new Thread(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Hashtable newSession = (Hashtable) MemberPresenceListenerSWT.this.sF
						.getRegisteredSession(
								(String) MemberPresenceListenerSWT.this.owner
										.getSession().get("supra_sphere"),
								"DialogsMainCli").clone();
				newSession.put("sphere_id", systemName);
				logger.info("searchSphere ");
				MemberPresenceListenerSWT.this.owner.getClientProtocol().searchSphere(newSession,
						null, "false");
			}
		}).start();
	}

	private boolean selectAlreadyOpenedPersonalSphere(String memberContactName) {
		return this.sF.selectTabByTitle( memberContactName );
		
	}

	@SuppressWarnings("unchecked")
	private boolean startConnection(final String memberContactName, boolean isOnline ) {

		logger.warn("SphereMember.getName()" + memberContactName);

		final String loginName = this.owner.getClientProtocol().getVerifyAuth()
				.getLoginForContact(memberContactName);
		logger.warn("openPersonalSphere: " + loginName + " : " + memberContactName );

		final Document contactDoc = this.owner.getClientProtocol().getContactFromLogin(this.owner
				.getSession(), loginName);
		if (contactDoc == null) {
			return false;
		}
		final ContactStatement contact = ContactStatement.wrap(contactDoc);
		final String sphereURL = contact.getHomeSphere();
		if (sphereURL == null || sphereURL.length() == 0) {
			return false;
		}
		logger.warn("Startign connection again...");
		//final MessagesPane rootMessagePane = this.sF.getMainMessagesPane();
		if (sphereURL.equals((String) SupraSphereFrame.INSTANCE.client.getSession().get(
				"sphereURL"))) {
			return false;
		}
		final String systemName = this.owner.getClientProtocol().getVerifyAuth().getSystemName(memberContactName);
		if (!isOnline) {
			final String reciprocalLogin = contact.getReciprocalLogin();
			this.sF.startConnection(null, this.owner.getSession(), sphereURL,
					reciprocalLogin, systemName);
		} else {
			final String cname = contact
					.getContactNameByFirstAndLastNames();
			String system = this.owner.getClientProtocol().getVerifyAuth().getSystemName(
					cname);
			Hashtable newSession = (Hashtable) this.owner.getSession().clone();
			newSession.put("localSphereId", systemName);
			newSession.put("externalConnection", "true");
			this.owner.getClientProtocol().openAnotherSphere(newSession, system, null, false, false);
		}
		return true;
	}

}
