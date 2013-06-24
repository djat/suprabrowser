/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;

import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class MultiCompositeContainer extends Composite{
    
    private Logger logger = SSLogger.getLogger(this.getClass());

    private StackLayout layout;
    
    private ArrayList<Composite> content;
    
    public MultiCompositeContainer(Composite parent) {
        super(parent, SWT.NONE);
        this.layout = new StackLayout();
        this.layout.marginHeight = 0;
        this.layout.marginWidth = 0;
        this.layout.topControl = null; 
        this.content = new ArrayList<Composite>();
        setLayout(this.layout);
    }

    public void addComposite(Composite comp){
        if (comp.getParent().equals(this)){
            this.content.add(comp);
            /*this.layout.topControl = comp;
            layout();*/
        } else {
            this.logger.error("Not child composite is added to MultiContainer");
        }
    }
    
    public void removeComposite(Composite comp){
        if (!(this.content.contains(comp))){
            this.logger.warn("trying to delete not contained composite");
            return;
        }
        this.content.remove(comp);
    }
    
    public void switchTo(Composite comp){
        if (!(this.content.contains(comp))){
            this.logger.warn("trying to switch to not contained composite");
            return;
        }
        this.layout.topControl = comp;
        comp.setVisible(true);
        layout();
    }
    
    public void doubleSwitch(){
        if (this.content.size() != 2){
            this.logger.warn("trying to double switch with not 2 child components");
            return;
        }
        Composite comp = (Composite)this.layout.topControl;
        if (this.content.indexOf(comp) == 0){
            this.layout.topControl = this.content.get(1);
        } else 
            this.layout.topControl = this.content.get(0);
        layout();
    }
    
}
