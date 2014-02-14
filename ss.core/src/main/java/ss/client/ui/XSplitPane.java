package ss.client.ui;

/*
 * 
 * Kind of a kludge for some problems with JSplitPane layout with nested
 * JSplitPanes
 *  
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.browser.SupraBrowser;


public class XSplitPane extends SashForm{
    
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XSplitPane.class);
	
    private boolean browsed = false;

    public static final int HORIZONTAL = 0;

    public static final int VERTICAL = 1;

    public static final int SPLIT = 2;

    private double proportionalLocation;

    private boolean isPainted = false;
    
    private Frame frame;
    
    private Component component;
    
    private JScrollPane preview = null;

    private SupraSphereFrame sF = null;
    private Composite switchComposite;

    private Composite mainComposite;
    
    private SupraBrowser mozillaBrowser;
    
    private Frame previewFrame;

    /*public XSplitPane() {
        super();
    }

    public XSplitPane(int newOrientation) {
        super(newOrientation);
    }

    public XSplitPane(int newOrientation, boolean newContinuousLayout) {
        super(newOrientation, newContinuousLayout);
    }

    public XSplitPane(int newOrientation, boolean newContinuousLayout,
            Component newLeftComponent, Component newRightComponent) {
        super(newOrientation, newContinuousLayout, newLeftComponent,
                newRightComponent);
    }

    public XSplitPane(int newOrientation, Component newLeftComponent,
            Component newRightComponent) {
        super(newOrientation, newLeftComponent, newRightComponent);
    }*/
    
    
    
    public XSplitPane(Composite composite, int orientation) {
        super(composite, orientation);
    }
    
    public void setSupraSphereFrame(SupraSphereFrame sF) {
    	this.sF = sF;
    }

    public void setDividerLocation(double proportionalLocation) {

        if (!this.isPainted) {
            this.proportionalLocation = proportionalLocation;
        } else {
            int i = (int)Math.round(proportionalLocation*100);
            int[] mass = new int[2];
            
            mass[0] = i;
            mass[1] = 100 - i;
            setWeights(mass);
         //   super.setDividerLocation(proportionalLocation);
        }

    }

    public void setIntLocation(int intLocation) {
        if (this.isPainted) {
        	setDividerLocationZ(intLocation);
        }   
    }
    
    private void setDividerLocationZ(int location){
        int[] mass = new int[2];
        Point size = getSize();
        mass[0] = location;
        if (getOrientation() == SWT.VERTICAL){
            mass[1] = size.y - mass[0];
        } else {
            mass[1] = size.x - mass[0];
        }
        setWeights(mass);
    }

    /*public void paint(Graphics g) {
        if (!this.isPainted) {
            if (this.hasProportionalLocation)
                super.setDividerLocation(this.proportionalLocation);
            this.isPainted = true;
        }
        super.paint(g);
    }*/

    // added by zhonghai Sept.17 2004
    public double caculatePropLocation(int orientation) {
        double div = .65;
        int[] i = getWeights();
        div = i[0];
        /*double dl = getDividerLocation();

        if (orientation == VERTICAL)
            div = dl / size.getHeight();
        else
            div = dl / size.getWidth();*/

        if (div < 0)
            return 0.65; // default

        return div;
    }

    public double getProportionalLocation() {
        return this.proportionalLocation;
    }
    
    public SupraBrowser getCurrentBrowser() {
    	logger.debug("browser call");
    	return this.mozillaBrowser;
    }

    public void setProportionalLocation(double proportionalLocation) {
        this.proportionalLocation = proportionalLocation;
    }

    public void resetDividerLocation() {
        setDividerLocation(this.proportionalLocation);
    }
    
    public void setResizeWeight(double value){
        // NEED IMPLEMENT
    }
    
    public void setDividerSize(int newSize){
        //NEED IMPLEMENT
    }
    
    public void setBottomComponent(Component comp){
        addComponent(comp);
    }
    
    public void addComponent(Component component){
        Composite c;
        if (component instanceof JScrollPane){
            Composite comp = new Composite(this, SWT.NONE);
            GridData layoutData = new GridData();
            layoutData.grabExcessHorizontalSpace = true;
            layoutData.grabExcessVerticalSpace = true;
            layoutData.verticalAlignment = GridData.FILL;
            layoutData.horizontalAlignment = GridData.FILL;
            comp.setLayout(new GridLayout());
            //comp.setLayoutData(layoutData);
            
            c = new Composite(comp,SWT.EMBEDDED);
            c.setLayoutData(layoutData);
            
            this.preview = (JScrollPane)component;
            this.switchComposite = c;
            this.mainComposite = comp;
            
            this.previewFrame = SWT_AWT.new_Frame(c);

            this.previewFrame.setLayout(new BorderLayout());

            this.previewFrame.add(component, BorderLayout.CENTER);
            this.previewFrame.setVisible(true);
        } else {
            this.component = component;
            c = new Composite(this,SWT.EMBEDDED);
            this.frame = SWT_AWT.new_Frame(c);

            this.frame.setLayout(new BorderLayout());

            this.frame.add(this.component, BorderLayout.CENTER);
            this.frame.setVisible(true);
        }
    }
    
    public void reSetComponent(Component component){
        this.frame.setVisible(false);
        this.frame.remove(this.component);
        this.component = component;
        this.frame.add(this.component, BorderLayout.CENTER);
        this.frame.setVisible(true);
    }
    
    public boolean switchToBrowser(boolean turn, String URL){
        if (turn){
            if (this.browsed){
                this.mozillaBrowser.setUrl(URL);
            } else {
                this.previewFrame.dispose();
                this.switchComposite.dispose();
                this.switchComposite = new Composite(this.mainComposite,SWT.NONE);
                GridData layoutData = new GridData();
                layoutData.grabExcessHorizontalSpace = true;
                layoutData.grabExcessVerticalSpace = true;
                layoutData.verticalAlignment = GridData.FILL;
                layoutData.horizontalAlignment = GridData.FILL;
                this.switchComposite.setLayoutData(layoutData);
                this.switchComposite.setLayout(new GridLayout());
                loadBrowser(this.switchComposite,URL);
                this.browsed = true;
                this.switchComposite.layout();
                this.mainComposite.layout();
                return true;
            }
        } else if (this.browsed) {
            if (this.mozillaBrowser != null)
                this.mozillaBrowser.dispose();
            this.switchComposite.dispose();
            this.switchComposite = new Composite(this.mainComposite,SWT.EMBEDDED);
            GridData layoutData = new GridData();
            layoutData.grabExcessHorizontalSpace = true;
            layoutData.grabExcessVerticalSpace = true;
            layoutData.verticalAlignment = GridData.FILL;
            layoutData.horizontalAlignment = GridData.FILL;
            this.switchComposite.setLayoutData(layoutData);
            
            this.previewFrame = SWT_AWT.new_Frame(this.switchComposite);

            this.previewFrame.setLayout(new BorderLayout());

            this.previewFrame.add(this.preview, BorderLayout.CENTER);
            this.previewFrame.setVisible(true);
            this.browsed = false;
            this.mainComposite.layout();
            return true;
        }
        return false;
    }
    
    private void loadBrowser(Composite parent, String URL){

        final SupraBrowser mb = new SupraBrowser(parent, SWT.MOZILLA | SWT.BORDER);
        
        final MessagesPane mp = (MessagesPane)this.sF.tabbedPane.getSelectedMessagesPane();
        if (mp == null) {
			logger.error("Selected message pane is null");
			return;
		}
        
        
        mb.getMozillaBrowserController().setCurrentSession(mp.getRawSession());
        
        GridData layoutData = new GridData();
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.horizontalAlignment = GridData.FILL;
        mb.setLayoutData(layoutData);

        mb.setUrl(URL);

        mb.setVisible(true);
        
        this.mozillaBrowser = mb;
    }
}