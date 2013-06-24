/*
 * Created on Jan 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JMenuItem;

import org.dom4j.Document;
import org.dom4j.Element;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.event.createevents.CreateContactAction;
import ss.client.event.createevents.CreateFileAction;
import ss.client.event.createevents.CreateKeywordsAction;
import ss.client.event.createevents.CreateMessageAction;
import ss.client.event.createevents.CreateRssAction;
import ss.client.event.createevents.CreateSphereAction;
import ss.client.event.createevents.CreateTerseAction;
import ss.client.localization.LocalizationLinks;
import ss.client.ui.MessagesPane;
import ss.client.ui.SearchInputWindow;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.XmlDocumentUtils;
import ss.util.SupraXMLConstants;

public class UnifiedListeners {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(UnifiedListeners.class);

	private ResourceBundle bundle = ResourceBundle
	.getBundle(LocalizationLinks.CLIENT_EVENT_UNIFIEDLISTENERS);

	private static final String ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_SPHERE = "UNIFIEDLISTENERS.ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_SPHERE";

	private final SupraSphereFrame sF;

	public UnifiedListeners(SupraSphereFrame sF) {
		this.sF = sF;
	}


	public MenuItem addQueryListener(MenuItem add) {
		add.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				executeQuery();
			}
		});
		return add;
	}
	
	public JMenuItem addQueryListener(JMenuItem add) {
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				executeQuery();
			}
		});
		return add;
	}

	public MenuItem addSphereRemoveListener(MenuItem add) {
		add.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent arg0) {
				UnifiedListeners ul = UnifiedListeners.this;

				MessagesPane selectedPane = (MessagesPane) ul.sF.tabbedPane.getSelectedMessagesPane();
				if ( selectedPane == null ) {
					return;
				}
				
				Document doc = selectedPane.getLastSelectedDoc();

				logger.warn("removing : "
						+ doc.getRootElement().attributeValue(
						"display_name"));
				UserMessageDialogCreator.warningRemoveSphere(ul.bundle
						.getString(ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_SPHERE), 
						ul.sF.client, 
						selectedPane.getRawSession(), doc);
			}

		});

		return add;
	}
	
	
	public JMenuItem addSphereRemoveListener(JMenuItem add) {
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UnifiedListeners ul = UnifiedListeners.this;

				MessagesPane selectedPane = (MessagesPane) ul.sF.tabbedPane.getSelectedMessagesPane();
				if ( selectedPane == null ) {
					return;
				}
				
				Document doc = selectedPane.getLastSelectedDoc();

				logger.warn("removing : "
						+ doc.getRootElement().attributeValue(
						"display_name"));
				UserMessageDialogCreator.warningRemoveSphere(ul.bundle
						.getString(ARE_YOU_SURE_YOU_WANT_TO_REMOVE_THIS_SPHERE), 
						ul.sF.client, 
						selectedPane.getRawSession(), doc);
			}

		});

		return add;
	}

	private void executeQuery() {
		Thread t = new Thread() {
			@SuppressWarnings("unchecked")
			public void run() {

				UnifiedListeners.this.sF.tabbedPane.resetMarkForSelectedTab();
				final MessagesPane selectedPane = UnifiedListeners.this.sF.tabbedPane.getSelectedMessagesPane();
				if ( selectedPane == null ) {
					return;
				}

				Document lastSelected = selectedPane
						.getLastSelectedDoc();
				if (lastSelected == null) {
					lastSelected = selectedPane.getMessagesTree().getSelectedDoc();
				}

				String systemName = lastSelected.getRootElement()
						.attributeValue("system_name");
				// String default_delivery = null;
				// String default_type = null;
				// sF.tabbedPane.setForegroundAt(it, null);
				final Document doc = selectedPane.getSphereDefinition();
				String apath = "//sphere/thread_types/*";

				Vector results = new Vector();
				Vector<String> assetTypes = new Vector<String>();
				if (doc != null) {
					results.addAll( XmlDocumentUtils.selectElementListByXPath(doc, apath));

					for (int j = 0; j < results.size(); j++) {

						Element elem = (Element) results.get(j);
						String enabled = elem.attributeValue("enabled");
						if (enabled != null) {
							if (enabled.equals("true")) {

								String type = elem.getName();
								char originalStart = type.charAt(0);
								char upperStart = Character
										.toUpperCase(originalStart);
								type = upperStart + type.substring(1);

								if (!assetTypes.contains(type)) {

									assetTypes.add(type);

								}

							}

						}
					}

				} else {
					logger.info("it was null...");

					assetTypes.add(CreateTerseAction.TERSE_TITLE);
					assetTypes.add(CreateMessageAction.MESSAGE_TITLE);
					assetTypes
							.add(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL_BIG);
					assetTypes.add(CreateBookmarkAction.BOOKMARK_TITLE);
					assetTypes.add(CreateContactAction.CONTACT_TITLE);
					assetTypes.add(CreateKeywordsAction.KEYWORD_TITLE);
					assetTypes.add(CreateRssAction.RSS_TITLE);
					assetTypes.add(CreateFileAction.FILE_TITLE);
					assetTypes.add(CreateSphereAction.SPHERE_TITLE);

				}
				Vector<String> oneSphere = new Vector<String>();
				oneSphere.add(systemName);
				SearchInputWindow siw = new SearchInputWindow(
						UnifiedListeners.this.sF, selectedPane, selectedPane.getRawSession(),
						oneSphere, assetTypes, false);
				siw.layoutUIAndSetFocus();

			}
		};
		t.start();
	}

}
