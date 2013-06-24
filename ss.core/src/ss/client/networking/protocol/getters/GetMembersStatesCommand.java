package ss.client.networking.protocol.getters;

import java.util.List;

public class GetMembersStatesCommand extends AbstractGetterCommand {

	/**
	 * 
	 */
	private static final String CONTACTS = "CONTACTS";
	/**
	 * 
	 */
	private static final long serialVersionUID = -1762919527361268215L;

	/**
	 * 
	 */
	public GetMembersStatesCommand( List<String> contactNames ) {
		super();
		for (String contactName : contactNames) {
			addMember(contactName);
		}
	}

	private synchronized void addMember( String contactName ) {
		getContacts().add( contactName );
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<String> getContacts() {
		return getLazyList( CONTACTS );
	}
	
}
