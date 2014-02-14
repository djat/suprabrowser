/**
 * 
 */
package ss.server.domain.service;

/**
 *
 */
public interface IEntitleCotactForOneSphere extends ISupraSphereFeature {

	void entitleContactForOneSphere(String existingMemberLogin,
			String existingMemberContact, String loginBeingEntitled,
			String contactBeingEntitled);

}