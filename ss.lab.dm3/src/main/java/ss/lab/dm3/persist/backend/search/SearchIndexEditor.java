package ss.lab.dm3.persist.backend.search;

import static d1.FastAccess.$;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import ss.lab.dm3.orm.QualifiedUtils;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.changeset.ChangeSet;
import ss.lab.dm3.persist.search.ISearchable;

public class SearchIndexEditor {
	
	private static final String ID_FIELD_NAME = "id";

	private static final String QUALIFIER_FIELD_NAME = "qualifier";

	protected final Log log = LogFactory.getLog( getClass() );

	private final IndexFactory indexFactory;

	public SearchIndexEditor(IndexFactory indexFactory) {
		this.indexFactory = indexFactory;
	}

	private void update(IndexWriter indexWriter, ISearchable searchable) {
		remove(indexWriter, searchable);
		add(indexWriter, searchable);
	}

	private void remove(IndexWriter indexWriter, ISearchable searchable) {
		Document document = new Document();
		searchable.collectFields(document);
		if (searchable.getId() != null) {
			Term deletionTerm = new Term(ID_FIELD_NAME, String.valueOf(searchable
					.getId()));
			try {
				indexWriter.deleteDocuments(deletionTerm);
			} catch (IOException ex) {
				log.error( $("Can't delete document by {0}", deletionTerm), ex );
			}
		}
	}

	private void add(IndexWriter indexWriter, ISearchable searchable) {
		try {
			Document document = new Document();
			searchable.collectFields(document);
			addSpecificFields(searchable, document);
			indexWriter.addDocument(document);
		} catch (IOException ex) {
			log.error( $( "Can't add document by {0}", searchable ), ex);
		}
	}

	private void addSpecificFields(ISearchable searchable, Document document) {
		document.add(new Field(ID_FIELD_NAME, String.valueOf(searchable.getId()),
				Field.Store.YES, Field.Index.TOKENIZED));
		final String qualifier = QualifiedUtils.resolveQualifier(searchable);
		document.add(new Field(QUALIFIER_FIELD_NAME, qualifier, Field.Store.YES,
				Field.Index.TOKENIZED));
	}

	/**
	 * @param changes
	 */
	public void applyChanges(ChangeSet changes) {
		IndexWriter indexWriter = this.indexFactory.openIndexWriterOrNull();
		if ( indexWriter == null ) {
			return;
		}
		try {
			for (DomainObject domainObject : changes.getNewObjects()) {
				if (domainObject instanceof ISearchable) {
					add( indexWriter, (ISearchable) domainObject);
				}
			}
			for (DomainObject domainObject : changes.getDirtyObjects()) {
				if (domainObject instanceof ISearchable) {
					update( indexWriter, (ISearchable) domainObject);
				}
			}
			for (DomainObject domainObject : changes.getRemovedObjects()) {
				if (domainObject instanceof ISearchable) {
					remove( indexWriter, (ISearchable) domainObject);
				}
			}
			indexWriter.optimize();
		} catch (IOException ex) {
			log.warn( $( "Can't apply changes {0}", changes ), ex);
		}
		finally {
			closeSilently( indexWriter );
		}
	}

	public void reIndex(List<? extends DomainObject> objects) {
		this.indexFactory.deleteIndexDirectory();
		IndexWriter indexWriter = this.indexFactory.openIndexWriterOrNull();
		try {
			for (DomainObject domainObject : objects) {
				add( indexWriter, (ISearchable) domainObject );
			}
			indexWriter.optimize();
		} catch (IOException ex) {
			log.error("Can't reindex", ex);
		}
		finally {
			closeSilently( indexWriter );
		}
	}

	private void closeSilently(IndexWriter indexWriter) {
		try {
			indexWriter.close();
		} catch (Exception ex) {
			log.error( $("Can't close index writer {0}", indexWriter), ex );
		} 
	}

	public IndexFactory getIndexFactory() {
		return this.indexFactory;
	}

}
