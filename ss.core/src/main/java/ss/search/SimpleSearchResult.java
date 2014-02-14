package ss.search;

import java.io.StringReader;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.DefaultEncoder;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import ss.common.StringUtils;
import ss.domainmodel.IdItem;
import ss.domainmodel.SearchResultObject;
import ss.domainmodel.SphereStatement;
import ss.global.SSLogger;

public class SimpleSearchResult implements ISearchResult {
	
	private static String [] FIELDS = {"Last Name","Title","Organization","Street","Department", "Role",
		"Mobile","City","Voice1","State","Voice2","Code","Fax","Country","Login","Email","URL","Home Sphere","Original Note"};

	private static Logger logger = SSLogger.getLogger(SimpleSearchResult.class);

	private String messageId;

	private String giver;

	private String subject;

	private String formattedSubject;

	private Query query;

	private String body;

	private String formattedBody;

	private String formattedContent;

	private String content;

	private String comment;

	private String formattedComment;

	private String contact;

	private String formattedContact;

	private String keywords;

	private String formattedKeywords;

	private String address;
	
	private String type;
	
	private String sphereId;
	
	private String role;

	public SimpleSearchResult(Document doc, Query query) {
		logDoc(doc);
		initFields(doc, query);
		format();
	}

	/**
	 * @param doc
	 * @param query
	 */
	protected void initFields(Document doc, Query query) {
		this.messageId = doc.get("message_id");
		this.giver = doc.get("giver");
		this.subject = doc.get("subject");
		this.type = doc.get("type");
		this.comment = doc.get("comment");
		this.body = doc.get("body");
		this.content = doc.get("content");
		this.contact = StringUtils.cleanUpSearchResultText( doc.get("contact") );
		this.keywords = doc.get("keywords");
		this.address = doc.get("address");
		this.sphereId = doc.get("sphere_id");
		this.role = doc.get("role");
		this.query = query;
	}

	public boolean isSuch(ISearchResult result) {
		return false;
	}

	public String getType() {
		return this.type;
	}
	
	public String getRole(){
		return this.role;
	}
	
	public String getSphereId() {
		return this.sphereId;
	}

	public String getMessageId() {
		return this.messageId;
	}

	public String getGiver() {
		return this.giver;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getBody() {
		return this.body;
	}

	public String getComparisonParameter() {
		if(getType().equals("group")) {
			return getMessageId();
		}
		return getSubject();
	}
	
	public void format() {
		this.formattedBody = highlight(this.body, "body");
		this.formattedSubject = highlight(this.subject, "subject");
		this.formattedContent = highlight(this.content, "content");
		this.formattedComment = highlight(this.comment, "comment");
		this.formattedContact = highlight(this.contact, "contact");
		this.formattedKeywords = highlight(this.keywords, "keywords");
		logAll();
	}

	protected String highlight( final String field, final String fieldName ) {
		if (field == null) {
			return null;
		}
		try {
			String bestFragment;
			Highlighter highlighter = new Highlighter(
					new SimpleHTMLFormatter(
							"<font class=\"search_hightlightbody\">", "</font>"),
					new DefaultEncoder(), new QueryScorer(this.query,
							fieldName));
			Analyzer analyzer = new StandardAnalyzer();
			TokenStream tokenStream = analyzer.tokenStream(fieldName,
					new StringReader(field));
			bestFragment = highlighter.getBestFragments(tokenStream, field,
					3, "...");
			bestFragment = StringUtils.normalizeHtml( bestFragment );
			return bestFragment;
		} catch (Exception ex) {
			logger.error("Exception in finding best fragments", ex);
			return null;
		}
	}

	/**
	 * 
	 */
	protected void logAll() {
		if (logger.isInfoEnabled()) {
			logger.info("Body =" + this.body);
			logger.info("Comment =" + this.comment);
			logger.info("Contact =" + this.contact);
			logger.info("Keywords =" + this.keywords);
			logger.info("FBody =" + this.formattedBody);
			logger.info("FComment =" + this.formattedComment);
			logger.info("FContact =" + this.formattedContact);
			logger.info("FKeywords =" + this.formattedKeywords);
		}
	}

	public String getFormattedBody() {
		return (StringUtils.isBlank(this.formattedBody)) ? StringUtils.cutOfTooLongRemainder(this.body, 500) : this.formattedBody;
	}

	public String getFormattedContent() {
		return StringUtils.cutOfTooLongRemainder( 
				(StringUtils.isBlank(this.formattedContent)) ? this.content : this.formattedContent , 300 );
	}

	public String getFormattedComment() {
		return (StringUtils.isBlank(this.formattedComment)) ? this.comment : this.formattedComment;
	}

	public String getFormattedContact() {
		final String origString = (StringUtils.isBlank(this.formattedContact)) ? this.contact : this.formattedContact;
		String string = origString; 
		if( StringUtils.isNotBlank(string) ) {
			try {
				for(int i = 0; i < FIELDS.length; i++ ) {
					string = string.replaceAll(FIELDS[i], "; " + FIELDS[i]);
				}	
				string = string.replaceAll(":;", ": - ;");
				string = string.replace(":<", ": - ;<");
			} catch ( Throwable ex ) {
				logger.error( "Exception in replacing in Formatted Contact" , ex );
			}
		}
		return StringUtils.normalizeHtml( StringUtils.isNotBlank( string ) ? string : origString ); 
	}

	public String getFormattedKeywords() {
		return (StringUtils.isBlank(this.formattedKeywords)) ? this.keywords : this.formattedKeywords;
	}
	
	public String getKeywords() {
		return this.keywords;
	}

	public String getAddress() {
		return this.address;
	}

	public String getFormattedSubject() {
		return (StringUtils.isBlank(this.formattedSubject)) ? this.subject : this.formattedSubject;
	}

	/**
	 * @param doc
	 */
	@SuppressWarnings("unchecked")
	protected void logDoc(Document doc) {
		if (logger.isDebugEnabled()) {
			for (Enumeration<Field> e = doc.fields(); e.hasMoreElements();) {
				Field f = e.nextElement();
				logger.info("FieldName=" + f.name());
				logger.info("FieldValue=" + f.stringValue());
			}
		}
	}
	
	public boolean isGroupItem() {
		return getType().equals("contact") || SphereStatement.isClubdealType(getType());
	}

	public boolean isComposed() {
		return false;
	}

	public SearchResultObject getSearchResultObject() {
		SearchResultObject resultObject = new SearchResultObject();
		resultObject.setSubject(getFormattedSubject());
		resultObject.setAddress(getAddress());
		resultObject.setBody(getFormattedBody());
		resultObject.setComment(getFormattedComment());
		resultObject.setContact(getFormattedContact());
		resultObject.setContent(getFormattedContent());
		resultObject.setGiver(getGiver());
		resultObject.setKeywords(getFormattedKeywords());
		resultObject.setType(getType());
		resultObject.setRole( StringUtils.getNotNullString(getRole()) );
		
		IdItem item = new IdItem();
		item.setMessageId(getMessageId());
		item.setSphereId(getSphereId());
		resultObject.getIdCollection().add(item);
		
		return resultObject;
	}
}