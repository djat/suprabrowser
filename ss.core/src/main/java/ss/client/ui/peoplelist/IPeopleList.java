/**
 * 
 */
package ss.client.ui.peoplelist;

import java.util.Vector;

import org.dom4j.Document;

/**
 *
 */
public interface IPeopleList extends IMemberSelection {
	
	String getSelectedMemberName();
	
	Vector<String> getSelectedMembersNames();
	
	void setMembers(Vector<String> members);
	
	void update(boolean clearSelection);
	
	void updateRefresh(boolean clearSelection);
	
	Document getSelectedDocument();
	
	void updateMember(final SphereMember member);
	
	void extractNotExistedMembers(Vector<String> targetList);
	
	boolean setAsTyping(String typingUser, String replyId);
	
	void refreshMemberPresence(String memberName, boolean isOnline);
	
	boolean setAsNotTyping(String typingUser);
	
	Vector<String> getMembers();
	
	void addMouseListener();
	
	SphereMember findMember(String memberName);
	
}
