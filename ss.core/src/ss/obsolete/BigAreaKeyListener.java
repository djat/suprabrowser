/*
 * Created on May 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.obsolete;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;

import javax.swing.JTextPane;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.event.SendCreateAction;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewWeblink;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.rss.RSSParser;
import ss.util.VariousUtils;
import ss.util.XMLSchemaTransform;

/**
 * @author david
 * 
 */
public class BigAreaKeyListener implements KeyListener {

	private JTextPane bigArea;

	private MessagesPane mP;

	private Hashtable session;

	private SupraSphereFrame sF;

	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BigAreaKeyListener.class);

	public BigAreaKeyListener(MessagesPane mP, JTextPane bigArea) {

		this.bigArea = bigArea;
		this.mP = mP;
		this.session = mP.getRawSession();
		this.sF = mP.sF;

	}

	@SuppressWarnings("unchecked")
	public void keyPressed(KeyEvent ke) {
		boolean isReply = false;
		if (this.mP.isReplyChecked()) {
			isReply = true;

		}
		final boolean finalReply = isReply;

		if (ke.getKeyChar() == '\n') {

			String saveText = this.bigArea.getText();

			if (this.sF.isTagChecked()) {

				this.mP.doTagAction(this.mP.getLastSelectedDoc(), saveText);

				this.sF.setReplyChecked(false);
				this.sF.setTagChecked(false);

			} else {
				if (VariousUtils.isDomain(saveText)) {
					saveText = VariousUtils.convertToFullURL(saveText);

					final String text = saveText;
					Thread t = new Thread() {
						public void run() {

							String url = text;

							String mainTitle = null;
							if (mainTitle == null) {
								mainTitle = RSSParser.getTitleFromURL(text);
							}

							logger.warn("Here is the title now: " + mainTitle);

							BigAreaKeyListener.this.session
									.put(
											"sphere_id",
											(String) ((MessagesPane) BigAreaKeyListener.this.sF.tabbedPane
													.getSelectedMessagesPane())
													.getRawSession().get(
															"sphere_id"));
							Hashtable temp = (Hashtable) BigAreaKeyListener.this.sF
									.getRegisteredSession(
											(String) BigAreaKeyListener.this.session
													.get("supra_sphere"),
											"DialogsMainCli");
							String sessionId = (String) temp.get("session");
							BigAreaKeyListener.this.session.put("session",
									sessionId);
							if (!finalReply) {
								logger.warn("it was not a reply");

								String desiredSubject = (mainTitle != null) ? mainTitle : url;
								NewWeblink nw = new NewWeblink(BigAreaKeyListener.this.session, null, "normal", url, desiredSubject, null);

								nw.doPublishAction();
							} else {
								logger.warn("It was a reply!!");

								String desiredSubject = (mainTitle != null) ? mainTitle : url;
								
								NewWeblink nw = new NewWeblink(
										BigAreaKeyListener.this.session,
										BigAreaKeyListener.this.mP,
										BigAreaKeyListener.this.mP
												.getLastSelectedDoc(),
												DeliveryFactory.INSTANCE.create("normal"), true, url, desiredSubject);

								nw.doPublishAction();

							}

							BigAreaKeyListener.this.sF.setReplyChecked(false);
							;

							BigAreaKeyListener.this.sF.setSendText( "");

						}
					};
					t.start();

				} else if (saveText.toLowerCase().startsWith("invite::")) {

					logger.info("try accepting invitation");

					SendCreateAction sca = new SendCreateAction( this.mP );
					Document contactDoc = BigAreaKeyListener.this.sF.client
							.getMyContact(BigAreaKeyListener.this.session);

					contactDoc = XMLSchemaTransform.removeLocations(contactDoc);
					sca.doInvitationAcceptAction(BigAreaKeyListener.this.sF,
							BigAreaKeyListener.this.session, saveText,
							contactDoc);

				} else {

					// MessagesPane main =
					// (MessagesPane)sF.messagePanes.get((String)session.get("supra_sphere"));
					// int i =
					// BigAreaKeyListener.this.sF.tabbedPane.getSelectedIndex();
					// if ((isonline(title) == true)
					// ||(delivery_type.equals("Normal"))) {
					Hashtable send_session = (Hashtable) BigAreaKeyListener.this.session
							.clone();
					Document doc = null;
					// Document orig_doc = null;
					// Document lastSelectedDoc = null;
					// System.out.println("ABSOLUTELY TERSE");
					doc = this.mP.returnTerseStatement(BigAreaKeyListener.this.bigArea
							.getText()).getBindedDocument();
					doc.getRootElement().addElement("confirmed").addAttribute(
							"value", "true");
					if (this.mP.getLastSelectedDoc() != null
							&& this.mP.isReplyChecked()) {
						logger.info("last selected: "
								+ this.mP.getLastSelectedDoc().asXML());
						// table.getSelectionModel().removeSelectionInterval(currently_selected,
						// currently_selected);
						// currently_selected = -1;
						// System.out.println("message id of reply:
						// "+lastSelectedDoc.getRootElement().element("message_id").attributeValue("value"));

						try {
							// System.out.println("setting response id");
							doc.getRootElement().element("type").addAttribute(
									"value", "terse");
							doc.getRootElement().addElement("response_id")
									.addAttribute(
											"value",

											this.mP.getLastSelectedDoc()
													.getRootElement().element(
															"message_id")
													.attributeValue("value"));
							doc.getRootElement().addElement("thread_id")
									.addAttribute(
											"value",
											this.mP.getLastSelectedDoc()
													.getRootElement().element(
															"thread_id")
													.attributeValue("value"));

						} catch (NullPointerException npe) {
							logger.error(npe.getMessage(), npe);
						}
					}
					// System.out.println("selectedsession:
					// "+(String)selected.session.get("sphere_id")+" :
					// suprasession : "+(String)session.get("sphere_id"));
					if (!((String) send_session.get("sphere_id"))
							.equals((String) BigAreaKeyListener.this.session
									.get("sphere_id"))) {
						send_session.put("suprasphere", "false");
						// System.out.println("SETTING SS FALSE");
					} else {
						send_session.put("suprasphere", "true");
						// System.out.println("SETTING SS TRUE");
					}
					send_session.put("delivery_type", "normal");

					if ((this.bigArea.getText().trim()).length() > 0) {

						try {
							String parentSphere = this.mP.getLastSelectedDoc()
									.getRootElement().element("current_sphere")
									.attributeValue("value");

							if (!parentSphere.equals((String) send_session
									.get("sphere_id"))) {
								logger.warn("Send it somewhere else too: "
										+ parentSphere
										+ " : "
										+ (String) send_session
												.get("sphere_id"));

								send_session.put("multi_loc_sphere",
										(String) send_session.get("sphere_id"));
								doc.getRootElement().addElement(
										"multi_loc_sphere").addAttribute(
										"value",
										(String) send_session.get("sphere_id"));
								send_session.put("sphere_id", parentSphere);

							}

							Element multiLocElem = this.mP.getLastSelectedDoc()
									.getRootElement().element(
											"multi_loc_sphere");

							if (multiLocElem != null) {

								send_session.put("multi_loc_sphere",
										multiLocElem.attributeValue("value"));
								send_session.put("sphere_id", parentSphere);

							}

						} catch (Exception e) {

						}

						this.mP.client.publishTerse(send_session, doc);

					}
					logger.info("PUBLISHING: " + doc.asXML());
				}
			}
			this.sF.setReplyChecked(false);
			this.sF.setTagChecked(false);
		}

	}

	public void keyTyped(KeyEvent e) {

		if (e.getKeyChar() == '\n') {
			this.bigArea.setText(null);

		}

	}

	public void keyReleased(KeyEvent arg0) {

	}
}