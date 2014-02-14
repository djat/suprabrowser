/**
 * 
 */
package ss.client.ui.docking;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.event.MessagesTreeMouseListenerSWT;
import ss.client.event.ShowSystemListener;
import ss.client.event.ThreadViewSelectionListener;
import ss.client.event.tagging.ShowAllTagsSelectionListener;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.tree.MessagesTree;
import ss.util.ImagesPaths;
import swtdock.ILayoutPart;

/**
 * @author zobo
 * 
 */
public class MessagesTreeDocking extends AbstractDockingComponent {

	private static final String MESSAGES_TREE = "MESSAGESTREEDOCKING.MESSAGES_TREE";
	private static final String THREAD_VIEW = "MESSAGESTREEDOCKING.THREAD_VIEW";
	private static final String SHOW_SYSTEM_MESSAGES = "MESSAGESTREEDOCKING.SHOW_SYSTEM_MESSAGES";

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_DOCKING_MESSAGESTREEDOCKING);

	private MessagesTree tree;

	private MessagesPane mP;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeDocking.class);

	public Hashtable session;

	private MenuItem threadView;
	private MenuItem showSystemButton;
	private MenuItem showTags;
	
	private Image threadViewIcon = null;
	private Image showSystemIcon = null;

	/**
	 * @param dm
	 */
	public MessagesTreeDocking(SupraDockingManager dm, Hashtable session,
			MessagesPane mP) {
		super(dm);
		this.session = session;
		this.mP = mP;
	}

	public void setTree(MessagesTree tree) {
		this.tree = tree;
	}

	@Override
	public String getName() {
		return this.bundle.getString(MESSAGES_TREE);
	}

	@Override
	public void createContent(Composite parent) {
		parent.setLayout(new FillLayout());
		MessagesTree messagesTree = new MessagesTree(parent, this.mP.client.getVerifyAuth()
				.getUserSession(), this.mP);
		MessagesTreeMouseListenerSWT messagesTreeMouseListener = new MessagesTreeMouseListenerSWT(
				this.mP);
		messagesTree.setMouseListener(messagesTreeMouseListener);
		logger.info("Messages Tree Docking Content created");
	}

	@Override
	public MessagesTree getContent() {
		return this.tree;
	}

	@Override
	public int getMinimumWidth() {
		return 0;
	}

	@Override
	public int getMinimumHeight() {
		return 0;
	}

	@Override
	public boolean checkPossibilityOfDocking(int direction, ILayoutPart target) {
		if (target == null) {
		} else if (!super.checkPossibilityOfDocking(direction, target))
			return false;
		return true;
	}

	@Override
	public boolean checkIfCanDockOn(int direction) {
		return true;
	}

	public MessagesPane getMessagesPane() {
		return this.mP;
	}

	@Override
	protected void createToolBar(Composite parent) {
	
		try {
			this.threadViewIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.THREAD_VIEW_ICON).openStream());
			this.showSystemIcon = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SHOW_SYSTEM_MESSAGES).openStream());
		} catch (IOException ex) {
			logger.error(ex);
		}

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.numColumns = 2;
        parent.setLayout(gridLayout);

		final ToolBar toolbar = new ToolBar(parent, SWT.RIGHT);

		createDropDownItem( toolbar );

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.FILL;
		toolbar.setLayoutData(gridData);
		
		toolbar.pack();
		
		parent.layout();
	}
	
	protected ToolItem createDropDownItem( final ToolBar toolbar ) {

		final ToolItem toolItem = new ToolItem(toolbar, SWT.DROP_DOWN);
		toolItem.setImage(this.showSystemIcon);

		final Menu dropDownMenu = new Menu( toolbar.getShell(), SWT.POP_UP );
		toolItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
//				if (event.detail == SWT.ARROW) {
				final Rectangle rect = toolItem.getBounds();
				dropDownMenu.setLocation( 
						toolbar.toDisplay( new Point(rect.x, rect.y + rect.height) ) );
				dropDownMenu.setVisible(true);
//				}
			}

		});
		
		this.threadView = new MenuItem( dropDownMenu, SWT.CHECK );
		this.threadView.setText(this.bundle.getString(THREAD_VIEW));
		this.threadView.addSelectionListener(new ThreadViewSelectionListener(this.mP));
		
		this.showSystemButton = new MenuItem( dropDownMenu, SWT.CHECK );
		this.showSystemButton.setText(this.bundle.getString(SHOW_SYSTEM_MESSAGES));
		this.showSystemButton.addSelectionListener(new ShowSystemListener(this.mP));
		
		this.showTags = new MenuItem( dropDownMenu, SWT.CHECK );
		this.showTags.setText("Show tags");
		this.showTags.addSelectionListener(new ShowAllTagsSelectionListener(this.mP));

		return toolItem;
	}
	
//	protected void createComposite(Composite parent) {
//		
//		try {
//			this.threadViewIcon = new Image(Display.getDefault(), getClass()
//					.getResource(ImagesPaths.THREAD_VIEW_ICON).openStream());
//			this.showSystemIcon = new Image(Display.getDefault(), getClass()
//					.getResource(ImagesPaths.SHOW_SYSTEM_MESSAGES).openStream());
//		} catch (IOException ex) {
//			logger.error(ex);
//		}
//
//        GridLayout gridLayout = new GridLayout();
//        gridLayout.marginHeight = 0;
//        gridLayout.marginWidth = 0;
//        gridLayout.horizontalSpacing = 0;
//        gridLayout.verticalSpacing = 0;
//        gridLayout.numColumns = 2;
//        parent.setLayout(gridLayout);
//
//		Composite toolbar = new Composite(parent, SWT.RIGHT);
//
//		GridLayout gl = new GridLayout();
//		gl.marginHeight = 0;
//		gl.marginWidth = 0;
//		gl.marginBottom = 0;
//		gl.marginLeft = 0;
//		gl.marginRight = 0;
//		gl.marginTop = 0;
//		gl.numColumns = 2;
//		toolbar.setLayout(gl);
//
//		this.threadView = new Button(toolbar, SWT.TOGGLE);
//		this.threadView.setToolTipText(this.bundle.getString(THREAD_VIEW));
//		this.threadView.setImage(this.threadViewIcon);
//		this.threadView.setVisible(true);
//		GridData gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.verticalAlignment = GridData.FILL;
//		this.threadView.setLayoutData(gridData);
//		
//		this.threadView.addSelectionListener(new ThreadViewSelectionListener(
//				this));
//		
//		this.showSystemButton = new Button(toolbar, SWT.TOGGLE);
//		this.showSystemButton.setImage(this.showSystemIcon);
//		this.showSystemButton.setToolTipText(this.bundle.getString(SHOW_SYSTEM_MESSAGES));
//		this.showSystemButton.addSelectionListener(new ShowSystemListener(this.mP));
//		gridData = new GridData();
//		gridData.verticalAlignment = GridData.FILL;
//		gridData.horizontalAlignment = GridData.END;
//		this.showSystemButton.setLayoutData(gridData);
//
//		gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = false;
//		gridData.horizontalAlignment = GridData.FILL;
//		gridData.grabExcessVerticalSpace = false;
//		gridData.verticalAlignment = GridData.FILL;
//		toolbar.setLayoutData(gridData);
//		
//		toolbar.pack();
//		
//		parent.layout();
//	}
	
	public MenuItem getShowSystemButton() {
		return this.showSystemButton;
	}
	
	public MenuItem getThreadViewButton() {
		return this.threadView;
	}
}
