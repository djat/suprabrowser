package ss.client.ui.models.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ss.common.UiUtils;

/**
 * This is base implementation of DataModel. Can be used directli for non server
 * connected autocomplite values with apropriative DataSourceLabeler and
 * useFilter==true. Or maybe extended for adding some additional
 * functionality(like KeyWordsModel do).
 * 
 * @author dankosedin
 * @param <T>
 */
public class BaseDataModel<T> implements DataModel<T> {

	protected List<T> data = Collections.synchronizedList(new ArrayList<T>());

	protected List<DataListener> dataListeners = Collections
			.synchronizedList(new ArrayList<DataListener>());

	private String filter = "";

	protected DataSourceLabeler<T> dataSourceLabeler;

	private FilterType useFilter;

	public BaseDataModel(FilterType useFilter) {
		this(null, useFilter);
	}

	public BaseDataModel(Collection<T> startSource, FilterType useFilter) {
		this.useFilter = useFilter;
		if (startSource != null) {
			this.data.addAll(startSource);
		}
	}

	public void setDataSourceLabeler(DataSourceLabeler<T> labeler) {
		this.dataSourceLabeler = labeler;
	}

	public void setFilter(String prefixFilter) {
		this.filter = prefixFilter;
		fireNewData();
	}

	public String getFilter() {
		return this.filter;
	}

	public void addDataListener(DataListener listener) {
		this.dataListeners.add(listener);
	}

	public void removeDataListener(DataListener listener) {
		this.dataListeners.remove(listener);
	}

	protected void fireNewData() {
		for (final DataListener dl : this.dataListeners) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					dl.newData();
				}
			});
		}
	}

	protected void fireLoadingData() {
		for (final DataListener dl : this.dataListeners) {
			UiUtils.swtBeginInvoke(new Runnable() {
				public void run() {
					dl.loadingData();
				}
			});
		}
	}

	/**
	 * @return
	 */
	protected Iterable<T> getDataIterAble() {
		if (this.useFilter == FilterType.NoFilter) {
			return this.data;
		} else {
			return new Iterable<T>() {

				public Iterator<T> iterator() {
					return new PrefixFilteredIterator<T>(
							BaseDataModel.this.data.iterator(),
							BaseDataModel.this.filter,
							BaseDataModel.this.dataSourceLabeler,
							(BaseDataModel.this.useFilter == FilterType.CaseSensitive));
				}
			};
		}
	}

	public enum FilterType {
		NoFilter, CaseSensitive, CaseInsensitive;
	}

	public final ProposalCollection<T> getProposals() {
		ProposalCollection<T> filterredData = new ProposalCollection<T>();
		for (T item : getDataIterAble()) {
			filterredData.add(new Proposal<T>(item, this.dataSourceLabeler
					.getDataLabel(item)));
		}
		return filterredData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.models.autocomplete.DataModel#dropCashedData()
	 */
	public void dropCashedData() {
		this.filter = "";
		this.data.clear();
		fireNewData();
	}

}
