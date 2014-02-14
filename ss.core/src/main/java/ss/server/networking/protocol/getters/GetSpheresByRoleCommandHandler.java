/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.search.Query;
import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetSpheresByRoleCommand;
import ss.client.ui.LuceneSearchDialog;
import ss.common.SearchSettings;
import ss.common.SphereReferenceList;
import ss.common.StringUtils;
import ss.domainmodel.SearchResultObject;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.framework.networking2.CommandHandleException;
import ss.search.ISearchResult;
import ss.search.LuceneSearch;
import ss.search.SearchResults;
import ss.server.networking.DialogsMainPeer;

/**
 * @author roman
 *
 */
public class GetSpheresByRoleCommandHandler extends
		AbstractGetterCommandHandler<GetSpheresByRoleCommand, SphereReferenceList> {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetSpheresByRoleCommandHandler.class);

	public GetSpheresByRoleCommandHandler(final DialogsMainPeer peer) {
		super(GetSpheresByRoleCommand.class, peer);
	}
	
	@Override
	protected SphereReferenceList evaluate(GetSpheresByRoleCommand command)
			throws CommandHandleException {
		
		final List<SphereReference> spheresToSend = new ArrayList<SphereReference>();
		
		final Vector<String> roles = command.getSphereRoleList();
		final String contactName = command.getContactName();
		final String keyword = command.getKeyword();
		
		if (logger.isDebugEnabled()) {
			logger.debug("command ---");
			logger.debug("roles selected size: " + roles.size());
			for ( String r : roles ) {
				logger.debug("role: " + r );
			}
			logger.debug("contactName: " + contactName);
			logger.debug("keyword: " + keyword);
		}
		
		final Collection<String> spheresByKeyword = getSpheresByKeyword(keyword, contactName);
		if (logger.isDebugEnabled()) {
			logger.debug("-----");
			logger.debug("spheresByKeyword size: " + spheresByKeyword.size());
			for ( String r : spheresByKeyword ) {
				logger.debug("Sphere next: " + r );
			}
		}
		Collection<SphereStatement> spheresByType = this.peer.getXmldb().getSpheresByRole(contactName, roles);
		if (logger.isDebugEnabled()) {
			logger.debug("-----");
			logger.debug("spheresByType size: " + spheresByType.size());
			for ( SphereStatement st : spheresByType ) {
				logger.debug("Sphere next: " + st.getDisplayName() + ", " + st.getSystemName() );
			}
		}
		for(SphereStatement sphereSt : spheresByType) {
			if(StringUtils.isBlank(keyword) || (StringUtils.isNotBlank(keyword) && spheresByKeyword.contains(sphereSt.getSystemName()))) {
				SphereReference ref = new SphereReference();
				ref.setDisplayName(sphereSt.getDisplayName());
				ref.setSystemName(sphereSt.getSystemName());
				spheresToSend.add(ref);
			}
		}
		SphereReferenceList list = new SphereReferenceList(spheresToSend);
		if (logger.isDebugEnabled()) {
			logger.debug("-------");
			logger.debug("result list: ");
			for (SphereReference sr : list) {
				logger.debug("next ref: " + sr.getDisplayName() + ", " + sr.getSystemName());
			}
		}
		return list;
	}

	private Collection<String> getSpheresByKeyword(final String keyword, final String contactName) {
		final Collection<String> spheresByKeyword = new HashSet<String>();

		if(StringUtils.isBlank(keyword)) {
			return spheresByKeyword;
		}

		String queryString = "+( subject:("+keyword+") content:("+keyword+") comment:("+keyword+") body:("+keyword+") role:("+keyword+") contact:("+keyword+") keywords:("+keyword+")) +(type:( ||sphere ||group ||clubdeal))";
		Query query = LuceneSearchDialog.getQuery(queryString);
		int resultId = LuceneSearch.search(contactName, new SearchSettings(query, keyword, false, false), this.peer.getVerifyAuth().getEnabledSpheres(contactName));
		SearchResults results = LuceneSearch.getResults(resultId);
		int pagesCount = results.getPagesCount();
		for(int i=0; i<pagesCount; i++) {
			try {
				for(ISearchResult result : results.getPage(i)) {
					SearchResultObject resultObject = result.getSearchResultObject();
					String sphereId = resultObject.getIdCollection().get(0).getSphereId();
					String messageId = resultObject.getIdCollection().get(0).getMessageId();
					Document doc = this.peer.getXmldb().getSpecificMessage(messageId, sphereId);
					if(doc==null) {
						continue;
					}
					SphereStatement sphere = SphereStatement.wrap(doc);
					spheresByKeyword.add(sphere.getSystemName());
				}
			} catch (IOException ex) {
				logger.error("error in selecting spheres for keyword", ex);
			}
		}

		return spheresByKeyword;
	}
}
