/**
 * 
 */
package ss.domainmodel.preferences;

import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesUser;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 * 
 */
public class UserPersonalPreferences extends XmlEntityObject {

	private final ISimpleEntityProperty canChangeDefaultTypeForSphere = super
			.createAttributeProperty("globalPreferences/canChangeDefaultTypeForSphere/@value");

	private final ISimpleEntityProperty NormalMessageSoundPlay = super
			.createAttributeProperty("globalPreferences/NormalMessageSoundPlay/@value");

	private final ISimpleEntityProperty NormalMessageSoundPlayModify = super
			.createAttributeProperty("globalPreferences/NormalMessageSoundPlay/@modify");

	private final ISimpleEntityProperty ConfirmRecieptMessageSoundPlay = super
			.createAttributeProperty("globalPreferences/ConfirmRecieptMessageSoundPlay/@value");

	private final ISimpleEntityProperty ConfirmRecieptMessageSoundPlayModify = super
			.createAttributeProperty("globalPreferences/ConfirmRecieptMessageSoundPlay/@modify");

	private final ISimpleEntityProperty p2pSpheresDefaultDeliveryType = super
			.createAttributeProperty("globalPreferences/p2pSpheresDefaultDeliveryType/@value");

	private final ISimpleEntityProperty p2pSpheresDefaultDeliveryTypeModify = super
			.createAttributeProperty("globalPreferences/p2pSpheresDefaultDeliveryType/@modify");

	private final ISimpleEntityProperty popUpOnTop = super
			.createAttributeProperty("globalPreferences/popUpOnTop/@value");

	private final ISimpleEntityProperty popUpOnTopModify = super
			.createAttributeProperty("globalPreferences/popUpOnTop/@modify");

	private final IComplexEntityProperty<LuceneSearchPreferences> searchPreferences = super
			.createComplexProperty("searchPreferences",
					LuceneSearchPreferences.class);
	
	private final IComplexEntityProperty<EmailForwardingPreferencesUser> emailForwardingPreferences = super
			.createComplexProperty("emailForwardingPreferences",
					EmailForwardingPreferencesUser.class);
	
	public UserPersonalPreferences() {
		super();
	}

	/**
	 * @return the canChangeDefaultTypeForSphere
	 */
	public boolean isCanChangeDefaultTypeForSphere() {
		return this.canChangeDefaultTypeForSphere.getBooleanValue(true);
	}

	/**
	 * @param canChangeDefaultTypeForSphere
	 *            the canChangeDefaultTypeForSphere to set
	 */
	public void setCanChangeDefaultTypeForSphere(
			boolean canChangeDefaultTypeForSphere) {
		this.canChangeDefaultTypeForSphere
				.setBooleanValue(canChangeDefaultTypeForSphere);
	}

	public boolean isConfirmRecieptMessageSoundPlay() {
		return this.ConfirmRecieptMessageSoundPlay.getBooleanValue();
	}

	/**
	 * @param confirmRecieptMessageSoundPlay
	 *            the confirmRecieptMessageSoundPlay to set
	 */
	public void setConfirmRecieptMessageSoundPlay(
			boolean confirmRecieptMessageSoundPlay) {
		this.ConfirmRecieptMessageSoundPlay
				.setBooleanValue(confirmRecieptMessageSoundPlay);
	}

	public boolean isConfirmRecieptMessageSoundPlayModify() {
		return this.ConfirmRecieptMessageSoundPlayModify.getBooleanValue(true);
	}

	/**
	 * @param confirmRecieptMessageSoundPlayModify
	 *            the confirmRecieptMessageSoundPlayModify to set
	 */
	public void setConfirmRecieptMessageSoundPlayModify(
			boolean confirmRecieptMessageSoundPlayModify) {
		this.ConfirmRecieptMessageSoundPlayModify
				.setBooleanValue(confirmRecieptMessageSoundPlayModify);
	}

	/**
	 * @return the normalMessageSoundPlay
	 */
	public boolean isNormalMessageSoundPlay() {
		return this.NormalMessageSoundPlay.getBooleanValue();
	}

	/**
	 * @param normalMessageSoundPlay
	 *            the normalMessageSoundPlay to set
	 */
	public void setNormalMessageSoundPlay(boolean normalMessageSoundPlay) {
		this.NormalMessageSoundPlay.setBooleanValue(normalMessageSoundPlay);
	}

	/**
	 * @return the normalMessageSoundPlayModify
	 */
	public boolean isNormalMessageSoundPlayModify() {
		return this.NormalMessageSoundPlayModify.getBooleanValue(true);
	}

	/**
	 * @param normalMessageSoundPlayModify
	 *            the normalMessageSoundPlayModify to set
	 */
	public void setNormalMessageSoundPlayModify(
			boolean normalMessageSoundPlayModify) {
		this.NormalMessageSoundPlayModify
				.setBooleanValue(normalMessageSoundPlayModify);
	}

	/**
	 * @return the p2pSpheresDefaultDeliveryType
	 */
	public String getP2pSpheresDefaultDeliveryType() {
		return this.p2pSpheresDefaultDeliveryType.getValue();
	}

	/**
	 * @return the p2pSpheresDefaultDeliveryTypeModify
	 */
	public boolean isP2pSpheresDefaultDeliveryTypeModify() {
		return this.p2pSpheresDefaultDeliveryTypeModify.getBooleanValue(true);
	}

	/**
	 * @param spheresDefaultDeliveryType
	 *            the p2pSpheresDefaultDeliveryType to set
	 */
	public void setP2pSpheresDefaultDeliveryType(
			String spheresDefaultDeliveryType) {
		this.p2pSpheresDefaultDeliveryType.setValue(spheresDefaultDeliveryType);
	}

	/**
	 * @param spheresDefaultDeliveryTypeModify
	 *            the p2pSpheresDefaultDeliveryTypeModify to set
	 */
	public void setP2pSpheresDefaultDeliveryTypeModify(
			boolean spheresDefaultDeliveryTypeModify) {
		this.p2pSpheresDefaultDeliveryTypeModify
				.setBooleanValue(spheresDefaultDeliveryTypeModify);
	}

	/**
	 * @return the popUpOnTopModify
	 */
	public boolean ispopUpOnTopModify() {
		return this.popUpOnTopModify.getBooleanValue(true);
	}

	/**
	 * @param popUpOnTopModify
	 *            the popUpOnTopModify to set
	 */
	public void setpopUpOnTopModify(boolean popUpOnTopModify) {
		this.popUpOnTopModify.setBooleanValue(popUpOnTopModify);
	}

	/**
	 * @return the popUpOnTop
	 */
	public boolean ispopUpOnTop() {
		return this.popUpOnTop.getBooleanValue(true);
	}

	/**
	 * @param popUpOnTop
	 *            the popUpOnTop to set
	 */
	public void setpopUpOnTop(boolean popUpOnTop) {
		this.popUpOnTop.setBooleanValue(popUpOnTop);
	}

	public LuceneSearchPreferences getSearchPreferences() {
		return this.searchPreferences.getValue();
	}

	/**
	 * @return the emailForwardingPreferences
	 */
	public EmailForwardingPreferencesUser getEmailForwardingPreferences() {
		return this.emailForwardingPreferences.getValue();
	}	
	
}
