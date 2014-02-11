package ss.lab.dm3.persist.backend.search;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

/**
 * 
 */
public class IndexFactory {

	protected final Log log = LogFactory.getLog(getClass());

	private final SearchConfiguration searchConfiguration;

	private Directory directory;

	public Directory getDirectory() {
		return this.directory;
	}

	public IndexFactory(SearchConfiguration searchConfiguration) {
		super();
		if (searchConfiguration == null) {
			throw new NullPointerException("searchConfiguration");
		}
		this.searchConfiguration = searchConfiguration;
		try {
			this.directory = FSDirectory.getDirectory(this.searchConfiguration
					.getBaseDir());
		} catch (IOException ex) {
			log.error("Can't create index directory by "
					+ this.searchConfiguration);
		}
		// TODO lock check
	}

	public IndexReader openIndexReaderOrNull() {
		try {
			return IndexReader.open(this.directory);
		} catch (CorruptIndexException ex) {
			log.warn("Can't open index writer", ex);
		} catch (IOException ex) {
			log.warn("Can't open index writer", ex);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.persist.backend.search.IIndexFactory#openIndexWriter()
	 */
	public IndexWriter openIndexWriterOrNull() {
		try {
			return new IndexWriter(this.directory, true, new StandardAnalyzer() );
		} catch (CorruptIndexException ex) {
			log.warn("Can't open index writer", ex );
		} catch (LockObtainFailedException ex) {
			log.warn("Can't open index writer", ex );
		} catch (IOException ex) {
			log.warn("Can't open index writer", ex );
		}
		return null;
	}

	public SearchConfiguration getSearchConfiguration() {
		return this.searchConfiguration;
	}

	public void deleteIndexDirectory() {
		final File fileIndex = this.searchConfiguration.getBaseDir();
		if (fileIndex.exists()) {
			deleteIndexDirectory(fileIndex.listFiles());
		}
	}

	private static void deleteIndexDirectory(File[] files) {
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (!file.delete()) {
				deleteIndexDirectory(file.listFiles());
			}
		}
	}

}
