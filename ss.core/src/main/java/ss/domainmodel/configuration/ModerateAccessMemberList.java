/**
 * 
 */
package ss.domainmodel.configuration;

import org.apache.log4j.Logger;

import ss.framework.entities.xmlentities.XmlListEntityObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ModerateAccessMemberList extends XmlListEntityObject<ModerateAccessMember> {

	private static final Logger logger = SSLogger.getLogger(ModerateAccessMemberList.class);
	
	public ModerateAccessMemberList() {
		super(ModerateAccessMember.class, ModerateAccessMember.ROOT_ELEMENT_NAME );
	}
	
	public void addMember(final ModerateAccessMember member) {
		if(member==null) {
			logger.error("can't add null member");
			return;
		}
		super.internalAdd(member);
	}
	
	public void removeMember(final ModerateAccessMember member) {
		if(member==null) {
			logger.error("Can't remove null member");
			return;
		}
		super.internalRemove(member);
	}
	
	public void removeMember(final String contactName) { 
		removeMember(getMemberByContactName(contactName));
	}
	
	public ModerateAccessMember getMemberByContactName(final String contactName) {
		for(ModerateAccessMember member : this) {
			if(member.getContactName().equals(contactName)) {
				return member;
			}
		}
		return null;
	}
	
//	public ModerateAccessMember getMemberByLoginName(final String loginName) {
//		for(ModerateAccessMember member : this) {
//			if(member.getLoginName().equals(loginName)) {
//				return member;
//			}
//		}
//		return null;
//	}

	/**
	 * 
	 */
	public void clear() {
		super.internalClear();
	}
}
