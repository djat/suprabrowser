/**
 * 
 */
package ss.client.ui.email;

import java.util.List;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.FilteredDataSource;
import ss.client.ui.models.autocomplete.FilteredModel;
import ss.client.ui.models.autocomplete.ResultListener;
import ss.client.ui.typeahead.IControlContentProvider;
import ss.client.ui.typeahead.TextContentProvider;
import ss.client.ui.typeahead.TypeAheadComponent;
import ss.util.StringProcessor;

/**
 * @author zobo
 * 
 */
public abstract class EmailAbstractShellCompositeUnit extends Composite {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailAbstractShellCompositeUnit.class);

	protected Text text;

	protected static final int LABEL_WIDTH = 70;

	@Override
	public void dispose() {
		this.typeAhead.dispose();
		super.dispose();
	}

	protected Label label;

	private TypeAheadComponent<String> typeAhead;

	public EmailAbstractShellCompositeUnit(Composite parent, int style) {
		super(parent, style);
		createContent(this);
	}

	protected abstract void createContent(Composite parent);

	public void activateTypeAhead(final List<String> data) {
		if (logger.isDebugEnabled()){
			if (data != null){
				logger.debug("Activate Type Ahead performed, data size: " + data.size());
			} else {
				logger.debug("Activate Type Ahead performed, data is null");
			}
		}
		this.typeAhead = new TypeAheadComponent<String>(this.text,
				new FilteredModel<String>(
						new FilteredDataSource<String>() {

							public Vector<String> getData(String filter) {
								return processDataFiltered(data, filter);
							}
						}, 200, BaseDataModel.FilterType.NoFilter,
						new DataSourceLabeler<String>() {

							public String getDataLabel(String data) {
								return data;
							}
						}), getResultListener(), getContentProvider(this.text), false, false);
	}

	/**
	 * @return
	 */
	protected IControlContentProvider getContentProvider(final Text text) {
		return new TextContentProvider( text );
	}

	public String getText() {
		return this.text.getText();
	}

	public void setText(String text) {
		if(text==null) {
			logger.error("text is null !!!!!");
			return;
		}
		this.text.setText(text);
	}

	protected abstract ResultListener<String> getResultListener();

	/**
	 * @param data
	 * @param filter
	 * @return
	 */
	protected Vector<String> processDataFiltered(final List<String> data, String filter) {
		if ((filter == null)
				|| (filter.trim().equals(""))){
			if (logger.isDebugEnabled()){
				logger.debug("Filter is blank, returning whole data");
			}
			return new Vector<String>(data);
		}
		Vector<String> out = new Vector<String>();
		String localFilter = filter.toLowerCase();
		if (logger.isDebugEnabled()){
			logger.debug("Filter is " + localFilter);
		}
		for (String s : data) {
			String alias = SpherePossibleEmailsSet
					.parseSingleAddress(s);
			String description = StringProcessor.unsuitFromLapki(SpherePossibleEmailsSet
					.getDescriptionFromAddress(s)
					.toLowerCase());
			if ((alias.startsWith(localFilter))
					|| (description
							.startsWith(localFilter))) {
				if (logger.isDebugEnabled()){
					logger.debug("Address passed: " + s);
				}
				out.add(s);
			} else {
				if (logger.isDebugEnabled()){
					logger.debug("Address did not passed: " + s);
				}				
			}
		}
		if (logger.isDebugEnabled()){
			logger.debug("Returning filtered set with size: " + out.size());
		}
		return out;
	}
}
