/**
 * 
 */
package ss.client.ui.docking;

import java.util.Hashtable;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.EmailControlPanel;
import ss.client.ui.tempComponents.ExternalEmailPane;
import ss.client.ui.tempComponents.SupraColors;
import swtdock.ILayoutPart;
import swtdock.PartDragDrop;

/**
 * @author zobo
 * 
 */
public class EmailControlPanelDocking extends AbstractDockingComponent {

	private static final String EMAIL_CONTROL_PANEL = "EMAILCONTROLPANELDOCKING.EMAIL_CONTROL_PANEL";

	private EmailControlPanel controlPanel;

	private SupraSphereFrame sF;

	private Hashtable session;

	private int minHeight = 30;

	private ExternalEmailPane ep;
	
	private final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_DOCKING_EMAILCONTROLPANELDOCKING);

	/**
	 * @param id
	 * @param dm
	 */
	public EmailControlPanelDocking(ExternalEmailPane ep) {
		super(ep.getDockingManager());
		this.sF = ep.getSF();
		this.session = ep.getSession();
		this.ep = ep;
	}

	@Override
	public String getName() {
		return this.bundle.getString(EMAIL_CONTROL_PANEL);
	}

	@Override
	public void createContent(Composite parent) {
		parent.setLayout(new FillLayout());
		this.controlPanel = new EmailControlPanel(this, parent);
	}

	@Override
	public EmailControlPanel getContent() {
		return this.controlPanel;
	}

	@Override
	public int getMinimumWidth() {
		return 0;
	}

	@Override
	public int getMinimumHeight() {
		return this.minHeight;
	}

	public void setMinimumHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
		if (target == null) {
			if ((direction == PartDragDrop.SHELL_RIGHT)
					|| (direction == PartDragDrop.SHELL_LEFT))
				return false;
		} else if (!super.checkPossibilityOfDocking(direction, target))
			return false;
		if ((direction == PartDragDrop.RIGHT)
				|| (direction == PartDragDrop.LEFT))
			return false;
		return true;
	}

	public void createControl(Composite parent) {
		if (this.control != null && !this.control.isDisposed())
			return;

		this.control = new Composite(parent, SWT.NONE);
		this.control.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		this.control.setLayout(gridLayout);

		DockingTopTitle headComposite = new DockingTopTitle(this.control,
				SWT.NONE);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.verticalAlignment = GridData.FILL;
		headComposite.setLayoutData(gridData);

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		headComposite.setLayout(gridLayout);

		this.label = new CLabel(headComposite, SWT.NONE);
		this.label.setAlignment(SWT.LEFT);
		this.label.setText(getName());
		this.label.setBackground(SupraColors.DOCKING_LABEL_NORMAL);
		/*
		 * ToolBar toolbar = new ToolBar(headComposite, SWT.FLAT); ToolItem
		 * zoomButton = new ToolItem(toolbar, SWT.PUSH);
		 * zoomButton.addListener(SWT.Selection, new Listener() { private
		 * AbstractDockingComponent comp = AbstractDockingComponent.this; public
		 * void handleEvent(Event e) { if(!this.comp.dockingManager.isZoomed())
		 * this.comp.dockingManager.zoomIn(AbstractDockingComponent.this); else
		 * this.comp.dockingManager.zoomOut();
		 *  } });
		 * 
		 * ToolItem closeButton = new ToolItem(toolbar, SWT.PUSH);
		 * closeButton.addListener(SWT.Selection, new Listener() { private
		 * AbstractDockingComponent comp = AbstractDockingComponent.this; public
		 * void handleEvent(Event e) { if
		 * (AbstractDockingComponent.this.isZoomed()) {
		 * this.comp.dockingManager.zoomOut(); }
		 * this.comp.dockingManager.removePart(AbstractDockingComponent.this);
		 * dispose(); }
		 * 
		 * });
		 * 
		 * closeButton.setText("X"); zoomButton.setText("Z"); toolbar.pack();
		 */

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		this.label.setLayoutData(gridData);

		/*
		 * gridData = new GridData(); gridData.verticalAlignment =
		 * GridData.CENTER; toolbar.setLayoutData(gridData);
		 */

		Composite mainControl = new Composite(this.control, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		mainControl.setLayoutData(gridData);

		createContent(mainControl);
	}

	public int getSpacing() {
		return 0;
	}

	@Override
	public boolean checkIfCanDockOn(int direction) {
		if ((direction == PartDragDrop.RIGHT)
				|| (direction == PartDragDrop.LEFT))
			return false;
		return true;
	}

	@Override
	protected void createToolBar(Composite parent) {
		// DO NOTHING
	}

	public SupraSphereFrame getSF() {
		return this.sF;
	}

	public Hashtable getSession() {
		return this.session;
	}

	public ExternalEmailPane getEP() {
		return this.ep;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.docking.AbstractDockingComponent#getBrowser()
	 */
	@Override
	public SupraBrowser getBrowser() {
		return this.ep.getBrowserDocking().getBrowser();
	}
}