package ss.server.networking.protocol.getters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetAllKeywordsCommand;
import ss.domainmodel.KeywordStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.Statement;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.util.VariousUtils;

public class GetAllKeywordsHandler extends AbstractGetterCommandHandler<GetAllKeywordsCommand, Vector<Document> >{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(GetAllKeywordsHandler.class);
	
	/**
	 * @param acceptableCommandClass
	 * @param peer
	 */
	public GetAllKeywordsHandler( DialogsMainPeer peer) {
		super(GetAllKeywordsCommand.class, peer);
	}


	/* (non-Javadoc)
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected Vector<Document> evaluate(GetAllKeywordsCommand command) throws CommandHandleException {
		final String filter = command.getStringArg(SC.FILTER);
		final String sphereId = command.getStringArg( SC.SPHERE_ID2 );
		final String loginName = command.getStringArg( SC.USERNAME );
		if (logger.isDebugEnabled()) {
			logger.debug("GetAllKeywordsHandler evaluated");
			logger.debug( "filter: " + filter );
			logger.debug( "sphereId: " + sphereId );
			logger.debug( "loginName: " + loginName );
		}
		
		final String homeSphereId = this.peer.getVerifyAuth()
			.getPersonalSphereFromLogin(loginName);
		final Vector<Document> allPrivateKeyWords = this.peer.getXmldb().getKeywords(
				homeSphereId, filter);
		final Vector<Document> allKeyWords = getKeywordsRelatedToSphereFromUser( sphereId, loginName );
		if (logger.isDebugEnabled()) {
			logger.debug( "Private keywords size: " + allPrivateKeyWords.size() );
			logger.debug( "Other members keywords size: " + allKeyWords.size() );
		}
			
		final TreeSet<Document> set = new TreeSet<Document>(new Comparator<Document>(){
			public int compare(Document o1, Document o2) {
				KeywordStatement key1 = KeywordStatement.wrap(o1);
				KeywordStatement key2 = KeywordStatement.wrap(o2);				
				return key1.getSubject().compareTo(key2.getSubject());
			}});
		set.addAll(allKeyWords);
		set.addAll(allPrivateKeyWords);
		if (logger.isDebugEnabled()) {
			logger.debug( "Total keywords size: " + set.size() );
		}
		return new Vector<Document>(set);
	}

	private Vector<Document> getKeywordsRelatedToSphereFromUser( final String sphereId, final String currentLogin ){
		if ( sphereId == null ) {
			logger.error("SphereId is null");
			return new Vector<Document>();
		}
		final List<MemberReference> members = this.peer.getVerifyAuth().getMembersForSphere( sphereId );
		if ( members == null ) {
			logger.error("No members returned");
			return new Vector<Document>();
		}
		final List<String> sphereIds = new ArrayList<String>();
		
		for ( MemberReference member : members ) {
			if (!currentLogin.equals( member.getLoginName() )) {
				sphereIds.add( this.peer.getVerifyAuth()
						.getPersonalSphereFromLogin( member.getLoginName() ) );
			}
		}
		if (sphereIds.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug( "No members except current user: " + currentLogin );
			}
			return new Vector<Document>();
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "Looking for related keywords for sphere_id: " + sphereId );
		}
		final Vector<Document> keywords = new Vector<Document>();
		for ( Document doc : this.peer.getXmldb().getKeywords( sphereIds ) ) {
			if (VariousUtils.checkElementAttributeValueExists(doc, "multi_loc_sphere", sphereId)) {
				if (logger.isDebugEnabled()) {
					logger.debug( "Keyword contains multiloc value, keyword subject: " + Statement.wrap( doc ).getSubject() );
				}
				keywords.add( doc );
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug( "Keyword does not contains multiloc value, keyword subject: " + Statement.wrap( doc ).getSubject() );
				}
			}
		}
		return keywords;
	}
}
