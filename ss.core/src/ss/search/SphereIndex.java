package ss.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.dom4j.Attribute;
import org.dom4j.Element;

import ss.common.ExceptionHandler;
import ss.common.FileUtils;
import ss.common.StringUtils;
import ss.domainmodel.CommentStatement;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.VotedMember;
import ss.global.SSLogger;
import ss.rss.XSLTransform;
import ss.server.db.XMLDB;
import ss.util.DateTimeParser;

public class SphereIndex {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereIndex.class);
	
	private static final String BDIR = System.getProperty("user.dir");

	private static final String FSEP = System.getProperty("file.separator");

	private static Hashtable<String, SphereIndex> index = new Hashtable<String, SphereIndex>();

	private Directory dir;

	boolean needCreateIndex = false;

	private String sphereId;

	private SphereIndex(File base) throws IOException {
		try {
			if (!base.exists()) {
				base.mkdirs();
				this.needCreateIndex = true;
			}
			this.dir = FSDirectory.getDirectory(base, this.needCreateIndex);
		} catch (IOException e) {
			logger.error("Failed to create Directory", e);
			ExceptionHandler.handleException(this, e);
			throw e;
		}
	}

	private SphereIndex(String sphereId) throws IOException {
		this(new File(BDIR + FSEP + "index" + FSEP + sphereId));
		this.sphereId = sphereId;
	}
	
	public static void deleteSphereIndex( final String sphereId ) throws Exception {
		final File toDelete = new File(BDIR + FSEP + "index" + FSEP + sphereId);
		if (toDelete.exists()) {
			FileUtils.deleteFolder(toDelete);
		}
	}

	public synchronized boolean addDoc(org.dom4j.Document doc) {
		return addDoc(doc, true, false);
	}
	
	public synchronized boolean addDoc(org.dom4j.Document doc, boolean forcedAdd) {
		return addDoc(doc, true, forcedAdd);
	}

	public synchronized boolean addDoc(org.dom4j.Document doc,
			boolean searchContent, boolean forcedAdd) {
		if ( doc == null ) {
			logger.error("Document is null");
			return false;
		}
		final Statement statement = Statement.wrap(doc);
		if (logger.isDebugEnabled()) {
			logger.debug("doc is : " + statement.getSubject());
		}
		if ( !forcedAdd && !isDocumentIndexable(statement) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Document is not indexable");
			}
			return false;
		}
		final Document addDoc = createLuceneDoc(doc);
		final boolean resultOfOperation = addDoc(addDoc);
		if (logger.isDebugEnabled()) {
			logger.debug("resultOfOperation: " + resultOfOperation);
			logger.debug("searchContent: " + searchContent);
		}
		if ( resultOfOperation && searchContent ) {
			seacrhContent(doc);
		}
		return resultOfOperation;
	}
	
	/**
	 * Index only documents that should be indexed
	 */
	private boolean isDocumentIndexable( final Statement statement ){
		if (statement.isTerse()) return true;
		if (statement.isMessage()) return true;
		if (statement.isBookmark()) return true;
		if (statement.isEmail()) return true;
		if (statement.isComment()) return true;
		if (statement.isFile()) return true;
		if (statement.isKeywords()) return true;
		if (statement.isRss()) return true;
		if (statement.isResult()) return true;
		if (statement.isContact()) return true;
		if( statement.isSphere() ) {
			SphereStatement sphere = SphereStatement.wrap(statement.getBindedDocument());
			if (sphere.isEmailBox()){
				logger.info("Email box is not indexing");
				return false;
			}
			if(!this.sphereId.equals( sphere.getSystemName() )) {
				if (logger.isDebugEnabled()) {
					logger.debug("its sphereDefinition in core sphere, no need to index : " + sphere.getDisplayName());
				}
				return false;
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("its sphereDefinition in its own sphere, will index : " + sphere.getDisplayName());
				}
			}
			return true;
		}
		return false;
	}

	private boolean addDoc(Document addDoc) {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(this.dir, new StandardAnalyzer(),
					this.needCreateIndex);
			this.needCreateIndex = false;
			writer.addDocument(addDoc);
			writer.optimize();
			writer.close();
			return true;
		} catch (IOException e) {
			ExceptionHandler.handleException(this, e);
			return false;
		}
	}

	/**
	 * @param doc
	 * @return
	 */
	private Document createLuceneDoc(org.dom4j.Document doc) {

		Statement statement = Statement.wrap(doc);

		Document addDoc = new Document();
		processGeneral(addDoc, statement);
		processVotingModel( addDoc, statement );

		if ( statement.isComment() ) {
			processComment(addDoc, doc);
		}
		if ( statement.isContact() ) {
			processContact(addDoc, doc);
		}
		if ( statement.isBookmark() ) {
			processBookmark(addDoc, statement);
		}
		if ( statement.isEmail() ) {
			processEmail(addDoc, statement);
		}
		if ( statement.isSphere() ) {
			processSphere(addDoc, SphereStatement.wrap( doc ));
		}
		String keywords = getKeywords(doc);
		if (!keywords.equals("")) {
			logger.info("There is some keywords :" + keywords);
			addDoc.add(new Field("keywords", keywords, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		return addDoc;
	}

	/**
	 * @param addDoc
	 * @param statement
	 */
	private void processVotingModel( final Document addDoc, final Statement statement ) {
		String voted = "";
		boolean addComma = false;
		if ( statement.getVotedMembers() != null ) {
			for (VotedMember member : statement.getVotedMembers()) {
				if (StringUtils.isNotBlank( member.getName() )) {
					if (addComma) {
						voted += ",";
					} else {
						addComma = true;
					}
					voted += "[" + member.getName().trim() + "]";
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug( "Vounting processing for doc type: " + statement.getType() + 
					", subject: " + statement.getSubject() );
			logger.debug("Added voting is : " + (StringUtils.isNotBlank(voted) ? voted : "Nothing..."));
		}
		addDoc.add(new Field("voted", voted, Field.Store.YES,
			Field.Index.TOKENIZED));
	}

	/**
	 * @param addDoc
	 * @param statment
	 */
	private void processEmail(Document addDoc, Statement statment) {
		ExternalEmailStatement email = ExternalEmailStatement.wrap(statment
				.getBindedDocument());
		String reciever = email.getReciever();
		if (reciever != null) {
			addDoc.add(new Field("reciever", reciever, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		String ccrecievers = email.getCcrecievers();
		if (ccrecievers != null) {
			addDoc.add(new Field("cc", ccrecievers, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		String bccrecievers = email.getBccrecievers();
		if (bccrecievers != null) {
			addDoc.add(new Field("bcc", bccrecievers, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		String status = email.getStatus();
		if (status != null) {
			addDoc.add(new Field("status", status, Field.Store.YES,
					Field.Index.TOKENIZED));
		}

	}

	private void processGeneral(Document addDoc, Statement statement) {
		addDoc.add(new Field("sphere_id", this.sphereId, Field.Store.YES,
				Field.Index.UN_TOKENIZED));

		String messageId = statement.getMessageId();
		if (messageId != null) {
			addDoc.add(new Field("message_id", messageId, Field.Store.YES,
					Field.Index.UN_TOKENIZED));
		}
		String giver = statement.getGiver();
		if (giver != null) {
			addDoc.add(new Field("giver", giver, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		String subject = statement.getSubject();
		if (subject != null) {
			addDoc.add(new Field("subject", subject, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		String type = statement.isSphere() ? SphereStatement.wrap(
				statement.getBindedDocument()).getSphereType().name()
				.toLowerCase() : statement.getType();
		if (type != null) {
			addDoc.add(new Field("type", type, Field.Store.YES,
					Field.Index.UN_TOKENIZED));
		}
		String origBody = addBody(addDoc, statement);
		String date = statement.getMoment();
		if (date != null) {
			Date moment = DateTimeParser.INSTANCE.parseToDate(date);
			addDoc.add(new Field("moment", DateTools.dateToString(moment,
					DateTools.Resolution.HOUR), Field.Store.NO,
					Field.Index.UN_TOKENIZED));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Indexing new asset:");
			logger.debug("Its messageID = " + messageId);
			logger.debug("Its giver = " + giver);
			logger.debug("Its subject = " + subject);
			logger.debug("Its type = " + type);
			logger.debug("Its body = " + origBody);
		}
	}
	
	private String addBody( Document addDoc, Statement statement ){
		String body;
		if (statement.isSphere()) {
			SphereStatement sphere = SphereStatement.wrap( statement.getBindedDocument() );
			String email = StringUtils.getNotNullString( sphere.getPhisicalLocation().getEmail() );
			String telephone = StringUtils.getNotNullString( sphere.getPhisicalLocation().getTelephone() );
			String fax = StringUtils.getNotNullString( sphere.getPhisicalLocation().getFax() );
			String country = StringUtils.getNotNullString( sphere.getPhisicalLocation().getCountry() );
			String state = StringUtils.getNotNullString( sphere.getPhisicalLocation().getState() );
			String region = StringUtils.getNotNullString( sphere.getPhisicalLocation().getRegion() );
			String city = StringUtils.getNotNullString( sphere.getPhisicalLocation().getCity() );
			String street = StringUtils.getNotNullString( sphere.getPhisicalLocation().getStreet() );
			String streetcont = StringUtils.getNotNullString( sphere.getPhisicalLocation().getStreetcont() );
			String zipcode = StringUtils.getNotNullString( sphere.getPhisicalLocation().getZipcode() );
			String address = StringUtils.getNotNullString( sphere.getPhisicalLocation().getAddress() );
			String description = StringUtils.getNotNullString( sphere.getPhisicalLocation().getDescription() );
			body = email + " ; " + telephone + " ; " + fax + ". ";
			body += zipcode + " ; " + country + " ; " + state + " ; " + region + " ; " + city + " ; " 
						+ street + " ; " + streetcont + " ; " + address + ". " + description;
		} else {
			body = statement.getBody();
		}
		if ( body != null ) {
			addDoc.add(new Field("body", body, Field.Store.YES,
				Field.Index.TOKENIZED));
		}
		return StringUtils.getNotNullString( body );
	}

	private void processBookmark(Document addDoc, Statement statment) {
		String adress = statment.getAddress();
		if (adress != null) {
			addDoc.add(new Field("address", adress, Field.Store.YES,
					Field.Index.UN_TOKENIZED));
		}
	}
	
	private void processSphere( final Document addDoc, final SphereStatement st ){
		if ( st != null ) {
			final String role = st.getRole();
			if (StringUtils.isNotBlank( role )) {
				addDoc.add(new Field("role", role, Field.Store.YES,
						Field.Index.TOKENIZED));
			}
		}
	}

	private void processContact(Document addDoc, org.dom4j.Document doc) {
		String contact = XSLTransform.transformContact(doc);
		if (logger.isDebugEnabled()) {
			logger.debug("ADD CONTACT HERE: " + contact);
		}
		if (contact != null) {
			addDoc.add(new Field("contact", contact, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
		final ContactStatement st = ContactStatement.wrap( doc );
		if ((st != null) && (st.isContact())) {
			final String role = st.getRole();
			addDoc.add(new Field("role", role, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
	}

	private void processComment(Document addDoc, org.dom4j.Document doc) {
		CommentStatement statment = CommentStatement.wrap(doc);
		String comment = statment.getComment();
		if (comment != null) {
			logger.info("Its comment = " + comment);
			addDoc.add(new Field("comment", comment, Field.Store.YES,
					Field.Index.TOKENIZED));
		}
	}

	/**
	 * @param doc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getKeywords(org.dom4j.Document doc) {
		String keywords = "";
		Element root = doc.getRootElement();

		Element search = root.element("search");
		if (search != null) {
			Element interest = search.element("interest");
			if (interest != null) {
				List<Element> keywordsList = interest.elements("keywords");
				boolean addComma = false;
				for (Element e : keywordsList) {
					if (addComma) {
						keywords += ", ";
					} else {
						addComma = true;
					}
					keywords += e.attributeValue("value");
				}
			}
		}
		return keywords.trim();
	}

	@SuppressWarnings("unused")
	private String getFromBody(org.dom4j.Document doc, String sElement) {
		Element body = doc.getRootElement().element("body");
		if (body != null) {
			Element element = body.element(sElement);
			if (element != null) {
				return element.getText();
			}
		}
		return null;
	}

	public IndexReader getReader() throws IOException {
		if (IndexReader.indexExists(this.dir)) {
			return IndexReader.open(this.dir);
		} else {
			// TODO: Maybe create?
			logger.error("No index for sphere: " + this.sphereId);
			return null;
		}
	}

	/**
	 * @param doc
	 * @param elementName
	 * @return
	 */
	// TODO replace by domainmodel
	private String getSimpleAttributeValue(org.dom4j.Document doc,
			String elementName) {
		Element element = doc.getRootElement().element(elementName);
		if (element != null) {
			Attribute value = element.attribute("value");
			if (value != null) {
				return value.getValue();
			}
		}
		return null;
	}

	public String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @return true if positive number of documents deleted, in not then false
	 */
	public synchronized boolean removeDoc(org.dom4j.Document doc) {
		String messageId = getSimpleAttributeValue(doc, "message_id");
		int i = 0;
		if (messageId != null) {
			logger.info("Deleting from lucene index. messageId = "
					+ messageId);
			Term deletionTerm = new Term("message_id", messageId);
			IndexReader reader = null;
			try {
				reader = IndexReader.open(this.dir);
				i = reader.deleteDocuments(deletionTerm);
				logger.info("The " + i + " documents deleted");
				reader.close();
			} catch (IOException e) {
				logger.error("", e);
				ExceptionHandler.handleException(this, e);
				return false;
			}
		}
		if ( i > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized void updateDoc(org.dom4j.Document doc) {
		if (!Statement.wrap(doc).getType().equals("keywords")) {
			if (removeDoc(doc)) {
				addDoc(doc);
			} else {
				logger.warn("Not updated document, because did not existed");
			}
		}

	}

	public static SphereIndex get(String sphereId) throws IOException {
		SphereIndex sIndex = index.get(sphereId);
		if (sIndex == null) {
			sIndex = new SphereIndex(sphereId);
			index.put(sphereId, sIndex);
		}
		return sIndex;
	}

	/**
	 * @param doc
	 * @param documentFile
	 */
	public void addContentToDoc(org.dom4j.Document doc, File documentFile) {
		if (FileUtils.isBinary(documentFile)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Content is not adding for bynary file");
			}
			return;
		}
		removeDoc(doc);
		Document addDoc = createLuceneDoc(doc);
		try {
			String content = getContent(new BufferedReader(new FileReader(
					documentFile)));
			if (!documentFile.getAbsolutePath().endsWith(".txt")) {
				content = getContent(content);
			}
			logger.debug(content);
			addDoc.add(new Field("content", content, Field.Store.YES,
					Field.Index.TOKENIZED));
			addDoc(addDoc);
		} catch (FileNotFoundException ex) {
			logger.error("", ex);
		}
	}

	/**
	 * @param content
	 * @return
	 */
	private String getContent(String content) {
		return content.replaceAll("<[^>]*>", "");
	}

	/**
	 * @param reader
	 * @return
	 */
	private String getContent(Reader reader) {
		StringBuffer content = new StringBuffer();
		char[] buff = new char[10240];
		try {
			for (int i = 0; i != -1;) {
				i = reader.read(buff, 0, 10240);
				if (i != -1) {
					content.append(buff, 0, i);
				}
			}
		} catch (IOException ex) {
			logger.error("", ex);
		}
		return content.toString();
	}

	/**
	 * @param messageId
	 * @param documentFile
	 * @param xmldb
	 *            TODO
	 */
	public static void addToSphereIndex(String messageId, File documentFile,
			XMLDB xmldb) {
		org.dom4j.Document doc = xmldb.getSpecificMessage(messageId);
		String sphereId = doc.getRootElement().element("current_sphere")
				.attributeValue("value");
		try {
			SphereIndex index = SphereIndex.get(sphereId);
			index.addContentToDoc(doc, documentFile);
		} catch (IOException ex) {
			SSLogger.getLogger(SphereIndex.class).error("", ex);
		}

	}

	/**
	 * @param doc
	 */
	private void seacrhContent(org.dom4j.Document doc) {
		Element eMessageId = doc.getRootElement().element("message_id");
		Element eThreadId = doc.getRootElement().element("thread_id");
		if (eMessageId != null && eThreadId != null) {
			String messageId = eMessageId.attributeValue("value");
			String threadId = eThreadId.attributeValue("value");
			File file = new File(BDIR + FSEP + "urls" + FSEP + threadId + FSEP
					+ messageId);
			if (file.exists()) {
				addContentToDoc(doc, file);
			}
		}

		final XMLDB xmldb = new XMLDB();

		String supraSphere = "";
		try {
			supraSphere = xmldb.getSupraSphere().getName();
		} catch (NullPointerException ex1) {
			logger.error("Can't get suprasphere document", ex1);
		}
		Element dataId = doc.getRootElement().element("data_id");
		if (dataId != null) {
			final String name = BDIR + FSEP + "roots" + FSEP + supraSphere
					+ FSEP + "File" + FSEP + dataId.attributeValue("value");
			File file = new File(name);
			if (file.exists()) {
				addContentToDoc(doc, file);
			}
		}
	}
}
