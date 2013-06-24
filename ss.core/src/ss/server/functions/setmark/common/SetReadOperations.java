/**
 * 
 */
package ss.server.functions.setmark.common;

import java.util.List;
import java.util.Vector;

import org.dom4j.Document;

import ss.domainmodel.Statement;
import ss.server.networking.DialogsMainPeer;
import ss.util.VotingEngine;

/**
 * @author zobo
 *
 */
public class SetReadOperations {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetReadOperations.class);
	
	public static void voteSphere( final String sphereId, final String contactName, final DialogsMainPeer peer ){
		if (logger.isDebugEnabled()) {
			logger.debug("Voting sphere : " + sphereId);
		}
		final Vector<Document> docs = peer.getXmldb().getAllMessages(sphereId);
		if ( docs != null ) {
			voteDocs( docs, sphereId, contactName, peer );
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("docs is null");
			}
		}
	}
	
	private static void voteDocs( final List<Document> docs, final String sphereId, final String contactName, final DialogsMainPeer peer ){
		if (logger.isDebugEnabled()) {
			logger.debug("Voting docs size: " + docs.size());
		}
		for ( Document doc : docs ) {
			if ( isNeededToVote( doc, contactName) ) {
				if (logger.isDebugEnabled()) {
					logger.debug("VOTING: " + Statement.wrap(doc).getSubject());
				}
				try {
					peer.getXmldb().voteDoc(doc, sphereId, contactName);
				} catch (Exception ex) {
					logger.error("Error in vouting doc: " + doc.asXML());
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("... skipped: " + Statement.wrap(doc).getSubject());
				}				
			}
		}
	}
	
	public static boolean isNeededToVote( final Document doc, final String contactName ){
		return !VotingEngine.votedContact(contactName, doc);
	}
}
