/**
 * 
 */
package ss.domainmodel.systemstatemessage;

import java.util.ResourceBundle;

import ss.client.localization.LocalizationLinks;
import ss.domainmodel.Statement;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;


/**
 * @author roman
 *
 */
public class SystemStateMessage extends Statement {
	
	private static final String FROM = "SYSTEMSTATEMESSAGE.FROM";
	private static final String GIVER_EMAIL_ADDRESS = "SYSTEMSTATEMESSAGE.GIVER_EMAIL_ADDRESS";
	private static final String DESCRIPTION = "SYSTEMSTATEMESSAGE.DESCRIPTION";
	private static final String LOGFILE_CONTENT = "SYSTEMSTATEMESSAGE.LOGFILE_CONTENT";
	private static final String SYSTEM_PROPERTIES = "SYSTEMSTATEMESSAGE.SYSTEM_PROPERTIES";
	private static final String CHECKSUMS = "SYSTEMSTATEMESSAGE.CHECKSUMS";
	
	private ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.DOMAINMODEL_SYSTEMSTATEMESSAGE);
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SystemStateMessage.class);
	
	private final ISimpleEntityProperty logContent = super
	.createTextProperty("log/content" );
	
	private final ISimpleEntityProperty description = super
	.createTextProperty("description" );
	
	private final ISimpleEntityProperty giverEmail = super
	.createAttributeProperty("email_address/@value" );
	
	private final SystemPropertiesCollection properties = super
	.bindListProperty( new SystemPropertiesCollection(), "properties" );
	
	private final ChecksumCollection checksums = super
	.bindListProperty( new ChecksumCollection(), "checksums" );
	
	public SystemStateMessage() {
		super( "email");
	}
	
	/**
	 * Create message object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SystemStateMessage wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, SystemStateMessage.class);
	}

	/**
	 * Create message object that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static SystemStateMessage wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, SystemStateMessage.class);
	}

	
	public ChecksumCollection getChecksums() {
		return this.checksums;
	}
	
	public SystemPropertiesCollection getSystemProperties() {
		return this.properties;
	}
	
	public String getLogContent() {
		return this.logContent.getValue();
	}
	
	public void setLogContent(String content) {
		this.logContent.setValue(content);
	}
	
	public String getDescription() {
		return this.description.getValue();
	}
	
	public void setDescription(String content) {
		this.description.setValue(content);
	}
	
	public String getGiverEmail() {
		return this.giverEmail.getValue();
	}
	
	public void setGiverEmail(String content) {
		this.giverEmail.setValue(content);
	}

	/**
	 * @return
	 */
	public String getHTMLView() {
		StringBuffer htmlBuffer = new StringBuffer();
		htmlBuffer.append("<html><head></head><body>");

		htmlBuffer.append("<h3>"+getSubject()+" "+this.bundle.getString(FROM)+getGiver()+"</h3>");
		htmlBuffer.append("<b>"+this.bundle.getString(GIVER_EMAIL_ADDRESS)+"</b> "+(getGiverEmail()==null ? " - " : getGiverEmail())+"<br>");
		htmlBuffer.append("<b>"+this.bundle.getString(DESCRIPTION)+"</b> "+(getDescription()==null ? " - " : getDescription())+"<br><br>");
		
		htmlBuffer.append("<b>"+this.bundle.getString(LOGFILE_CONTENT)+"</b>");
		htmlBuffer.append(""+getPreparedLogContent()+"");
		htmlBuffer.append("<br><br><b>"+this.bundle.getString(SYSTEM_PROPERTIES)+"</b><br>");
		
		for(SystemPropertyObject property : getSystemProperties()) {
			htmlBuffer.append(property.getName()+" = "+property.getValue()+"<br>");
		}
		
		htmlBuffer.append("<br><b>"+this.bundle.getString(CHECKSUMS)+"</b><br>");
		for(ChecksumItem checksum : getChecksums()) {
			htmlBuffer.append(checksum.getFilename()+" = "+checksum.getValue()+"<br>");
		}
		
		htmlBuffer.append("</body></html>");
		return htmlBuffer.toString();
	}

	/**
	 * @return
	 */
	private String getPreparedLogContent() {
		String content = getLogContent();
		
		String error = "ERROR";
		String warn = "WARN";
		String debug = "DEBUG";
		String info = "INFO";
		
		content = findAndBreakNextLogContent(content);
		content = findAndBoldString(content, error);
		content = findAndBoldString(content, warn);
		content = findAndBoldString(content, debug);
		content = findAndBoldString(content, info);
		
		return content;
	}

	/**
	 * @param content
	 * @return
	 */
	private String findAndBreakNextLogContent(String content) {
		String tempContent = content;
		int fromIndex = 0;
		while (tempContent.indexOf("Content of ", fromIndex) >= 0) {
			int index = tempContent.indexOf("Content of ", fromIndex);
			String appendString = index > 0 ? "<br><br>" : "<br>";
			tempContent = tempContent.substring(0,index)+appendString+tempContent.substring(index);
			fromIndex = tempContent.length() > index + appendString.length()
					+ 1 ? index + appendString.length() + 1 : tempContent
					.length();
		}
		return tempContent;
	}

	private String findAndBoldString(String content, String error) {
		String tempContent = content;
		String appendString = "<br><b style=\"font-size:13; color:rgb(130, 130, 200);\"><u>";
		String appendStringCloser = "</u></b>";
		int fromIndex = 0;
		while (tempContent.indexOf(error, fromIndex) >= 0) {
			int index = tempContent.indexOf(error, fromIndex);
			tempContent = tempContent.substring(0, index) + appendString
					+ tempContent.substring(index, index + error.length())
					+ appendStringCloser
					+ tempContent.substring(index + error.length());

			fromIndex = tempContent.length() > index + appendString.length()
					+ 1 ? index + appendString.length() + 1 : tempContent
					.length();
		}
		return tempContent;
	}
}
