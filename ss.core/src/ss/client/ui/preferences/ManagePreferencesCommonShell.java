/**
 * 
 */
package ss.client.ui.preferences;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.preferences.ForwardingController;
import ss.client.preferences.PreferencesAdminController;
import ss.client.preferences.PreferencesUILoader;
import ss.client.preferences.PreferencesUILoader.OptionsTypes;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.admin.EmailAliasesManageComposite;
import ss.client.ui.preferences.changesdetector.IChangesDetector;
import ss.client.ui.preferences.delivery.CommonDeliveryComposite;
import ss.client.ui.preferences.forwarding.SphereForwardingSubComposite;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.spheremanagement.memberaccess.IChangable;
import ss.client.ui.spheremanagement.memberaccess.ManageAccessComposite;
import ss.util.ImagesPaths;

/**
 * @author zobo
 * 
 */
public class ManagePreferencesCommonShell {

	private static final String CLOSE = "MANAGEPREFERENCESCOMMONSHELL.CLOSE";

	private static final String EMAIL_ALIASES = "MANAGEPREFERENCESCOMMONSHELL.EMAIL_ALIASES";

//	private static final String SPHERES_MEMBERS_ACCESS = "MANAGEPREFERENCESCOMMONSHELL.SPHERES_MEMBERS_ACCESS";

	private static final String ADMINISTRATE = "MANAGEPREFERENCESCOMMONSHELL.ADMINISTRATE";

	private static final String USERS_PREFERENCES = "MANAGEPREFERENCESCOMMONSHELL.USERS_PREFERENCES";
	
	private static final String WORKFLOW_PREFERENCES = "MANAGEPREFERENCESCOMMONSHELL.WORKFLOW_PREFERENCES";
	
	private static final String SPHERES_PREFERENCES = "MANAGEPREFERENCESCOMMONSHELL.SPHERES_PREFERENCES";
	
	private static final String EMAIL_FORWARDING_RULES = "MANAGEPREFERENCESCOMMONSHELL.EMAIL_FORWARDING_RULES";
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_MANAGEPREFERENCESCOMMONSHELL);

	private static Image usersImage = null;
	
	private static Image membersSphereAccessImage = null;
	
	private static Image workflowImage = null;
	
	private static Image emailsManageImage = null;
	
	private static Image sphereImage = null;
	
	private static Image emailsForwardingImage = null;
	
	private Shell shell;

	private SupraSphereFrame sF;
	
	private CTabFolder tabFolder;

	private SpheresPreferencesManagerComposite spheres;

	private PreferencesAdminController controller;
	
//	private SphereAccessComposite sphereMemberManagement;
	
	private UsersPreferencesComposite users;
		
	private CommonDeliveryComposite deliveryComposite;

	private EmailAliasesManageComposite emailAliasesManage;

	private ForwardingController forwardingController;

	//private ForwardingRulesAdminComposite forwardingComposite;
	
	private SphereForwardingSubComposite forwardingComposite;
	
	public static ManagePreferencesCommonShell INSTANCE;
	
	private CTabItem previousItem = null;
	
	private IChangesDetector currentDetector = null;

	private CTabItem nextItem = null;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ManagePreferencesCommonShell.class);

	/**
	 * @param sf
	 * @param controller
	 */
	public ManagePreferencesCommonShell(SupraSphereFrame sf,
			PreferencesAdminController controller) {
		super();
		if (INSTANCE != null){
			return;
		}
		INSTANCE = this;
		this.sF = sf;
		this.controller = controller;
		this.forwardingController = this.sF.client.getForwardingController();
		this.shell = new Shell(this.sF.getDisplay());

		centerComponent(this.shell);
		this.shell.setText(bundle.getString(ADMINISTRATE));
		this.shell.setSize(840, 500);
		initIcons();
		createCommonGUI(this.shell);
		this.shell.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				INSTANCE = null;
				PreferencesUILoader.INSTANCE.destroyed(OptionsTypes.ADMINISTRATE);
			}
		});
		this.shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent se) {
				se.doit = false;
				if(getDetector().hasChanges()) {
					getDetector().setIsLocalTransit(false);
					getDetector().showDisposingDialog(getCurrentChangable());
					//se.widget.dispose();
					return;
				}
				se.widget.dispose();
			}
		});
		this.shell.open();
		makeVisible(null);
		PreferencesUILoader.INSTANCE.finished(OptionsTypes.ADMINISTRATE);
	}
	
	/**
	 * @return
	 */
	protected IChangable getCurrentChangable() {
		return (IChangable)this.tabFolder.getSelection().getControl();
	}

	private void makeVisible(String sphereId) {
		if (sphereId == null){
			this.tabFolder.setSelection(0);
		} else {
			this.tabFolder.setSelection(1);
			this.spheres.setSelection(sphereId);
		}
		setPreviousItem(this.tabFolder.getSelection());
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("selection detector: "+((IChangable)this.tabFolder.getSelection().getControl()).getDetector());
			}
			setDetector(((IChangable)this.tabFolder.getSelection().getControl()).getDetector());
		} catch(Exception ex) {
			logger.error("can't setup detector");
		}
	}
	
	private void centerComponent(Composite comp) {

		Monitor primary = this.sF.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();

		Rectangle rect = comp.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 4;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		comp.setLocation(x, y);
	}
	
	protected void initIcons() {
		try {
			Image image = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.ADMINISTRATE).openStream());
			sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
			usersImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.PREFERENCES_USERS_ICON).openStream());
			membersSphereAccessImage = new Image(Display.getDefault(),
					ImagesPaths.openStream(ImagesPaths.MEMBER_SPHERE_ACCESS_ICON));
			workflowImage = new Image(Display.getDefault(),
					ImagesPaths.openStream(ImagesPaths.WORKFLOW_ICON));
			emailsManageImage = new Image(Display.getDefault(),
					ImagesPaths.openStream(ImagesPaths.EMAIL_MANAGE_ICON));
			emailsForwardingImage = new Image(Display.getDefault(),
					ImagesPaths.openStream(ImagesPaths.EMAIL_FORWARDING_ICON));
			this.shell.setImage(image);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	protected void createCommonGUI(Shell shell) {
		shell.setLayout(new GridLayout());

		this.tabFolder = new CTabFolder(shell, SWT.EMBEDDED);
		this.tabFolder.setLayout(new GridLayout());
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		this.tabFolder.setLayoutData(layoutData);
		this.tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent se) {
				CTabItem nextItem = (CTabItem)se.item; 
				setNextItem(nextItem);
				chackHasChanges(nextItem);
			}
		});

		CTabItem item = null;

		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(SPHERES_PREFERENCES));
		item.setImage(sphereImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		if (logger.isDebugEnabled()){
			logger.debug("SpheresPreferencesManagerComposite creating started");
		}
		this.spheres = new SpheresPreferencesManagerComposite(this);
		this.spheres.setLayoutData(layoutData);
		item.setControl(this.spheres);

		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(USERS_PREFERENCES));
		item.setImage(usersImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		if (logger.isDebugEnabled()){
			logger.debug("UsersPreferencesComposite creating started");
		}
		this.users = new UsersPreferencesComposite(this);
		this.users.setLayoutData(layoutData);
		item.setControl(this.users);
		
		if (logger.isDebugEnabled()){
			logger.debug("CommonDeliveryComposite creating started");
		}
		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(WORKFLOW_PREFERENCES));
		item.setImage(workflowImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		if (logger.isDebugEnabled()){
			logger.debug("CommonDeliveryComposite creating started");
		}
		this.deliveryComposite = new CommonDeliveryComposite(this);
		item.setControl(this.deliveryComposite);
				
		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText("Member access");
		item.setImage(membersSphereAccessImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		Composite mangeAccess = new ManageAccessComposite(this);
		mangeAccess.setLayoutData( LayoutUtils.createFullFillGridData() );
		item.setControl(mangeAccess);
		
		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(EMAIL_ALIASES));
		item.setImage(emailsManageImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		if (logger.isDebugEnabled()){
			logger.debug("EmailAliasesManageComposite creating started");
		}
		this.emailAliasesManage = new EmailAliasesManageComposite( this );
		this.emailAliasesManage.setLayoutData(layoutData);
		item.setControl(this.emailAliasesManage);

		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(EMAIL_FORWARDING_RULES));
		item.setImage(emailsForwardingImage);
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;
		if (logger.isDebugEnabled()){
			logger.debug("ForwardingRulesAdminComposite creating started");
		}
		this.forwardingComposite = new SphereForwardingSubComposite( this );
		this.forwardingComposite.setLayoutData(layoutData);
		item.setControl(this.forwardingComposite);

		Composite buttons = new Composite(shell, SWT.NONE);

		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = false;
		layoutData.verticalAlignment = GridData.BEGINNING;
		layoutData.horizontalAlignment = GridData.FILL;
		buttons.setLayoutData(layoutData);
		
		buttons.setLayout(new GridLayout());
		
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = SWT.RIGHT;
		layoutData.verticalAlignment = SWT.BEGINNING;
		Button buttonClose = new Button(buttons, SWT.PUSH);
		buttonClose.setText(bundle.getString(CLOSE));
		buttonClose.addSelectionListener(new SelectionListener(){
            public void widgetDefaultSelected(SelectionEvent arg0) {        
            }
            public void widgetSelected(SelectionEvent arg0) {
            	ManagePreferencesCommonShell.this.shell.dispose();
            }
        });
		buttonClose.setLayoutData(layoutData);
	}

	/**
	 * @param item
	 */
	protected void setNextItem(CTabItem item) {
		this.nextItem = item;
	}
	
	protected CTabItem getNextItem() {
		return this.nextItem;
	}

	public void setFocus() {
		this.shell.forceFocus();
	}

	/**
	 * @return the previousItem
	 */
	CTabItem getPreviousItem() {
		return this.previousItem;
	}

	/**
	 * @param previousItem the previousItem to set
	 */
	void setPreviousItem(CTabItem previousItem) {
		this.previousItem = previousItem;
	}
	
	private IChangesDetector getDetector() {
		return this.currentDetector;
	}
	
	private void setDetector(IChangesDetector detector) {
		this.currentDetector = detector;
	}

	/**
	 * @return
	 */
	public CTabFolder getTabFolder() {
		return this.tabFolder;
	}

	/**
	 * @return
	 */
	public PreferencesAdminController getController() {
		return this.controller;
	}

	/**
	 * 
	 */
	public void jumpToNextItem() {
		if(getNextItem()!=null) {
			this.tabFolder.setSelection(getNextItem());
		}
		
		try {
			setDetector(((IChangable)this.tabFolder.getSelection().getControl()).getDetector());
		} catch(Exception ex) {
			logger.error("can't setup not null detector");
			setDetector(null);
		}
	}
	
	public ForwardingController getForwardingController() {
		return this.forwardingController;
	}

	private void chackHasChanges(CTabItem nextItem) {
		if(getPreviousItem() != null && getDetector()!=null && getDetector().hasChanges()) {
			ManagePreferencesCommonShell.this.tabFolder.setSelection(getPreviousItem());
			getDetector().setIsLocalTransit(false);
			getDetector().showDialog((IChangable)getPreviousItem().getControl());
		} else {
			setPreviousItem(nextItem);
			try {
				setDetector(((IChangable)nextItem.getControl()).getDetector());
			} catch (Exception ex) {
				logger.error("has not changes detector");
				setDetector(null);
			}
		}
	}
}
