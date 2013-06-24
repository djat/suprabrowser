/**
 * 
 */
package ss.client.ui;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.event.MessagesTreeMouseListenerSWT;
import ss.client.event.supramenu.listeners.AboutMenuListener;
import ss.client.event.supramenu.listeners.AddCurrentSphereSelectionListener;
import ss.client.event.supramenu.listeners.AdministrateMenuListener;
import ss.client.event.supramenu.listeners.CloseAllTabsSelectionListener;
import ss.client.event.supramenu.listeners.ClubdealSelectionListener;
import ss.client.event.supramenu.listeners.EmailBoxItemSelectionListener;
import ss.client.event.supramenu.listeners.FavouriteItemListener;
import ss.client.event.supramenu.listeners.HelpMenuListener;
import ss.client.event.supramenu.listeners.OpenBlankSelectionListener;
import ss.client.event.supramenu.listeners.PreferencesMenuListener;
import ss.client.event.supramenu.listeners.RemoveSphereSelectionListener;
import ss.client.event.supramenu.listeners.SaveGlobalMarkSelectionListener;
import ss.client.event.supramenu.listeners.SaveOrderSelectionListener;
import ss.client.event.supramenu.listeners.SavePositionSelectionListener;
import ss.client.event.supramenu.listeners.SupraSearchSelectionListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.tree.MessagesTreeMenuCreator;
import ss.common.UiUtils;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;
import ss.util.ImagesPaths;

/**
 * @author roman
 *
 */
public class SupraMenuBar {
	
	private final Menu menuBar;
	private final SupraSphereFrame sF;
	private MenuItem emailBoxItem;
	private MenuItem addItem;
	private MenuItem removeItem;
	private Menu dropDownSphere;
	private MenuItem sphereItem;
	private Vector<MenuItem> favourites;
	private Vector<String> favIds;
	private boolean filled = false;
	private Menu dropDownOptions;
	private Menu dropDownAsset;
	private MenuItem preferencesAdminItem;
	
	private static Image sphereImage = null;
	private static Image emailBoxImage = null;
	private static Image removeImage = null;
	private static Image preferencesImage = null;
	private static Image administrateImage = null;
	private static Image supraSearchImage = null;
	private static Image setGlobalImage = null;
	private static Image saveOrderImage = null;
	private static Image savePositionImage = null;
	private static Image closeAllImage = null;
	private static Image aboutImage = null;
	private static Image addToFavouritesImage = null;
	private static Image helpImage = null;
	private static Image contactAdministrateImage = null;
	
	private static final String SPHERE = "SUPRAMENUBAR.SPHERE";
	private static final String OPTIONS = "SUPRAMENUBAR.OPTIONS";
	private static final String ASSET = "SUPRAMENUBAR.ASSET";
	private static final String EMAIL_BOX = "SUPRAMENUBAR.EMAIL_BOX";
	private static final String ADD_CURRENT = "SUPRAMENUBAR.ADD_CURRENT";
	private static final String REMOVE_CURRENT = "SUPRAMENUBAR.REMOVE_CURRENT";
	private static final String HELP = "SUPRAMENUBAR.HELP";
	private static final String ABOUT_SUPRASPHERE = "SUPRAMENUBAR.ABOUT_SUPRASPHERE";
	private static final String COMMAND = "SUPRAMENUBAR.COMMAND";
	private static final String SUPRASEARCH = "SUPRAMENUBAR.SUPRASEARCH";
	private static final String CLOSE_ALL = "SUPRAMENUBAR.CLOSE_ALL_TABS";
	private static final String SAVE_POSITION = "SUPRAMENUBAR.SAVE_POSITION";
	private static final String SAVE_ORDER = "SUPRAMENUBAR.SAVE_ORDER";
	private static final String SET_GLOBAL = "SUPRAMENUBAR.SET_GLOBAL_MARK";
	private static final String PREFERENCES = "SUPRAMENUBAR.PREFERENCES";
	private static final String ADMINISTRATE = "SUPRAMENUBAR.ADMINISTRATE";
	private static final String OPEN_BLANK_TAB = "SUPRAMENUBAR.OPEN_BLANK_TAB";
//	private static final String SPHERE_HIERARCHY = "SUPRAMENUBAR.SPHERE_HIERARCHY";
	
	private static final Logger logger = SSLogger.getLogger(SupraMenuBar.class);
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_SUPRA_MENU_BAR);
	
	public SupraMenuBar(Menu menu, SupraSphereFrame sF) {
		this.menuBar = menu;
		enableWholeMenu(false);
		this.sF = sF;
		this.favourites = new Vector<MenuItem>();
		this.favIds = new Vector<String>();

		try{
			sphereImage = new Image(Display.getDefault(), getClass()
				.getResource(ImagesPaths.SPHERE).openStream());
			emailBoxImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.MENU_ICON_EMAIL_BOX).openStream());
			removeImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.MENU_ICON_DELETE_FAVORITE_SPHERE).openStream());
			preferencesImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.PREFERENCES_ICON).openStream());
			administrateImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.ADMINISTRATE).openStream());
			supraSearchImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRASEARCH).openStream());
			closeAllImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.CLOSE_ALL_TABS).openStream());
			setGlobalImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SET_GLOBAL_MARK).openStream());
			saveOrderImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SAVE_TAB_ORDER).openStream());
			savePositionImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SAVE_WINDOW_POSITIONS).openStream());
			aboutImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.ABOUT_TITLE_IMAGE).openStream());
			helpImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.HELP_ICON).openStream());
			contactAdministrateImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.CONTACT).openStream());
			//addToFavouritesImage = new Image(Display.getDefault(), getClass()
			//		.getResource(ImagesPaths.ADD_TO_FAVOURITES).openStream());
			
		} catch (IOException ex) {
			logger.error("Image load failed", ex);
		}
		
		createSphereMenuItem();
		createAssetMenuItem();
		createOptionsMenuItem();
		createCommandMenuItem();
		createHelpMenuItem();
	}
	
	private void createSphereMenuItem() {
		this.sphereItem = new MenuItem(this.menuBar, SWT.CASCADE);
		this.sphereItem.setText(bundle.getString(SPHERE));

		this.dropDownSphere = new Menu(this.sphereItem);
		this.sphereItem.setMenu(this.dropDownSphere);

		this.addItem = new MenuItem(this.dropDownSphere, SWT.PUSH);
		this.addItem.setText(bundle.getString(ADD_CURRENT));
		this.addItem.setImage(addToFavouritesImage);
		this.addItem.addSelectionListener(new AddCurrentSphereSelectionListener(this.sF));

		this.removeItem = new MenuItem(this.dropDownSphere, SWT.PUSH);
		this.removeItem.setImage(removeImage);
		this.removeItem.setText(bundle.getString(REMOVE_CURRENT));
		this.removeItem.addSelectionListener(new RemoveSphereSelectionListener(this.sF));

		new MenuItem(this.dropDownSphere, SWT.SEPARATOR);

		this.emailBoxItem = new MenuItem(this.dropDownSphere, SWT.PUSH);
		this.emailBoxItem.setImage(emailBoxImage);
		this.emailBoxItem.addSelectionListener(new EmailBoxItemSelectionListener(this.sF));

		activate();
	}
	
	/**
	 * 
	 */
	public void fillFavourites() {
		String emailBoxString = SupraSphereFrame.INSTANCE.client.getVerifyAuth().getContactStatement().getLogin()+" "+bundle.getString(EMAIL_BOX);
		this.emailBoxItem.setText(emailBoxString);
		
		if(!this.filled) {
			Hashtable<String, String> spheres = this.sF.getFavouriteSpheres();
			for(String id : spheres.keySet()) {
				MenuItem item = new MenuItem(this.dropDownSphere, SWT.PUSH);
				item.setImage(sphereImage);
				item.setText(spheres.get(id));
				item.addSelectionListener(new FavouriteItemListener(this.sF));

				this.favourites.add(item);
				this.favIds.add(id);
				this.filled = true;
			}
		}

	}

	private void createOptionsMenuItem() {
		MenuItem optionsItem = new MenuItem(this.menuBar, SWT.CASCADE);
		optionsItem.setText(bundle.getString(OPTIONS));
		
		this.dropDownOptions = new Menu(optionsItem);
		optionsItem.setMenu(this.dropDownOptions);
		
		MenuItem preferencesItem = new MenuItem(this.dropDownOptions, SWT.PUSH);
		preferencesItem.setImage(preferencesImage);
		preferencesItem.setText(bundle.getString(PREFERENCES));
		preferencesItem.addSelectionListener(new PreferencesMenuListener());
	}
	
	private void createAssetMenuItem() {
		MenuItem assetItem = new MenuItem(this.menuBar, SWT.CASCADE);
		assetItem.setText(bundle.getString(ASSET));
		
		this.dropDownAsset = new Menu(assetItem);
		assetItem.setMenu(this.dropDownAsset);
		
		//updateAssetMenu();
	}

	public void updateAssetMenu() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				for(MenuItem item : SupraMenuBar.this.dropDownAsset.getItems()) {
					item.dispose();
				}
				try {
					MessagesPane mp = SupraMenuBar.this.sF.tabbedPane.getSelectedMessagesPane();
					if(mp != null && mp.getMessagesTree()!=null) {
						MessagesTreeMouseListenerSWT listener = mp.getMessagesTree().getListener();
						MessagesTreeMenuCreator creator = new MessagesTreeMenuCreator(listener.getListener(), mp);
						
						if(mp.getMessagesTree().getSelectedDoc()!=null) {
							creator.createPopupMenu(SupraMenuBar.this.dropDownAsset);
						}
					}
				} catch (NullPointerException ex) {
					logger.error(" null pointer ", ex );
				}
			}
		});
	}
	
	public void addAdministrativeItems(){
		logger.info("Adding Administrative menu items");
		if(this.preferencesAdminItem != null) {
			return;
		}
		this.preferencesAdminItem = new MenuItem(this.dropDownOptions, SWT.PUSH);
		this.preferencesAdminItem.setImage(administrateImage);
		this.preferencesAdminItem.setText(bundle.getString(ADMINISTRATE));
		this.preferencesAdminItem.addSelectionListener(new AdministrateMenuListener());
	}
	
	private void createHelpMenuItem() {
		MenuItem helpItem = new MenuItem(this.menuBar, SWT.CASCADE);
		helpItem.setText(bundle.getString(HELP));
		
		Menu dropDownHelp = new Menu(helpItem);
		helpItem.setMenu(dropDownHelp);
		
		MenuItem helpSubItem = new MenuItem(dropDownHelp, SWT.PUSH);
		helpSubItem.setImage(helpImage);
		helpSubItem.setText(bundle.getString(HELP));
		helpSubItem.addSelectionListener(new HelpMenuListener(this.sF));
		
		new MenuItem(dropDownHelp, SWT.SEPARATOR);
		
		MenuItem aboutItem = new MenuItem(dropDownHelp, SWT.PUSH);
		aboutItem.setImage(aboutImage);
		aboutItem.setText(bundle.getString(ABOUT_SUPRASPHERE));
		aboutItem.addSelectionListener(new AboutMenuListener(this.sF));
	}
	
	private void createCommandMenuItem() {
		MenuItem loadItem = new MenuItem(this.menuBar, SWT.CASCADE);
		loadItem.setText(bundle.getString(COMMAND));
		
		Menu loadMenu = new Menu(loadItem);
		loadItem.setMenu(loadMenu);
		
		MenuItem supraSearch = new MenuItem(loadMenu, SWT.PUSH);
		supraSearch.setText(bundle.getString(SUPRASEARCH));
		supraSearch.setImage(supraSearchImage);
		supraSearch.addSelectionListener(new SupraSearchSelectionListener(this.sF));
		
		MenuItem setGlobalMark = new MenuItem(loadMenu, SWT.PUSH);
		setGlobalMark.setText(bundle.getString(SET_GLOBAL));
		setGlobalMark.setImage(setGlobalImage);
		setGlobalMark.addSelectionListener(new SaveGlobalMarkSelectionListener(this.sF));
		
		MenuItem saveOrder = new MenuItem(loadMenu, SWT.PUSH);
		saveOrder.setText(bundle.getString(SAVE_ORDER));
		saveOrder.setImage(saveOrderImage);
		saveOrder.addSelectionListener(new SaveOrderSelectionListener(this.sF));
		
		MenuItem savePosition = new MenuItem(loadMenu, SWT.PUSH);
		savePosition.setText(bundle.getString(SAVE_POSITION));
		savePosition.setImage(savePositionImage);
		savePosition.addSelectionListener(new SavePositionSelectionListener(this.sF));
		
		MenuItem closeAll = new MenuItem(loadMenu, SWT.PUSH);
		closeAll.setText(bundle.getString(CLOSE_ALL));
		closeAll.setImage(closeAllImage);
		closeAll.addSelectionListener(new CloseAllTabsSelectionListener(this.sF));
		
		MenuItem openBlankTab = new MenuItem(loadMenu, SWT.PUSH);
		openBlankTab.setText(bundle.getString(OPEN_BLANK_TAB));
		openBlankTab.setImage(setGlobalImage);
		openBlankTab.addSelectionListener(new OpenBlankSelectionListener(this.sF));
	}
	
	private void activate() {
			this.removeItem.setEnabled(false);
			this.addItem.setEnabled(false);
	}
	
	public MenuItem getRemoveSphereItem() {
		return this.removeItem;
	}
	
	public MenuItem getAddSphereItem() {
		return this.addItem;
	}

	/**
	 * 
	 */
	public void disposeEmailItem() {
		this.emailBoxItem.dispose();	
	}
	
	public Vector<String> getFavIds() {
		return this.favIds;
	}
	
	public void removeFromFavourites(MessagesPane mp) {
		SphereStatement sphere = SphereStatement.wrap(mp.getSphereDefinition());

		removeFromFavourites(sphere.getSystemName());
	}

	public void removeFromFavourites(final String systemName) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				int i = SupraMenuBar.this.favIds.indexOf(systemName);
				MenuItem item = SupraMenuBar.this.favourites.get(i);
				SupraMenuBar.this.favourites.remove(item);
				SupraMenuBar.this.favIds.remove(i);
				item.dispose();
			}
		});
	}
	
	public void addToFavourites(MessagesPane mp) {
		SphereStatement sphere = SphereStatement.wrap(mp.getSphereDefinition());
		
		MenuItem item = new MenuItem(this.dropDownSphere, SWT.PUSH);
		item.setImage(sphereImage);
		item.setText(sphere.getDisplayName());
		item.addSelectionListener(new FavouriteItemListener(this.sF));
		
		this.favourites.add(item);
		this.favIds.add(sphere.getSystemName());
	}
	
	public String getItemId(MenuItem item){
		int index = this.favourites.indexOf(item);
		return this.favIds.get(index);
	}
	
	public void checkAddRemoveEnabled() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				checkAddRemoveEnabledImpl();
				updateAssetMenu();
			}
		});
	}
	
	private void checkAddRemoveEnabledImpl() {
		MessagesPane selected = this.sF.tabbedPane.getSelectedMessagesPane();
		String itemText = SupraMenuBar.this.sF.tabbedPane.getSelection().getText();
		if(selected==null || selected.isRootView() || itemText.contains(EMAIL_BOX)) {
			this.removeItem.setEnabled(false);
			this.addItem.setEnabled(false);
		} else {
			String id = SphereStatement.wrap(selected.getSphereDefinition()).getSystemName();
        	final boolean flag = this.sF.getMenuBar().getFavIds().contains(id);
        	UiUtils.swtBeginInvoke(new Runnable() {
        		public void run() {
        			SupraMenuBar.this.sF.getMenuBar().getRemoveSphereItem().setEnabled(flag);
        			SupraMenuBar.this.sF.getMenuBar().getAddSphereItem().setEnabled(!flag);
        		}
        	});
        	
		}
	}
	
	public void enableWholeMenu(boolean enabled){
		this.menuBar.setEnabled(enabled);
	}

	/**
	 * 
	 */
	public void addClubdealAdministrateItem() {
		MenuItem clubdealItem = new MenuItem(this.dropDownOptions, SWT.PUSH);
		clubdealItem.setImage(contactAdministrateImage);
		clubdealItem.setText("Contact management...");
		clubdealItem.addSelectionListener(new ClubdealSelectionListener());
	}

}
