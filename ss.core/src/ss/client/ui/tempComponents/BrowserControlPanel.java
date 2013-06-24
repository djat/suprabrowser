/**
 * 
 */
package ss.client.ui.tempComponents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.protocol.getters.GetBookmarkAddressesCommand;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.Listeners.browser.BrowserControlPanelAddressFieldListener;
import ss.client.ui.Listeners.browser.BrowserControlPanelBackListener;
import ss.client.ui.Listeners.browser.BrowserControlPanelDiscussListener;
import ss.client.ui.Listeners.browser.BrowserControlPanelForwardListener;
import ss.client.ui.Listeners.browser.BrowserControlPanelReloadListener;
import ss.client.ui.Listeners.browser.BrowserControlPanelSaveListener;
import ss.client.ui.Listeners.browser.BrowserControlPanelStartListener;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.docking.BrowserControlPanelDocking;
import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.FilteredDataSource;
import ss.client.ui.models.autocomplete.FilteredModel;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.tempComponents.researchcomponent.ReSearchToolItemComponent;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.framework.networking2.ReplyObjectHandler;
import ss.util.ImagesPaths;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class BrowserControlPanel extends Composite {
	
	private static final String WWW = "www.";

	private static final String HTTP = "http://";
	
	private static final String HTTPWWW = "http://www.";

	private SupraSphereFrame sF = null;

	private SupraBrowser mb = null;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BrowserControlPanel.class);

	private ToolItem itemForward;

	private ToolItem itemReload;

	private ToolItem itemBack;

	private ToolItem itemDiscuss;
	
	private ReSearchToolItemComponent itemReSearch;

	private Text addressField;

	private Button startPage;

	private Button discuss;

	private Button savePage;

	private BrowserPane bp;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_TEMPCOMPONENTS_BROWSERCONTROLPANEL);

	private static final String BACK = "BROWSERCONTROLPANEL.BACK";

	private static final String RELOAD = "BROWSERCONTROLPANEL.RELOAD";

	private static final String FORWARD = "BROWSERCONTROLPANEL.FORWARD";

	private static final String DISCUSS = "BROWSERCONTROLPANEL.DISCUSS";

	private static final String TO_START = "BROWSERCONTROLPANEL.TO_START_PAGE";

	private static final String SAVE_AS_MESSAGE = "BROWSERCONTROLPANEL.SAVE_AS_MESSAGE";

	public BrowserControlPanel( final BrowserControlPanelDocking docking,
			final Composite parentComposite) {
		super(parentComposite, SWT.NONE);
		this.sF = docking.getSF();
		this.bp = docking.getBrowserPane();
		layoutComposite();
		logger.info("Control Panel for sBrowser created");
	}

	public BrowserControlPanel(SupraSphereFrame sF, Composite parentComposite) {
		super(parentComposite, SWT.NONE);
		this.sF = sF;
		layoutComposite();
		logger.info("Control Panel for sBrowser created");
	}

	public void activate(SupraBrowser mb) {
		if (mb == null || mb.isDisposed())
			return;
		this.mb = mb;

		this.savePage.setEnabled(this.mb.getUrl() != null
				&& !this.mb.getUrl().equals("about:blank"));
		
		if (this.mb.getUrl().equals("about:blank")) {
			logger.warn("IT WAS EQUAL BLANK..");
			this.selectTextInAddressField();

		}

		this.discuss.setEnabled(this.bp.hasComment());
		this.discuss
				.addSelectionListener(new BrowserControlPanelDiscussListener(
						this));

		this.itemBack.addSelectionListener(new BrowserControlPanelBackListener(
				this.mb));
		this.itemBack.setEnabled(this.mb.isBackEnabled());

		this.itemReSearch.activate( this.mb );

		this.itemForward
				.addSelectionListener(new BrowserControlPanelForwardListener(
						this.mb));
		this.itemForward.setEnabled(this.mb.isForwardEnabled());

		this.itemReload
				.addSelectionListener(new BrowserControlPanelReloadListener(
						this.mb));
		this.itemReload.setEnabled(true);

		this.mb.addLocationListener(new LocationListener() {
			SupraBrowser browser = BrowserControlPanel.this.mb;
			public void changed(LocationEvent le) {
				boolean canEnabledSave = (le.location != null && !le.location
						.equals("about:blank"));
				BrowserControlPanel.this.savePage.setEnabled(canEnabledSave);
				
				BrowserControlPanel.this.itemBack
						.setEnabled(this.browser.isBackEnabled());
				BrowserControlPanel.this.itemForward
						.setEnabled(this.browser
								.isForwardEnabled());
				getAddressField().setText(le.location);
				if(le.location.equals("about:blank")) {
					getAddressField().selectAll();
				}
			}

			public void changing(LocationEvent le) {
				logger.debug("changing location: "+le.location);
			}
		});
		logger.info("Control Panel for sBrowser activated");
	}

	private void layoutComposite() {

		GridLayout layout = new GridLayout(5, false);
		layout.marginBottom = 0;
		layout.marginLeft = 0;
		layout.marginTop = 0;
		layout.marginRight = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		createToolBar();

		createAddressField();

		createStartPageButton();

		createSavePageButton();
		
	}
	
	private void createToolBar(){
		final ToolBar toolBar = new ToolBar(this, SWT.NONE);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(data);

		this.itemBack = new ToolItem(toolBar, SWT.PUSH);
		this.itemBack.setText(this.bundle.getString(BACK));
		this.itemBack.setEnabled(false);

		this.itemForward = new ToolItem(toolBar, SWT.PUSH);
		this.itemForward.setText(this.bundle.getString(FORWARD));
		this.itemForward.setEnabled(false);

		this.itemReload = new ToolItem(toolBar, SWT.PUSH);
		this.itemReload.setText(this.bundle.getString(RELOAD));
		this.itemReload.setEnabled(false);
		
		this.itemReSearch = new ReSearchToolItemComponent( toolBar );

		this.itemDiscuss = new ToolItem(toolBar, SWT.SEPARATOR);
		this.discuss = new Button(toolBar, SWT.TOGGLE);
		this.discuss.setText(this.bundle.getString(DISCUSS));
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		this.discuss.setLayoutData(data);
		this.itemDiscuss.setWidth(68);
		this.itemDiscuss.setControl(this.discuss);

		toolBar.setVisible(true);
	}
	
	private void createStartPageButton() {
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.BEGINNING;

		Image startPageImage = null;
		try {
			startPageImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.START_PAGE_ICON).openStream());
		} catch (IOException ex) {
			logger.error("Can't create start page icon", ex);
		}

		this.startPage = new Button(this, SWT.PUSH);
		this.startPage.setToolTipText(this.bundle.getString(TO_START));
		this.startPage.setImage(startPageImage);
		this.startPage.setEnabled(true);
		this.startPage.setVisible(true);
		this.startPage.setLayoutData(layoutData);

		this.startPage
				.addSelectionListener(new BrowserControlPanelStartListener(
						this.bp));
	}

	private void createSavePageButton() {
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = false;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.BEGINNING;

		Image startPageImage = null;
		try {
			startPageImage = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.BOOKMARK).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}

		this.savePage = new Button(this, SWT.PUSH);
		this.savePage.setToolTipText(this.bundle.getString(SAVE_AS_MESSAGE));
		this.savePage.setImage(startPageImage);
		this.savePage.setEnabled(true);
		this.savePage.setVisible(true);
		this.savePage.setLayoutData(layoutData);

		this.savePage.addSelectionListener(new BrowserControlPanelSaveListener(
				this.bp));
		this.savePage.setEnabled(false);
	}

	@SuppressWarnings("unchecked")
	private void createAddressField() {
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.horizontalAlignment = GridData.FILL;

		this.addressField = new Text(this, SWT.BORDER);
		this.addressField.setEditable(true);
		this.addressField.setEnabled(true);
		this.addressField.setVisible(true);
		this.addressField.setLayoutData(layoutData);

		this.addressField
				.addKeyListener(new BrowserControlPanelAddressFieldListener(
						this));
		GetBookmarkAddressesCommand command = new GetBookmarkAddressesCommand();
		command.addLookupSpheres(this.sF.client.getVerifyAuth().getCurrentMemberEnabledSpheres());
		command.beginExecuteForUi(this.sF.client, new ReplyObjectHandler<ArrayList<String>>( (Class)ArrayList.class) {
			@Override
			protected void objectReturned(final ArrayList<String> reply) {
				activateTypeAhead(reply);				
			}
		} );
	}

	public void activateTypeAhead(final List<String> recievedData) {
		final TreeSet<String> set = new TreeSet<String>(new Comparator<String>(){
			public int compare(String o1, String o2) {
				return getCuttedAddress(o1).compareTo(getCuttedAddress(o2));
			}
		});
		set.addAll(recievedData);
		new TypeAheadComponent<String>(this.addressField,
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
						BrowserControlPanel.this.addressField.setText(realData);
						performLoad();
					}
				});
	}
	
	private String getCuttedAddress(String address){
		String str = address;
		if (str.startsWith(HTTP)){
			str = str.substring(7);
			if (str.startsWith(WWW)){
				str = str.substring(4);
			}
		} else if (str.startsWith(WWW)){
			str = str.substring(4);
		}
		return str;
	}

	private Vector<String> processDataFiltered(final TreeSet<String> data,
			String filter) {
		if ((filter == null) || (filter.trim().equals("")))
			return new Vector<String>(data);
		if ((HTTPWWW.startsWith(filter)) || (WWW.startsWith(filter))){
			return new Vector<String>(data);
		}
		Vector<String> out = new Vector<String>();
		for (String s : data) {
			if (getCuttedAddress(s).startsWith(getCuttedAddress(filter))){
				out.add(s);
			}
		}
		return out;
	}
	
	public Text getAddressField() {
		return this.addressField;
	}
	
	public void selectTextInAddressField() {
		this.addressField.selectAll();
	}

	public SupraBrowser getBrowser() {
		logger.debug("call browser");
		return this.mb;
	}

	public Button getDiscuss() {
		return this.discuss;
	}

	public SupraSphereFrame getSF() {
		return this.sF;
	}

	public BrowserPane getBrowserPane() {
		return this.bp;
	}

	/**
	 * 
	 */
	public void performLoad() {
		String address = getAddressField().getText();
		if (address != null) {
			getBrowser().setUrl(address);
			getBrowser().getMozillaBrowserController()
					.addBrowserLocationListener();
			getBrowser().setFocus();
			getBrowser().getMozillaBrowserController()
					.setTitleToNewURL(
							VariousUtils.convertToFullURL(address));
		}		
	}
}
