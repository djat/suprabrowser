package ss.client.ui.models.autocomplete;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class used by AbstractDataModel for filtering simple not connected to
 * server collections of datasource
 * 
 * @author dankosedin
 * @param <DataSourceType>
 */
public class PrefixFilteredIterator<DataSourceType> implements Iterator<DataSourceType> {
    private String filter;

    private Iterator<DataSourceType> iterator;

    private DataSourceType next = null;

    private boolean finded = false;

    private boolean needFind = true;

    private DataSourceLabeler<DataSourceType> dataSourceLabeler;

    private boolean caseSensitive;

    public PrefixFilteredIterator(Iterator<DataSourceType> iterator, String filter,
	    DataSourceLabeler<DataSourceType> dataSourceLabeler) {
	this(iterator, filter, dataSourceLabeler, true);
    }

    public PrefixFilteredIterator(Iterator<DataSourceType> iterator, String filter,
	    DataSourceLabeler<DataSourceType> dataSourceLabeler,
	    boolean caseSensitive) {
	this.filter = filter;
	this.iterator = iterator;
	this.dataSourceLabeler = dataSourceLabeler;
	this.caseSensitive = caseSensitive;
    }

    public boolean hasNext() {
	find();
	return this.finded;
    }

    public DataSourceType next() {
	find();
	this.needFind = true;
	if (this.finded) {
	    return this.next;
	} else {
	    throw new NoSuchElementException();
	}
    }

    public void remove() {
    }

    private void find() {
	if (this.needFind) {
	    this.finded = false;
	    while ((!this.finded) && (this.iterator.hasNext())) {
		DataSourceType data = this.iterator.next();
		if (labelStartsWith(data)) {
		    this.finded = true;
		    this.next = data;
		}
	    }
	    this.needFind = false;
	}
    }

    /**
         * @param data
         * @return
         */
    private boolean labelStartsWith(DataSourceType data) {
	String dataLabel = this.dataSourceLabeler.getDataLabel(data);
	String filter = this.filter;
	if (!this.caseSensitive) {
	    dataLabel = dataLabel.toLowerCase();
	    filter = filter.toLowerCase();
	}
	return dataLabel.startsWith(filter);
    }
}