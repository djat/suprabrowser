/**
 * 
 */
package ss.server.functions.setmark.message;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.domainmodel.Statement;
import ss.server.functions.setmark.common.SetReadOperations;
import ss.server.functions.setmark.message.SetMessageMarkReadData.MessageData;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SetMessageMarkReadProcedure extends SetMessageMarkProcedure<SetMessageMarkReadData>{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetMessageMarkReadProcedure.class);
	
	/**
	 * @param data
	 * @param peer
	 */
	public SetMessageMarkReadProcedure(SetMessageMarkReadData data,
			DialogsMainPeer peer) {
		super(data, peer);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see ss.server.functions.setmark.SetMarkProcedure#proccesImpl()
	 */
	@Override
	protected void proccesImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug(" -- SetMessageMarkReadProcedure performed");
		}
		if (getData().isEmpty()){
			logger.warn("Data with no messages to mark recieved");
			return;
		}
		final String contactName = getPeer().getUserContactName();
		if (StringUtils.isBlank( contactName )) {
			logger.error( "ContactName is blank" );
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Data : " + getData());
		}
		for (MessageData message : getData().getList()) {
			final Document doc = getPeer().getXmldb().getSpecificMessage(message.getMesageId(), message.getSphereId());
			if ( doc != null ) {
				if (logger.isDebugEnabled()) {
					logger.debug("Doc is voting: " + Statement.wrap(doc).getSubject());
				}
				if (SetReadOperations.isNeededToVote(doc, contactName)){
					getPeer().getXmldb().voteDoc(doc, message.getSphereId(), contactName);
				}
			} else {
				logger.error("Doc is null for message " + message);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(" -- SetMessageMarkReadProcedure ended");
		}
	}

}
