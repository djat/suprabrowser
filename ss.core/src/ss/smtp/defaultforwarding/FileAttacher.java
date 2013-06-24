/**
 * 
 */
package ss.smtp.defaultforwarding;

import java.io.IOException;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.ui.email.AttachedFile;
import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.IAttachedFile;
import ss.common.StringUtils;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.processing.FileProcessor;

/**
 * @author zobo
 *
 */
public class FileAttacher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileAttacher.class);
	
	/**
	 * @param statement
	 * @param peer 
	 * @return
	 */
	public static AttachedFileCollection create(final Statement statement, final DialogsMainPeer peer) {
		if (!statement.isEmail() && !statement.isMessage()){
			return null;
		}
		return createAttachedFiles( statement.getMessageId(), statement.getThreadId(), statement.getCurrentSphere(), peer );
	}

	/**
	 * @param messageId
	 * @param currentSphere
	 * @param xmldb 
	 * @return
	 */
	private static AttachedFileCollection createAttachedFiles( final String messageId, final String threadId,
			final String currentSphere, final DialogsMainPeer peer ) {
		if (StringUtils.isBlank(messageId)){
			logger.error("messageId is blank");
			return null;
		}
		if (StringUtils.isBlank(currentSphere)){
			logger.error("currentSphere is blank");
			return null;
		}
		final String supraSphere = peer.getVerifyAuth().getSupraSphereName();
		if ( StringUtils.isBlank( supraSphere ) ) {
			logger.error("supraSphere is blank");
			return null;
		}
		final Vector<Document> files = peer.getXmldb().getFilesFromSphereInThread(currentSphere, threadId);
		if ((files == null) || (files.isEmpty())) {
			if (logger.isDebugEnabled()) {
				logger.debug("files is empty");
			}
			return null;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("files count: " + files.size());
			}
		}
		final AttachedFileCollection collection = new AttachedFileCollection();
		for ( Document doc : files ) {
			FileStatement file = FileStatement.wrap(doc);
			if (file.isFile()) {
				if (logger.isDebugEnabled()) {
					logger.debug("This is file, subject: " + file.getSubject());
				}
				String responceId = file.getResponseId();
				if ((responceId != null) && (responceId.equals(messageId))){
					try {
						if (logger.isDebugEnabled()) {
							logger.debug("This file is responce to root element");
						}
						IAttachedFile attachedFile = createAttachedFile( file, supraSphere );
						if ( attachedFile != null ) {
							collection.add( attachedFile );
						}
					} catch (Exception ex) {
						logger.error("Error in attaching file: " + file.getSubject() + ", with dataID: " + file.getDataId(), ex);
					}
				}
			}
		}
		return collection;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	private static IAttachedFile createAttachedFile( final FileStatement file, final String supraSphere ) throws IOException {
		final String fileName = file.getDataId();
		if (StringUtils.isBlank( fileName )) {
			return null;
		}
		return new AttachedFile( FileProcessor.getFullPathName(fileName, supraSphere) );
	}
}
