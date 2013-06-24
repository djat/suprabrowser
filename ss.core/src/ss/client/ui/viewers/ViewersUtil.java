package ss.client.ui.viewers;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * @author zobo
 *
 */
public class ViewersUtil {

    public ViewersUtil() {
        super();
        
    }

    public static void centerFrame( final JFrame frame ) {
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension us = frame.getSize();
        frame.setLocation((screen.width - us.width) / 2,
        		(screen.height - us.height) / 2);
    }
}
