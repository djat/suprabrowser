/**
 * 
 */
package ss.client.ui.tempComponents;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;

/**
 * @author zobo
 *
 */
public class SupraColors {

    public static Color PREVIEW = new Color(255, 255, 255);//new Color(102, 152, 0);
    
    public static Color LIST = new Color(255, 255, 255);//new Color(102, 152, 0);
    
    public static org.eclipse.swt.graphics.Color DOCKING_LABEL_NORMAL = 
        new org.eclipse.swt.graphics.Color(Display.getDefault(),211,211,195);
    
    public static org.eclipse.swt.graphics.Color DOCKING_LABEL_HIGHLITED = 
        new org.eclipse.swt.graphics.Color(Display.getDefault(),211,211,195);
    
    public static org.eclipse.swt.graphics.Color SASH_BACKGROUND_COLOR = 
        new org.eclipse.swt.graphics.Color(Display.getDefault(),150,150,150);
    /**
     * 
     */
    private SupraColors() {
        super();
    }

}
