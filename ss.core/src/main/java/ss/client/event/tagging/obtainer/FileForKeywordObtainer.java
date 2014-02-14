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
public class FileForKeywordObtainer extends AbstractForTagObtainer {

	public class FilesForTag implements IObjectForTag {
		
		private final String subject;
		
		private final String messageId;
		
		private final String sphereId;
		
		FilesForTag( final String subject, final String messageId, final String sphereId ){
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
			return "FilesForTag: subject: " + this.subject + ", messageId: " + this.messageId + ", sphereId: " + this.sphereId;
		}
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileForKeywordObtainer.class);
	
	private List<FilesForTag> list;
	
	public List<FilesForTag> getList(){
		if (this.list != null) {
			return this.list;
		}
		this.list = new ArrayList<FilesForTag>();
		final SearchResultCollection results = getData().getResults();
		if ( results == null ) {
			return this.list;
		}
		for (SearchResultObject result : results) {
			this.list.add( new FilesForTag( result.getSubject(),
					result.getIdCollection().get(0).getMessageId(),
					result.getIdCollection().get(0).getSphereId() ) );
		}
		if (logger.isDebugEnabled()) {
			for (FilesForTag f : this.list) {
				logger.debug( f.toString() );
			}
		}
		return this.list;
	}
	
	/**
	 * @param tag
	 * @param client
	 */
	public FileForKeywordObtainer(String tag, DialogsMainCli client) {
		super(tag, client);
	}

	/* (non-Javadoc)
	 * @see ss.client.event.tagging.obtainer.AbstractForTagObtainer#getType()
	 */
	@Override
	protected String getType() {
		return "file";
	}

	public void saveFile( final FilesForTag fileForTag ){
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
