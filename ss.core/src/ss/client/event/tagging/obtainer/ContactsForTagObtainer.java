/**
 * 
 */
package ss.client.event.tagging.obtainer;

import java.util.ArrayList;
import java.util.List;

import ss.client.networking.DialogsMainCli;
import ss.domainmodel.SearchResultCollection;
import ss.domainmodel.SearchResultObject;

/**
 * @author zobo
 *
 */
public class ContactsForTagObtainer extends AbstractForTagObtainer {
	
	public class ContactForTag implements IObjectForTag {
		
		private final String subject;
		
		private final String messageId;
		
		private final String sphereId;
		
		ContactForTag( final String subject, final String messageId, final String sphereId ){
			this.subject = subject;
			this.messageId = messageId;
			this.sphereId = sphereId;
		}

		public String getSubject() {
			return this.subject;
		}

		public String getMessageId() {
			return this.messageId;
		}

		public String getSphereId() {
			return this.sphereId;
		}

		@Override
		public String toString() {
			return "ContactForTag: subject: " + this.subject + ", messageId: " + this.messageId + ", sphereId: " + this.sphereId;
		}
	}
	
	/**
	 * @param tag
	 * @param client
	 */
	public ContactsForTagObtainer(final String tag, final DialogsMainCli client) {
		super(tag, client);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ContactsForTagObtainer.class);


	
	private List<ContactForTag> list;
	
	public List<ContactForTag> getList(){
		if (this.list != null) {
			return this.list;
		}
		this.list = new ArrayList<ContactForTag>();
		final SearchResultCollection results = getData().getResults();
		if ( results == null ) {
			return this.list;
		}
		for (SearchResultObject result : results) {
			this.list.add( new ContactForTag( result.getSubject(),
					result.getIdCollection().get(0).getMessageId(),
					result.getIdCollection().get(0).getSphereId() ) );
		}
		if (logger.isDebugEnabled()) {
			for (ContactForTag c : this.list) {
				logger.debug( c.toString() );
			}
		}
		return this.list;
	}
	
	/* (non-Javadoc)
	 * @see ss.client.event.tagging.obtainer.AbstractForTagObtainer#getType()
	 */
	@Override
	protected String getType() {
		return "contact";
	}

	public void showContact( final ContactForTag contactForTag ){
		// TODO: implement
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.obtainer.AbstractForTagObtainer#getCount()
	 */
	@Override
	public int getCount() {
		return getList().size();
	}
}
