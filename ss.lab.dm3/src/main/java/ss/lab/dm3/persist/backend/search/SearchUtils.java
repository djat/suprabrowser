package ss.lab.dm3.persist.backend.search;

import org.apache.lucene.document.Document;

import ss.lab.dm3.persist.search.ISearchable;

public class SearchUtils {

	private final ISearchable domainObject;
	
	/**
	 * Use SearchHelper.collectSearchableFields(DomainObject,Document) instead.
	 */
	@Deprecated
	public SearchUtils(ISearchable domainObject) {
		super();
		this.domainObject = domainObject;
	}

	/**
	 * Use SearchHelper.collectSearchableFields(DomainObject,Document) instead.
	 */
	@Deprecated
	public void processSearchableFields(Document collector) {
		SearchHelper.collectSearchableFields( this.domainObject, collector );
	}
	
	
}
