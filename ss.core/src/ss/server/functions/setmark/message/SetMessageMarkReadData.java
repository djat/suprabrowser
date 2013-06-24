/**
 * 
 */
package ss.server.functions.setmark.message;

import java.util.ArrayList;
import java.util.List;

import ss.common.StringUtils;

/**
 * @author zobo
 *
 */
public class SetMessageMarkReadData extends SetMessageMarkData {
	
	public class MessageData {
		private final String mesageId;
		
		private final String sphereId;

		private MessageData(String mesageId, String sphereId) {
			this.mesageId = mesageId;
			this.sphereId = sphereId;
		}

		public String getMesageId() {
			return this.mesageId;
		}

		public String getSphereId() {
			return this.sphereId;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof MessageData)) {
				return false;
			}
			final MessageData outer = (MessageData)obj;
			return ( StringUtils.equals(getMesageId(), outer.getMesageId()) && 
					StringUtils.equals(getSphereId(), outer.getSphereId()) );
		}

		@Override
		public String toString() {
			return "messageId: " + getMesageId() + ", sphereId: " + getSphereId();
		}
	};

	private static final long serialVersionUID = 3169598193705344433L;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetMessageMarkReadData.class);
	
	private final List<MessageData> list = new ArrayList<MessageData>();

	public void addMessage( final String messageId, final String sphereId ){
		if ( StringUtils.isBlank( messageId ) || StringUtils.isBlank( sphereId ) ){
			logger.error("Data is wrong, one of strings is blank");
			return;
		}
		final MessageData newMessageData = new MessageData( messageId, sphereId );
		if (!this.list.contains( newMessageData )){
			this.list.add( newMessageData );
		}
	}
	
	public boolean isEmpty(){
		return this.list.isEmpty();
	}
	
	public List<MessageData> getList(){
		return this.list;
	}

	@Override
	public String toString() {
		return "SetMessageMarkReadData contains " + (isEmpty() ? "no elements" : (this.list.size() + "elements"));
	}
}
