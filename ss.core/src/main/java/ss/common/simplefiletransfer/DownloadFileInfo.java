package ss.common.simplefiletransfer;

import java.io.Serializable;

public class DownloadFileInfo implements Serializable {

	private final String messageId;
	
	private static final long serialVersionUID = -24752322363582393L;

	
	public DownloadFileInfo(final String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageId() {
		return this.messageId;
	}
}
