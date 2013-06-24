/**
 * 
 */
package ss.client.ui.root;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;

import ss.client.ui.root.actions.RootCreateSphereAction;
import ss.client.ui.root.actions.RootCreateUserAction;

/**
 * @author zobo
 *
 */
public class RootControlPanel implements ILayoutable{
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootControlPanel.class);

	private final Composite control;

	private RootDropDownToolItem rootItem;
	
	private RootTab rootTab;
	
	private RootCreateUserAction rootCreateUserAction;
	
	private RootCreateSphereAction rootCreateSphereAction;
	
	public RootControlPanel(final Composite parent, int style, RootTab rootTab){
		this.control = new Composite(parent, style);
		this.rootTab = rootTab;
		createContents(this.control);
	}

	private void createContents(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		GridData layoutData;
		
        createRootSphereLabel(parent);
        
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
        createDropDownItem(parent).setLayoutData(layoutData);
        
		parent.setVisible(true);
	    parent.layout();
	}

	/**
	 * @param parent
	 */
	private void createRootSphereLabel(Composite parent) {
		Label text = new Label(parent, SWT.SINGLE | SWT.RIGHT);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.verticalAlignment = SWT.CENTER;
		data.horizontalAlignment = GridData.BEGINNING;
		
		text.setLayoutData(data);
		text.setText("Create:");
		text.setVisible(true);
		text.setEnabled(true);
	}
	
	protected Control createDropDownItem(Composite parent) {
		ToolBar toolbar = new ToolBar(parent, SWT.RIGHT);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		data.verticalAlignment = GridData.CENTER;
		data.horizontalAlignment = GridData.FILL;
		toolbar.setData(data);
		
		this.rootItem = new RootDropDownToolItem(toolbar, this);

		this.rootCreateSphereAction = new RootCreateSphereAction(this.rootTab);
		this.rootCreateUserAction = new RootCreateUserAction(this.rootTab);
       	this.rootItem.addAction( this.rootCreateSphereAction );
       	this.rootItem.addAction( this.rootCreateUserAction );

		toolbar.pack();
		
		return toolbar;
	}

	/**
	 * @param layoutData
	 */
	public void setLayoutData(Object layoutData) {
		this.control.setLayoutData(layoutData);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.root.ILayoutable#layout()
	 */
	public void layout() {
		this.control.layout();
	}
	
	void performCreateSphereAction(){
		this.rootCreateSphereAction.perform();
	}
	
	void performCreateUserAction(){
		this.rootCreateUserAction.perform();
	}
}
