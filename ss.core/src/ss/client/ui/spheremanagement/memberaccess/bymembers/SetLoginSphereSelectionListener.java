/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.bymembers;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;


/**
 * @author roman
 *
 */
public class SetLoginSphereSelectionListener implements SelectionListener {

	private CheckedSpheresHieararchyComposite treeComp;
	
	public SetLoginSphereSelectionListener(CheckedSpheresHieararchyComposite treeComp) {
		this.treeComp = treeComp;
	}
	
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}
	
	public void widgetSelected(SelectionEvent se) {
		TreeItem[] selection = this.treeComp.getTreeSelection();
		if(selection == null || selection.length==0) {
			return;
		}
		
		TreeItem tempItem = selection[0];
		this.treeComp.setAsLoginSphere(tempItem);
		this.treeComp.refreshTree();		
	}
}
