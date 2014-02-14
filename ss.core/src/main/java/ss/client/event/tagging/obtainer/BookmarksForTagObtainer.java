/**
 * 
 */
package ss.client.event.tagging.obtainer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.getters.GetSpecificIdCommand;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.SearchResultCollection;
import ss.domainmodel.SearchResultObject;
import ss.server.networking.SC;

/**
 * @author zobo
 *
 */
public class BookmarksForTagObtainer extends AbstractForTagObtainer {
	
	public class BookmarkForTag implements IObjectForTag {
		
		private final String URL;
		
		private final String subject;
		
		private final String messageId;
		
		private final String sphereId;
		
		BookmarkForTag( final String URL, final String subject, final String messageId, final String sphereId ){
			this.URL = URL;
			this.subject = subject;
			this.messageId = messageId;
			this.sphereId = sphereId;
		}

		public String getSubject() {
			return this.subject;
		}

		public String getURL() {
			return this.URL;
		}

		public String getMessageId() {
			return this.messageId;
		}

		public String getSphereId() {
			return this.sphereId;
		}
		
		@Override
		public String toString() {
			return "BookmarkForTag: subject: " + this.subject + ", URL: " + this.URL +", messageId: " + this.messageId + ", sphereId: " + this.sphereId;
		}
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BookmarksForTagObtainer.class);
	
	private List<BookmarkForTag> list;
	
	public BookmarksForTagObtainer( final String tag, final DialogsMainCli client ){
		super( tag, client );
	}
	
	public List<BookmarkForTag> getList(){
		if (this.list != null) {
			return this.list;
		}
		this.list = new ArrayList<BookmarkForTag>();
		final SearchResultCollection results = getData().getResults();
		if ( results == null ) {
			return this.list;
		}
		for (SearchResultObject result : results) {
			this.list.add( new BookmarkForTag( result.getAddress(), result.getSubject(),
					result.getIdCollection().get(0).getMessageId(),
					result.getIdCollection().get(0).getSphereId() ) );
		}
		if (logger.isDebugEnabled()) {
			for (BookmarkForTag b : this.list) {
				logger.debug( b.toString() );
			}
		}
		return this.list;
	}
	
	@Override
	protected String getType(){
		return "bookmark";
	}

	/**
	 * @param bookmarkSelected
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BookmarkStatement getBookmarkStatement( final BookmarkForTag bookmarkSelected ) {
		final GetSpecificIdCommand command = new GetSpecificIdCommand();
		final Hashtable session = new Hashtable();
		session.put(SC.SPHERE_ID, bookmarkSelected.getSphereId());
		command.putSessionArg(session);
		command.putArg(SC.MESSAGE_ID, bookmarkSelected.getMessageId());
		final Document doc = command.execute(getClient(), AbstractDocument.class);
		return (doc == null ? null : BookmarkStatement.wrap( doc ));
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.obtainer.AbstractForTagObtainer#getCount()
	 */
	@Override
	public int getCount() {
		return getList().size();
	}
}
