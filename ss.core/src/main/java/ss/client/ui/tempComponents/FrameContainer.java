/**
 * 
 */
package ss.client.ui.tempComponents;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author zobo
 *
 */
public class FrameContainer extends Composite{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FrameContainer.class);
	
    private Frame frame;
    
    private Component component;

    /**
     * 
     */
    public FrameContainer(Composite parent, Component component) {
        super(parent,SWT.EMBEDDED);
        this.component = component;
        addComponent(this.component);
    }

    private void addComponent(Component component) {
        this.frame = SWT_AWT.new_Frame(this);
        this.frame.setLayout(new BorderLayout());

        this.frame.add(component, BorderLayout.CENTER);
        component.setVisible(true);
        this.frame.setVisible(true);
    }
	
	@Override
	public void setBounds(int arg0, int arg1, int arg2, int arg3) {
		FrameContainer.this.frame.setSize(arg2, arg3);
		super.setBounds(arg0, arg1, arg2, arg3);
	}
}
