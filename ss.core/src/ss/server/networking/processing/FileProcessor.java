/**
 * 
 */
package ss.server.networking.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.activation.DataHandler;
import javax.mail.MessagingException;

import org.dom4j.Document;

import ss.client.ui.email.AttachedFileProxy;
import ss.client.ui.email.IAttachedFile;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.domainmodel.FileStatement;
import ss.domainmodel.VotedMember;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class FileProcessor {

	private class SavedFileInfo {
		String fname;

		int bytes;

		public SavedFileInfo(String fname, int bytes) {
			super();
			this.fname = fname;
			this.bytes = bytes;
		}

		public int getBytes() {
			return this.bytes;
		}

		public String getFname() {
			return this.fname;
		}
	}
	
	private static final String NULL_GIVER = " NULL ";

	private static final String bdir = System.getProperty("user.dir");

	private static final String fsep = System.getProperty("file.separator");

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(FileProcessor.class);

	public final static FileProcessor INSTANCE = new FileProcessor();

	private FileProcessor() {
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public static final String getFullPathName( final String fileName, final String supraSphere ){
		if ( fileName == null ) {
			throw new NullPointerException("fileName can not be null");
		}		
		if ( supraSphere == null ) {
			throw new NullPointerException("supraSphere can not be null");
		}
		return bdir + fsep + "roots" + fsep + supraSphere
			+ fsep + "File" + fsep + fileName;
	}

	private SavedFileInfo saveFile(final IAttachedFile file,
			final String supraSphere) {
		final String originalFname = file.getName();
		logger.info("For this attachment, will write: "
				+ originalFname.toString());

		if(file instanceof AttachedFileProxy) {
			AttachedFileProxy proxy = (AttachedFileProxy)file;
			return new SavedFileInfo(proxy.getOriginalDataId(), proxy.getSize());
		}
		
		final String fname = VariousUtils.getNextRandomLong() + "_____"
				+ originalFname;
		final File out = new File(getFullPathName(fname, supraSphere));
		// File out = new File(fname);

		// TODO: add file path creation

		// mp.getBodyPart(i).writeTo(fout);
		try {
			int bytes = -1;
			final FileOutputStream fout = new FileOutputStream(out);
			(new DataHandler(file.createDataSource())).writeTo(fout);
			fout.close();
			final FileInputStream fin = new FileInputStream(getFullPathName(fname, supraSphere));
			bytes = fin.available();
			if (logger.isDebugEnabled()) {
				logger.debug("File saved: " + fname);
			}
			return new SavedFileInfo(fname, bytes);
		} catch (Exception ex) {
			logger.error("Saving file exception for file: " + fname, ex);
			return null;
		}
	}
	
	public Document processFile(final String responseId,
			final String threadType, final IAttachedFile file,
			final Hashtable session, final String giver)
			throws MessagingException, FileNotFoundException {
		return processFile(responseId, threadType, file, session, giver, null, null, null);
	}

	public Document processFile(final String responseId,
			final String threadType, final IAttachedFile file,
			final Hashtable session, final String giver, final String desiredMessageId, final String subject, final String body)
			throws MessagingException, FileNotFoundException {

		final SavedFileInfo savedFileInfo = saveFile(file, (String) session
				.get("supra_sphere"));
		if (savedFileInfo == null) {
			return null;
		}

		final FileStatement fileStatement = new FileStatement();

		final String from = (giver == null) ? NULL_GIVER : giver;
		fileStatement.setGiver(from);
		if(StringUtils.isNotBlank(subject)) {
			fileStatement.setSubject(subject);
		} else {
			fileStatement.setSubject(file.getName());
		}
		fileStatement.setBody(StringUtils.getTrimmedString(body));
		fileStatement.setVersion("3000");
		fileStatement.setOrigBody(fileStatement.getBody());

		final Date current = new Date();
		final String moment = DateFormat.getTimeInstance(DateFormat.LONG)
				.format(current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

		final String messageId = StringUtils.isNotBlank(desiredMessageId) ? desiredMessageId : VariousUtils.createMessageId();
		
		fileStatement.setMessageId(messageId);
		fileStatement.setOriginalId(messageId);
		
		if(StringUtils.isNotBlank(responseId)) {
			fileStatement.setResponseId(responseId);
			fileStatement.setThreadId(responseId);
		} else {
			fileStatement.setThreadId(messageId);
		}
		
		fileStatement.setMoment(moment);
		fileStatement.setLastUpdated(moment);
		
		VotedMember vMember = new VotedMember();
		vMember.setName(from);
		vMember.setVotedMoment(moment);
		fileStatement.getVotedMembers().add(vMember);
		
		fileStatement.setType("file");
		fileStatement.setConfirmed(true);
		fileStatement.setThreadType(threadType);
		fileStatement.setCurrentSphere((String) session.get(SessionConstants.SPHERE_ID));
		fileStatement.setBytes(new Integer(file.getSize()).toString());
		fileStatement.setOriginalDataId(savedFileInfo.getFname());
		fileStatement.setDataId(savedFileInfo.getFname());
		
//      email.addElement("giver").addAttribute("value", from);
//		email.addElement("subject").addAttribute("value", file.getName());
// 		email.addElement("last_updated_by").addAttribute("value",
// 		(String) this.session.get("contact_name"));
//		final DefaultElement body = new DefaultElement("body");
//		body.setText("");
//		body.addElement("version").addAttribute("value", "3000");
//		body.addElement("orig_body").setText("");
//		email.add(body);		
//		email.addElement("message_id").addAttribute("value", messageId);
//		email.addElement("original_id").addAttribute("value", messageId);
//		email.addElement("response_id").addAttribute("value", responseId);
//		email.addElement("thread_id").addAttribute("value", responseId);
//		email.addElement("moment").addAttribute("value", moment);
//		email.addElement("last_updated").addAttribute("value", moment);
//		email.addElement("voting_model").addElement("tally");
//		email.addElement("confirmed").addAttribute("value", "true");
//		email.element("voting_model").element("tally").addElement("member")
//				.addAttribute("value", from)
//				.addAttribute("vote_moment", moment);
//
//		email.addElement("type").addAttribute("value", "file");		
//		email.addElement("thread_type").addAttribute("value", threadType);
//		email.addElement("current_sphere").addAttribute("value",
//				(String) session.get(SessionConstants.SPHERE_ID));	
//		email.addElement("bytes").addAttribute("value",
//				new Integer(savedFileInfo.getBytes()).toString());
//		email.addElement("original_data_id").addAttribute("value",
//				savedFileInfo.getFname());
//		email.addElement("data_id").addAttribute("value",
//				savedFileInfo.getFname());
//		logger.info("Created file document is: " + createDoc.asXML());
		return fileStatement.getBindedDocument();
	}
	
	@SuppressWarnings("unchecked")
	public void publishFile(final Hashtable toSend, final Document doc, final DialogsMainPeer peer) {

		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.PUBLISH);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, doc);

		Hashtable cliSession = toSend;// this.cli.getSession();
		logger.warn("publishing in this sphere: !!!"
				+ (String) toSend.get(SessionConstants.SPHERE_ID));

		if (cliSession.get(SessionConstants.EXTERNAL_CONNECTION) != null) {

			update.put(SessionConstants.EXTERNAL_CONNECTION, "true");

			logger.warn("it has an external connection...good");
			String localSphereURL = (String) cliSession
					.get(SessionConstants.LOCAL_SPHERE);
			if (localSphereURL == null) {
				localSphereURL = (String) cliSession
						.get(SessionConstants.SPHERE_URL);
			}

			logger.warn("it was not null " + localSphereURL);

			try {
				// update.put("remoteSphereURL",localSphereURL);
				update.put(SessionConstants.REMOTE_USERNAME,
						(String) cliSession.get(SessionConstants.USERNAME));
				// update.put("remoteSphereId",localSphereId);
			} catch (Exception e) {

			}

			Hashtable sendSession = (Hashtable) toSend.clone();
			String before = (String) sendSession
					.get(SessionConstants.SPHERE_ID);

			String localSphereId = (String) cliSession
					.get(SessionConstants.LOCAL_SPHERE_ID);

			sendSession.put(SessionConstants.SPHERE_ID, localSphereId);

			Hashtable localUpdate = (Hashtable) update.clone();

			logger
					.warn("Will send local update to this sphere, this is the critical part: "
							+ localSphereId);

			if (!localSphereId.equals(before)) {
				localUpdate.put(SessionConstants.SESSION, sendSession);
				localUpdate.put(SessionConstants.SPHERE, localSphereId);
				localUpdate.put(SessionConstants.REPRESS_NOTIFICATION, "true");

				logger.warn("sending also to this sphere: " + localSphereId);
			}

		} else {
			logger
					.warn("it does not have an external connection variable...boo");
		}

		peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH)
				.handle(update);
	}
}
