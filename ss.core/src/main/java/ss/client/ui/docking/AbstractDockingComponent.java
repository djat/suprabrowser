/**
 * 
 */
package ss.client.ui.docking;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import ss.client.event.RefreshPeopleListListener;
import ss.client.ui.MessagesPane;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.tempComponents.SupraColors;
import ss.common.UiUtils;
import swtdock.ILayoutContainer;
import swtdock.ILayoutPart;
import swtdock.LayoutPart;
import swtdock.PartTabFolder;

/**
 * @author zobo
 * 
 */
public abstract class AbstractDockingComponent extends LayoutPart {

	@SuppressWarnings("unused")
	private static final int HEIGHT_HINT = 32;

	protected static int SPACING = 5;

	@SuppressWarnings("unused")
	private SupraDockingManager dockingManager;

	protected Composite control;

	protected CLabel label;

	protected Composite mainControl;

	protected DockingTopTitle headComposite;

	private static int id_counter = 0;

	/**
	 * 
	 */
	public AbstractDockingComponent(SupraDockingManager dm) {
		super("" + id_counter);
		id_counter++;
		this.dockingManager = dm;
	}

	public abstract String getName();

	public void setLabelText(String text) {
		this.label.setText(text);
	}

	@Override
	public boolean isViewPane() {
		return true;
	}

	@Override
	public void createControl(Composite parent) {
		if (this.control != null && !this.control.isDisposed())
			return;

		this.control = new Composite(parent, SWT.NONE);
		this.control.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = getSpacing();
		gridLayout.marginWidth = getSpacing();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		this.control.setLayout(gridLayout);

		this.headComposite = new DockingTopTitle(this.control, SWT.NONE);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = HEIGHT_HINT;
		this.headComposite.setLayoutData(gridData);

		gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		this.headComposite.setLayout(gridLayout);

		this.label = new CLabel(this.headComposite, SWT.NONE);
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
		 * this.comp.dockingManager.zoomOut(); } });
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

		createToolBar(this.headComposite);

		/*
		 * gridData = new GridData(); gridData.verticalAlignment =
		 * GridData.CENTER; toolbar.setLayoutData(gridData);
		 */

		this.mainControl = new Composite(this.control, SWT.NONE);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.mainControl.setLayoutData(gridData);

		createContent(this.mainControl);

		addMouseListener();
	}

	/**
	 * 
	 */
	private void addMouseListener() {
		new Thread(new Runnable() {
			
			private AbstractDockingComponent dock = AbstractDockingComponent.this;

			public void run() {
				boolean interapted = false;
				MessagesPane messagesPane;
				while (((messagesPane = getMP()) == null)
						&& (interapted || Thread.interrupted())) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						interapted = true;
					}
				}
				if (getMP() != null) {
					RefreshPeopleListListener listener = new RefreshPeopleListListener(
							messagesPane, true);
					this.dock.headComposite.addMouseListener(listener);
					this.dock.control.addMouseListener(listener);
					this.dock.label.addMouseListener(listener);
					this.dock.mainControl.addMouseListener(listener);
				}
			}

			private MessagesPane getMP() {
				return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
					public MessagesPane call() throws Exception {
						return getMessagesPane();
					}
				});
			}
		});
	}

	public Composite getMainControl() {
		return this.mainControl;
	}

	public abstract void createContent(Composite parent);

	protected abstract void createToolBar(Composite parent);

	public abstract Object getContent();

	@Override
	public Control getControl() {
		return this.control;
	}

	public Control getDragHandle() {
		return this.label;
	}

	public ILayoutPart targetPartFor(ILayoutPart dragSource) {
		ILayoutContainer container = getContainer();
		if (container instanceof PartTabFolder)
			return (PartTabFolder) container;
		else
			return this;
	}

	public abstract int getMinimumWidth();

	/**
	 * Returns the minimum height a part can have. Subclasses may override as
	 * necessary.
	 */
	public abstract int getMinimumHeight();

	public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
		if (target instanceof AbstractDockingComponent)
			return ((AbstractDockingComponent) target)
					.checkIfCanDockOn(direction);
		return true;
	}

	public abstract boolean checkIfCanDockOn(int direction);

	public int getSpacing() {
		return SPACING;
	}

	public int getWidth() {
		return this.control.getSize().x;
	}

	public int getHeight() {
		return this.control.getSize().y;
	}

	public MessagesPane getMessagesPane() {
		return null;
	}

	public SupraBrowser getBrowser() {
		return null;
	}

	public void removeSearchPane() {

	}
}
