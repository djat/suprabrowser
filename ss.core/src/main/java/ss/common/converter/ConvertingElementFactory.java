/**
 * 
 */
package ss.common.converter;

import java.util.Hashtable;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
public class ConvertingElementFactory {

	/**
	 * @param session
	 * @param threadId
	 * @param messageId
	 * @param name
	 * @return
	 */
	public static ConvertingElement createConvert(Hashtable session, String threadId, String messageId, String name) {
		return new ConvertingElement(session, threadId, messageId, name, null, null, null, false, true);
	}
	
	public static ConvertingElement createConvertAndPublish(Hashtable session, String threadId, String messageId, String name, 
			String sphereId, Document fileDoc, String contact, boolean isToIndex) {
		return new ConvertingElement(session, threadId, messageId, name, sphereId, fileDoc, contact, true, isToIndex);
	}
}
