package ss.lab.dm3.persist.backend.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

import ss.lab.dm3.orm.QualifiedUtils;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.backend.hibernate.ObjectSelector;
import ss.lab.dm3.persist.backend.hibernate.QuerySelectResult;

public class SearchEngine {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());

	private final SearchConfiguration configuration;

	private final IndexFactory indexFactory;

	public SearchEngine(SearchConfiguration configuration) {
		super();
		this.configuration = configuration;
		this.indexFactory = new IndexFactory(this.configuration);
	}

	/**
	 * @param queryObj
	 * @param collector
	 */
	public QuerySelectResult search(SearchQueryAdaptor queryObj, ObjectSelector selector) {
		IndexReader reader = this.indexFactory.openIndexReaderOrNull();
		if (reader != null) {
			try {
				final Searcher searcher = new IndexSearcher(reader);
				try {
					StringBuilder queryText = new StringBuilder( queryObj.getText() );
					
					SearchHelper.addQualifierRestriction( queryText, queryObj.getClassDomainObject() );
					
					if ( queryObj.isSecure() ) {
						SearchHelper.addSecureRestriction(queryText, queryObj.getSecureKeys() );
					}
										
					if (this.log.isDebugEnabled()) {
						this.log.debug("Lucene text query : " + queryText.toString());
					}
					Query luceneQuery = getQuery(queryText.toString());
					if ( luceneQuery != null ) {
						luceneQuery.rewrite(reader);
						Hits hits = searcher.search(luceneQuery);
						return processHits(queryObj, selector, hits);
					}
				} catch (IOException ex ) {
					log.error("Can't search by " + queryObj, ex );
				} finally {
					try {
						searcher.close();
					} catch (IOException ex) {
						log.warn("Can't close searcher " + searcher, ex );
					}
				}
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException ex) {
					log.warn("Can't close reader " + reader, ex );
				}
			}
		}
		return new QuerySelectResult(new ArrayList<DomainObject>());
	}

	@SuppressWarnings("unchecked")
	private QuerySelectResult processHits(SearchQueryAdaptor query,
			ObjectSelector selector, Hits hits) throws CorruptIndexException,
			IOException {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Query offset " + query.getLimitOffset() + " size " + query.getLimitSize() );
		}
		List<DomainObject> objects = new ArrayList<DomainObject>();
		int start = Math.max( 0, query.getLimitOffset() );
		int end;
		if ( query.getLimitSize() >= 0 ) { 
			end = Math.min( hits.length(), start + query.getLimitSize() );
		}
		else {
			end = hits.length();
		}
		for (int i = start; i < end; i++) {
			Document document = hits.doc(i);
			String id = document.getField("id").stringValue();
			String qualifier = document.getField("qualifier").stringValue();
			final Class<? extends DomainObject> resolveClass = (Class<? extends DomainObject>) QualifiedUtils
					.resolveClass(qualifier);
			if (this.log.isDebugEnabled()) {
				this.log.debug("ResolveClass : " + resolveClass.getSimpleName()
						+ "   ClassDomainObject : "
						+ query.getClassDomainObject().getSimpleName());
			}
			DomainObject domainObject = selector.select(resolveClass, Long
					.parseLong(id));
			if (this.log.isDebugEnabled()) {
				this.log.debug(domainObject + " with id = " + id);
			}
			if (domainObject != null) {
				objects.add(domainObject);
			}
		}
		QuerySelectResult result = new QuerySelectResult(objects);
		result.setItemsTotalCount( hits.length() );
		if (log.isDebugEnabled()) {
			log.debug("Selected " + result.getItemsTotalCount() );
		}
		return result;
	}

	

	public SearchIndexEditor beginEdit() {
		return new SearchIndexEditor(this.indexFactory);
	}

	private Query getQuery(String rawQuery) {
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(rawQuery);
		} catch (ParseException ex) {
			log.error(" Can't parse query " + rawQuery );
		}
		return query;
	}

	public SearchConfiguration getConfiguration() {
		return this.configuration;
	}

	public IndexFactory getIndexFactory() {
		return this.indexFactory;
	}
}