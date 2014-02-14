/**
 * 
 */
package ss.util;

import org.dom4j.Document;

import ss.common.StringUtils;
import ss.domainmodel.Statement;

/**
 * @author zobo
 *
 */
public class EmailUtils {
	
	private static final String EMAIL_BOX_ATTACHMENT = " Email Box";

	public static String getEmailSphereOnLogin(String login) {
		return (login + EMAIL_BOX_ATTACHMENT);
	}
	
	public static String getLoginIfEmailBox(String dysplayName) {
		return dysplayName.replaceAll(EMAIL_BOX_ATTACHMENT, "");
	}
	
	public static Document supplyWithIDs(Document doc,final String messageId, final String threadId, final String responceId, String currentSphereId){
		Statement st = Statement.wrap(doc);
		
		st.setMessageId(messageId);
		st.setOriginalId(messageId);
		
		if (StringUtils.isNotBlank(threadId)){
			st.setThreadId(threadId);
		}
		if (StringUtils.isNotBlank(responceId)){
			st.setResponseId(responceId);
		}
		
		st.setCurrentSphere(currentSphereId);
		
		return st.getBindedDocument();
	}
}
