/**
 * 
 */
package ss.util;

import org.apache.log4j.Logger;

import ss.client.networking.DialogsMainCli;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.configuration.ModerateAccessMember;
import ss.domainmodel.configuration.ModerationAccessModel;
import ss.domainmodel.configuration.ModerationAccessModelList;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
public class AdministrationUtils {
	
	private static final Logger logger = SSLogger
	.getLogger(AdministrationUtils.class);

	public static boolean canModerate(DialogsMainCli client, final String systemName, final String name) {
		if( client.getVerifyAuth()!=null && client.getVerifyAuth().isAdmin() ) {
			return true;
		}
		if ( client.isAdmin() ) {
			return true;
		}
		if (name == null) {
			return false;
		}
		ModerationAccessModelList cdAccessList = null;
		try {
			cdAccessList = SsDomain.CONFIGURATION
				.getMainConfigurationValue().getClubdealModerateAccesses();
			if ( cdAccessList == null ) {
				throw new NullPointerException("cdAccessList is null");
			}
		} catch (Throwable ex) {
			logger.error("Cannot reach configuration" ,ex);
			return false;
		}
		ModerationAccessModel cdAccess = cdAccessList.getBySystemName(systemName);
		if ( cdAccess == null ) {
			logger.error("cdAccess is null for systemName : " + systemName);
			return false;
		}
		String ownName = (String) client.session
				.get(SessionConstants.REAL_NAME);
		ModerateAccessMember ownMember = cdAccess.getMemberList()
				.getMemberByContactName(ownName);
		if (ownMember == null || !ownMember.isModerator()) {
			return false;
		}
		ModerateAccessMember member = cdAccess.getMemberList()
				.getMemberByContactName(name);
		if (member != null && member.isModerator() && !name.equals(ownName)) {
			return false;
		}
		return true;
	}
}
