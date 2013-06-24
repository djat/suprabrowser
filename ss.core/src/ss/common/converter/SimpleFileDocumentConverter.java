/**
 * 
 */
package ss.common.converter;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.domainmodel.FileStatement;
import ss.server.networking.processing.FileProcessor;

/**
 * @author roman
 *
 */
public class SimpleFileDocumentConverter {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SimpleFileDocumentConverter.class);
	
	@SuppressWarnings("unused")
	private static final String bdir = System.getProperty("user.dir");

	@SuppressWarnings("unused")
	private static final String fsep = System.getProperty("file.separator");
	
	/**
	 * File Document have to be inserted in database before this method
	 */
	public static void convert(Hashtable session, String supraSpherename, String giver, String sphereId, Document fileDoc) {
		FileStatement fileDocStatement = FileStatement.wrap(fileDoc);
		final String name = FileProcessor.getFullPathName(fileDocStatement.getDataId(), supraSpherename);
		final String threadId = fileDocStatement.getThreadId();
		final String messageId = fileDocStatement.getMessageId();
		if (logger.isDebugEnabled()) {
			logger.debug("name: " + name + ", threadId: " + threadId +", messageId: " + messageId + ", giver: " + giver + ", sphereId: " + sphereId);
		}
		fileDocStatement.setGiver(giver);
		fileDocStatement.setCurrentSphere(sphereId);
		DocumentConverterAndIndexer.INSTANCE.convert(ConvertingElementFactory.
				createConvertAndPublish(session, threadId, messageId, name, sphereId, fileDocStatement.getBindedDocument(), giver, true));
	}

}
