/**
 * 
 */
package ss.client.event.tagging.obtainer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.getters.SearchSupraSphereCommand;
import ss.common.SearchSettings;
import ss.domainmodel.SupraSearchResultsObject;
import ss.util.SessionConstants;

/**
 * @author zobo
 *
 */
public abstract class AbstractForTagObtainer {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractForTagObtainer.class);

	private final String tag;
	
	private final DialogsMainCli client;
	
	private SupraSearchResultsObject searchResult;

	public AbstractForTagObtainer( final String tag, final DialogsMainCli client ) {
		super();
		this.tag = tag;
		this.client = client;
	}
	
	public abstract int getCount();
	
	private SupraSearchResultsObject load(){
		final SearchSupraSphereCommand command = new SearchSupraSphereCommand();
		SearchSettings settings = new SearchSettings(getQuery( getStringQuery( this.tag ) ), this.tag, true, false); 
	 	command.putArg( SessionConstants.SEARCH_SETTINGS, settings );
		final AbstractDocument resultDoc = command.execute(this.client,
				AbstractDocument.class);
		if (resultDoc == null) {
			logger.error("Result Document is null");
			return new SupraSearchResultsObject();
		} else {
			return SupraSearchResultsObject.wrap( resultDoc );
		}
	}
	
	private Query getQuery(String rawQuery) {
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(rawQuery);
		} catch (ParseException ex) {
			logger.error("error" + ex);
		}
		return query;
	}
	
	protected SupraSearchResultsObject getData(){
		if (this.searchResult == null) {
			this.searchResult = load();
		}
		return this.searchResult;
	}
	
	private String getStringQuery(String query) {
		return "+((+keywords:"
		+ query
		+ ")) +(type:" + getType() + ")";
	}
	
	protected abstract String getType();

	protected DialogsMainCli getClient() {
		return this.client;
	}

	public String getTag() {
		return this.tag;
	}
}
