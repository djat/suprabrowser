/**
 * 
 */
package ss.common.domain.model.preferences;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;

/**
 * @author roman
 *
 */
public class UserPersonalPreferences extends DomainObject {

	private boolean canChangeDefaultTypeForSphere;
	
	private boolean normalMessageSoundPlay;
	
	private boolean normalMessageSoundPlayModify;
	
	private boolean confirmRecieptMessageSoundPlay;
	
	private boolean confirmRecieptMessageSoundPlayModify;
	
	private boolean p2pSpheresDefaultDeliveryType;
	
	private boolean p2pSpheresDefaultDeliveryTypeModify;
	
	private boolean popUpOnTop;
	
	private boolean popUpOnTopModify;
	
	private final DomainReference<LuceneSearchPreferences> luceneSearchPreferencesRef = DomainReference.create(LuceneSearchPreferences.class);
	
	private final DomainReference<EmailForwardingPreferencesUser> emailForwardingPreferencesRef = DomainReference.create(EmailForwardingPreferencesUser.class);

	/**
	 * @return the canChangeDefaultTypeForSphere
	 */
	public boolean isCanChangeDefaultTypeForSphere() {
		return this.canChangeDefaultTypeForSphere;
	}

	/**
	 * @param canChangeDefaultTypeForSphere the canChangeDefaultTypeForSphere to set
	 */
	public void setCanChangeDefaultTypeForSphere(
			boolean canChangeDefaultTypeForSphere) {
		this.canChangeDefaultTypeForSphere = canChangeDefaultTypeForSphere;
	}

	/**
	 * @return the normalMessageSoundPlay
	 */
	public boolean isNormalMessageSoundPlay() {
		return this.normalMessageSoundPlay;
	}

	/**
	 * @param normalMessageSoundPlay the normalMessageSoundPlay to set
	 */
	public void setNormalMessageSoundPlay(boolean normalMessageSoundPlay) {
		this.normalMessageSoundPlay = normalMessageSoundPlay;
	}

	/**
	 * @return the normalMessageSoundPlayModify
	 */
	public boolean isNormalMessageSoundPlayModify() {
		return this.normalMessageSoundPlayModify;
	}

	/**
	 * @param normalMessageSoundPlayModify the normalMessageSoundPlayModify to set
	 */
	public void setNormalMessageSoundPlayModify(boolean normalMessageSoundPlayModify) {
		this.normalMessageSoundPlayModify = normalMessageSoundPlayModify;
	}

	/**
	 * @return the confirmRecieptMessageSoundPlay
	 */
	public boolean isConfirmRecieptMessageSoundPlay() {
		return this.confirmRecieptMessageSoundPlay;
	}

	/**
	 * @param confirmRecieptMessageSoundPlay the confirmRecieptMessageSoundPlay to set
	 */
	public void setConfirmRecieptMessageSoundPlay(
			boolean confirmRecieptMessageSoundPlay) {
		this.confirmRecieptMessageSoundPlay = confirmRecieptMessageSoundPlay;
	}

	/**
	 * @return the confirmRecieptMessageSoundPlayModify
	 */
	public boolean isConfirmRecieptMessageSoundPlayModify() {
		return this.confirmRecieptMessageSoundPlayModify;
	}

	/**
	 * @param confirmRecieptMessageSoundPlayModify the confirmRecieptMessageSoundPlayModify to set
	 */
	public void setConfirmRecieptMessageSoundPlayModify(
			boolean confirmRecieptMessageSoundPlayModify) {
		this.confirmRecieptMessageSoundPlayModify = confirmRecieptMessageSoundPlayModify;
	}

	/**
	 * @return the p2pSpheresDefaultDeliveryType
	 */
	public boolean isP2pSpheresDefaultDeliveryType() {
		return this.p2pSpheresDefaultDeliveryType;
	}

	/**
	 * @param spheresDefaultDeliveryType the p2pSpheresDefaultDeliveryType to set
	 */
	public void setP2pSpheresDefaultDeliveryType(boolean spheresDefaultDeliveryType) {
		this.p2pSpheresDefaultDeliveryType = spheresDefaultDeliveryType;
	}

	/**
	 * @return the p2pSpheresDefaultDeliveryTypeModify
	 */
	public boolean isP2pSpheresDefaultDeliveryTypeModify() {
		return this.p2pSpheresDefaultDeliveryTypeModify;
	}

	/**
	 * @param spheresDefaultDeliveryTypeModify the p2pSpheresDefaultDeliveryTypeModify to set
	 */
	public void setP2pSpheresDefaultDeliveryTypeModify(
			boolean spheresDefaultDeliveryTypeModify) {
		this.p2pSpheresDefaultDeliveryTypeModify = spheresDefaultDeliveryTypeModify;
	}

	/**
	 * @return the popUpOnTop
	 */
	public boolean isPopUpOnTop() {
		return this.popUpOnTop;
	}

	/**
	 * @param popUpOnTop the popUpOnTop to set
	 */
	public void setPopUpOnTop(boolean popUpOnTop) {
		this.popUpOnTop = popUpOnTop;
	}

	/**
	 * @return the popUpOnTopModify
	 */
	public boolean isPopUpOnTopModify() {
		return this.popUpOnTopModify;
	}

	/**
	 * @param popUpOnTopModify the popUpOnTopModify to set
	 */
	public void setPopUpOnTopModify(boolean popUpOnTopModify) {
		this.popUpOnTopModify = popUpOnTopModify;
	}

	/**
	 * @return the luceneSearchRef
	 */
	public DomainReference<LuceneSearchPreferences> getLuceneSearchPreferencesRef() {
		return this.luceneSearchPreferencesRef;
	}
	
	public LuceneSearchPreferences getLuceneSearchPreferences() {
		return this.luceneSearchPreferencesRef.get();
	}
	
	public void setLuceneSearchPreferences(final LuceneSearchPreferences preferences) {
		this.luceneSearchPreferencesRef.set(preferences);
	}

	/**
	 * @return the emailForwardingPreferencesRef
	 */
	public DomainReference<EmailForwardingPreferencesUser> getEmailForwardingPreferencesRef() {
		return this.emailForwardingPreferencesRef;
	}
	
	public EmailForwardingPreferencesUser getEmailForwardingPreferences() {
		return this.emailForwardingPreferencesRef.get();
	}
	
	public void setEmailForwardingPreferences(final EmailForwardingPreferencesUser preferences) {
		this.emailForwardingPreferencesRef.set(preferences);
	}
}
