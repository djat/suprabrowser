/**
 * 
 */
package ss.client.event;

import java.util.Hashtable;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.messagedeliver.popup.PopUpController;
import ss.client.ui.messagedeliver.popup.SOptionPane;
import ss.common.UiUtils;
import ss.domainmodel.Statement;
import ss.domainmodel.WorkflowResponse;
import ss.domainmodel.workflow.ConfirmReceiptDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.util.SessionConstants;

	/**
	 * @author roman
	 *
	 */
public final class SOptionPaneButtonListener implements Listener, KeyListener {

		private final Shell shell;
		private final Text tf;
		private final Statement statement;
		private final SOptionPane so;
		private final Document doc;
		private final Hashtable paneSession;
		
		@SuppressWarnings("unused")
		private static final org.apache.log4j.Logger logger = ss.global.SSLogger
				.getLogger(SOptionPaneButtonListener.class);

		/**
		 * @param shell
		 */
		public SOptionPaneButtonListener(SOptionPane so) {
			this.shell = so.getShell();
			this.tf = so.getInputField();
			this.statement = so.getStatement();
			this.doc = so.getDoc();
			this.paneSession = so.getMessagesPaneSession();
			this.so = so;
			if (logger.isDebugEnabled()){
				logger.debug("S Option Pane Button Listenter created");
				if (this.tf == null){
					logger.debug("Text field is null");
				} else {
					logger.debug("Text field is not null");
				}
				if (this.statement != null){
					logger.debug("The incoming statement is: " + this.statement.getBindedDocument().asXML());
				} else {
					logger.debug("The incoming statement is null");
				}
			}
		}
		
		public void keyPressed(KeyEvent e) {
			if (e.character == SWT.CR) {
				process();
			}
		}

		public void keyReleased(KeyEvent e) {
			
		}

		public void handleEvent(Event event) {
			process();
		}
		
		private void process(){
			if (logger.isDebugEnabled()){
				logger.debug("Handle Event performed");
			}
			synchronized (PopUpController.INSTANCE.popupsMutex) {
				Thread t = new Thread() {
		
					public void run() {
						SOptionPaneButtonListener listener = SOptionPaneButtonListener.this;
						try {	
							if(listener.tf!=null && !(listener.tf.getText().trim().length()>0)){
								processTextIsNull();	
							} else {
								processTextNotNull();
							}
						} catch (Exception e) {		
							logger.error(e.getMessage(), e);	
						}
					}			
				};
				UiUtils.swtBeginInvoke(t);	
			}	
		}
		
		@SuppressWarnings("unchecked")
		private void processTextIsNull() {
			if (logger.isDebugEnabled()){
				logger.debug("Processing when text is null");
			}

			String sphere_id = this.statement
					.getCurrentSphere();

			logger.info("Will update to : "
							+ sphere_id);

			Hashtable newsess = (Hashtable) this.paneSession
					.clone();
			newsess.put("sphere_id",
					sphere_id);

			SupraSphereFrame.INSTANCE.client
					.updateDocument(
							newsess,
							this.statement
									.getMessageId(),
									this.doc);					
			PopUpController.INSTANCE.setCurrent_popup(this.shell
					.getLocation());

			if (logger.isDebugEnabled()){
				logger.debug("Shell Disposed");
			}
			this.shell.dispose();
		}
		
		
		@SuppressWarnings("unchecked")
		private void processTextNotNull() {
			if (logger.isDebugEnabled()){
				logger.debug("Processing when text is not null");
			}

			String sphere_id = this.statement.getCurrentSphere();
			
			Hashtable newsess = (Hashtable) this.paneSession.clone();
			newsess.put("sphere_id",sphere_id);
			newsess.put("delivery_type","normal");
			
			Document pubdoc = this.so.messageDoc();
			if (logger.isDebugEnabled()){
				if (pubdoc != null) {
					logger.debug("PubDoc before is: " + pubdoc.asXML());
				} else {
					logger.debug("PubDoc before is null");
				}
			}

			pubdoc.getRootElement().addElement("confirmed").addAttribute("value","false");
			
			if(this.tf!=null) {
				if (logger.isDebugEnabled()){
					logger.debug("Text field is not null, processing...");
				}
				Statement statement = Statement.wrap(pubdoc);
				statement.setWorkflowType(DeliveryFactory.INSTANCE.getDeliveryTypeByDeliveryClass(ConfirmReceiptDelivery.class));
				
				SupraSphereFrame.INSTANCE.client.updateDocument(newsess,this.statement.getMessageId(),this.doc);
				newsess.put("delivery_type","confirm_receipt");
			} else {
				if (logger.isDebugEnabled()){
					logger.debug("Text field is null, processing...");
				}
				newsess.put(SessionConstants.RESULT_ID, this.statement.getResultId());
				newsess.put(SessionConstants.CURRENT_SPHERE, this.statement.getCurrentSphere());
				newsess.put(SessionConstants.WORKFLOW_DOC, createWorkflowResponse().getBindedDocument());
				
				pubdoc.getRootElement().addElement("workflowResponse");
				pubdoc.getRootElement().element("confirmed").addAttribute("value", "true");
			}
			
			if (logger.isDebugEnabled()){
				if (pubdoc != null) {
					logger.debug("PubDoc after is: " + pubdoc.asXML());
					logger.debug("session: " + newsess);
				} else {
					logger.debug("PubDoc after is null");
				}
			}
			SupraSphereFrame.INSTANCE.client.publishTerse(newsess,pubdoc);

			final String display_name = SupraSphereFrame.INSTANCE.client
					.getVerifyAuth()
					.getDisplayName(
							sphere_id);

			SupraSphereFrame.INSTANCE.tabbedPane.selectTabByTitle(display_name);			
			PopUpController.INSTANCE.setCurrent_popup(this.shell.getLocation());

			if (logger.isDebugEnabled()){
				logger.debug("Shell Disposed");
			}
			this.shell.dispose();
		}

		
		private WorkflowResponse createWorkflowResponse() {
			if (logger.isDebugEnabled()){
				logger.debug("Creating workflow responce");
			}

			WorkflowResponse wr = new WorkflowResponse();
			wr.setContactName((String)SupraSphereFrame.INSTANCE.client.session.get(SessionConstants.REAL_NAME));
			wr.setLoginName((String)SupraSphereFrame.INSTANCE.client.session.get(SessionConstants.USERNAME));
			wr.setValue(this.so.getChoice());
			if (logger.isDebugEnabled()){
				logger.debug("Workflow responce: " + wr.getBindedDocument().asXML());
			}
			return wr;
		}
}
