/**
 * 
 */
package ss.client.event.tagging.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public abstract class AbstractListForTagComposite extends Composite implements ITitleContainer {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractListForTagComposite.class);
	
	protected final DataForTagObtainer obtainer;

	private Text text;

	private List list;
	
	private String[] items;
	
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractListForTagComposite(Composite parent, int style, final DataForTagObtainer obtainer) {
		super(parent, style);
		this.obtainer = obtainer;
		createContents( this );
	}

	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());

		createList(parent).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createFindText(parent).setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comp.setLayout(new GridLayout(2, false));
		Button load = new Button(comp, SWT.PUSH);
		load.setText("Load");
		load.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				loadSafe( AbstractListForTagComposite.this.list.getSelectionIndex() );
			}

		});
		load.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false,
				false));

		Button close = new Button(comp, SWT.PUSH);
		close.setText("Cancel");
		close.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				AbstractListForTagComposite.this.getShell().close();
			}

		});
		close.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false,
				false));

		return parent;
	}
	
	private Control createFindText( final Composite parent ){
		this.text = new Text( parent, SWT.BORDER | SWT.SINGLE );
		this.text.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 13) {
		        	loadSafe( AbstractListForTagComposite.this.list.getSelectionIndex() );
		        	e.doit = false;           
		        }
			}

			public void keyReleased(KeyEvent e) {
				processKeyPressed( e );
			}
			
		});
		return this.text;
	}
	
	private void processKeyPressed( KeyEvent e ){
		if (e.keyCode == 13) {
			return;
		}
		String str = this.text.getText();
		if (StringUtils.isBlank(str)) {
			this.list.select(0);
			return;
		}
		str = str.toLowerCase();
		for (int i = 0; i < this.items.length; i++) {
			if (this.items[i].toLowerCase().startsWith(str)){
				this.list.select( i );
				return;
			}
		}
	}

	private Control createList(final Composite parent) {
		this.list = new List(parent, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.items = getItemsData();
		if (this.items != null) {
			this.list.setItems( this.items );
		}
		this.list.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				loadSafe( AbstractListForTagComposite.this.list.getSelectionIndex() );
			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});
		return this.list;
	}

	/**
	 * @return
	 */
	protected abstract String[] getItemsData();

	protected abstract void load( int index );
	
	private void loadSafe( int index ){
		if ( index == -1 ) {
			return;
		}
		load( index );
	}
	
	public void setFocusToField(){
		this.text.setFocus();
	}
}
