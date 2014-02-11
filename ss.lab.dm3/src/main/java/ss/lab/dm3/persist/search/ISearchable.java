package ss.lab.dm3.persist.search;

import org.apache.lucene.document.Document;

import ss.lab.dm3.orm.MappedObject;

public interface ISearchable extends MappedObject {
	
	void collectFields( Document collector );
	
}
