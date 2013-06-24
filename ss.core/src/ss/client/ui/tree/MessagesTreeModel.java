package ss.client.ui.tree;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import ss.client.ui.MessagesPane;

/**
 * This is a place holder for messages tree model refactoring  
 */

@SuppressWarnings("serial")
class MessagesTreeModel extends DefaultTreeModel{
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeModel.class);
	
	public MessagesTreeModel(MessagesPane pane){
		this(new MessagesMutableTreeNode(pane, "", "", "messages","none", "off"));
	}
	
	public MessagesTreeModel(TreeNode root){
		super(root);
	}
	
	public TreeNode[] getPathToRoot(TreeNode aNode){
		TreeNode[] nodes = super.getPathToRoot(aNode);
		return nodes;
	}
	
	public void removeNodeFromParent(MutableTreeNode node){		
		super.removeNodeFromParent(node);
	}
	
	public void insertNodeInto(MutableTreeNode newChild,
            MutableTreeNode parent, int index){
		
		super.insertNodeInto(newChild, parent, index);		
	}

	/**
	 * Removes node from message tree by message_id if with specified id 
	 * not found nothing happens.
	 * @param builder TODO
	 * @param messageId
	 */
	public void removeNodeByMessageId(String messageId) {
		MessagesMutableTreeNode node = findNodeByMessageId(messageId);
		if (node != null) {
			this.removeNodeFromParent(node);
		}
	}
	
	/**
	 * Find and return node by message id, null if no node found 
	 * @param messageId
	 * @return
	 */
	public MessagesMutableTreeNode findNodeByMessageId(String messageId) {
		MessagesMutableTreeNode node = (MessagesMutableTreeNode) this.getRoot();
		while ((node != null)
				&& (!node.getMessageId().equals(messageId))) {
			node = (MessagesMutableTreeNode) node.getNextNode();
		}
		return node;
	}	
	
	
}