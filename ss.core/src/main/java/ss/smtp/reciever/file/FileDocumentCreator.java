/**
 * 
 */
package ss.smtp.reciever.file;

import java.text.DateFormat;
import java.util.Date;

import ss.domainmodel.FileStatement;
import ss.util.SupraXMLConstants;

/**
 * @author zobo
 *
 */
public class FileDocumentCreator {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileDocumentCreator.class);
	
	/*
	 * With out current_sphere, message_id, original_id, thread_id, responce_id
	 */
	public static FileStatement createFileDocument( final String originalFileName, final String sender, final String systemFileName, final int bytes ) {
		FileStatement file = new FileStatement();

		file.setGiver( (sender == null) ? " NULL " : sender);
		file.setSubject(originalFileName);
		file.setType("file");
		file.setThreadType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
		file.setOrigBody("");
		
        Date current = new Date();
        String moment = DateFormat.getTimeInstance(DateFormat.LONG).format(
                current)
                + " "
                + DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);
		file.setMoment(moment);
		file.setLastUpdated(moment);
		
		file.setBytes(new Integer(bytes).toString());
		int index = systemFileName.lastIndexOf(System.getProperty("file.separator"));
		String dataId = systemFileName.substring(index+1, systemFileName.length());
		file.setDataId(dataId);
		file.setOriginalDataId(dataId);
		
		file.setConfirmed(true);
		
		logger.info("File Document created: " + file.getSubject());
		return file;
	}
}
