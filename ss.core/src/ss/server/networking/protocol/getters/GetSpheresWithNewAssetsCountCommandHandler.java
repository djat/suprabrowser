/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

import ss.client.networking.protocol.getters.GetSpheresWithNewAssetsCountCommand;
import ss.common.SearchSettings;
import ss.common.SphereReferenceList;
import ss.common.StringUtils;
import ss.common.sphereinfo.SpheresWithNewAssets;
import ss.domainmodel.SphereReference;
import ss.framework.networking2.CommandHandleException;
import ss.search.LuceneSearch;
import ss.search.SearchResults;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetSpheresWithNewAssetsCountCommandHandler extends
		AbstractGetterCommandHandler<GetSpheresWithNewAssetsCountCommand,SpheresWithNewAssets> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetSpheresWithNewAssetsCountCommandHandler.class);
	
	public GetSpheresWithNewAssetsCountCommandHandler(DialogsMainPeer peer) {
		super(GetSpheresWithNewAssetsCountCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected SpheresWithNewAssets evaluate(
			GetSpheresWithNewAssetsCountCommand command)
			throws CommandHandleException {
		
		String contactName = command.getContactName();
		if (StringUtils.isBlank(contactName)) {
			contactName = this.peer.getUserContactName();
			if (logger.isDebugEnabled()) {
				logger.debug("Contact name will be used default(current): " + contactName);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Contact name will be used taken from command: " + contactName);
			}
		}
		if (StringUtils.isBlank(contactName)) {
			logger.error("Cannot get contact name");
			return null;
		}
		return getForUser( contactName );
	}

	private SpheresWithNewAssets getForUser(String contactName) {
		final SpheresWithNewAssets spheresNewAssets = new SpheresWithNewAssets();
		final String login = this.peer.getVerifyAuth().getLoginForContact(contactName);
		if (StringUtils.isBlank(login)) {
			logger.error("Login for " + contactName + " is blank");
			return spheresNewAssets;
		}
		final SphereReferenceList spheres = this.peer.getVerifyAuth().getSupraSphere().getAllEnabledSpheresByLogin(login);
		if ( spheres == null ) {
			logger.error("spheres is null");
			return spheresNewAssets;
		}
		for ( SphereReference ref : spheres ) {
			try {
				String sphereId = ref.getSystemName();
				String sphereName = ref.getDisplayName();
				int count = getForSphere(contactName, sphereId);
				if ( ref.isMember() ) {
					spheresNewAssets.addPersonal( sphereId, sphereName, count );
				} else if ( ref.isEmailBox() ) {
					spheresNewAssets.addEmail( sphereId, sphereName, count );
				} else {
					spheresNewAssets.addGroup( sphereId, sphereName, count );
				}
			} catch (Throwable ex) {
				logger.error("Error processing new assets count for sphere : " + ref.getDisplayName());
			}
		}
		return spheresNewAssets;
	}
	
	private int getForSphere( final String contactName, final String sphereId ){
		final String sQuery = "-(voted:(\"["
				+ contactName
				+ "]\")) +(type:( ||terse ||message ||externalemail ||bookmark ||file ||contact ||rss)) +(sphere_id:( ||"
				+ sphereId + "))";
		final Query query = getQuery(sQuery);
		final int resultId = LuceneSearch.search(contactName, new SearchSettings(
				query, sQuery, false, false), this.peer.getVerifyAuth()
				.getEnabledSpheres(contactName), false);
		final SearchResults results = LuceneSearch.getResults(resultId);
		int resultsCount = (results != null ? results.getResultsCount() : 0 );
		LuceneSearch.free(resultId);
		return resultsCount;
	}
	
	private Query getQuery(final String sQuery) { 
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(sQuery);
		} catch (ParseException ex) {
			logger.error("", ex);
		}
		return query;
	}
}
