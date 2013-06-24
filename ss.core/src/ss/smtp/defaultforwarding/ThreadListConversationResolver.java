/**
 * 
 */
package ss.smtp.defaultforwarding;

import java.util.ArrayList;
import java.util.List;

import net.sf.vcard4j.java.type.ADR;

import org.dom4j.Document;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.StringUtils;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.Statement;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class ThreadListConversationResolver {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ThreadListConversationResolver.class);
	
	public static boolean addSpecificThreadConversation( final String sphereId, final SpherePossibleEmailsSet set, final Statement st, final DialogsMainPeer peer ){
		if ( st == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Statement is null, returning false");
			}
			return false;
		}
		if ( peer == null ) {
			logger.error("Peer is null, error");
			return false;
		}
		final String threadId = st.getThreadId();
		if ( StringUtils.isBlank(threadId) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("ThreadId is blank, returning false");
			}
			return false;
		}
		if ( st.isEmail() ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Statement is email");
			}
			ExternalEmailStatement mail = ExternalEmailStatement.wrap(st.getBindedDocument());
			if ( mail.isMailingThread() ) {
				if (logger.isDebugEnabled()) {
					logger.debug("It is a start of mailing thread, no forwarding is needed, and returning true");
				}
				return true;
			}
		}
		if ( threadId.equals( st.getMessageId() )) {
			if (logger.isDebugEnabled()) {
				logger.debug("root asset, no mailing thread could be");
			}
			return false;
		}
		final List<String> rawAddresses = getAddressesFromRootEmail( sphereId, threadId, peer );
		if ( rawAddresses == null ) {
			if (logger.isDebugEnabled()) {
				logger.debug("rawAddresses is null, no specific forwarding is needed");
				return false;
			}
		}
		return addAddressesToSet( set, rawAddresses, st );
	}
	
	private static boolean addAddressesToSet( final SpherePossibleEmailsSet set,
			final List<String> rawAddresses, final Statement st) {
		if ( rawAddresses == null ) {
			return false;
		}
		for ( String s : rawAddresses ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Adding addresses: " + s);
			}
			set.addAddresses( s );
		}
		if ( st.isEmail() ) {
			ExternalEmailStatement mail = ExternalEmailStatement.wrap(st.getBindedDocument());
			set.deleteAddresses(mail.getReciever());
			set.deleteAddresses(mail.getCcrecievers());
			//set.deleteAddresses(mail.getGiver());
		}
		return true;
	}

	private static List<String> getAddressesFromRootEmail( final String sphereId, 
			final String threadId, final DialogsMainPeer peer ){
		final Document doc = peer.getXmldb().getSpecificMessage( threadId, sphereId );
		if ( doc == null ) {
			logger.error("doc is null for messageId: " + threadId + ", sphereId: " + sphereId);
			return null;
		}
		final Statement st = Statement.wrap( doc );
		if (logger.isDebugEnabled()) {
			logger.debug("Document taken, subject: " + st.getSubject());
		}
		if (!st.isEmail()) {
			if (logger.isDebugEnabled()) {
				logger.debug("not a email, root asset is: " + st.getType());
			}
			return null;
		}
		final ExternalEmailStatement mail = ExternalEmailStatement.wrap( doc );
		if ( !mail.isMailingThread() ) {
			if (logger.isDebugEnabled()) {
				logger.debug("This is email, but not mailing thread");
			}
			return null;
		}
		List<String> addresses = new ArrayList<String>();
		if ( StringUtils.isNotBlank(mail.getReciever()) ) {
			addresses.add(mail.getReciever());
		}
		if ( StringUtils.isNotBlank(mail.getCcrecievers()) ) {
			addresses.add(mail.getCcrecievers());
		}
		if ( StringUtils.isNotBlank(mail.getBccrecievers()) ) {
			addresses.add(mail.getBccrecievers());
		}
		return addresses;
	}
}
