/**
 * 
 */
package ss.client.ui.tree;

import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public class NodeDocumentsBundle {
	Statement nodeStatement = null;
	
	Statement parentStatement = null;
	
	public NodeDocumentsBundle(Statement nodeStatement, Statement parentStatement) {
		this.nodeStatement = nodeStatement;
		this.parentStatement = parentStatement;
	}
	
	public Statement getNodeStatement() {
		return this.nodeStatement;
	}
	
	public Statement getParentStatement() {
		return this.parentStatement;
	}
}
