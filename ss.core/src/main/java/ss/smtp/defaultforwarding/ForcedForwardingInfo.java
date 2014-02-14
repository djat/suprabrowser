/**
 * 
 */
package ss.smtp.defaultforwarding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author zobo
 *
 */
public class ForcedForwardingInfo implements Serializable{

	private static final long serialVersionUID = -5955641579859197753L;
	
	private boolean allAddressesAs_BCC_insteadOf_CC;
	
	private boolean startThreadMailingConversation = false;
	
	private boolean addMembers;
	
	private boolean addContacts;
	
	private final List<String> receipientAddresses = new ArrayList<String>();

	public ForcedForwardingInfo(final Collection<String> addresses,
			boolean allAddressesAs_BCC_insteadOf_CC) {
		this.receipientAddresses.addAll(addresses);
		this.allAddressesAs_BCC_insteadOf_CC = allAddressesAs_BCC_insteadOf_CC;
	}
	
	public ForcedForwardingInfo(boolean addMembers, boolean addContacts,
			boolean allAddressesAs_BCC_insteadOf_CC) {
		this.addContacts = addContacts;
		this.addMembers = addMembers;
		this.allAddressesAs_BCC_insteadOf_CC = allAddressesAs_BCC_insteadOf_CC;
	}

	public boolean isAddMembers() {
		return this.addMembers;
	}

	public boolean isAddContacts() {
		return this.addContacts;
	}

	public boolean isAllAddressesAs_BCC_insteadOf_CC() {
		return this.allAddressesAs_BCC_insteadOf_CC;
	}

	public boolean isStartThreadMailingConversation() {
		return this.startThreadMailingConversation;
	}

	public void setStartThreadMailingConversation(
			boolean startThreadMailingConversation) {
		this.startThreadMailingConversation = startThreadMailingConversation;
	}

	public void setAllAddressesAs_BCC_insteadOf_CC(
			boolean allAddressesAs_BCC_insteadOf_CC) {
		this.allAddressesAs_BCC_insteadOf_CC = allAddressesAs_BCC_insteadOf_CC;
	}
	
	public List<String> getReciepientAddressesList() {
		return this.receipientAddresses;
	}
	
	public boolean isAddressListEmpty() {
		return this.receipientAddresses.isEmpty();
	}
}
