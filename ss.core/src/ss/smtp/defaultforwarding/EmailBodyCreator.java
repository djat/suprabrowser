/**
 * 
 */
package ss.smtp.defaultforwarding;

import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.MessageStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;

/**
 * @author zobo
 *
 */
public class EmailBodyCreator {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailBodyCreator.class);
	
	private static int SUBJECT_LENGTH = 20;
	
	public static EmailBody create(final Statement statement, final VerifyAuth auth){
		if (statement.isTerse()){
			return  createTerse(TerseStatement.wrap(statement.getBindedDocument()), auth);
		}
		if (statement.isMessage()){
			return  createMessage(MessageStatement.wrap(statement.getBindedDocument()), auth);
		}
		if (statement.isEmail()){
			return  createEmail(ExternalEmailStatement.wrap(statement.getBindedDocument()), auth);
		}
		return null;
	}
	
	private static EmailBody createTerse(final TerseStatement terse, final VerifyAuth auth){
		String subject = getSphereName(terse.getCurrentSphere(), auth) + ": " + cutOffSubject(terse.getSubject());
		return new EmailBody(subject, terse.getSubject());
	}
	
	private static EmailBody createEmail(final ExternalEmailStatement email, final VerifyAuth auth){
		String sphereName = getSphereName(email.getCurrentSphere(), auth);
		sphereName = (sphereName != null) ? sphereName : "";
		final String originalSubject = email.getSubject();
		String subject = (originalSubject != null) ? originalSubject : "";
		if ( !subject.toLowerCase().contains( sphereName.toLowerCase() ) ) {
			subject = (StringUtils.isNotBlank( sphereName ) ? (sphereName + ": ") : "") + subject;
		}
		return new EmailBody(subject, email.getOrigBody());
	}
	
	private static EmailBody createMessage(final MessageStatement message, final VerifyAuth auth){
		String subject = getSphereName(message.getCurrentSphere(), auth) + ": " + message.getSubject();
		return new EmailBody(subject, message.getOrigBody());
	}
	
	private static String getSphereName(final String sphereId, final VerifyAuth auth){
		String dysplayName = auth.getDisplayNameWithoutRealName(sphereId);
		if (logger.isDebugEnabled()){
			logger.debug("SphereId and dysplayName: " + sphereId + ", " + dysplayName);
		}
		return dysplayName;
	}
	
	private static String cutOffSubject(final String subject){
		if (subject.length() <= SUBJECT_LENGTH){
			return subject;
		} else {
			return (subject.substring(0, SUBJECT_LENGTH) + "...");
		}
	}
}
