/**
 * 
 */
package ss.client.ui.tempComponents;

import ss.client.ui.docking.SupraTableDocking;
import ss.common.UiUtils;

/**
 * @author zobo
 *
 */
public class SupraTableStateListener {

    private SupraTableDocking supraTableDocking = null;

    /**
     * 
     */
    public SupraTableStateListener(SupraTableDocking supraTableDocking) {
        super();
        this.supraTableDocking  = supraTableDocking;
    }
    
    public void rowsChanged(){
        UiUtils.swtBeginInvoke(new Runnable() {
            public void run() {
                SupraTableStateListener.this.supraTableDocking.setInfoToLabel();
            }
        });
    }
}
