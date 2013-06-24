/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.ui.spheremanagement.memberaccess.IChangable;

/**
 * @author roman
 *
 */
public interface IChangesDetector {
	
	boolean hasChanges();
	
	void setChanged(boolean value);
	
	void showDialog(IChangable changable);
	
	void rollbackChanges();
	
	void collectChangesAndUpdate();
	
	boolean isLocalTransit();
	
	void setIsLocalTransit(boolean value);

	void showDisposingDialog(IChangable currentChangable);
}
