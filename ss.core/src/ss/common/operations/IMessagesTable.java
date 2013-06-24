/**
 * 
 */
package ss.common.operations;

import java.util.List;

import ss.domainmodel.KeywordStatement;
import ss.domainmodel.Statement;

/**
 * @author roman
 *
 */
public interface IMessagesTable {
	
	public void replaceStatement(Statement statement);
	
	public void addStatement(Statement statement);
	
	public void removeStatement(String id);
	
	public void hiddenRemoveStatement(String id);
	
	public void findMessagesToPopup();
	
	public Statement getSelectedElement();
	
	public void selectElement(String id);
	
	public void removeAllNonRootKeywords();
	
	public void updateKeyword( final KeywordStatement st );
	
	public void clear();
	
	public void scrollToTop();
	
	public int getRowCount();
	
	public List getAllMessages();
	
	public void setInput(List<Statement> input);

	/**
	 * 
	 */
	public void refresh();
	
	public Object getInput();
	
	public void update();

}
