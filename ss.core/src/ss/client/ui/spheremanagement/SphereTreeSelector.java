/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author roman
 *
 */
public class SphereTreeSelector {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereTreeSelector.class);
	
	private final TreeViewer tv;
	
	private final Tree tree;
	
	private final List<ManagedSphere> oldSpheres;

	private List<ManagedSphere> newSpheres;
	
	private final Object[] expanded;
	
	Hashtable<String, TreeItem> sphereTable = new Hashtable<String, TreeItem>();
	
	public SphereTreeSelector(TreeViewer tv) {
		this.tv = tv;
		this.tree = this.tv.getTree();
		this.expanded = this.tv.getExpandedElements();
		
		this.oldSpheres = getAllTreeSpheresFromTree();
	}

	/**
	 * @param oldSpheres
	 * @param newSpheres
	 */
	public void findAndSelectNewSphere() {
		this.newSpheres = getAllTreeSpheresFromTree();
		
		recoverExpanding();
		
		for(ManagedSphere newSphere : this.newSpheres) {
			if(newSphere!=null) {
				String id = newSphere.getId();
				if(!isOldSphere(id)) {
					findAndSelectItemForSphere(this.tree.getItems(), newSphere);
					return;
				}
			}
		}
	}
	
	private void findAndSelectItemForSphere(TreeItem[] items, ManagedSphere newSphere) {
		for(TreeItem item : items) {
			if(item.getData().equals(newSphere)) {
				this.tree.setSelection(item);
				
				this.tree.showSelection();
	
				return;
			} else {
				findAndSelectItemForSphere(item.getItems(), newSphere);
			}
		}
	}

	private void recoverExpanding() {
		if(this.expanded!=null && this.expanded.length>0) {
			for(Object o : this.expanded) {
				expandItem(this.sphereTable.get(((ManagedSphere)o).getId()));
			}
		}
	}

	/**
	 * @param item
	 */
	private void expandItem(TreeItem item) {
		if(item==null) {
			return;
		}
		if(item.getItemCount()>0) {
			this.tree.showItem(item.getItems()[0]);
		} else {
			this.tree.showItem(item);
		}
	}

	private boolean isOldSphere(String id) {
		for(ManagedSphere oldSphere : this.oldSpheres) {
			if(oldSphere.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	public List<ManagedSphere> getAllTreeSpheres() {
		ManagedSphere rootSphere = (ManagedSphere)((Object[])this.tv.getInput())[0];
		if(rootSphere!=null) {
			return rootSphere.listDescendants();
		}
		return null;
	}
	
	public List<ManagedSphere> getAllTreeSpheresFromTree() {
		List<ManagedSphere> allSpheres = new ArrayList<ManagedSphere>();
		this.sphereTable.clear();
		this.tv.expandAll();
		getSpheresFromItems(this.tree.getItems(), allSpheres);
		this.tv.collapseAll();
		return allSpheres;
	}
	
	private void getSpheresFromItems(TreeItem[] items, List<ManagedSphere> spheres) {
		for(TreeItem item : items) {
			this.sphereTable.put(((ManagedSphere)item.getData()).getId(), item);
			spheres.add((ManagedSphere)item.getData());
			if(item.getItems()!=null && item.getItemCount()>0) {
				getSpheresFromItems(item.getItems(), spheres);
			}
		}
	}
}
