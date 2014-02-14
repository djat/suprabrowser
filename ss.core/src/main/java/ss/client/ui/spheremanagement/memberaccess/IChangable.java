/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import ss.client.ui.preferences.changesdetector.IChangesDetector;

/**
 * @author roman
 *
 */
public interface IChangable {

	void performFinalAction();
	
	void revertSelection();
	
	IChangesDetector getDetector();

	void jumpToNextItem();
}
