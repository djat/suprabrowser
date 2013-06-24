package ss.search;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

import ss.common.FileUtils;
import ss.common.ListUtils;
import ss.common.SearchSettings;
import ss.common.TimeLogWriter;
import ss.global.LoggerConfiguration;
import ss.global.SSLogger;
import ss.server.db.XMLDB;

public class LuceneSearch {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LuceneSearch.class);

	private static Hashtable<Integer, SearchResults> cashedResults = new Hashtable<Integer, SearchResults>();

	/**
	 * @return
	 * @throws IOException
	 */
	private static IndexReader getIndexReader(List<String> spheres)
			throws IOException {
		if (spheres.size() > 0) {
			Vector<IndexReader> vReaders = new Vector<IndexReader>();

			for (int i = 0; i < spheres.size(); i++) {
				try {
					IndexReader index = SphereIndex.get(spheres.get(i))
							.getReader();
					if (index != null) {
						vReaders.add(index);
					}
				} catch (IOException e) {
					logger.error("Failed to create Index Reader for sphere"
							+ spheres.get(i), e);
				}
			}
			IndexReader[] readers = vReaders.toArray(new IndexReader[vReaders
					.size()]);
			if (spheres.size() == 1) {
				return readers[0];
			} else {
				return new MultiReader(readers);
			}
		}

		return null;
	}

	public static void main(String[] args) {
		SSLogger.initialize(LoggerConfiguration.DEFAULT);
		ss.common.LocationUtils.init();
		File dir = new File("./index");
		FileUtils.deleteFolder(dir);
		XMLDB xmldb = new XMLDB();
		xmldb.reIndexAllDocuments();

	}

	private static void addHitsImpl(SearchResults result,
			SearchSettings settings, List<String> spheres) {
		if (result == null) {
			logger.error("result is null");
			return;
		}
		final IndexReader reader;
		try {
			reader = getIndexReader(spheres);
		} catch (IOException ex) {
			logger.error("Can't open lucena reader for "
					+ ListUtils.valuesToString(spheres), ex);
			return;
		}
		try {
			final Searcher searcher = new IndexSearcher(reader);
			try {
				settings.rewriteQuery(reader);
				Hits hits = searcher.search(settings.getQuery());
				if (logger.isDebugEnabled()) {
					logger.debug("hits.length=" + hits.length());
				}
				result.addHits(hits);
				return;
			} catch (IOException e) {
				logger.error("Can't perform lucena search query "
						+ settings.getQuery() + " in "
						+ ListUtils.valuesToString(spheres), e);
			} finally {
				try {
					searcher.close();
				} catch (IOException ex) {
					logger.error("Can't close lucena searcher", ex);
				}
			}
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				logger.error("Can't close lucena reader", ex);
			}
		}
	}
	
	public static int search(final String contactName, SearchSettings settings,
			List<String> spheres) {
		return search(contactName, settings, spheres, true);
	}
	
	public static int search(final String contactName, SearchSettings settings,
			List<String> spheres, final boolean isSearchSubSpheres) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Searching here: "
						+ settings.getQuery().toString());
			}
			
			Query originalQuery = settings.getQuery();
			SearchResults result = new SearchResults(contactName, settings);
			if (isSearchSubSpheres) {
				try {
					final SearchForAdditionalContatsQueryCreator creator = new SearchForAdditionalContatsQueryCreator(
							spheres, settings.getQuery());
					if (creator.isValid()) {
						settings.setQuery(creator.getQuery());
						addHitsImpl(result, settings, creator.getSpheres());
					} else {
						if (logger.isDebugEnabled()) {
							logger
									.debug("SearchForAdditionalContatsQueryCreator is not valid");
						}
					}
				} catch (Exception ex) {
					logger.error("Error trying to search in sub spheres", ex);
				}
			}
			settings.setQuery(originalQuery);
			addHitsImpl(result, settings, spheres);
			if ( (result != null) && result.organizeContacts() ) {
				cashedResults.put(result.getId(), result);
				return result.getId();
			} else {
				return -1;
			}
		} catch (Exception e) {
			logger.error("Error in lucena search", e);
			return -1;
		}
	}

	public static SearchResults getResults(int id) {
		return cashedResults.get(id);
	}

	public static void free(int freeQuery) {
		cashedResults.remove(freeQuery);
	}
	
	public static final void deletedSpheres( final List<String> sphereIds ){
		if ( sphereIds == null ) {
			logger.error("sphereIds is null");
			return;
		}
		for ( String sphereId : sphereIds ) {
			try {
				logger.error( "DELETING NEXT: " + sphereId );
				SphereIndex.deleteSphereIndex( sphereId );
			} catch (Exception ex) {
				logger.error( "Error occured for deleting index of sphereId: " + sphereId, ex );
			}
		}
	}
}
