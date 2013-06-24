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
import ss.client.preferences.PreferencesController;
import ss.client.preferences.PreferencesUILoader;
import ss.client.preferences.PreferencesUILoader.OptionsTypes;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.forwarding.ForwardingRulesComposite;
import ss.util.ImagesPaths;


/**
 * @author zobo
 *
 */
public class SetPreferencesCommonShell {
	
	/**
	 * 
	 */
	private static final String CLOSE = "PREFERENCESCOMMONSHELL.CLOSE";

	private static final String SPHERES_PREFERENCES = "PREFERENCESCOMMONSHELL.SPHERES_PREFERENCES";

	private static final String GLOBAL_PREFERENCES = "PREFERENCESCOMMONSHELL.GLOBAL_PREFERENCES";
	
	private static final String PREFERENCES = "PREFERENCESCOMMONSHELL.PREFERENCES";
	
	private static final String EMAIL_FORWARDING = "PREFERENCESCOMMONSHELL.EMAIL_FORWARDING";
	
	private static final ResourceBundle bundle = ResourceBundle
		.getBundle(LocalizationLinks.CLIENT_UI_PREFERENCES_SETPREFERENCESCOMMONSHELL);
	
	private Shell shell;

	private SupraSphereFrame sF;

	private GlobalPreferencesSimpleUserComposite global;

	private SpheresPreferencesComposite spheres;

	private PreferencesController controller;
	
	private CTabFolder tabFolder;

	private ForwardingRulesComposite forwarding;

	private static Image globalImage = null;

	private static Image sphereImage = null;
	
	private static Image emailsForwardingImage = null;
	
	public static SetPreferencesCommonShell INSTANCE;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetPreferencesCommonShell.class);
	
	/**
	 * @param sf
	 * @param controller
	 */
	public SetPreferencesCommonShell(SupraSphereFrame sf, PreferencesController controller) {
		this(sf,controller,null);
	}
	
	/**
	 * @param sf
	 * @param controller
	 */
	public SetPreferencesCommonShell(SupraSphereFrame sf, PreferencesController controller, String sphereId) {
		super();
		if (INSTANCE != null){
			return;
		}
		INSTANCE = this;
		this.sF = sf;
		this.controller = controller;
		this.shell = new Shell(this.sF.getDisplay());
		this.shell.addDisposeListener(new DisposeListener(){

			public void widgetDisposed(DisposeEvent e) {
				INSTANCE = null;
				PreferencesUILoader.INSTANCE.destroyed(OptionsTypes.PREFERENCES);
			}
			
		});
		centerComponent(this.shell);
		this.shell.setText(bundle.getString(PREFERENCES));
		initIcons();
		createCommonGUI(this.shell);
		this.shell.open();
		makeVisible(sphereId);
		PreferencesUILoader.INSTANCE.finished(OptionsTypes.PREFERENCES);
	}
	
	/**
	 * @param sphereId
	 */
	private void makeVisible(String sphereId) {
		if (sphereId == null){
			this.tabFolder.setSelection(0);
		} else {
			this.tabFolder.setSelection(1);
			if (logger.isDebugEnabled()){
				logger.debug("Setting specific sphere selection for sphereId: " + sphereId);
			}
			this.spheres.setSelection(sphereId);
		}
	}

	/**
	 * 
	 */
	private void initIcons() {
		try {
			Image image = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.PREFERENCES_ICON).openStream());
			globalImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.PREFERENCES_ICON).openStream());
			sphereImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SPHERE).openStream());
			emailsForwardingImage = new Image(Display.getDefault(),
					ImagesPaths.openStream(ImagesPaths.EMAIL_FORWARDING_ICON));
			this.shell.setImage(image);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	private void centerComponent(Composite comp) {

		Monitor primary = this.sF.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();

		Rectangle rect = comp.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		comp.setLocation(x, y);
	}
	
	public void showSphere(String sphereId, boolean force) {
		if (force){
			makeVisible(sphereId);
			this.shell.forceFocus();
			this.shell.forceActive();
		} else {
			makeVisible(sphereId);
		}
	}
	
	/**
	 * @param shell
	 */
	private void createCommonGUI(Shell shell) {
		shell.setLayout(new GridLayout());
		
		this.tabFolder = new CTabFolder(shell, SWT.EMBEDDED);
		this.tabFolder.setLayout(new GridLayout());
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        this.tabFolder.setLayoutData(layoutData);
        
		CTabItem item = null;
		
		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(GLOBAL_PREFERENCES));
		item.setImage(globalImage);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        this.global = new GlobalPreferencesSimpleUserComposite(this.tabFolder, SWT.NONE, this.controller);
        this.global.setLayoutData(layoutData);
        item.setControl(this.global);
		
		item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(SPHERES_PREFERENCES));
		item.setImage(sphereImage);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        this.spheres = new SpheresPreferencesSimpleUserComposite(this.tabFolder, SWT.NONE, this.controller, this.sF);
        this.spheres.setLayoutData(layoutData);
        item.setControl(this.spheres);
        
        item = new CTabItem(this.tabFolder, SWT.CENTER);
		item.setText(bundle.getString(EMAIL_FORWARDING));
		item.setImage(emailsForwardingImage);
        layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        this.forwarding = new ForwardingRulesComposite(this.tabFolder, SWT.NONE, this.sF.client.getForwardingController(), this.controller, this.sF);
        this.forwarding.setLayoutData(layoutData);
        item.setControl(this.forwarding);
        
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
		layoutData.horizontalAlignment = SWT.END;
		layoutData.verticalAlignment = SWT.BEGINNING;
		Button buttonClose = new Button(buttons, SWT.PUSH);
		buttonClose.setText(bundle.getString(CLOSE));
		buttonClose.addSelectionListener(new SelectionListener(){

            public void widgetDefaultSelected(SelectionEvent arg0) {        
            }

            public void widgetSelected(SelectionEvent arg0) {
            	SetPreferencesCommonShell.this.shell.dispose();
            }
            
        });
		buttonClose.setLayoutData(layoutData);
	}
}
