/**
 * 
 */
package ss.client.ui.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTree;

import org.dom4j.Document;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ss.common.ArgumentNullPointerException;
import ss.common.UiUtils;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.Statement;

/**
 *
 */
public class ChangingTree {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangingTree.class);
	
	private JTree tree;
	
	private TreeViewer treeViewer;

	/**
	 * @param model
	 * @param renderer 
	 */
	public ChangingTree(Composite parent, MessagesTreeModel model) {
		if (logger.isDebugEnabled()) {
			logger.debug("start to create tree");
		}
		this.treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI);
		this.treeViewer.setLabelProvider(new MessagesTreeLabelProvider());
		this.treeViewer.setContentProvider(new MessagesTreeContentProvider());
		this.treeViewer.setInput(model);
	}

	/**
	 * @return
	 */
	public JComponent asComponent() {
		return this.tree;
	}

	/**
	 * @param listener
	 */
	public void addTreeSelectionListener(ISelectionChangedListener listener) {
		this.treeViewer.addSelectionChangedListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addMouseListener( org.eclipse.swt.events.MouseListener listener) {
		this.treeViewer.getTree().addMouseListener(listener);
	}

	/**
	 * 
	 */
	public void redraw() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				Object[] expanded = ChangingTree.this.treeViewer.getExpandedElements();
				
				for(Object ex: expanded)
				logger.debug("expanded: "+ex);
				
				List<String> selectionIds = new ArrayList<String>();
				for(TreeItem item : ChangingTree.this.treeViewer.getTree().getSelection()) {
					selectionIds.add(((MessagesMutableTreeNode)item.getData()).getMessageId());
				}
				
				ChangingTree.this.treeViewer.refresh();
				
				ChangingTree.this.treeViewer.setExpandedElements(expanded);
				
				for(String id : selectionIds) {
					setSelectionPath(getNodeById(id));
				}
				ChangingTree.this.treeViewer.getTree().redraw();
			}
		});
	}
	
	
	public void redraw(final MessagesMutableTreeNode node) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				Object[] expanded = ChangingTree.this.treeViewer.getExpandedElements();
				
				List<String> selectionIds = new ArrayList<String>();
				for(TreeItem item : ChangingTree.this.treeViewer.getTree().getSelection()) {
					selectionIds.add(((MessagesMutableTreeNode)item.getData()).getMessageId());
				}
				
				ChangingTree.this.treeViewer.refresh(node);
				
				ChangingTree.this.treeViewer.setExpandedElements(expanded);
				
				for(String id : selectionIds) {
					setSelectionPath(getNodeById(id));
				}
			}
		});
	}

	/**
	 * @return
	 */
	public MessagesMutableTreeNode getLastSelectedPathComponent() {
		if(this.treeViewer.getTree().getSelectionCount() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Selection in Tree is not null, returning");
			}
			return (MessagesMutableTreeNode)this.treeViewer.getTree().getSelection()[0].getData();	
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Selection in Tree is null, returning");
			}
			return null;
		}
	}


	/**
	 * @param temp
	 */
	public void expandElement(final MessagesMutableTreeNode temp) {
		if(temp == null || temp.getParent()==null) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				ChangingTree.this.treeViewer.setExpandedState(temp.getParent(), true);
			}
		});
	}

	/**
	 * @param temp
	 */
	public void setSelectionPath(final MessagesMutableTreeNode temp) {
		if(temp == null) {
			return;
		}
		final TreePath path = new TreePath(temp.getPath());
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				ChangingTree.this.treeViewer.setSelection(new TreeSelection(path));
			}
		});
	}

	/**
	 * @param nodeById
	 */
	public void scrollElementToVisible(final MessagesMutableTreeNode temp) {
		if(temp == null) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				ChangingTree.this.treeViewer.setExpandedState(temp.getParent(), true);
			}
		});
	}
	
	MessagesMutableTreeNode getNodeById(String messageId) {
		if(messageId == null) {
			throw new ArgumentNullPointerException("cannot find node by null id");
		}
		Enumeration enumer = ((MessagesMutableTreeNode)((MessagesTreeModel)this.treeViewer.getInput()).getRoot()).breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
					.nextElement();
			if (tempNode.getMessageId().equals(messageId)) {
				return tempNode;
			}
		}
		return null;
	}

	/**
	 * @param messageId
	 */
	void resetNodeStatus(final String messageId) {
		if(messageId == null) {
			return;
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				MessagesMutableTreeNode node = getNodeById(messageId);
				node.setStatus(MessagesTree.STATUS_OFF);
				redraw(node);
			}
		});
	}

	/**
	 * @return
	 */
	public TreeItem[] getSelectedElements() {
		return this.treeViewer.getTree().getSelection();
	}

	/**
	 * @return
	 */
	public TreeItem getSelectedElement() {
		if(this.treeViewer.getTree().getSelectionCount()<1) {
			return null;
		}
		return this.treeViewer.getTree().getSelection()[0];
	}

	/**
	 * @return
	 */
	public Vector<Document> getSelectedDocs() {
		Vector<Document> docs = new Vector<Document>();
		
		if(this.treeViewer.getTree().getSelectionCount()>0) {
			for(TreeItem item : this.treeViewer.getTree().getSelection()) {
				try {
					Object userObj = ((MessagesMutableTreeNode)item.getData()).getUserObject();
					docs.add(((Statement)userObj).getBindedDocument());
				} catch (NullPointerException ex) {
					logger.error("Can't obtain doc from item: "+item.getText());
				}
			}
		}
		return docs;
	}

	public void scrollToTop() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				final Tree swttree = ChangingTree.this.treeViewer.getTree();
				if (swttree.getItemCount() <= 0){
					if (logger.isDebugEnabled()) {
						logger.debug("No items in tree");
					}
					return;
				}
				final TreeItem item = swttree.getItem(0);
				final TreeItem lastItem = swttree.getItem(swttree.getItemCount()-1);
				if(item!=null) {
					swttree.showItem(lastItem);
					swttree.showItem(item);
					swttree.redraw();
				}
			}
		});
		
	}

	/**
	 * @return
	 */
	public List<MessagesMutableTreeNode> collectAllNonRootKeywords() {
		List<MessagesMutableTreeNode> nodes = new ArrayList<MessagesMutableTreeNode>();
		Enumeration enumer = ((MessagesMutableTreeNode)((MessagesTreeModel)this.treeViewer.getInput()).getRoot()).breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
					.nextElement();
			if (tempNode.getType().equals("keywords")) {
				if (tempNode.getRoot() != tempNode.getParent()){
					nodes.add( tempNode );
				}
			}
		}
		return nodes;
	}
	
	public List<MessagesMutableTreeNode> collectAllKeywordsInstances( final KeywordStatement st ) {
		List<MessagesMutableTreeNode> nodes = new ArrayList<MessagesMutableTreeNode>();
		Enumeration enumer = ((MessagesMutableTreeNode)((MessagesTreeModel)this.treeViewer.getInput()).getRoot()).breadthFirstEnumeration();
		String messageId = st.getMessageId();
		while (enumer.hasMoreElements()) {
			MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
					.nextElement();
			if (tempNode.getType().equals("keywords")) {
				if ( tempNode.getMessageId().equals( messageId ) ) {
					nodes.add( tempNode );
				}
			}
		}
		return nodes;
	}

	/**
	 * @param doc
	 * @return
	 */
	public MessagesMutableTreeNode getNodeByDocument( final Document doc ) {
		if( doc == null ) {
			throw new ArgumentNullPointerException("cannot find node by null Document");
		}
		final Enumeration enumer = ((MessagesMutableTreeNode)((MessagesTreeModel)this.treeViewer.getInput()).getRoot()).breadthFirstEnumeration();
		while (enumer.hasMoreElements()) {
			MessagesMutableTreeNode tempNode = (MessagesMutableTreeNode) enumer
					.nextElement();
			if (tempNode.returnDoc().equals( doc )) {
				return tempNode;
			}
		}
		return null;
	}
}
