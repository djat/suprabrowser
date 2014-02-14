package ss.client.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.docking.DockingTopTitle;

public class RootSphereControlPanel extends AbstractControlPanel {
	
	Text text = null;
	
	public RootSphereControlPanel(SupraSphereFrame sF, Composite parentComposite, MessagesPane mP, DockingTopTitle headComposite) {
		super(sF, parentComposite, mP, headComposite);
		layoutComposite(headComposite);
	}
	
	protected void layoutComposite(DockingTopTitle headComposite) {
		FormData data;
        this.createToolBar();
        this.createRootSphereLabel();
        
        data = new FormData();
        data.left = new FormAttachment(headComposite);
        data.top = new FormAttachment(0,0);
        data.right = new FormAttachment(this.buttonComposite);
        data.bottom = new FormAttachment(100,0);
        
        this.text.setLayoutData(data);
        
        this.parent.setVisible(true);
        this.parent.layout();
	}

	protected void createToolBar() {
		FormData data;
		//this.toolBar = new ToolBar(this.parent, SWT.RIGHT);
		this.buttonComposite = new Composite(this.parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.makeColumnsEqualWidth = false;
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		layout.verticalSpacing = 0;
		this.buttonComposite.setLayout(layout);
		
        data = new FormData();
        data.right = new FormAttachment(100,0);
        data.top = new FormAttachment(0,0);
        data.bottom = new FormAttachment(100,0);
        
        this.buttonComposite.setLayoutData(data);
        
        createCreateLabel();
        createDropDownItem();
        
        //this.buttonComposite.pack();
	}
	
	
	
	private void createRootSphereLabel() {
		this.text = new Text(this.parent, SWT.CENTER);
		this.text.setText(AbstractControlPanel.getDefaultValueForRootTab());
		this.text.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
		this.text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		this.text.setEnabled(true);
		this.text.setVisible(true);
		this.text.setEditable(false);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.AbstractControlPanel#setFocusToSendField()
	 */
	@Override
	public void setFocusToSendField(){
		//Do nothing
    }
	
	public void selectTextInSendField() {
		//Do nothing
	}
    
}
