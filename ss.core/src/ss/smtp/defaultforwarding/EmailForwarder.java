/**
 * 
 */
package ss.smtp.defaultforwarding;

import java.util.List;

import ss.client.ui.email.AttachedFileCollection;
import ss.client.ui.email.EmailAddressesContainer;
import ss.common.ThreadUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.Statement;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.smtp.responcetosphere.Responcer;
import ss.smtp.sender.Mailer;
import ss.smtp.sender.SendingElement;
import ss.smtp.sender.SendingElementFactory;

/**
 * @author zobo
 * 
 */
public final class EmailForwarder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EmailForwarder.class);

	public static final EmailForwarder INSTANCE = new EmailForwarder();

	private final ForwardingLine forwardLine;

	private final Thread forwarder;

	private EmailForwarder() {
		this.forwardLine = new ForwardingLine();
		this.forwarder = new Thread() {
			@Override
			public void run() {
				while (true) {
					deliver(getNextElement());
				}
			}
		};
		ThreadUtils.startDemon(this.forwarder, "Messages Forward to Email Dispatcher");
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private ForwardingElement getNextElement() {
		return this.forwardLine.take();
	}
	
	/**
	 * @param element
	 */
	public void send(final ForwardingElement element) {
		this.forwardLine.put(element);
	}

	private void deliver(final ForwardingElement element) {
		try {
			if ( element == null ) {
				logger.error( "ForwardingElement is null" );
				return;
			}

			DialogsMainPeer peer = DialogsMainPeerManager.INSTANCE
					.getHandlers().iterator().next();
			VerifyAuth auth = peer.getVerifyAuth();

			Statement statement = Statement.wrap(element.getDoc());
			String messageId = statement.getMessageId();
			String sphereId = statement.getCurrentSphere();
			String giver = statement.getGiver();

			EmailBody emailBody = EmailBodyCreator.create(statement, auth);
			if (emailBody == null) {
				return;
			}
			
			EmailAddressesContainer addressesContainer = EmailAddressesCreator
					.create(element.getSphereId(), messageId, giver, peer, element.getForcedForwardingData(), statement);
			if (addressesContainer == null) {
				if (logger.isDebugEnabled()) {
					logger.debug( "addressesContainer is null" );
				}
				return;
			}

			AttachedFileCollection files = (element.getForcedForwardingData() == null) ? 
					FileAttacher.create(statement, peer) : element.getForcedForwardingData().getAttachedFileCollection();

			List<SendingElement> sendingElements = SendingElementFactory
					.createForwarded(addressesContainer, files, emailBody, messageId, sphereId);
			if (sendingElements.isEmpty()) {
				logger.error("Sending element is null, return...");
				return;
			}
			Responcer.INSTANCE.initiateResponceElement(messageId, element.getSphereId(), sendingElements.size());
			for (SendingElement sendingElement : sendingElements) {
				if (logger.isDebugEnabled()) {
					logger.debug("Sending email: " + sendingElement);
				}
				Mailer.INSTANCE.send(sendingElement);
			}
		} catch (Exception ex) {
			logger.error("exception",ex);
		}
	}
}
