/**
 * 
 */
package ss.client.ui.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author roman
 *
 */
public class MessagesTreeContentProvider implements ITreeContentProvider {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeContentProvider.class);

	public Object[] getChildren(Object element) {
		return ((MessagesMutableTreeNode)element).childrenAsArray();
	}

	public Object getParent(Object element) {
		return ((MessagesMutableTreeNode)element).getParent();
	}

	public boolean hasChildren(Object element) {
		return !((MessagesMutableTreeNode)element).isLeaf();
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		return ((MessagesMutableTreeNode)((MessagesTreeModel)inputElement).getRoot()).childrenAsArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}
}