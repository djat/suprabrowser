package ss.client.ui;

import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.util.DateTimeParser;
import ss.util.SessionConstants;

public class PreviewHtmlTextCreator {

	private String text;

	private boolean enabled;

	private static final String endTags = "</body></html>";

	MessagesPane ownPane = null;

	private static final String outcoming_color = "blue";
	
	private static final String outcoming_color_light = "rgb(80, 80, 255)";

	private static final String incoming_color = "green";
	
	private static final String incoming_color_light = "rgb(85, 140, 90)";

	private static final String reply_color_light = "rgb(100,100,100)";
	
	private static final Logger logger = SSLogger
			.getLogger(PreviewHtmlTextCreator.class);

	private static final String HEAD = "<html><head><style type=\"text/css\">"
			+ ".send{font-family:times; font-size:14; font-weight:bold; color:"
			+ outcoming_color
			+ "}"
			+ ".send_incom{font-family:times; font-size:14; font-weight:bold; color:"
			+ incoming_color
			+ "}"
			+ ".time{font-family:times;font-size:10; color:"
			+ outcoming_color
			+ ";}"
			+ ".time_incom{font-family:times;font-size:10; color:"
			+ incoming_color
			+ ";}"
			+ ".time_reply{font-family:times; font-size:10; color:"
			+ outcoming_color_light
			+ ";}"
			+ ".time_reply_incom{font-family:times; font-size:10; color:"
			+ incoming_color_light
			+ ";}"
			+ ".subj{font-family:arial;font-size:14; font-color:black;}"
			+ ".resp{font-family:times; font-size:10; font-weight:bold; color:"
			+ outcoming_color_light
			+ "}"
			+ ".reply_sender_incom{font-family:times; font-size:14; color:"
			+ incoming_color_light
			+ "}"
			+ ".reply_sender_outcom{font-family:times; font-size:14; color:"
			+ outcoming_color_light
			+ "}"
			+ ".resp_incom{font-family:times; font-size:10; font-weight:bold; color:"
			+ incoming_color_light
			+ "}"
			+ ".resp_subj{font-family:times; font-size:14; color:"
			+ reply_color_light
			+ "}"
			+ ".subj_h{font-family:arial;font-size:14; font-color:black;;background-color:gainsboro}"
			+ "</style></head><body>";

	public PreviewHtmlTextCreator(MessagesPane ownPane) {
		this.ownPane = ownPane;
		this.text = "";
		this.enabled = false;
	}
	
	public PreviewHtmlTextCreator(){
		this.text = "";
	}

	public String getText() {
		return HEAD + this.text + endTags;
	}

	public void addDocText(Document doc) {
		Statement statement = Statement.wrap(doc);

		String message_id = statement.getMessageId();
		String response_id = statement.getResponseId();
		String sender = statement.getGiver() + ":";
		String time = null;
		String subject = null;

		String moment = statement.getMoment();
		if (moment != null) {
			StringTokenizer sto = new StringTokenizer(moment, " ");
			time = " (" + sto.nextToken() + ") ";
		} else {
			time = "(--:--:--)";
		}

		subject = statement.getSubject();
		if (subject == null) {
			subject = "NULL";
		}

		subject = subject.trim();
		subject = subject.replaceAll("&", "&amp;");
		subject = subject.replaceAll("<", "&lt;");
		subject = subject.replaceAll(">", "&gt;");
		subject = subject.replaceAll("\"", "&quot;");
		boolean hystory = false;
		try {
			hystory = isHystoryMessage(statement);
		} catch (Throwable ex) {
			logger.error("Cannot figure out if message expired", ex);
		}

		addText(sender, time, subject, response_id, message_id, hystory);

	}

	/**
	 * @param statement
	 * @return
	 */
	private boolean isHystoryMessage(Statement statement) {
		String moment = statement.getMoment();
		Date messageDate = DateTimeParser.INSTANCE.parseToDate(moment);
		Date expiration = calculateExp(this.ownPane.getSphereDefinition());
		return messageDate.before(expiration);
	}

	public Date calculateExp(Document sphereDef) {
		String expiration = null;
		try {
			expiration = sphereDef.getRootElement().element("expiration").attributeValue("value");
		} catch(Exception ex) {
			logger.warn("no expiration in sphere "+sphereDef);
			expiration = null;
		}
		
		long currentTime = System.currentTimeMillis();
		long expirationTime = 0;
		if (expiration != null) {
			if ((!expiration.equals("all") && !expiration.equals("none"))) {
				StringTokenizer st = new StringTokenizer(expiration, " ");
				String begin = st.nextToken();
				Integer conv = new Integer(begin);
				if (expiration.lastIndexOf("hour") != -1) {
					expirationTime = (conv.intValue() * 1000 * 60 * 60);
				} else if (expiration.lastIndexOf("week") != -1) {
					expirationTime = (conv.longValue() * 1000 * 60 * 60 * 24 * 7);
				} else if (expiration.lastIndexOf("day") != -1) {
					expirationTime = (conv.intValue() * 1000 * 60 * 60 * 24);
				}
			} else {
				expirationTime = Long.MAX_VALUE;
			}
		} else {
			expirationTime = 4 * 1000 * 60 * 60 * 24;
		}
		return new Date(currentTime - expirationTime);
	}

	public void addText(String sender, String time, String subject,
			Object response_id, String messageID, boolean history) {
		String owner = (String) SupraSphereFrame.INSTANCE.client.session
				.get(SessionConstants.REAL_NAME)
				+ ":";
		String resp_style = (owner.equals(sender) ? "resp" : "resp_incom");
		
		String time_style = null;
		String subj_style = null;
		String giver_style = null;
		
		if(response_id==null) {
			subj_style = "subj" + (history ? "_h" : "");
			giver_style = (owner.equals(sender) ? "send" : "send_incom");
			time_style = (owner.equals(sender) ? "time" : "time_incom");
		} else {
			subj_style = "resp_subj";
			giver_style = (owner.equals(sender) ? "reply_sender_outcom" : "reply_sender_incom");
			time_style = (owner.equals(sender) ? "time_reply" : "time_reply_incom");
			
			//sender = sender;
		}
		
		String appendString = "<div id=\""
				+ messageID
				+ "\" style=\"background-color:white\" "
				+ "ondblclick=\"scroller_object.on_dbl_click('"
				+ messageID
				+ "')\" "
				+ "onClick=\"scroller_object.on_mouse_click('"
				+ messageID
				+ "')\">"
				+ "<font class=\""
				+ giver_style
				+ "\">"
				+ sender
				+ "</font>"
				+ "<font class=\""
				+ time_style
				+ "\">"
				+ time
				+ "</font>"
				+ (response_id != null ? "<font class=\"" + resp_style
						+ "\"><u> r</u></font>" : "") + "<font class=\""
				+ subj_style + "\"> " + subject + "</font></div>";

		this.text = this.text + appendString;
	}

	public void addText(String sender, String time, String subject) {
		addText(sender, time, subject, null, null, false);
	}

	public void addText(String text) {

		try {
			text = text.replaceAll("\n", "<br>");
			String appendString = "<div class=\"subj\">" + text + "</div>";
			this.text = this.text + appendString;

		} catch (NullPointerException ex) {
			logger.error(ex);
		}
	}

	public void addBlueText(String text) {
		addText(text, "", "", null, null, false);
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean b) {
		this.enabled = b;

	}

	public static String prepearText(String text) {
		if (text == null || text.length() == 0) {
			return "";
		}
		text = text.trim();
		final char[] buff = new char[text.length()];
		text.getChars(0, buff.length, buff, 0);
		final StringBuilder sb = new StringBuilder(buff.length);
		for (int n = 0; n < buff.length; n++) {
			char ch = buff[n];
			switch (ch) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '\"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&#39;");
				break;
			case '\\':
				sb.append("&#92;");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}

	public static String prepareTextWithoutAmps(String text) {
		if (text == null || text.length() == 0) {
			return "";
		}
		text = text.trim();
		final char[] buff = new char[text.length()];
		text.getChars(0, buff.length, buff, 0);
		final StringBuilder sb = new StringBuilder(buff.length);
		for (int n = 0; n < buff.length; n++) {
			char ch = buff[n];
			switch (ch) {
			// case '\"':
			// sb.append( "&quot;" );
			// break;
			case '\'':
				sb.append("&#39;");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}
	
	public static String excludeTag(String originString, String tagName) {
		String tempString = originString;
		tempString = tempString.replaceAll(getClosingTag(tagName), "");
		while (tempString.indexOf(getOpeningTag(tagName)) >= 0) {
			int index = tempString.indexOf(getOpeningTag(tagName));
			int indexClosing = index + tempString.substring(index).indexOf(">");
			String beforeString = tempString.substring(0, index);
			String afterString = tempString.substring(indexClosing + 1);
			tempString = beforeString + afterString;
		}
		return tempString;
	}

	private static String getClosingTag(String tagName) {
		return "</"+tagName+">";
	}

	private static String getOpeningTag(String tagName) {
		return "<"+tagName;
	}

	public static String excludeReferences(String originString) {
		return excludeTag(originString, "a");
	}

	/**
	 * @param origBody
	 * @return
	 */
	public static String excludePreformatted(String origBody) {
		return excludeTag(origBody, "pre");
	}
	
	/**
	 * @param origBody
	 * @return
	 */
	public static String excludeParagraphs(String origBody) {
		return excludeTag(origBody, "p");
	}

	public static boolean isHtml(final String text){
		if (contains(text, "p") || contains(text, "br") || contains(text, "a")){
			return true;
		}
		return false;
	}
	
	private static boolean contains(final String text, final String tag){
		if (text.indexOf(getClosingTag(tag)) >= 0){
			return true;
		}
		if (text.indexOf(getOpeningTag(tag)) >= 0){
			return true;
		}
		return false;
	}
}
