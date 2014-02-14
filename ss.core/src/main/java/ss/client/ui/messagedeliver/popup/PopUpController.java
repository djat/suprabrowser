/**
 * 
 */
package ss.client.ui.messagedeliver.popup;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.graphics.Point;

import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.common.UiUtils;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.util.SessionConstants;

/**
 * @author zobo
 * 
 */
public class PopUpController {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PopUpController.class);

	public static PopUpController INSTANCE = new PopUpController();

	private PopUpController() {
		this.popupsMutex = new Object();
		this.elements = new PopUpsLine();
	}

	private SOptionPane activeSO = null;
	
	private boolean shellInCreation = false;

	public Object popupsMutex;

	private String contactName;

	private String login;

	private Point current_popup = null;
	
	private final PopUpsLine elements;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public synchronized void popupPerform(final PopUpElement element) {
		if (this.activeSO != null){
			logger.error("Popup performed when shell is active");
			return;
		}
		this.shellInCreation = true;
		final Thread t = new Thread() {

			public void run() {
				try {
					final Document doc = element.getDoc();
					boolean isPopup = true;
					if (!(element.isPopupAnyway())){
						final Element forwarded_by = doc.getRootElement().element(
							"forwarded_by");
						final String author2 = (forwarded_by != null) ? (forwarded_by.attributeValue("value")) : 
							(doc.getRootElement().element("giver").attributeValue("value"));
						isPopup = !(getCurrentContactName().equals(author2));
					}
					if (isPopup) {
						UiUtils.swtInvoke(new Runnable() {
							public void run() {
								logger.info("HERE POPUP AND INSERT");
								PopUpController.this.activeSO = new SOptionPane(doc, SupraSphereFrame.INSTANCE.client.session);
								PopUpController.this.shellInCreation = false;
								PopUpController.this.activeSO.open();
							}
						});
					}
				} catch (Exception e) {
					logger.error("", e);

				}
			}
		};
		t.start();

	}

	public void recallPopup(final org.dom4j.Document doc) {
		if (doc == null){
			logger.error("Document is null");
			return;
		}
		synchronized (this.popupsMutex) {
			final String messageId = doc.getRootElement().element("message_id")
				.attributeValue("value");
			if (messageId == null){
				logger.error("MessageId is null");
				return;
			}
			this.elements.remove(messageId);
			if (this.activeSO != null){
				final Document docOfSO = this.activeSO.getDoc();
				if (docOfSO == null){
					logger.error("docOfSO is null");
					return;
				}
				final String messageIdOfSO = docOfSO.getRootElement().element("message_id")
					.attributeValue("value");
				if (messageIdOfSO == null){
					logger.error("messageIdOfSO is null");
					return;
				}
				if (messageIdOfSO.equals(messageId)){
					this.activeSO.closeFromWithin();
				}
			}
		}
	}

	public void popup(final Document doc) {
		putToPopups(doc, false, false);
	}
	
	public void popupForced(final Document doc) {
		putToPopups(doc, false, true);
	}
	
	public synchronized void popupNotification(final Document doc) {
		putToPopups(doc, true, false);
	}
	
	private void putToPopups(final Document doc, final boolean isPopUpAnyway, final boolean forced){
		if (doc == null){
			logger.error("Doc is null");
			return;
		}
		if (!forced){
			if (!isPopUpPreference()){
				if (logger.isDebugEnabled()) {
					logger.debug("Setted to not pop-up");
				}
				return;
			}
		}
		synchronized (this.popupsMutex) {
			final String messageId = Statement.wrap(doc).getMessageId();
			if (messageId == null){
				logger.error("MessageId is null");
				return;
			}
			this.elements.offer(doc, messageId, isPopUpAnyway);
		}
		firePopupExisted();
	}

	public boolean shouldPopupMessage(final Statement statement,
			final WorkflowConfiguration configuration, final MessagesPane pane) {
		String deliveryType = statement.getDeliveryType();
		AbstractDelivery delivery = configuration
				.getDeliveryByTypeOrNormal(deliveryType);
		String username = getCurrentLogin();
		String contactName = getCurrentContactName();

		ResultStatement result = null;
		if (statement.getResultId() != null) {
			result = pane.getResultForMessage(statement);
		}
		return delivery.checkMessagesToPopup(statement, result, contactName,
				username);
	}

	private String getCurrentContactName() {
		if (this.contactName == null) {
			this.contactName = (String) SupraSphereFrame.INSTANCE.client
					.getSession().get(SessionConstants.REAL_NAME);
		}
		return this.contactName;
	}

	private String getCurrentLogin() {
		if (this.login == null) {
			this.login = (String) SupraSphereFrame.INSTANCE.client.getSession()
					.get(SessionConstants.USERNAME);
		}
		return this.login;
	}

	/**
	 * @param location
	 */
	public void setCurrent_popup(final Point location) {
		this.current_popup = location;
	}

	public Point getCurrent_popup() {
		return this.current_popup;
	}

	/**
	 * @param statement
	 * @param configuration
	 */
	public void checkIsPopupElement(Statement statement, WorkflowConfiguration configuration, MessagesPane pane) {
		boolean shouldPopup = shouldPopupMessage(statement, configuration, pane);
		if (shouldPopup) {
			synchronized (this.popupsMutex) {
				popup(statement.getBindedDocument());
			}
		}		
	}

	/**
	 * @param doc
	 */
	public void closedOptionPane() {
		this.activeSO = null;
		firePopupExisted();
	}
	
	private void firePopupExisted(){
		if ((this.activeSO != null)||(this.shellInCreation)){
			return;
		}
		final PopUpElement element = this.elements.take();
		if (element != null){
			if (logger.isDebugEnabled()) {
				logger.debug("Next element to popup with messageId: " + element.getMessageId());
			}
			popupPerform(element);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("No anymore elements to popup");
			}
		}
	}
	
	private boolean isPopUpPreference(){
		return SupraSphereFrame.INSTANCE.client.getPreferencesChecker().isPopUpOnTop();
	}
}
