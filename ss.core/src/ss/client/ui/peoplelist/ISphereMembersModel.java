package ss.client.ui.peoplelist;

import java.util.List;
import java.util.Vector;

public interface ISphereMembersModel {

	boolean setAsTyping(String memberName, String replyMessageId);

	boolean setAsNotTyping(String memberName);

	boolean isTyping(String memberName);

	String getReplyMessageId(String memberName);

	void updateMemberOrder(SphereMember member);

	/**
	 * @param member
	 */
	void updateMemberLabel(SphereMember member);

	SphereMember findMember(String memberName);

	/**
	 * Add member to list
	 * 
	 * @param memberName
	 */
	SphereMember addMemberAndNotify(String memberName);

	/**
	 * Add member to list
	 * 
	 * @param memberName
	 */
	SphereMember removeMemberAndNotify(String memberName);

	/**
	 * @return
	 */
	Object getSyncRoot();

	/**
	 * @param index
	 * @return
	 */
	SphereMember getElementAt(int index);
	
	/**
	 * @return Returns memebers names (with asteriks if memeber is online)
	 */
	Vector<String> getMembersNames();

	/**
	 * @param members
	 */
	void setMembers(List<String> members);

}