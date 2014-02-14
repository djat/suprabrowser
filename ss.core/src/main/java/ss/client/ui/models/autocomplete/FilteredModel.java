package ss.client.ui.models.autocomplete;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ss.common.ListUtils;
import ss.common.ThreadUtils;

/**
 * This class is example of implementation server connected datamodels. Note:
 * For ignoring multiple quick changes of its filter this class buffers it. This
 * prevents multiple client-server handshaking.
 * 
 * @author dankosedin
 */

public class FilteredModel<DataSourceType> extends
		BaseDataModel<DataSourceType> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FilteredModel.class);
	
	private AtomicReference<StampedFilter> newFilter = new AtomicReference<StampedFilter>();

	private AtomicBoolean started = new AtomicBoolean(false);

	private int senseLevel;

	private FilteredDataSource<DataSourceType> dataSource;

	public FilteredModel(FilteredDataSource<DataSourceType> dataSource,
			int senseLevel) {
		this(dataSource, senseLevel, BaseDataModel.FilterType.CaseSensitive);
	}

	public FilteredModel(FilteredDataSource<DataSourceType> dataSource,
			int senseLevel, BaseDataModel.FilterType type) {
		this(dataSource, senseLevel, BaseDataModel.FilterType.CaseSensitive,
				null);
	}

	public FilteredModel(FilteredDataSource<DataSourceType> dataSource,
			int senseLevel, BaseDataModel.FilterType type,
			DataSourceLabeler<DataSourceType> labeler) {
		super(type);
		this.dataSource = dataSource;
		this.senseLevel = Math.abs(senseLevel);
		super.setDataSourceLabeler(labeler);
	}

	@Override
	public void setFilter(final String prefixFilter) {
		this.newFilter.set(new StampedFilter(prefixFilter));
		if (this.started.compareAndSet(false, true)) {
			ThreadUtils.startDemon( new Runnable() {

				public void run() {
					StampedFilter filter = FilteredModel.this.newFilter.get();
					while (new Date().before(new Date(filter.stamp.getTime()
							+ FilteredModel.this.senseLevel))) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
						}
						filter = FilteredModel.this.newFilter.get();
					}
					FilteredModel.super.setFilter(filter.filter);
					retrieveData();
					FilteredModel.this.started.set(false);
				}
			}, "FilteredModel.retrieveData");
		}
	}

	/**
	 * 
	 */
	private void retrieveData() {
		fireLoadingData();
		ThreadUtils.startDemon( new Runnable() {
			public void run() {
				if (logger.isDebugEnabled()) {
					logger.debug("Request server date from "
							+ FilteredModel.this.dataSource);
				}

				Vector<DataSourceType> allKeyWords = FilteredModel.this.dataSource
						.getData(getFilter());
				if (logger.isDebugEnabled()) {
					logger.debug("Result is " + ListUtils.valuesToString(allKeyWords) );
				}				
				FilteredModel.this.data.clear();
				if ( allKeyWords != null ) {
					FilteredModel.this.data.addAll(allKeyWords);
				}
				fireNewData();
			}
		}, "FilteredModel.retrieveData");
	}

	private class StampedFilter {

		String filter;

		Date stamp;

		public StampedFilter(String filter) {
			this.filter = filter;
			this.stamp = new Date();
		}

	}

}
