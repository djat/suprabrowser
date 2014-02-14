/**
 * 
 */
package ss.client.ui.balloons;

import java.util.Hashtable;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.balloons.BalloonElement.BalloonTypes;
import ss.common.UiUtils;
import ss.domainmodel.Statement;

/**
 * @author zobo
 * 
 */
class BalloonFactory {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BalloonFactory.class);

	private static final ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_BALLOONS_BALLOONFACTORY);

	private static final String DROP_AND_DRAG = "BALLOONFACTORY.DROP_AND_DRAG";

	static BalloonElement createBalloonElement(final Document doc,
			final Document replyDoc, final MessagesPane pane) {
		Statement st = Statement.wrap(doc);
		return new BalloonElement(st.getMessageId(), doc, replyDoc, false,
				pane, BalloonTypes.REPLY);
	}

	static BalloonElement createBalloonElement(final Document doc,
			final boolean author, final MessagesPane pane) {
		Statement st = Statement.wrap(doc);
		return new BalloonElement(st.getMessageId(), doc, null, author, pane,
				BalloonTypes.SIMPLE);
	}

	static BalloonElement createDragAndDropBalloonElement(
			final MessagesPane pane) {
		return new BalloonElement(null, null, null, false, pane,
				BalloonTypes.DRAGANDDROP);
	}

	static void createBalloon(final BalloonElement element,
			final IBalloonListener listener) {
		if ((element.getPane() == null) || (element.getPane().isDisposed())) {
			if (logger.isDebugEnabled()) {
				logger.debug("MessagesPane is closed for messageId: "
						+ element.getMessageId());
			}
			listener.closed();
		} else {
			BalloonTypes type = element.getType();
			Runnable run;
			if (type == BalloonTypes.DRAGANDDROP) {
				run = createDragAndDropBalloonWindow(element, listener);
			} else if (type == BalloonTypes.SIMPLE) {
				run = createSimpleBalloonWindow(element, listener);
			} else if (type == BalloonTypes.REPLY) {
				run = createReplyBalloonWindow(element, listener);
			} else {
				listener.closed();
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Creating balloon for messageId: "
						+ element.getMessageId());
			}
			UiUtils.swtBeginInvoke(run);
		}
	}

	private static Runnable createSimpleBalloonWindow(
			final BalloonElement element, final IBalloonListener listener) {
		return new Runnable() {

			public void run() {
				Document doc = element.getDoc();
				MessagesPane mPane = element.getPane();
				final BalloonWindow bw = new BalloonWindow(
						Display.getDefault(), (Document) doc.clone(),
						(Hashtable) mPane.getRawSession().clone(), SWT.ON_TOP
								| SWT.CLOSE, listener, false);

				bw.setAnchor(SWT.NONE); // change this to make
				// it look like a
				// conversation
				// dialog...

				String subject = doc.getRootElement().element("subject")
						.attributeValue("value");

				if (subject.length() > 45) {
					subject = subject.substring(0, 45);

				}

				String body = subject;
				/*
				 * String body = Statement.wrap(doc).getBody(); if
				 * (body.equals("")){ body = Statement.wrap(doc).getOrigBody();
				 * if (body.equals("")){ body = subject; } } if (body.length() >
				 * 45) { body = body.substring(0, 45); }
				 */

				// bw.setText(subject);
				if (element.isAuthor()) {
					String fromAuthor = doc.getRootElement().element("giver")
							.attributeValue("value");
					if (doc.getRootElement().element("response_id") != null) {
						if (subject.length() > 25) {
							bw.setText(fromAuthor + "," + " RE: "
									+ subject.substring(0, 25));
						} else {
							bw.setText(fromAuthor + "," + " RE: " + subject);

						}
					} else {
						if (subject.length() > 25) {
							bw.setText(fromAuthor + ":" + " "
									+ subject.substring(0, 25));
						} else {
							bw.setText(fromAuthor + ":" + " " + subject);

						}

					}
				} else {
					bw.setText(subject);
				}

				final Label label = new Label(bw.getContents(), SWT.WRAP);
				label.setText(body);
				label.setSize(label.computeSize(260, 50));
				label.setBackground(bw.getShell().getBackground());
				bw.getContents().setSize(label.getSize());
				bw.addSelectionControl(label);

				bw.setVisible(true);
				listener.created(bw);

				if (logger.isDebugEnabled()) {
					logger.debug("Balloon simple type created");
				}

			}
		};
	}

	private static Runnable createReplyBalloonWindow(
			final BalloonElement element, final IBalloonListener listener) {

		return new Runnable() {

			public void run() {
				Document doc = element.getDoc();
				Document replyToThisDoc = element.getReplyToThisDoc();
				MessagesPane mPane = element.getPane();
				final BalloonWindow bw = new BalloonWindow(
						Display.getDefault(), (Document) doc.clone(),
						(Hashtable) mPane.getRawSession().clone(), SWT.ON_TOP
								| SWT.CLOSE, listener, false);

				bw.setAnchor(SWT.NONE); // change this to make
				// it look like a
				// conversation
				// dialog...

				String body = doc.getRootElement().element("body").getText();
				if (body.length() <= 0) {
					String subject = doc.getRootElement().element("subject")
							.attributeValue("value");

					if (subject.length() > 45) {
						subject = subject.substring(0, 45);

					}

					String fromRE = replyToThisDoc.getRootElement().element(
							"subject").attributeValue("value");
					String fromAuthor = replyToThisDoc.getRootElement()
							.element("giver").attributeValue("value");

					if (fromRE.length() > 25) {
						bw.setText(fromAuthor + "," + " RE: "
								+ fromRE.substring(0, 25));
					} else {
						bw.setText(fromAuthor + "," + " RE: " + fromRE);

					}
					// bw.setText(subject);

					final Label label = new Label(bw.getContents(), SWT.WRAP);

					label.setText(subject);
					label.setSize(label.computeSize(260, 50));

					label.setBackground(bw.getShell().getBackground());
					bw.getContents().setSize(label.getSize());
					bw.addSelectionControl(label);

				} else {

					if (body.length() > 45) {
						body = body.substring(0, 45);
					}
					final Label label = new Label(bw.getContents(), SWT.WRAP);

					label.setText(body);
					label.setSize(label.computeSize(260, 50));

					label.setBackground(bw.getShell().getBackground());
					bw.getContents().setSize(label.getSize());
					bw.addSelectionControl(label);

				}

				bw.setVisible(true);
				listener.created(bw);

				if (logger.isDebugEnabled()) {
					logger.debug("Balloon to reply message created");
				}

			}
		};
	}

	private static Runnable createDragAndDropBalloonWindow(
			final BalloonElement element, final IBalloonListener listener) {
		return new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				MessagesPane main = element.getPane();

				final Hashtable bwSession = (Hashtable) main.getRawSession()
						.clone();

				bwSession.put("sphere_id", main.sF.client.getVerifyAuth()
						.getSystemName((String) bwSession.get("real_name")));

				BalloonWindow bw = new BalloonWindow(Display.getCurrent(),
						null, bwSession, SWT.ON_TOP | SWT.CLOSE,
						listener, true);

				bw.setAnchor(SWT.NONE); // change this to make
				// it look like a
				// conversation
				// dialog...

				bw.setText(bundle.getString(DROP_AND_DRAG));

				final Label label = new Label(bw.getContents(), SWT.WRAP);

				// label.setText("Drag and Drop any item to
				// SupraIndex It!");
				label.setSize(label.computeSize(260, 50));
				label.setBackground(bw.getShell().getBackground());
				bw.getContents().setSize(label.getSize());
				bw.addSelectionControl(label);
				// bw.setSize(250,40);

				bw.setVisible(true);
				listener.created(bw);
			}
		};
	}
}
