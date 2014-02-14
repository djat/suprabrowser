/**
 * 
 */
package ss.refactor.supraspheredoc.old;

import org.dom4j.Document;

import ss.common.SSProtocolConstants;
import ss.domainmodel.SupraSphereStatement;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.server.db.XMLDB;
import ss.server.domain.service.ISupraSphereFeature;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.Expression;
import ss.server.networking.util.Filter;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.server.networking.util.UnaryOperation;

/**
 *
 */
@Refactoring(classify=SupraSphereRefactor.class)
public abstract class AbstractSsDocFeature implements ISupraSphereFeature {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractSsDocFeature.class);

	protected DialogsMainPeer peer;
	
	protected XMLDB xmldb;
	
	private Utils utils;
	
	/**
	 * @return the peer
	 */
	public DialogsMainPeer getPeer() {
		return this.peer;
	}

	/**
	 * @return the xmldb
	 */
	public XMLDB getXmldb() {
		return this.xmldb;
	}

	/**
	 * @param peer the peer to set
	 */
	public void setPeer(DialogsMainPeer peer) {
		this.peer = peer;
	}

	/**
	 * @param xmldb the xmldb to set
	 */
	public void setXmldb(XMLDB xmldb) {
		this.xmldb = xmldb;
	}

	/**
	 * @param utils the utils to set
	 */
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	public SupraSphereStatement getSupraSphere() {
		return getUtils().getSupraSphere();
	}
	
	public Document getSupraSphereDocument() {
		return getUtils().getSupraSphereDocument();

	}	

	/**
	 * @return
	 */
	protected String getSupraSphereSystemId() {
		return (String) this.peer.getSession().get("supra_sphere");
	}

	/**
	 * @return
	 */
	public Utils getUtils() {
		return this.utils;
	}
	
	
	/**
	 * @param supraSphereDoc
	 * @param session
	 */
	protected void sendUpdateVerify(Document supraSphereDocument, String sessionId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Trying..." + sessionId);
		}
		this.peer.getVerifyAuth().setSphereDocument(supraSphereDocument);

		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.UPDATE_VERIFY_SPHERE_DOCUMENT);
		dmpResponse.setDocumentValue(SC.SUPRA_SPHERE_DOCUMENT,
				supraSphereDocument);
		Filter filter = new Filter();
		filter.add(new Expression(HandlerKey.SESSION, sessionId,
				UnaryOperation.NOT));
		FilteredHandlers filteredHandlers = new FilteredHandlers(filter,
				DialogsMainPeerManager.INSTANCE.getHandlers());
		for (DialogsMainPeer handler : filteredHandlers) {
			if (logger.isDebugEnabled()) {
				logger.debug("Sending to this handler..." + handler.getName());
			}
			handler.getVerifyAuth().setSphereDocument(supraSphereDocument);
			handler.sendFromQueue(dmpResponse);
		}
	}
}
