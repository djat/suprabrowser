/**
 * 
 */
package ss.smtp.reciever.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.mail.BodyPart;
import javax.mail.MessagingException;

import ss.domainmodel.FileStatement;
import ss.smtp.reciever.RecieveList;
import ss.util.VariousUtils;

/**
 * @author zobo
 *
 */
public class FileProcessor {
	
	private static final String bdir = System.getProperty("user.dir");

	private static final String fsep = System.getProperty("file.separator");
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileProcessor.class);
	
	public static final FileProcessor INSTANCE = new FileProcessor();
	
	private FileProcessor() {
		
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public CreatedFileInfo createFileDocument( final RecieveList list, final BodyPart bodyPart, final String sender, final String suprasphere )
		throws MessagingException, FileNotFoundException {
		final CreatedFileInfo fileInfo = saveFile( bodyPart, suprasphere );
		final FileStatement fileStatement = FileDocumentCreator.createFileDocument(fileInfo.getOriginalFileName(), sender, fileInfo.getSystemFileName(), fileInfo.getFileSize());
		fileInfo.setFileDocument(fileStatement.getBindedDocument());
		return fileInfo;
	}
	
	private CreatedFileInfo saveFile(final BodyPart bodyPart, final String suprasphere) throws FileNotFoundException, MessagingException{
		String origfname = bodyPart.getFileName();
		logger.info("For this attachment, will write: " + origfname.toString());

		String sphereIdForPath = suprasphere;//list.getRecievers().get(0).getRecipientsSphere();
		
		String fname = VariousUtils.getNextRandomLong() + "_____" + origfname;
		String systemFileName = bdir + fsep + "roots" + fsep
						+ sphereIdForPath + fsep
						+ "File" + fsep + fname;
		File out = new File(systemFileName);

		FileOutputStream fout = new FileOutputStream(out);
		int bytes = -1;
		try {
			bodyPart.getDataHandler().writeTo(fout);
			fout.close();
			FileInputStream fin = new FileInputStream(systemFileName);
			bytes = fin.available();
			fin.close();
		} catch (Exception e) {
			logger.error("Something wrong with file", e);
		}
		return new CreatedFileInfo(systemFileName, origfname, bytes);
	}

	private static final String[] RESTRICTED_FILE_NAMES = { "winmail.dat" }; 
	
	public boolean isAllowedFileName( final String fileName ) {
		if ( fileName == null ) {
			return false;
		}
		for ( String s : RESTRICTED_FILE_NAMES ) {
			if ( s.equalsIgnoreCase( fileName ) ) {
				return false;
			}
		}
		return true;
	}
}
