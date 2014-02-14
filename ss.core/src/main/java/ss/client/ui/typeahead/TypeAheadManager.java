/**
 * 
 */
package ss.client.ui.typeahead;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.protocol.getters.GetBookmarkAddressesCommand;
import ss.client.ui.ControlPanel;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SimpleBrowserDataSource;
import ss.client.ui.email.EmailController;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.client.ui.models.autocomplete.BaseDataModel;
import ss.client.ui.models.autocomplete.DataSourceLabeler;
import ss.client.ui.models.autocomplete.FilteredDataSource;
import ss.client.ui.models.autocomplete.FilteredModel;
import ss.client.ui.models.autocomplete.ResultAdapter;
import ss.client.ui.models.autocomplete.TextResultListener;
import ss.client.ui.tempComponents.DropDownToolItem;
import ss.common.BookmarkUtils;
import ss.common.UiUtils;
import ss.global.SSLogger;
import ss.util.StringProcessor;

/**
 * @author roman
 *
 */
public class TypeAheadManager {

	private static final Logger logger = SSLogger.getLogger(TypeAheadManager.class);
	
	public static final TypeAheadManager INSTANCE = new TypeAheadManager();
	
	private final Hashtable<Text, TypeAheadComponent> typeaheads = new Hashtable<Text, TypeAheadComponent>();
	
	private TypeAheadManager() {
		super();
	}
	
	public void addKeywordAutoComplete(final Text text) {
		removeTypeAhead(text);
		FilteredModel model = new FilteredModel<Document>(
				new FilteredDataSource<Document>() {

					private DialogsMainCli cli = SupraSphereFrame.INSTANCE.client;

					private Hashtable session = cli.session;

					public Vector<Document> getData(String filter) {
						return this.cli
						.getAllKeyWords(filter, this.session);
					}
				}, 500, BaseDataModel.FilterType.CaseSensitive,
				new DataSourceLabeler<Document>() {

					public String getDataLabel(Document doc) {
						if (doc.getRootElement().element("subject") != null) {
							return doc.getRootElement().element("subject")
							.attributeValue("value");
						} else {
							return "";
						}
					}
				});
		setTypeahead(text, new TypeAheadComponent<Object>(text, model, new TextResultListener<Object>(
				text)));
	}
	
	public void removeTypeAhead(final Text text) { 
		if(this.typeaheads.get(text)!=null) {
			TypeAheadComponent typeahead = this.typeaheads.remove(text);
			text.removeKeyListener(typeahead.getKeyListener());
		}
	}
	
	private void setTypeahead(final Text text, final TypeAheadComponent typeAhead) {
		removeTypeAhead(text);
		this.typeaheads.put(text, typeAhead);
	}
	
	public void addBookmarkAutoComplite(final Text text) {
			removeTypeAhead(text);
			FilteredModel model = new FilteredModel<Object>(
					new FilteredDataSource<Object>() {
						private DialogsMainCli cli = SupraSphereFrame.INSTANCE.client;
						private Hashtable session = cli.session;

						public Vector<Object> getData(String filter) {
							Vector<String> domains = getDataDomain(filter);
							Vector<Document> keywords = getDataKeywords(filter);
							Vector<Object> data = new Vector<Object>(domains
									.size()
									+ keywords.size());
							data.addAll(domains);
							data.addAll(keywords);
							return data;
						}

						public Vector<String> getDataDomain(String filter) {
							GetBookmarkAddressesCommand command = new GetBookmarkAddressesCommand();
							command.addLookupSpheres(this.cli.getVerifyAuth()
									.getCurrentMemberEnabledSpheres());
							command.setFilter( filter );
							ArrayList reply = command.execute(this.cli,
									new ArrayList<String>().getClass());
							final TreeSet<String> set = new TreeSet<String>(
									new Comparator<String>() {
										public int compare(String o1, String o2) {
											return BookmarkUtils.getSignificantPart(o1)
													.compareTo(
															BookmarkUtils.getSignificantPart(o2));
										}
									});
							set.addAll(reply);
							return new Vector<String>( set );
						}

						public Vector<Document> getDataKeywords(String filter) {
							return this.cli
									.getAllKeyWords(filter, this.session);
						}

					}, 500, BaseDataModel.FilterType.NoFilter,
					new DataSourceLabeler<Object>() {

						public String getKeyWordDataLabel(Document doc) {
							if (doc.getRootElement().element("subject") != null) {
								return doc.getRootElement().element("subject")
										.attributeValue("value");
							} else {
								return "";
							}
						}

						public String getDomainDataLabel(String data) {
							return data;
						}

						public String getDataLabel(Object data) {
							if (data instanceof String) {
								return getDomainDataLabel((String) data);
							} else if (data instanceof Document) {
								return getKeyWordDataLabel((Document) data);
							} else
								return "";
						}
					});
			setTypeahead(text, new TypeAheadComponent<Object>(text, model, new ResultAdapter<Object>() {

				@Override
				public void processListSelection(String listSelection,
						Object real) {
					text.setText("");
					if (real instanceof String) {
						loadBrowser(listSelection);
					} else {
						addTaggedURLSAutoComplite(text, (Document) real);
					}
				}
			}));
		}
	
	protected void addTaggedURLSAutoComplite(final Text text, Document real) {
			final String uniqueId = real.getRootElement().element("unique_id")
					.attributeValue("value");
			removeTypeAhead(text);
			FilteredModel model = new FilteredModel<String>(
					new FilteredDataSource<String>() {

						private DialogsMainCli cli = SupraSphereFrame.INSTANCE.client;

						private Hashtable session = cli.session;
						
						private Vector<String> findURLSbyTag = this.cli
								.findURLSbyTag(uniqueId, this.session);

						public Vector<String> getData(String filter) {
							return this.findURLSbyTag;
						}
					}, 50, BaseDataModel.FilterType.NoFilter,
					new DataSourceLabeler<String>() {

						public String getDataLabel(String data) {
							return data;
						}
					});
			setTypeahead(text, new TypeAheadComponent<String>(text, model, new ResultAdapter<String>() {
				@Override
				public void processListSelection(String listSelection,
						String real) {
					text.setText("");
					loadBrowser(listSelection);
					removeTypeAhead(text);
					addBookmarkAutoComplite(text);
				}
				public void processEmptyResult() {
					UiUtils.swtInvoke(new Runnable() {
						public void run() {
							text.setText("");
							removeTypeAhead(text);
							addBookmarkAutoComplite(text);
						}
					});
				}
			}, true));
			if (this.getTypeAheadComponent(text) != null) {
				this.getTypeAheadComponent(text).openPopup();
			}
	}
	
	private void loadBrowser(String listSelection) {
		SupraSphereFrame.INSTANCE.addMozillaTab(SupraSphereFrame.INSTANCE.client.session, null,
				new SimpleBrowserDataSource(listSelection), true, null);

	}
	
	public void addDomainkAutoComplite(final Text text) {
		removeTypeAhead(text);
		FilteredModel model = new FilteredModel<String>(
				new FilteredDataSource<String>() {
					private DialogsMainCli cli = SupraSphereFrame.INSTANCE.client;
					private Hashtable session = this.cli.session;
					public Vector<String> getData(String filter) {
						return this.cli.getPrivateDomainNames(filter,
								this.session);
					}
				}, 500, BaseDataModel.FilterType.NoFilter,
				new DataSourceLabeler<String>() {

					public String getDataLabel(String data) {
						return data;
					}
				});
		setTypeahead(text, new TypeAheadComponent<Object>(text, model, new TextResultListener<Object>(
				text)));

	}
	
	public void addEmailAutoComplite(final ControlPanel cp, final Text text) {
		TypeAheadComponent<String> typeAhead = new TypeAheadComponent<String>(
				text, new FilteredModel<String>(
						new FilteredDataSource<String>() {
							public Vector<String> getData(String filter) {
								return processDataFiltered(cp, filter);
							}
						}, 200, BaseDataModel.FilterType.NoFilter,
						new DataSourceLabeler<String>() {

							public String getDataLabel(String data) {
								return data;
							}
						}), new ResultAdapter<String>() {
					@Override
					public void processListSelection(String selection,
							String realData) {
						text.setText(realData);
					}
				});
		setTypeahead(text, typeAhead);
	}
	
	private Vector<String> processDataFiltered(final ControlPanel cp, final String filter) {
		if (logger.isDebugEnabled()) {
			logger.debug("Processing data by filter: " + filter);
		}
		final List<String> data = EmailController
				.getPossibleSendToEmailsList(cp.getMP());
		if (logger.isDebugEnabled()) {
			logger.debug("Befor filtering addresses size is " + data.size());
			for (String s : data) {
				logger.debug("Address: " + s);
			}
		}

		if ((filter == null) || (filter.trim().equals(""))) {
			if (logger.isDebugEnabled()) {
				logger.debug("Filter is blank, returning not filtered data");
			}
			return new Vector<String>(data);
		}
		Vector<String> out = new Vector<String>();
		String localFilter = filter.toLowerCase();
		if (logger.isDebugEnabled()) {
			logger.debug("Modified filter is: " + localFilter);
		}
		for (String s : data) {
			String alias = SpherePossibleEmailsSet
					.parseSingleAddress(s).toLowerCase();
			String description = StringProcessor
					.unsuitFromLapki(SpherePossibleEmailsSet
							.getDescriptionFromAddress(s).toLowerCase());
			if (logger.isDebugEnabled()) {
				logger.debug("Checking is pass filter email address: " + s
						+ ", its description: " + description + " and alias: "
						+ alias);
			}
			if ((alias.startsWith(localFilter))
					|| (description.startsWith(localFilter))) {
				if (logger.isDebugEnabled()) {
					logger.debug("Passed, added to returning list");
				}
				out.add(s);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Not passed");
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Returning filtered addresses in size of "
					+ out.size());
		}
		return out;
	}
	
	private TypeAheadComponent getTypeAheadComponent(final Text text) {
		return this.typeaheads.get(text);
	}
}
