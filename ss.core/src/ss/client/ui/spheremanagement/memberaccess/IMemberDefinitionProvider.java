/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.Hashtable;
import java.util.List;

import ss.client.ui.spheremanagement.IOutOfDateable;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SupraSphereMember;

/**
 *
 */
public interface IMemberDefinitionProvider extends IOutOfDateable {

	List<SupraSphereMember> getMembers();

	boolean isMemberPresent( String sphereId, String userLogin);
	
	void update( List<SphereMemberBundle> added, List<SphereMemberBundle> removed ); 

	Hashtable<MemberReference,Boolean> getMembersOnlineState();
}
