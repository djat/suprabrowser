/**
 * 
 */
package ss.smtp.defaultforwarding;

import ss.client.ui.email.AttachedFileCollection;

/**
 * @author zobo
 *
 */
public class ForsedForwardingData {
	
	private final ForcedForwardingInfo info;

	private final AttachedFileCollection attachedFileCollection;
	
	public ForsedForwardingData(ForcedForwardingInfo info,
			AttachedFileCollection attachedFileCollection) {
		this.info = info;
		this.attachedFileCollection = attachedFileCollection;
	}

	public boolean isAddMembers() {
		return this.info.isAddMembers();
	}

	public boolean isAddContacts() {
		return this.info.isAddContacts();
	}

	public boolean isAllAddressesAs_BCC_insteadOf_CC() {
		return this.info.isAllAddressesAs_BCC_insteadOf_CC();
	}

	public AttachedFileCollection getAttachedFileCollection() {
		return this.attachedFileCollection;
	}
	
	public ForcedForwardingInfo getInfo(){
		return this.info;
	}
}
