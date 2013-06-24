/**
 * 
 */
package ss.client.ui.docking;

import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.AbstractControlPanel;
import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.tempComponents.FactoryOfControlPanel;
import ss.client.ui.tempComponents.SupraColors;
import ss.common.UiUtils;
import swtdock.ILayoutPart;
import swtdock.PartDragDrop;

/**
 * @author zobo
 * 
 */
public class ControlPanelDocking extends AbstractDockingComponent {

    private static final String USER_CONTROL_PANEL = "CONTORLPANELDOCKING.USER_CONTROL_PANEL";
    
    private ResourceBundle bundle = ResourceBundle
    .getBundle(LocalizationLinks.CLIENT_UI_DOCKING_CONTROLPANELDOCKING);

    private AbstractControlPanel controlPanel;

    private SupraSphereFrame sF;

    private MessagesPane mP;

    private DockingTopTitle headComposite;

    /**
     * @param id
     * @param dm
     */
    public ControlPanelDocking(SupraDockingManager dm, SupraSphereFrame sF,
            MessagesPane mP) {
        super(dm);
        this.sF = sF;
        this.mP = mP;
    }

    @Override
    public String getName() {
        return this.bundle.getString(USER_CONTROL_PANEL);
    }

    @Override
    public void createContent(Composite parent) {
       // parent.setLayout(new FillLayout());
        this.controlPanel = FactoryOfControlPanel.createControlPanel( this, parent);
        this.controlPanel.setTypeAndDelivery(this.mP.getSphereDefinition(),
                this.mP.getCreateDefinition(), this.mP.getRawSession());
        this.control.addControlListener(new ControlPanelDockingListener(this.controlPanel));
        /*if (this.mP.getInputListener() != null) {
            this.controlPanel.getSendField().addKeyListener(
                    new ControlPanelInputTextPaneListener(this.mP.sF,this.mP));
        }
        //this.controlPanel.getSendField()
        //        .addKeyListener(new BigAreaKeyListener(this.mP, this.controlPanel.getSendField()));
        this.controlPanel.getSendField()
                .addKeyListener(new SendFieldListener(this.mP.sF,
                        this.controlPanel, this.mP.getSession(), this.mP.sF.client));*/
    }

    @Override
    public AbstractControlPanel getContent() {
        return this.controlPanel;
    }

    @Override
    public int getMinimumWidth() {
        return this.controlPanel.getMinWidth();
    }

    @Override
    public int getMinimumHeight() {
        return 35;
    }

   
    public boolean checkPossibilityOfDocking(int direction, ILayoutPart target){
        if (target == null){
            if ((direction == PartDragDrop.SHELL_RIGHT)||(direction == PartDragDrop.SHELL_LEFT))
                return false;
        } else if (!super.checkPossibilityOfDocking(direction,target))
            return false;
        if ((direction == PartDragDrop.RIGHT)||(direction == PartDragDrop.LEFT))
            return false;
        return true;
    }
    
    public void createControl(Composite parent) {
        if (this.control != null && !this.control.isDisposed())
            return;
        
        this.control = new Composite(parent, SWT.NONE);
        this.control.setBackground(Display.getCurrent().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
        
        FormLayout formLayout = new FormLayout();
        formLayout.marginHeight = 0;
        formLayout.marginWidth = 0;
        formLayout.spacing = 0;
        this.control.setLayout(formLayout);

        /*GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.numColumns = 5;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        this.control.setLayout(gridLayout);*/

        this.headComposite = new DockingTopTitle(this.control, SWT.NONE);
        FormData data = new FormData();
        data.left = new FormAttachment(0,0);
        data.top = new FormAttachment(0,0);
        data.bottom = new FormAttachment(100, 0);
        
        /*GridData gridData = new GridData();
        //gridData.heightHint = HEIGHT_HINT;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;//true;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.verticalAlignment = GridData.BEGINNING;
        headComposite.setLayoutData(gridData);*/
        this.headComposite.setLayoutData(data);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        //gridLayout.numColumns = 2;
        gridLayout.marginHeight = 0;
        this.headComposite.setLayout(gridLayout);

        this.label = new CLabel(this.headComposite, SWT.NONE);
        this.label.setAlignment(SWT.LEFT);
        //this.label.setAlignment(SWT.CENTER);
        this.label.setText(getName());
        this.label.setBackground(SupraColors.DOCKING_LABEL_NORMAL);
/*
        ToolBar toolbar = new ToolBar(headComposite, SWT.FLAT);
        ToolItem zoomButton = new ToolItem(toolbar, SWT.PUSH);
        zoomButton.addListener(SWT.Selection, new Listener() {
            private AbstractDockingComponent comp = AbstractDockingComponent.this;
            public void handleEvent(Event e) {
                if(!this.comp.dockingManager.isZoomed())
                    this.comp.dockingManager.zoomIn(AbstractDockingComponent.this);
                else this.comp.dockingManager.zoomOut();
                
            }
        });

        ToolItem closeButton = new ToolItem(toolbar, SWT.PUSH);
        closeButton.addListener(SWT.Selection, new Listener() {
            private AbstractDockingComponent comp = AbstractDockingComponent.this;
            public void handleEvent(Event e) {
                if (AbstractDockingComponent.this.isZoomed()) {
                    this.comp.dockingManager.zoomOut();
                }
                this.comp.dockingManager.removePart(AbstractDockingComponent.this);
                dispose();
            }

        });

        closeButton.setText("X");
        zoomButton.setText("Z");
        toolbar.pack();*/

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;
        this.label.setLayoutData(gridData);

       /* gridData = new GridData();
        gridData.verticalAlignment = GridData.CENTER;
        toolbar.setLayoutData(gridData);*/

        //Composite mainControl = new Composite(this.control, SWT.NONE);
        /*gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        mainControl.setLayoutData(gridData);*/

        createContent(this.control);//mainControl);
    }
    
    public int getSpacing(){
        return 0;
    }

    @Override
    public boolean checkIfCanDockOn(int direction) {
        if ((direction == PartDragDrop.RIGHT)||(direction == PartDragDrop.LEFT))
            return false;
        return false;
    }
    
    public void setFocusToTextField(){
    	if(this.controlPanel instanceof ControlPanel) {
    		UiUtils.swtInvoke(new Runnable() {
    			public void run() {
    				((ControlPanel)ControlPanelDocking.this.controlPanel).setFocusToSendField();
    			}
    		});
    	}
    }
    
    public SupraSphereFrame getSupraFrame() {
    	return this.sF;
    }
    
    public MessagesPane getMessagesPane() {
    	return this.mP;
    }
    
    public DockingTopTitle getHeadComposite() {
    	return this.headComposite;
    }
    
	@Override
	protected void createToolBar(Composite parent) {
		// DO NOTHING
	}
}
