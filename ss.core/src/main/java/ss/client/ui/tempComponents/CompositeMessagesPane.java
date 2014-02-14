/**
 * 
 */
package ss.client.ui.tempComponents;

import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.UiUtils;

/**
 * @author zobo
 * 
 */
public class CompositeMessagesPane extends Composite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CompositeMessagesPane.class);
	
    /**
     * @param arg0
     * @param arg1
     */
    public CompositeMessagesPane(Composite arg0) {
        super(arg0, SWT.NONE);
    }
    
    public static MessagesPane createMessagesPane(final Hashtable session,
    		final SupraSphereFrame supraFrame, final String type, final DialogsMainCli cli){
        return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
			public MessagesPane call() throws Exception {
				return new MessagesPane(session, supraFrame, type, cli);
			}
        });
    }
    
    public static MessagesPane createMessagesPane(final Hashtable session,
            final SupraSphereFrame supraFrame, final String type, final DialogsMainCli cli, final Document sphereDefinition){
        return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
			public MessagesPane call() throws Exception {
				if (supraFrame.tabbedPane == null){
            		logger.error("tabbedPane is null");
            	} else {
            		logger.info("tabbedPane is not null");
            	}
				return new MessagesPane(session, supraFrame, type, cli, sphereDefinition);
			}
        });
    }
    
    public static MessagesPane createMessagesPane(final Hashtable session,
            final SupraSphereFrame supraFrame, final String type, final DialogsMainCli cli,
            final double div0, final double div1, final double div2, final double div3){
        return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
			public MessagesPane call() throws Exception {
				return new MessagesPane(session, supraFrame, type, cli,
                        div0, div1, div2, div3);
			}
        });
    }
    
    public static MessagesPane createMessagesPane(final Hashtable session,
            final SupraSphereFrame supraFrame, final String type, final DialogsMainCli cli, final Document sphereDefinition,
            final double div0, final double div1, final double div2, final double div3){
        return UiUtils.swtEvaluate(new Callable<MessagesPane>() {
			public MessagesPane call() throws Exception {
				return new MessagesPane(session, supraFrame, type, cli, sphereDefinition,
                        div0, div1, div2, div3);
			}
        });
    }

    public void repaint() {
    	UiUtils.swtInvoke(new Runnable() {
            public void run() {
                redraw();
            }
        });
    }

    public Rectangle getBoundsNew() {
    	org.eclipse.swt.graphics.Rectangle rect = getBounds();
        Rectangle boundsNew = new Rectangle();
        boundsNew.x = rect.x;
        boundsNew.y = rect.y;
        boundsNew.width = rect.width;
        boundsNew.height = rect.height;
        return boundsNew;
    	
    }
}
