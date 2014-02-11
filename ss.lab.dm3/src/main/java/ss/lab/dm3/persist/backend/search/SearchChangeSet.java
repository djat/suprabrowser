package ss.lab.dm3.persist.backend.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.Document;

/**
 * @author dmitry
 *
 */
public class SearchChangeSet implements Iterable<Document> {

	private List<Document> docs = new ArrayList<Document>();

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Document> iterator() {
		return this.docs.iterator();
	}
	
	public void add( Document document ) {
		this.docs.add( document );
	}
	
	public void addAll( Collection<Document> docs ) {
		this.docs.addAll( docs );
	}
	
	public void remove( Document document ) {
		if ( document == null ) {
			return;
		}
		this.docs.remove( document );
	}
	
	public int size() {
		return this.docs.size();
	}
	
	public Document get( int i ) {
		return size() > i ? this.docs.get( i ) : null;
	}
}
