/**
 * 
 */
package ss.common.networking2;

import ss.common.IdentityUtils;

/**
 *
 */
public class ProtocolUtils {

	/**
	 * @param groupName
	 * @param userLogin
	 */
	public static String generateProtocolDisplayName(String groupName, String userLogin) {
		return IdentityUtils.getNextRuntimeId( groupName + "[" + userLogin + "]" );
	}

}
