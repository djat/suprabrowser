/**
 * 
 */
package ss.smtp.sender.resendcontrol;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Message.RecipientType;

import ss.client.ui.email.SendList;
import ss.smtp.SMailMessage;

/**
 * @author zobo
 *
 */
public class PostpondedEmailsInfoGenerator {
	
	private final class SendListInfo {
		private final SendList sendList;
		
		private final int numberOfRetriesPerformed;
		
		private final int numberOfRetriesLeft;
		
		public SendListInfo(final SendList sendList, final int numberOfRetriesPerformed, final int numberOfRetriesLeft) {
			super();
			this.sendList = sendList;
			this.numberOfRetriesPerformed = numberOfRetriesPerformed;
			this.numberOfRetriesLeft = numberOfRetriesLeft;
		}

		void fill(final PrintWriter writer, final int index){
			Address[] addresses = this.sendList.getAddresses();
			if ((addresses == null)||(addresses.length == 0)){
				logger.error("Addresses for SendList is empty");
				return;
			}
			writer.println("SendList # " + index + ". Next addresses still in queue:");
			boolean comma = false;
			for (Address addr : addresses){
				if (comma) {
					writer.write(", "+ addr.toString());
				} else {
					writer.write(addr.toString());
					comma = true;
				}
			}
			writer.println();
			writer.println("Number of attempts performed: " + this.numberOfRetriesPerformed + ", still left: " + this.numberOfRetriesLeft);
		}
	}
	
	private final class EmailsInfo {
		
		private final String messageId;
		
		private final String sphereId;
		
		private final List<SendListInfo> sendLists;
		
		private final SMailMessage smessage;

		EmailsInfo(final String messageId, final String sphereId, final SMailMessage smessage) {
			this.sendLists = new ArrayList<SendListInfo>();
			this.messageId = messageId;
			this.sphereId = sphereId;
			this.smessage = smessage;
		}
		
		void addSendListInfo(final SendList sendList, final int numberOfRetriesPerformed, final int numberOfRetriesLeft){
			SendListInfo sendListInfo = new SendListInfo(sendList, numberOfRetriesPerformed, numberOfRetriesLeft);
			this.sendLists.add(sendListInfo);
		}

		void fill(final PrintWriter writer){
			try {
				writer.println("SUBJECT: " + this.smessage.getSubject());
				final Address[] from = this.smessage.getFrom();
				final Address[] replyTo = this.smessage.getReplyTo();
				if ((from != null)&&(from.length != 0)){
					writer.println("SENDER: " + from[0].toString());
				}
				if ((replyTo != null)&&(replyTo.length != 0)){
					writer.println("ReplyTO: " + replyTo[0].toString());
				}
				fillRecipients(writer, RecipientType.TO);
				fillRecipients(writer, RecipientType.CC);
				fillRecipients(writer, RecipientType.BCC);
				int count = 1;
				for (SendListInfo sendListInfo : this.sendLists){
					sendListInfo.fill(writer, count);
					count++;
				}
				writer.println("Email system info: messageId: " + this.messageId + ", sphereId: " + this.sphereId);
			} catch (MessagingException ex) {
				logger.error( "Error getting info for postponded email",ex);
			}
		}
		
		private void fillRecipients(final PrintWriter writer, final RecipientType type) throws MessagingException{
			Address[] rec = this.smessage.getRecipients(type);
			if ((rec == null)||(rec.length == 0)){
				return;
			}
			writer.print(type.toString() + ": ");
			boolean comma = false;
			for (Address addr : rec){
				if (comma) {
					writer.print(", "+ addr.toString());
				} else {
					writer.print(addr.toString());
					comma = true;
				}
			}
			writer.println();
		}
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PostpondedEmailsInfoGenerator.class);
	
	public static final PostpondedEmailsInfoGenerator INSTANCE = new PostpondedEmailsInfoGenerator();
	
	private PostpondedEmailsInfoGenerator(){
		
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public String getInfo(){
		logger.info("Get Postponded Emails status performed");
		List<PostpondedElement> elements = PostpondedMailProvider.INSTANCE.getCurrentStateData();
		if (logger.isDebugEnabled()) {
			logger.debug("Postponded Elements taken: " + elements.size());
		}
		StringWriter responce = new StringWriter();
		PrintWriter writer = new PrintWriter( responce );
		writer.println("--- Postponed emails status ---");
		writer.println();

		if ((elements == null)||(elements.isEmpty())){
			 writer.println("There are no postponded emails at the moment.");
		} else {
			 writer.println("Postponded emails: ");
			 int counter = 1;
			 for (EmailsInfo email : parseEmails(elements)){
				 writer.println();
				 writer.println("Email # " + counter + ":");
				 email.fill(writer);
				 counter++;
			 }
		}
		writer.println();
		writer.println("--- End ---");
		writer.flush();
		String resp = responce.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Responce: " + resp);
		}
		return resp;
	}

	private List<EmailsInfo> parseEmails(List<PostpondedElement> elements){
		List<EmailsInfo> list = new ArrayList<EmailsInfo>();
		String messageId;
		String sphereId;
		for (PostpondedElement element : elements){
			messageId = element.getElement().getMessageId();
			sphereId = element.getElement().getSphereId();
			SendList sendList = element.getElement().getSendList();
			SMailMessage smessage = element.getElement().getSmessage();
			int numberOfRetriesPerformed = element.getNumberOfRetriesPerformed();
			int numberOfRetriesLeft = element.getNumberOfRetriesLeft();
			boolean moved = false;
			for (EmailsInfo info : list){
				if (info.messageId.equals(messageId)){
					info.addSendListInfo(sendList, numberOfRetriesPerformed, numberOfRetriesLeft);
					moved = true;
					break;
				}
			}
			if (!moved) {
				EmailsInfo info = new EmailsInfo(messageId, sphereId, smessage);
				info.addSendListInfo(sendList, numberOfRetriesPerformed, numberOfRetriesLeft);
				list.add(info);
			}
		}
		return list;
	}
}
