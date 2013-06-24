/**
 * 
 */
package ss.smtp.responcetosphere;

import java.util.ArrayList;
import java.util.List;

import ss.client.ui.email.SendList;
import ss.smtp.responcetosphere.ResponceStringInfo.ResponceType;
import ss.smtp.sender.SendingElement;

/**
 * @author zobo
 *
 */
public class ResponceElementFactory {
	
	private static final String SUBJECT_DEFAULT = "Email sending";

	private static final String SUBJECT_PREFIX = "Email sending info:";

	private static final String EMAIL_SENDING_INFORMATION_RESPONCE_STRING = "Email sending information:";

	private static final String WAS_SENDING_TO_FOLLOWING_ADDRESSES = "Was sending to following addresses:";

	private static final String SERVER_COULD_NOT_SEND_TO_FOLLOWING_ADDRESSES = "Server could not send to following addresses:";

	private static final String SUCCESSFULLY_SUBJECT = "Emails sent successfully";

	private static final String SUCCESSFULLY_RESPONCE_POSTSTRING = "All emails have been successfully sent.";

	private static final String NOT_SUCCESSFULLY_SUBJECT = "Emails sent not successfully";

	private static final String PROBLEMS_SUBJECT = "Problems sending email";

	private static final String FAILED_SUBJECT = "Emails sending failed";

	private static final String SERVER_WILL_BE_TRYING_TO_RESEND_TO_FAILED_ADDRESSES = "Server will be trying to resend to failed addresses.";
	
	private static final String SPACE = " ";

	private static final String BR = "<br>";
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ResponceElementFactory.class);
	
	public static ResponceElement createBlank(String messageId, String sphereId, int count){
		return new ResponceElement(new ArrayList<SendList>(), 
				new ArrayList<SendList>(), messageId, sphereId, count);
	}
	
	public static ResponceElement createSuccessfull(SendingElement element){
		return new ResponceElement(element.getSendList(), element.getMessageId(), element.getSphereId(), true);
	}
	
	public static ResponceElement createFailed(SendingElement element){
		return new ResponceElement(element.getSendList(), element.getMessageId(), element.getSphereId(), false);
	}

	/**
	 * @param addressesContainerList
	 * @param notSent
	 * @return
	 */
	public static ResponceStringInfo createResponceString(ResponceElement element) {
		List<SendList> toSentList = element.getTosendLists();
		List<SendList> notSentList = element.getNotsendLists();
		ResponceType type = null;
		String subject = SUBJECT_DEFAULT;
		final String PREFIX = SUBJECT_PREFIX + SPACE; 
		String responce = EMAIL_SENDING_INFORMATION_RESPONCE_STRING + BR;
		if (logger.isDebugEnabled()){
			logger.debug("Send Lists:" + SPACE + toSentList.size() + SPACE + notSentList.size());
		}
		if ((toSentList != null)&&(!(toSentList.isEmpty()))){
			if ((notSentList != null)&&(!(notSentList.isEmpty()))){
				if (notSentList.size() == toSentList.size()){
					responce += getNotSent(notSentList);
					if (element.isFinalResponce()){
						type = ResponceType.ERROR;
						subject = PREFIX + FAILED_SUBJECT;
					} else {
						type = ResponceType.WARN;
						subject = PREFIX + PROBLEMS_SUBJECT;
						responce += BR + SERVER_WILL_BE_TRYING_TO_RESEND_TO_FAILED_ADDRESSES;
					}
				} else {
					responce += getSend(toSentList);
					responce += getNotSent(notSentList);
					if (element.isFinalResponce()){
						type = ResponceType.WARN;
						subject = PREFIX + NOT_SUCCESSFULLY_SUBJECT;
					} else {
						type = ResponceType.WARN;
						subject = PREFIX + PROBLEMS_SUBJECT;
						responce += BR + SERVER_WILL_BE_TRYING_TO_RESEND_TO_FAILED_ADDRESSES;
					}
				}
			} else {
				responce += getSend(toSentList);
				responce += BR + SUCCESSFULLY_RESPONCE_POSTSTRING;
				type = ResponceType.INFO;
				subject = PREFIX + SUCCESSFULLY_SUBJECT;
			}
		} else 	{
			return null;
		}
		return new ResponceStringInfo(type, subject, responce);
	}

	private static String getNotSent(final List<SendList> notSentList) {
		String responce = BR + SERVER_COULD_NOT_SEND_TO_FOLLOWING_ADDRESSES + BR;
		for (SendList notSent : notSentList){
			responce += SPACE + notSent.getSingleLineAddresses();
		}
		return responce;
	}

	private static String getSend(final List<SendList> toSentList){
		String responce = WAS_SENDING_TO_FOLLOWING_ADDRESSES + BR;
		for (SendList notSent : toSentList){
			responce += SPACE + notSent.getSingleLineAddresses();
		}
		return responce;
	}
}
