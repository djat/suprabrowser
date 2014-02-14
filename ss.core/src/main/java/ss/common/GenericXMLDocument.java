package ss.common;

/*
 * Created on Jan 24, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.domainmodel.ExternalEmailStatement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;
import ss.smtp.Mail;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author david
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class GenericXMLDocument {

    @SuppressWarnings("unused")
    private static final Logger logger = SSLogger
            .getLogger(GenericXMLDocument.class);

    Hashtable session = new Hashtable();

    private static Random tableIdGenerator = new Random();

    public GenericXMLDocument(Hashtable session) {
        this.session = session;

    }

    public GenericXMLDocument() {

    }

    public Document XMLDoc(Mail mail, String body, String giverText)
            throws MessagingException {

        Message message = mail.getMessage();
        Document createDoc = null;
        try {

            String subject = message.getSubject();
            if (subject == null) {
                subject = "";
            }

            // String giverText = mail.getMessage().getSender().toString();
            logger.info("GIVE TEXT: " + giverText);
            createDoc = XMLDoc(subject, body, giverText);

            String milliId = VariousUtils.getNextRandomLong();

            createDoc.getRootElement().addElement("type").addAttribute("value",
                    "message");
            createDoc.getRootElement().addElement("thread_type").addAttribute(
                    "value", "message");

            createDoc.getRootElement().addElement("original_id").addAttribute(
                    "value", milliId);

            createDoc.getRootElement().addElement("thread_id").addAttribute(
                    "value", milliId);

            createDoc.getRootElement().addElement("message_id").addAttribute(
                    "value", milliId);

            Date current = new Date();
            String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                    current)
                    + " "
                    + DateFormat.getDateInstance(DateFormat.MEDIUM).format(
                            current);

            createDoc.getRootElement().addElement("moment").addAttribute(
                    "value", moment);
            createDoc.getRootElement().addElement("last_updated").addAttribute(
                    "value", moment);

        } catch (Exception e) {

        	logger.error(e.getMessage(), e);

        }

        logger.info("RETURNING DOC FROM CREATE GENERIC:	 "
                + createDoc.asXML());
        return createDoc;

    }

    @SuppressWarnings("unchecked")
	public Document XMLDocOnMail(Mail mail, String body, String giverText)
            throws MessagingException {

        Message message = mail.getMessage();
        Document createDoc = null;
        try {

            String subject = message.getSubject();
            if (subject == null) {
                subject = "";
            }

            // String giverText = mail.getMessage().getSender().toString();
            logger.info("GIVE TEXT: " + giverText);
            createDoc = XMLDoc(subject, body, giverText);

            try {
                ExternalEmailStatement email = ExternalEmailStatement
                        .wrap(createDoc);
                List recipients = (List) mail.getRecipients();
                email.setReciever((String) recipients.get(0));
                List ccrecipients = new ArrayList(recipients);                
                ccrecipients.remove(0);
                List<String> ccrecivers = new ArrayList<String>();
                for (Iterator iter = ccrecipients.iterator(); iter.hasNext();) {
                    String element = (String) iter.next();
                    ccrecivers.add(element);
                }
                String cc = (new SpherePossibleEmailsSet(ccrecivers))
                        .getSingleStringEmails();
                email.setCcrecievers(cc);
                email.setBccrecievers("");
                createDoc = email.getBindedDocument();
            } catch (Exception e) {
                logger.error("Could not set reciever and CC recievers", e);
            }

            String milliId = VariousUtils.getNextRandomLong();

            createDoc.getRootElement().addElement("type").addAttribute("value",
                    SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
            createDoc.getRootElement().addElement("thread_type").addAttribute(
                    "value", SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);

            createDoc.getRootElement().addElement("original_id").addAttribute(
                    "value", milliId);

            createDoc.getRootElement().addElement("thread_id").addAttribute(
                    "value", milliId);

            createDoc.getRootElement().addElement("message_id").addAttribute(
                    "value", milliId);

            Date current = new Date();
            String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                    current)
                    + " "
                    + DateFormat.getDateInstance(DateFormat.MEDIUM).format(
                            current);

            createDoc.getRootElement().addElement("moment").addAttribute(
                    "value", moment);
            createDoc.getRootElement().addElement("last_updated").addAttribute(
                    "value", moment);

        } catch (Exception e) {

        	logger.error(e.getMessage(), e);

        }

        logger.info("RETURNING DOC FROM CREATE GENERIC:   "
                + createDoc.asXML());
        return createDoc;

    }

    public Document XMLDoc(String subjectText, String bodyText) {

        Document createDoc = DocumentHelper.createDocument();
        Element email = createDoc.addElement("email");

        email.addElement("giver").addAttribute("value",
                (String) this.session.get("real_name"));

        email.addElement("subject").addAttribute("value", subjectText);

        email.addElement("last_updated_by").addAttribute("value",
                (String) this.session.get("contact_name"));

        email.addElement("type").addAttribute("value", "bookmark");
        email.addElement("thread_type").addAttribute("value", "message");

        DefaultElement body = new DefaultElement("body");
        body.setText(bodyText);

        body.addElement("version").addAttribute("value", "3000");

        body.addElement("orig_body").setText(bodyText);

        Date current = new Date();
        String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                current)
                + " "
                + DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

        long longnum = getNextTableId();

        String message_id = Long.toString(longnum);
        // String response_id = null;

        email.addElement("original_id").addAttribute("value", message_id);
        email.addElement("message_id").addAttribute("value", message_id);

        email.addElement("moment").addAttribute("value", moment);
        email.addElement("last_updated").addAttribute("value", moment);
        String real_name = (String) this.session.get("real_name");

        email.addElement("voting_model").addElement("tally");

        email.addElement("confirmed").addAttribute("value", "true");
        email.element("voting_model").element("tally").addElement("member")
                .addAttribute("value", real_name).addAttribute("vote_moment",
                        moment);

        email.add(body);

        return createDoc;

    }

    public Document XMLDoc(String subjectText) {

        Document createDoc = DocumentHelper.createDocument();
        Element email = createDoc.addElement("email");

        email.addElement("giver").addAttribute("value",
                (String) this.session.get("real_name"));

        email.addElement("subject").addAttribute("value", subjectText);

        email.addElement("last_updated_by").addAttribute("value",
                (String) this.session.get("contact_name"));

        DefaultElement body = new DefaultElement("body");
        body.setText("");

        body.addElement("version").addAttribute("value", "3000");

        body.addElement("orig_body").setText("");

        Date current = new Date();
        String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                current)
                + " "
                + DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

        long longnum = getNextTableId();

        String message_id = Long.toString(longnum);
        // String response_id = null;

        email.addElement("original_id").addAttribute("value", message_id);
        email.addElement("message_id").addAttribute("value", message_id);
        email.addElement("thread_id").addAttribute("value", message_id);

        email.addElement("moment").addAttribute("value", moment);
        email.addElement("last_updated").addAttribute("value", moment);
        String real_name = (String) this.session.get("real_name");

        email.addElement("voting_model").addElement("tally");

        email.addElement("confirmed").addAttribute("value", "true");
        email.element("voting_model").element("tally").addElement("member")
                .addAttribute("value", real_name).addAttribute("vote_moment",
                        moment);

        email.add(body);

        return createDoc;

    }

    public Document XMLDoc(String subjectText, String bodyText,
            boolean serverCreate) {

        Document createDoc = DocumentHelper.createDocument();
        Element email = createDoc.addElement("email");

        email.addElement("giver").addAttribute("value",
                (String) this.session.get("real_name"));

        email.addElement("subject").addAttribute("value", subjectText);

        email.addElement("last_updated_by").addAttribute("value",
                (String) this.session.get("contact_name"));

        DefaultElement body = new DefaultElement("body");
        body.setText(bodyText);

        body.addElement("version").addAttribute("value", "3000");

        body.addElement("orig_body").setText(bodyText);

        Date current = new Date();
        String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                current)
                + " "
                + DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

        // String response_id = null;

        long longnum = getNextTableId();

        String message_id = Long.toString(longnum);

        email.addElement("original_id").addAttribute("value", message_id);
        email.addElement("message_id").addAttribute("value", message_id);
        email.addElement("thread_id").addAttribute("value", message_id);

        email.addElement("moment").addAttribute("value", moment);
        email.addElement("last_updated").addAttribute("value", moment);
        String real_name = (String) this.session.get("real_name");

        email.addElement("voting_model").addElement("tally");

        email.addElement("confirmed").addAttribute("value", "true");
        email.element("voting_model").element("tally").addElement("member")
                .addAttribute("value", real_name).addAttribute("vote_moment",
                        moment);

        email.add(body);

        return createDoc;

    }

    public Document XMLDoc(String subjectText, String bodyText,
            String thread_id, boolean serverCreate) {
        // long longnum = System.currentTimeMillis();

        String message_id = new Long(getNextTableId()).toString();// (Long.toString(longnum));

        Document createDoc = DocumentHelper.createDocument();
        Element email = createDoc.addElement("email");

        email.addElement("giver").addAttribute("value",
                (String) this.session.get("real_name"));

        email.addElement("subject").addAttribute("value", subjectText);

        email.addElement("last_updated_by").addAttribute("value",
                (String) this.session.get("contact_name"));

        DefaultElement body = new DefaultElement("body");
        body.setText(bodyText);

        body.addElement("version").addAttribute("value", "3000");

        body.addElement("orig_body").setText(bodyText);

        Date current = new Date();
        String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                current)
                + " "
                + DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

        // String response_id = null;

        email.addElement("original_id").addAttribute("value", message_id);
        email.addElement("message_id").addAttribute("value", message_id);
        email.addElement("thread_id").addAttribute("value", message_id);

        email.addElement("moment").addAttribute("value", moment);
        email.addElement("last_updated").addAttribute("value", moment);
        String real_name = (String) this.session.get("real_name");

        email.addElement("voting_model").addElement("tally");

        email.addElement("confirmed").addAttribute("value", "true");
        email.element("voting_model").element("tally").addElement("member")
                .addAttribute("value", real_name).addAttribute("vote_moment",
                        moment);

        email.add(body);

        return createDoc;

    }

    public static Document XMLDoc(String subjectText, String bodyText,
            String giverText) {

        String message_id = new Long(getNextTableId()).toString();// (Long.toString(longnum));
        if (subjectText.lastIndexOf("\\") != -1) {

            subjectText = subjectText.replace("\\", "/");

        }

        Document createDoc = DocumentHelper.createDocument();
        Element email = createDoc.addElement("email");

        email.addElement("giver").addAttribute("value", giverText);

        email.addElement("subject").addAttribute("value", subjectText);

        email.addElement("last_updated_by").addAttribute("value", giverText);

        // email.addElement("type").addAttribute("value","bookmark");
        // email.addElement("thread_type").addAttribute("value","message");
        email.addElement("original_id").addAttribute("value", message_id);
        email.addElement("message_id").addAttribute("value", message_id);
        email.addElement("thread_id").addAttribute("value", message_id);

        DefaultElement body = new DefaultElement("body");
        body.setText(bodyText);

        body.addElement("version").addAttribute("value", "3000");

        body.addElement("orig_body").setText(bodyText);

        email.add(body);

        return createDoc;

    }

    public static synchronized long getNextTableId() {

        return Math.abs(tableIdGenerator.nextLong());

    }

    public static Document createKeywordsDoc(String tagText, Hashtable sendSession, String unique) {

        String realName = (String) sendSession.get("real_name");
        String moment = DialogsMainPeer.getCurrentMoment();
		String messageId = VariousUtils.createMessageId();

        return createKeywordsDocMockUp( messageId, tagText, realName, moment, unique);
    }
    
    public static Document createKeywordsDocMockUp( final String messageId, final String tagText, final String realName
    		, final String moment, final String unique ){
        Document genericDoc = XMLDoc(tagText, "", realName);
        Element rootElement = genericDoc.getRootElement();

        addElementAndValue(rootElement, "type", "keywords");
        addElementAndValue(rootElement, "thread_type", "keywords");
        addElementAndValue(rootElement, "status", "confirmed");
        addElementAndValue(rootElement, "original_id", messageId);
        addElementAndValue(rootElement, "message_id", messageId);
        addElementAndValue(rootElement, "thread_id", messageId);
        addElementAndValue(rootElement, "moment", moment);
        addElementAndValue(rootElement, "last_updated", moment);
        addElementAndValue(rootElement, "unique_id", unique);

        Element stats = rootElement.addElement("stats");
        addElementAndValue(stats, "number_of_tags", "0");
        addElementAndValue(stats, "number_with_this_tag", "1");
        return genericDoc;    	
    }

    private static void addElementAndValue(Element rootElement, String sType,
            String sKey) {
        rootElement.addElement(sType).addAttribute("value", sKey);
    }

}
