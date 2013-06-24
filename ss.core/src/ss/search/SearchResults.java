package ss.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;

import ss.common.SearchSettings;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;

public class SearchResults {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(SearchResults.class);

	private static int DEFAULT_RESULTS_PER_PAGE = 20;

	private static int ID = 0;

	private static Object syncID = new Object();

	private List<ISearchResult> all = null;
	
	private List<SimpleSearchResult> temp = null;

	private int resultsPerPage;

	private int id;
	
	private boolean searchOnlyTagged = false;

	private Query query;
	
	private final String realName;

	public SearchResults(String realName, SearchSettings settings) throws IOException {
		this(realName, DEFAULT_RESULTS_PER_PAGE, settings);
	}

	public SearchResults(String realName, int resultsPerPage, SearchSettings settings) throws IOException {
		this.query = settings.getQuery();
		this.resultsPerPage = resultsPerPage;
		this.realName = realName;
		this.searchOnlyTagged = settings.isSearchOnlyTagged();
		synchronized (syncID) {
			this.id = ID;
			ID = ID + 1;
		}
	}
	
	public void addHits( final Hits hits ) throws IOException{
		final List<SimpleSearchResult> results = createSearchResults( hits );
		if ( this.temp != null ) {
			this.temp.addAll( results );
		} else {
			this.temp = results;
		}
	}

	/**
	 * @param hits
	 * @return
	 * @throws IOException 
	 */
	private List<SimpleSearchResult> createSearchResults(Hits hits) throws IOException {		
		List<SimpleSearchResult> results = new ArrayList<SimpleSearchResult>();
		for (int i = 0; i < hits.length(); ++ i ) {
			Document document = hits.doc(i);
			SimpleSearchResult result = new SimpleSearchResult(document, this.query);
			boolean canIncludeResult = canIncludeResult(result);
			if(!canIncludeResult) {
				continue;
			}
			if(!this.searchOnlyTagged) {
				results.add(result);
			} else if(result.getFormattedKeywords()!=null && result.getFormattedKeywords().length()>0) {
				results.add(result);
			}
		}
		return results;
	}
	
	public boolean organizeContacts(){
		if ( this.temp == null ) {
			return false;
		}
		this.all = FormattedResultsBuilder.formatResults( this.temp );
		this.temp = null;
		return true;
	}

	/**
	 * @param result
	 * @return
	 */
	private boolean canIncludeResult(SimpleSearchResult result) {
		if(!SphereStatement.isClubdealType(result.getType())) {
			return true;
		}
		DialogsMainPeer peer = DialogsMainPeerManager.INSTANCE.getHandlers().iterator().next();
		final String systemName = peer.getVerifyAuth().getSystemName(result.getSubject());
		return peer.getXmldb().isClubdealAvailableForMember(this.realName, systemName);
	}

	public int getId() {
		return this.id;
	}

	public int getPagesCount() {
		int perPage = this.resultsPerPage;
		int length = getResultsCount();
		return (length / perPage) + ((length % perPage == 0) ? 0 : 1);
	}

	public ISearchResult[] getPage(int pageID) throws IOException {
		int end = Math.min( getResultsCount(), (pageID + 1) * this.resultsPerPage );
		int start = pageID * this.resultsPerPage;
		ISearchResult[] results = new ISearchResult[end-start];
		for (int i = start; i < end; i++) {
			results[i - start] = getNextResult(i);
		}
		return results;
	}
	
	private ISearchResult getNextResult( final int i ){
		return (this.all != null) ? this.all.get(i) : 
			( (this.temp != null) ? this.temp.get(i) : null);
	}

	public int getResultsCount() {
		return (this.all != null) ? this.all.size() : ( (this.temp != null) ? this.temp.size() : 0);
	}
	
	public void release() {
		LuceneSearch.free( this.id );
	}
}
