package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.ui.email.EmailAddressesContainer;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.MethodProcessing;
import ss.server.networking.SC;
import ss.smtp.defaultforwarding.EmailBody;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElementFactory;

public class GetAndSendInviteTextHandler implements ProtocolHandler {

	private static final String $INVITER_NAME = "$inviterName";

	private static final String $RECIPIENT_NAME = "$recipientName";

	private static final String $INVITE_URL = "$inviteURL";

	private static final String EMAIL_ADDRESS = "email_address";

	private static final String FIRST_NAME = "first_name";

	private static final String VALUE = "value";

	private static final String MESSAGE_ID = "message_id";
	
	private static final Logger logger = SSLogger.getLogger(GetAndSendInviteTextHandler.class);

	public GetAndSendInviteTextHandler() {
		
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_AND_SEND_INVITE_TEXT;
	}

	public void handle(Hashtable update) {
		handleGetAndSendInviteText(update);
	}

	public void handleGetAndSendInviteText(final Hashtable update) {
		final Hashtable useSession = (Hashtable) update.get(SC.SESSION);
		Thread t = new Thread() {
			public void run() {

				Document contactDoc = (Document) update.get(SC.CONTACT_DOC);
				String fromDomain = (String) update.get(SC.FROM_DOMAIN);
				String fromEmail = (String) update.get(SC.FROM_EMAIL);

				String sphereId = (String) useSession.get(SC.SPHERE_ID);
				String realName = (String) useSession.get(SC.REAL_NAME);
				String address = (String) useSession.get(SC.ADDRESS);
				String port = (String) useSession.get(SC.PORT);
			//	String emailmessageId = (String) useSession.get(SC.);

				String text = MethodProcessing.getInviteTextFromXMLFile();

				String subject = "A Personal Invitation From " + realName;

				final String id = sphereId
						+ "."
						+ contactDoc.getRootElement().element(MESSAGE_ID)
								.attributeValue(VALUE);

				String inviteURL = ("invite::" + address + ":" + port + "," + id);

				logger.info("INVITE URL: "+inviteURL);
				text = text.replace($INVITE_URL, inviteURL);
				text = text.replace($RECIPIENT_NAME, contactDoc
						.getRootElement().element(FIRST_NAME).attributeValue(
								VALUE));
				StringTokenizer st = new StringTokenizer(realName, " ");
				String firstName = st.nextToken();
				text = text.replace($INVITER_NAME, firstName);

				//final String name = GetAndSendInviteTextHandler.this.peer
				//		.getVerifyAuth().getDisplayName(sphereId);

				final String toEmail = contactDoc.getRootElement().element(
						EMAIL_ADDRESS).attributeValue(VALUE);
				final String messageId = contactDoc.getRootElement().element(MESSAGE_ID).attributeValue(VALUE);
				final String replyTo = sphereId + "." + messageId + "@" + fromDomain;

				EmailAddressesContainer emailAddresses = new EmailAddressesContainer(toEmail, fromEmail, replyTo, null, null);
				List<SendingElement> sendingElements = SendingElementFactory.createCreated(
						emailAddresses, null, new EmailBody(subject, text), messageId, sphereId, null);
				Responcer.INSTANCE.initiateResponceElement(messageId, sphereId, sendingElements.size());
				for (SendingElement sendingElement : sendingElements) {
					Mailer.INSTANCE.send(sendingElement);
				}
				
				//eMailer em = new eMailer();
				//em.send(new EmailAddressesContainer(toEmail, fromEmail), null, new StringBuffer(
				//		text), subject, replySphere);
				// TODO ????
			}
		};
		t.start();
	}

}
