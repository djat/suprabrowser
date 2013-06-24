/**
 * 
 */
package ss.client.event.tagging.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;

/**
 * @author zobo
 * 
 */
public class ListForTagWindow extends ApplicationWindow {
	
	private class MockUp extends Composite implements ITitleContainer {

		private static final String NO_TAGGED_ELEMENTS = "No tagged elements";
		
		final private String title;

		public MockUp( Composite parent, String title ) {
			this( parent, NO_TAGGED_ELEMENTS , title);
		}
		
		public MockUp( Composite parent, String message, String title ) {
			super( parent, SWT.NONE );
			this.title = title;
			create( this , message );
		}

		private void create( final Composite parent , final String message ) {
			parent.setLayout(new GridLayout());
			final Label label = new Label( parent, SWT.LEFT );
			label.setText( message );
			label.setLayoutData( new GridData(SWT.CENTER, SWT.CENTER, true, true) );
		}

		/* (non-Javadoc)
		 * @see ss.client.event.tagging.gui.ITitleContainer#getTitle()
		 */
		public String getTitle() {
			return this.title;
		}
	}
	
	private List<Boolean> mockUps = new ArrayList<Boolean>();

	private static final String TITLE = "Tagged elements";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ListForTagWindow.class);
	
	private final DataForTagObtainer obtainer;
	
	private CTabFolder folder;
	
	List<AbstractListForTagComposite> items; 

	public ListForTagWindow(final DataForTagObtainer obtainer) {
		super(SupraSphereFrame.INSTANCE.getShell());
		this.obtainer = obtainer;
	}

	@Override
	protected void configureShell(final Shell shell) {
		shell.setSize(300, 400);
		shell.setLocation(SDisplay.display.get().getCursorLocation());
		shell.setText(TITLE);
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		this.folder = new CTabFolder(parent, SWT.NONE);
		this.folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.folder.setLayout( new GridLayout() );
		
		addItem( (this.obtainer.getBoomarks().getCount() <= 0) ? 
				new MockUp(this.folder, BookmarksListForTagComposite.TITLE) : 
					new BookmarksListForTagComposite (this.folder, SWT.NONE, this.obtainer) );
		addItem( (this.obtainer.getFiles().getCount() <= 0) ? 
				new MockUp(this.folder, FilesListForTagComposite.TITLE) : 
			new FilesListForTagComposite (this.folder, SWT.NONE, this.obtainer) );
		addItem( (this.obtainer.getContacts().getCount() <= 0) ? 
				new MockUp(this.folder, ContactsListForTagComposite.TITLE) : 
			new ContactsListForTagComposite (this.folder, SWT.NONE, this.obtainer) );
		
		setSelection();
		
		this.folder.redraw();
		return parent;
	}

	private void setSelection() {
		int index = 0;
		for (Boolean bool : this.mockUps) {
			if (!bool.booleanValue()) {
				this.folder.setSelection( index );
				return;
			}
			index++;
		}
	}

	@Override
	protected int getShellStyle() {
		return SWT.CLOSE | SWT.TITLE | SWT.RESIZE;
	}
	
	private void addItem( final Control component ){
		this.mockUps.add( new Boolean( (component instanceof MockUp) ? true : false ) );
		CTabItem item = new CTabItem(this.folder , SWT.CENTER);
		item.setControl(component);
		item.setText(ITitleContainer.class.cast(component).getTitle());
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        component.setLayoutData(layoutData);
	}
}
