package ss.client.ui;

import java.awt.Dimension;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.spheremanagement.LayoutUtils;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.StringUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.LuceneSearchPreferences;
import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.global.SSLogger;
import ss.util.ImagesPaths;
import ss.util.SessionConstants;

public class LuceneSearchDialog extends BaseDialog {

	private DialogsMainCli cli;

	private Button allSearch;

	private Text query;

	private Button phraseSearch;

	private Combo inAllSearch;

	private BitSet fields;

	private Table table;
	
	private Button searchForAllBookmark;
	
	private Button showNotReadAssetsOnly;
	
	private boolean searchOnlyBookmark = false;

	private static final Logger logger = SSLogger
			.getLogger(LuceneSearchDialog.class);

	private String spheresQuery = "";

	private String giversQuery = "";

	private LuceneSearchPreferences preferences;

	private UserPersonalPreferences personalPreferences;

	private Label lFrom;

	private Label lTo;

	private String sphereId = null;

	private static ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_LUCENESEARCHDIALOG);

	private final static String SUPRASEARCH = "LUCENESEARCHDIALOG.SUPRASEARCH";

	private static final String RESULT_DOCUMENT_CONTAINS = "LUCENESEARCHDIALOG.RESULT_DOCUMENT_CONTAINS";

	private static final String EXACT_PHRASE = "LUCENESEARCHDIALOG.EXACT_PHRASE";

	private static final String EVERY_WORD = "LUCENESEARCHDIALOG.EVERY_WORD";

	private static final String ANY_WORD = "LUCENESEARCHDIALOG.ANY_WORD";

	private static final String FIELDS_FOR_SEARCH = "LUCENESEARCHDIALOG.FIELDS_FOR_SEARCH";

	private static final String SUBJECT = "LUCENESEARCHDIALOG.SUBJECT";

	private static final String CONTENT = "LUCENESEARCHDIALOG.CONTENT";

	private static final String COMMENT = "LUCENESEARCHDIALOG.COMMENT";

	private static final String BODY = "LUCENESEARCHDIALOG.BODY";

	private static final String MATCH_IN = "LUCENESEARCHDIALOG.MATCH_IN";

	private static final String ANY = "LUCENESEARCHDIALOG.ANY";

	private static final String ALL = "LUCENESEARCHDIALOG.ALL";

	private static final String SPHERES_FOR_SEARCH = "LUCENESEARCHDIALOG.SPHERES_FOR_SEARCH";

	private static final String ALL_AVAILABLE_SPHERES = "LUCENESEARCHDIALOG.ALL_AVAILABLE_SPHERES";

	private static final String SEARCH = "LUCENESEARCHDIALOG.SEARCH";

	private static final String TERSE = "LUCENESEARCHDIALOG.TERSE";

	private static final String MESSAGE = "LUCENESEARCHDIALOG.MESSAGE";

	private static final String UPPER_COMMENT = "LUCENESEARCHDIALOG.UPPER_COMMENT";

	private static final String BOOKMARK = "LUCENESEARCHDIALOG.BOOKMARK";

	private static final String TAG = "LUCENESEARCHDIALOG.TAG";

	private static final String CONTACT = "LUCENESEARCHDIALOG.CONTACT";

	private static final String FILE = "LUCENESEARCHDIALOG.FILE";

	private static final String EMAIL = "LUCENESEARCHDIALOG.EMAIL";

	private static final String ALL_GIVERS = "LUCENESEARCHDIALOG.ALL_GIVERS";

	private static final String CANCEL = "LUCENESEARCHDIALOG.CANCEL";

	private static final String FROM = "LUCENESEARCHDIALOG.FROM";

	private static final String TO = "LUCENESEARCHDIALOG.TO";

	private static final String NONE = "LUCENESEARCHDIALOG.NONE";

	private static final String DATE_RANGES = "LUCENESEARCHDIALOG.DATE_RANGES";

	private static Hashtable<String, String> XMLTYPES = getTypes();

	private static String[] fieldsNames = { "subject", "content", "comment",
			"body", "role", "contact", "keywords", "file" };

	public LuceneSearchDialog(DialogsMainCli cli) {
		this.cli = cli;
	}

	@Override
	protected String getStartUpTitle() {
		return bundle.getString(SUPRASEARCH);
	}

	@Override
	protected void initializeControls() {
		initializePreferences();
		getShell().setLayout(new GridLayout(4, false));
		this.query = new Text(getShell(), SWT.SINGLE | SWT.BORDER);
		this.query.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
			}

			public void keyReleased(KeyEvent arg0) {
				if (arg0.keyCode == 13) {
					doSearchAction();
				}
			}
		});

		this.query.setSize(300, 70);
		this.query.setLayoutData(getTextFieldGridData());

		this.table = new Table(getShell(), SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		this.table.setLayoutData(getTableGridData());

		fillTable(this.table);

		this.table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				if (event.detail == SWT.CHECK) {
					int index = (Integer) item.getData();
					boolean checked = item.getChecked();
					switch (index) {
					case 0:
						getPreferences().setTerseSelected(checked);
						break;
					case 1:
						getPreferences().setMessageSelected(checked);
						break;
					case 2:
						getPreferences().setCommentSelected(checked);
						break;
					case 3:
						getPreferences().setBookmarkSelected(checked);
						break;
					case 4:
						getPreferences().setKeywordsSelected(checked);
						break;
					case 5:
						getPreferences().setContactSelected(checked);
						break;
					case 6:
						getPreferences().setFileSelected(checked);
						break;
					case 7:
						getPreferences().setEmailSelected(checked);
						break;
					default:
						break;
					}
				}
			}
		});

		final Composite groupContainer = new Composite(getShell(), SWT.NONE);
		groupContainer.setLayoutData(getGroupGridData());
		groupContainer.setLayout(new GridLayout(1, false));
		
		final Group bookmarkGroup = new Group(groupContainer, SWT.NONE);
		bookmarkGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		bookmarkGroup.setLayout(new GridLayout(1, false));
		bookmarkGroup.setText("Search mode");
		
		if(this.sphereId==null) {	
			this.searchForAllBookmark = new Button(bookmarkGroup, SWT.CHECK);
			this.searchForAllBookmark.setText("Just search for bookmarks");
			this.searchForAllBookmark.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		this.showNotReadAssetsOnly = new Button(bookmarkGroup, SWT.CHECK);
		this.showNotReadAssetsOnly.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.showNotReadAssetsOnly.setText("Show only new assets");
		this.showNotReadAssetsOnly.setSelection(false);

		Group typeOfSearch = new Group(groupContainer, SWT.SHADOW_NONE);
		typeOfSearch.setLayoutData(getGroupGridData());
		typeOfSearch.setText(bundle.getString(RESULT_DOCUMENT_CONTAINS));
		GridLayout layoutOfType = new GridLayout();
		layoutOfType.numColumns = 3;
		typeOfSearch.setLayout(layoutOfType);

		this.phraseSearch = new Button(typeOfSearch, SWT.CHECK);
		this.phraseSearch.setText(bundle.getString(EXACT_PHRASE));
		this.phraseSearch
				.setSelection(false);

		this.allSearch = new Button(typeOfSearch, SWT.CHECK);
		this.allSearch.setText(bundle.getString(EVERY_WORD));
		this.allSearch.setSelection(true);

		final Button anySearch = new Button(typeOfSearch, SWT.CHECK);
		anySearch.setText(bundle.getString(ANY_WORD));
		anySearch.setEnabled(false);
		anySearch.setSelection(false);

		this.phraseSearch.addSelectionListener(new SelectionAdapter() {
			LuceneSearchDialog dialog = LuceneSearchDialog.this;

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (this.dialog.phraseSearch.getSelection()) {
					this.dialog.allSearch.setSelection(false);
					anySearch.setSelection(false);
				} else if (!this.dialog.allSearch.getSelection()) {
					anySearch.setSelection(true);
				}
			}
		});

		this.allSearch.addSelectionListener(new SelectionAdapter() {
			LuceneSearchDialog dialog = LuceneSearchDialog.this;

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (this.dialog.allSearch.getSelection()) {
					this.dialog.phraseSearch.setSelection(false);
					anySearch.setSelection(false);
				} else if (!this.dialog.phraseSearch.getSelection()) {
					anySearch.setSelection(true);
				}

			}
		});

		anySearch.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});

		Group fieldsOfSearch = new Group(groupContainer, SWT.SHADOW_NONE);
		fieldsOfSearch.setLayoutData(getGroupGridData());
		fieldsOfSearch.setText(bundle.getString(FIELDS_FOR_SEARCH));
		GridLayout layoutOfFields = new GridLayout();
		layoutOfFields.numColumns = 3;
		fieldsOfSearch.setLayout(layoutOfFields);

		this.fields = new BitSet(6);

		if (this.preferences.isSubjectSelected()) {
			this.fields.set(0);
		}
		if (this.preferences.isContentSelected()) {
			this.fields.set(1);
		}
		if (this.preferences.isCommentSelected()) {
			this.fields.set(2);
		}
		if (this.preferences.isBodySelected()) {
			this.fields.set(3);
		}
		if (this.preferences.isRoleSelected()) {
			this.fields.set(4);
		}

		this.fields.set(5);
		this.fields.set(6);

		final Button subjectSearch = new Button(fieldsOfSearch, SWT.CHECK);
		subjectSearch.setText(bundle.getString(SUBJECT));
		subjectSearch.setData("order", 0);
		subjectSearch.setSelection(this.preferences.isSubjectSelected());

		final Button contentSearch = new Button(fieldsOfSearch, SWT.CHECK);
		contentSearch.setText(bundle.getString(CONTENT));
		contentSearch.setData("order", 1);
		contentSearch.setSelection(this.preferences.isContentSelected());

		final Button commentSearch = new Button(fieldsOfSearch, SWT.CHECK);
		commentSearch.setText(bundle.getString(COMMENT));
		commentSearch.setData("order", 2);
		commentSearch.setSelection(this.preferences.isCommentSelected());

		final Button bodySearch = new Button(fieldsOfSearch, SWT.CHECK);
		bodySearch.setText(bundle.getString(BODY));
		bodySearch.setData("order", 3);
		bodySearch.setSelection(this.preferences.isBodySelected());
		
		final Button roleSearch = new Button(fieldsOfSearch, SWT.CHECK);
		roleSearch.setText("type");
		roleSearch.setData("order", 4);
		roleSearch.setSelection(this.preferences.isRoleSelected());

		new Label(fieldsOfSearch, SWT.LEFT);
		
		final Label inAllSearchLabel = new Label(fieldsOfSearch, SWT.LEFT);
		inAllSearchLabel.setText(bundle.getString(MATCH_IN));

		this.inAllSearch = new Combo(fieldsOfSearch, SWT.READ_ONLY);
		this.inAllSearch.setLayoutData(getComboGridData());
		this.inAllSearch.setItems(new String[] { bundle.getString(ANY),
				bundle.getString(ALL) });
		this.inAllSearch.select(this.preferences.getFieldsModifier());
		this.inAllSearch.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getPreferences()
						.setFieldsModifier(
								LuceneSearchDialog.this.inAllSearch
										.getSelectionIndex());
			}
		});

		SelectionAdapter fieldAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				int order = (Integer) ((Widget) event.getSource())
						.getData("order");
				LuceneSearchDialog.this.fields.flip(order);
				switch (order) {
				case 0:
					getPreferences().setSubjectSelected(
							LuceneSearchDialog.this.fields.get(order));
					break;
				case 1:
					getPreferences().setContentSelected(
							LuceneSearchDialog.this.fields.get(order));
					break;
				case 2:
					getPreferences().setCommentBodySelected(
							LuceneSearchDialog.this.fields.get(order));
					break;
				case 3:
					getPreferences().setBodySelected(
							LuceneSearchDialog.this.fields.get(order));
					break;
				case 4:
					getPreferences().setRoleSelected(
							LuceneSearchDialog.this.fields.get(order));
				default:
					break;
				}
				if (!(subjectSearch.getSelection()
						|| contentSearch.getSelection()
						|| commentSearch.getSelection() || bodySearch
						.getSelection())) {
					bodySearch.setSelection(true);
					getPreferences().setBodySelected(true);
				}
			}
		};

		subjectSearch.addSelectionListener(fieldAdapter);
		contentSearch.addSelectionListener(fieldAdapter);
		commentSearch.addSelectionListener(fieldAdapter);
		bodySearch.addSelectionListener(fieldAdapter);

		final Group spheresOfSearch = new Group(groupContainer, SWT.SHADOW_NONE);
		spheresOfSearch.setLayoutData(getGroupGridData());
		spheresOfSearch.setText(bundle.getString(SPHERES_FOR_SEARCH));
		layoutOfType = new GridLayout();
		layoutOfType.numColumns = 3;
		spheresOfSearch.setLayout(layoutOfType);

		Button spheres = null;
		if (this.sphereId == null) {
			spheres = new Button(spheresOfSearch, SWT.CHECK);
			spheres.setText(bundle.getString(ALL_AVAILABLE_SPHERES));
			spheres.setSelection(true);
			spheres.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent se) {
					if (!((Button)se.widget).getSelection()) {
						SearchSpheresSelector selector = new SearchSpheresSelector(
								LuceneSearchDialog.this.cli.getVerifyAuth(),
								new LuceneSearchDialog.SpheresQuery(
										LuceneSearchDialog.this));
						selector.show(LuceneSearchDialog.this.getShell());

					}

				}
			});
		} else {
			this.setSpheresQuery("sphere_id:(\""+this.sphereId+"\")");
		}
		final Button spheresToDisable = spheres;

		final Button givers = new Button(spheresOfSearch, SWT.CHECK);
		givers.setText(bundle.getString(ALL_GIVERS));
		givers.setSelection(true);
		givers.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!givers.getSelection()) {
					SearchGiversSelector selector = new SearchGiversSelector(
							LuceneSearchDialog.this.cli.getVerifyAuth(),
							new LuceneSearchDialog.GiversQuery(
									LuceneSearchDialog.this));
					selector.show(LuceneSearchDialog.this.getShell());

				}

			}
		});

		final Group dateRanges = new Group(groupContainer, SWT.SHADOW_NONE);
		dateRanges.setLayoutData(getGroupGridData());
		dateRanges.setText(bundle.getString(DATE_RANGES));
		GridLayout layoutOfDate = new GridLayout();
		layoutOfDate.numColumns = 2;
		dateRanges.setLayout(layoutOfDate);

		final Button from = new Button(dateRanges, SWT.PUSH);
		from.setText(bundle.getString(FROM));
		from.setLayoutData(getBuutonGridData());

		this.lFrom = new Label(dateRanges, SWT.NONE);
		this.lFrom.setText(bundle.getString(NONE));

		from.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Date to = (Date) LuceneSearchDialog.this.lTo.getData("date");
				DateDialog qw = new DateDialog(new DateButtonListener(
						dateRanges, LuceneSearchDialog.this.lFrom), to);
				qw.show(getShell());
			}
		});

		final Button to = new Button(dateRanges, SWT.PUSH);
		to.setText(bundle.getString(TO));
		to.setLayoutData(getBuutonGridData());

		this.lTo = new Label(dateRanges, SWT.NONE);
		this.lTo.setText(bundle.getString(NONE));

		to.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Date from = (Date) LuceneSearchDialog.this.lFrom
						.getData("date");
				DateDialog qw = new DateDialog(new DateButtonListener(
						dateRanges, LuceneSearchDialog.this.lTo), from);
				qw.show(getShell());
			}
		});
		
		if (this.searchForAllBookmark != null) {
			this.searchForAllBookmark.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean selection = ((Button)e.widget).getSelection();
					to.setEnabled(!selection);
					from.setEnabled(!selection);
					givers.setEnabled(!selection);
					if(spheresToDisable!=null) {
						spheresToDisable.setEnabled(!selection);
					}
					if(selection) {
						for(TableItem item : LuceneSearchDialog.this.table.getItems()) {
							item.setChecked(item.getText().equals(bundle.getString(BOOKMARK)));
						}
					}
					LuceneSearchDialog.this.searchOnlyBookmark = selection;
					LuceneSearchDialog.this.table.setEnabled(!selection);
				}
			});
		}

		Composite buttonComp = new Composite(getShell(), SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		buttonComp.setLayout(layout);

		Button sendGeneralQuery = new Button(buttonComp, SWT.PUSH);
		sendGeneralQuery.setText(bundle.getString(SEARCH));
		sendGeneralQuery.setLayoutData(getBuutonGridData());
		sendGeneralQuery.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		sendGeneralQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				doSearchAction();
			}
		});

		Button cancelButton = new Button(buttonComp, SWT.PUSH);
		cancelButton.setText(bundle.getString(CANCEL));
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				// savePreferences();
				getShell().dispose();
			}
		});

		this.getShell().addShellListener(new ShellAdapter() {

			@Override
			public void shellClosed(ShellEvent arg0) {
				savePreferences();
			}
		});
	}

	/**
	 * 
	 */
	protected void savePreferences() {
		String login = (String) this.cli.getVerifyAuth().getSession().get(
				SessionConstants.USERNAME);
		logger.info(this.personalPreferences);
		SsDomain.MEMBER_HELPER.setMemberPreferences(login,
				this.personalPreferences);
	}

	/**
	 * 
	 */
	private void initializePreferences() {
		String login = (String) this.cli.getVerifyAuth().getSession().get(
				SessionConstants.USERNAME);
		this.personalPreferences = SsDomain.MEMBER_HELPER
				.getMemberPreferences(login);
		this.preferences = this.personalPreferences.getSearchPreferences();
	}

	private void fillTable(final Table table) {
		createItem(table, bundle.getString(TERSE), getImage(ImagesPaths.TERSE),
				0, this.preferences.isTerseChecked());
		createItem(table, bundle.getString(MESSAGE),
				getImage(ImagesPaths.MESSAGE), 1, this.preferences
						.isMessageChecked());
		createItem(table, bundle.getString(UPPER_COMMENT),
				getImage(ImagesPaths.COMMENT), 2, this.preferences
						.isCommentChecked());
		createItem(table, bundle.getString(BOOKMARK),
				getImage(ImagesPaths.BOOKMARK), 3, this.preferences
						.isBookmarkChecked());
		createItem(table, bundle.getString(TAG),
				getImage(ImagesPaths.KEYWORDS), 4, this.preferences
						.isKeywordsChecked());
		createItem(table, bundle.getString(CONTACT),
				getImage(ImagesPaths.CONTACT), 5, this.preferences
						.isContactChecked());
		createItem(table, bundle.getString(FILE), getImage(ImagesPaths.FILE),
				6, this.preferences.isFileChecked());
		createItem(table, bundle.getString(EMAIL),
				getImage(ImagesPaths.EMAIL_COMPOSE_ICON), 7, this.preferences
						.isEmailChecked());
	}

	private void createItem(final Table table, String type, Image image,
			int index, boolean checked) {
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(type);
		item.setImage(image);
		item.setChecked(checked);
		item.setData(index);
	}

	public static Query getQuery(String rawQuery) {
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(rawQuery);
		} catch (ParseException ex) {
			logger.error("", ex);
		}
		return query;
	}

	@Override
	public void show(Shell parentShell) {
		super.show(parentShell);
	}

	@Override
	public Dimension getStartUpDialogSize() {
		return new Dimension(500, 320);
	}

	private GridData getComboGridData() {
		GridData gridData = new GridData();

		gridData.horizontalSpan = 1;

		return gridData;
	}

	private Image getImage(String imagePath) {
		Image image = null;
		try {
			image = new Image(Display.getDefault(), getClass().getResource(
					imagePath).openStream());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return image;
	}

	private void doSearchAction() {
		final String outersQuery = this.query.getText().trim();
		final boolean outerOnlyNotRead = this.showNotReadAssetsOnly.getSelection();
		if ( StringUtils.isBlank( outersQuery ) && ( !outerOnlyNotRead )) {
			UserMessageDialogCreator.info("Please specify keyword to search or search for new messages", "Suprasearch usage");
			return;
		}
		final ArrayList<String> types = new ArrayList<String>();
		for (TableItem item : this.table.getItems()) {
			if (item.getChecked()) {
				types.add(XMLTYPES.get(item.getText()));
			}
		}
		Thread t = new Thread(new Runnable() {
			private DialogsMainCli cli = LuceneSearchDialog.this.cli;

			private LuceneSearchDialog dialog = LuceneSearchDialog.this;

			private String sQuery = outersQuery;

			private boolean allSelection = this.dialog.allSearch.getSelection();

			private boolean phraseSelection = this.dialog.phraseSearch
					.getSelection();

			private boolean allFieldsSearch = (this.dialog.inAllSearch
					.getSelectionIndex() == 1);

			private Date from = (Date) LuceneSearchDialog.this.lFrom
					.getData("date");

			private Date to = (Date) LuceneSearchDialog.this.lTo
					.getData("date");
			
			private boolean onlyNotRead = outerOnlyNotRead;
			
			private String ownContact = this.dialog.cli.getContact();

			public void run() {

				String constructedQuery = "";
				if (StringUtils.isNotBlank(this.sQuery)) {
					for (int i = 0; i < fieldsNames.length; i++) {
						if (this.dialog.fields.get(i)) {
							String fieldQuery = getFieldQuery(this.sQuery,
									getFieldKey());

							constructedQuery += (this.allFieldsSearch ? (i > 3 ? " "
									: " +")
									: " ")
									+ fieldsNames[i] + ":(" + fieldQuery + ")";
						}
					}
				}
				if (StringUtils.isNotBlank(constructedQuery)){
					constructedQuery = "+(" +constructedQuery + ")";
				}
				if (this.onlyNotRead) {
					constructedQuery += " -(voted:(\"[" + this.ownContact + "]\"))";
				}
				String typeQuery = getTypeQuery();
				constructedQuery = (typeQuery.length() > 0) ? (constructedQuery + " +(" + typeQuery + ")")
						: (constructedQuery);
				constructedQuery = (LuceneSearchDialog.this.spheresQuery
						.length() > 0) ? (constructedQuery + " +("
						+ LuceneSearchDialog.this.spheresQuery + ")")
						: (constructedQuery);
				constructedQuery = (LuceneSearchDialog.this.giversQuery
						.length() > 0) ? (constructedQuery + " +("
						+ LuceneSearchDialog.this.giversQuery + ")")
						: (constructedQuery);
				String momentQuery = getMomentQuery();
				constructedQuery = (momentQuery.length() > 0) ? (constructedQuery + " +(" + momentQuery + ")")
						: (constructedQuery);
//				constructedQuery = (typeQuery.length() > 0) ? ("+("
//						+ constructedQuery + ")" + " +(" + typeQuery + ")")
//						: (constructedQuery);
//				constructedQuery = (LuceneSearchDialog.this.spheresQuery
//						.length() > 0) ? ("+(" + constructedQuery + ")" + " +("
//						+ LuceneSearchDialog.this.spheresQuery + ")")
//						: (constructedQuery);
//				constructedQuery = (LuceneSearchDialog.this.giversQuery
//						.length() > 0) ? ("+(" + constructedQuery + ")" + " +("
//						+ LuceneSearchDialog.this.giversQuery + ")")
//						: (constructedQuery);
//				String momentQuery = getMomentQuery();
//				constructedQuery = (momentQuery.length() > 0) ? ("+("
//						+ constructedQuery + ")" + " +(" + momentQuery + ")")
//						: (constructedQuery);
				if (logger.isDebugEnabled()) {
					logger.debug("Query is :" + constructedQuery);
					logger.debug("Query string is :" + this.sQuery);
				}
//				logger.error(constructedQuery);
				this.cli.searchSupraSphere(this.sQuery,
						getQuery(constructedQuery), false, LuceneSearchDialog.this.searchOnlyBookmark);
			}

			private String getMomentQuery() {
				if ((this.from != null) && (this.to == null)) {
					this.to = new Date();
				} else if ((this.from == null) && (this.to != null)) {
					Calendar instance = Calendar.getInstance();
					instance.clear();
					this.from = instance.getTime();
				}
				if ((this.from != null) && (this.to != null)) {
					if (this.from.after(this.to)) {
						Date temp = this.to;
						this.to = this.from;
						this.from = temp;
					}
					return "moment:["
							+ DateTools.dateToString(this.from,
									DateTools.Resolution.DAY)
							+ " TO "
							+ DateTools.dateToString(this.to,
									DateTools.Resolution.DAY) + "]";
				} else
					return "";
			}

			private String getTypeQuery() {
				if (types.size() > 0) {
					logger.info("Bigger than zero");
					String typeQuery = "type:(";
					for (String type : types) {
						logger.info("TYPE: " + type);
						if (type != null) {
							if (type.equals("tag")) {
								type = "keywords";
							}
							typeQuery += " ||" + type;
						}
					}
					return typeQuery + ")";
				} else
					return "";
			}

			private String getFieldQuery(final String sQuery, final int fieldkey) {
				String fieldQuery = "";
				switch (fieldkey) {
				case 1:
					fieldQuery += "\"" + sQuery + "\"";
					break;
				case 2:
					String[] words = sQuery.split(" ");
					for (int i = 0; i < words.length; i++) {
						fieldQuery += "+" + words[i]
								+ ((i == words.length - 1) ? "" : " ");
					}
					break;
				default:
					fieldQuery += sQuery;
					break;
				}

				return fieldQuery;
			}

			private int getFieldKey() {
				if (this.phraseSelection) {
					return 1;
				}
				if (this.allSelection) {
					return 2;
				}
				return 0;
			}
		});
		t.start();
		close();
	}

	private GridData getTextFieldGridData() {
		GridData gridData = new GridData();
		gridData.heightHint = 25;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		return gridData;
	}

	private GridData getGroupGridData() {
		GridData gridData = new GridData();

		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		return gridData;
	}

	private GridData getBuutonGridData() {
		GridData gridData = new GridData();

		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.BEGINNING;
		return gridData;
	}

	private GridData getTableGridData() {
		GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		return gridData;
	}

	public void setSpheresQuery(String query) {
		this.spheresQuery = query;
	}

	public void setGiversQuery(String query) {
		this.giversQuery = query;
	}

	public LuceneSearchPreferences getPreferences() {
		return this.preferences;
	}

	private static Hashtable<String, String> getTypes() {
		Hashtable<String, String> types = new Hashtable<String, String>();
		types.put(bundle.getString(TERSE), "terse");
		types.put(bundle.getString(MESSAGE), "message");
		types.put(bundle.getString(UPPER_COMMENT), "comment");
		types.put(bundle.getString(BOOKMARK), "bookmark");
		types.put(bundle.getString(TAG), "tag");
		types.put(bundle.getString(CONTACT), "contact");
		types.put(bundle.getString(FILE), "file");
		types.put(bundle.getString(EMAIL), "externalemail");
		return types;
	}

	public static void performDefaultSupraSearch(final String queryText, boolean inSameTab) {
		BitSet fields = new BitSet();
		fields.set(0, 8);
		final ArrayList<String> types = new ArrayList<String>();
		types.add("terse");
		types.add("message");
		types.add("comment");
		types.add("bookmark");
		types.add("tag");
		types.add("contact");
		types.add("file");
		types.add("externalemail");

		final QueryCreater creator = new QueryCreater(types, queryText, 2,
				false, fields, "", "", null);
		final Query query = creator.create();
		SupraSphereFrame.INSTANCE.client.searchSupraSphere(queryText, query, inSameTab, false);
	}

	/**
	 * @author dankosedin
	 * 
	 */
	private final class DateButtonListener implements SWTCalendarListener {
		/**
		 * 
		 */
		private final Group parent;

		/**
		 * 
		 */
		private final Label label;

		/**
		 * @param ranges
		 * @param label
		 */
		private DateButtonListener(Group ranges, Label label) {
			this.parent = ranges;
			this.label = label;
		}

		public void dateChanged(SWTCalendarEvent event) {
			Date time = event.getCalendar().getTime();
			String text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(
					time);
			this.label.setData("date", time);
			this.label.setText(text);
			this.parent.layout();
		}
	}

	class SpheresQuery {

		private LuceneSearchDialog reciver;

		private String query;

		public SpheresQuery(LuceneSearchDialog reciver) {
			this.reciver = reciver;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public void deliver() {
			this.reciver.setSpheresQuery(this.query);
		}

	}

	class GiversQuery {

		private LuceneSearchDialog reciver;

		private String query;

		public GiversQuery(LuceneSearchDialog reciver) {
			this.reciver = reciver;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public void deliver() {
			this.reciver.setGiversQuery(this.query);
		}

	}

	static class QueryCreater {

		private final ArrayList<String> types;

		private final int fieldkey;

		private final boolean allFieldsSearch;

		private final String sQuery;

		private final BitSet fields;

		final private String spheres;

		final private String givers;
		
		final private String ownContact;

		QueryCreater(final ArrayList<String> types, final String sQuery,
				final int fieldkey, final boolean allFieldsSearch,
				final BitSet fields, final String spheres, final String givers, final String ownContact) {
			this.types = types;
			this.sQuery = sQuery;
			this.fieldkey = fieldkey;
			this.allFieldsSearch = allFieldsSearch;
			this.fields = fields;
			this.spheres = spheres;
			this.givers = givers;
			this.ownContact = ownContact;
		}

		Query create() {

			String constructedQuery = "";
			for (int i = 0; i < fieldsNames.length; i++) {
				if (this.fields.get(i)) {
					String fieldQuery = getFieldQuery(this.sQuery,
							this.fieldkey);

					constructedQuery += (this.allFieldsSearch ? (i > 3 ? " "
							: " +") : " ")
							+ fieldsNames[i] + ":(" + fieldQuery + ")";
				}
			}
			if (StringUtils.isNotBlank(this.ownContact)) {
				constructedQuery = "+(" +constructedQuery + ")" + " -(voted:(\"[" + this.ownContact + "]\"))";
			}
			String typeQuery = getTypeQuery();
			constructedQuery = (typeQuery.length() > 0) ? (constructedQuery + " +(" + typeQuery + ")")
					: (constructedQuery);
			constructedQuery = (this.spheres.length() > 0) ? (constructedQuery + " +(" + this.spheres + ")")
					: (constructedQuery);
			constructedQuery = (this.givers.length() > 0) ? (constructedQuery + " +(" + this.givers + ")")
					: (constructedQuery);
			if (logger.isDebugEnabled()) {
				logger.debug("Query is :" + constructedQuery);
			}
			return getQuery(constructedQuery);
		}

		private String getTypeQuery() {
			if (this.types.size() > 0) {
				logger.info("Bigger than zero");
				String typeQuery = "type:(";
				for (String type : this.types) {
					logger.info("TYPE: " + type);
					if (type != null) {
						if (type.equals("tag")) {
							type = "keywords";
						}
						typeQuery += " ||" + type;
					}
				}
				return typeQuery + ")";
			} else
				return "";
		}

		private String getFieldQuery(final String sQuery, final int fieldkey) {
			String fieldQuery = "";
			switch (fieldkey) {
			case 1:
				fieldQuery += "\"" + sQuery + "\"";
				break;
			case 2:
				String[] words = sQuery.split(" ");
				for (int i = 0; i < words.length; i++) {
					fieldQuery += "+" + words[i]
							+ ((i == words.length - 1) ? "" : " ");
				}
				break;
			default:
				fieldQuery += sQuery;
				break;
			}

			return fieldQuery;
		}
	}

	@Override
	protected void layoutDialog() {
		getShell().pack(true);
		super.layoutDialog();
	}

	/**
	 * @param sphereId
	 */
	public void setSearchInSphere(String sphereId) {
		this.sphereId = sphereId;
	}

}