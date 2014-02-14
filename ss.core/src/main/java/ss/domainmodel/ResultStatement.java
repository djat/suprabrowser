/**
 * 
 */
package ss.domainmodel;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import ss.client.localization.LocalizationLinks;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.global.SSLogger;


/**
 * @author roman
 *
 */
public class ResultStatement extends Statement {
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ResultStatement.class);
	
	private static final String IS_PASSED = "RESULTSTATEMENT.IS_PASSED";
	private static final String YES = "RESULTSTATEMENT.YES";
	private static final String NO = "RESULTSTATEMENT.NO";
	private static final String SUBJECT = "RESULTSTATEMENT.SUBJECT";
	private static final String WORKFLOW_TYPE = "RESULTSTATEMENT.WORKFLOW_TYPE";
	private static final String VOTING_ENDED = "RESULTSTATEMENT.VOTING_OVER";
	private static final String MEMBERS_COUNT = "RESULTSTATEMENT.MEMBER_COUNT";
	private static final String REPLY_COUNT = "RESULTSTATEMENT.REPLY_COUNT";
	private static final String VOTING_ON = "RESULTSTATEMENT.VOTING_ON";
	private static final String UPPER_PASSED = "RESULTSTATEMENT.UPPER_PASSED";
	private static final String NOT_PASSED_KEY = "RESULTSTATEMENT.NOT_PASSED";
	
	private static final ResourceBundle bundle = 
		ResourceBundle.getBundle(LocalizationLinks.DOMAINMODEL_RESULTSTATEMENT);
	
	public static final String VOTING_IS_ON_STRING = bundle.getString(VOTING_ON);
	public static final String PASSED = bundle.getString(UPPER_PASSED);
	public static final String NOT_PASSED = bundle.getString(NOT_PASSED_KEY);

	

	private final WorkflowResponseCollection responses = super
	.bindListProperty(new WorkflowResponseCollection());

	private final ISimpleEntityProperty parentDelivery = super
	.createAttributeProperty( "parent_delivery/@value" );

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ResultStatement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, ResultStatement.class);
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static ResultStatement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, ResultStatement.class);
	}
	
	public WorkflowResponseCollection getResponseCollection() {
		return this.responses;
	}

	public String getParentWorkflowType() {
		return this.parentDelivery.getValue();
	}
	
	public void setParentWorkflowType(String value) {
		this.parentDelivery.setValue(value);
	}
	
	
	public AbstractDelivery getModel() {
		return DeliveryFactory.INSTANCE.getDeliveryForSphereByType(getCurrentSphere(), getParentWorkflowType());
	}
	
	public String getHtmlText() {
		String text = "<body>";
		
		AbstractDelivery delivery = getModel();
		
		if(delivery.getMemberCollection().getCount()-1 == getResponseCollection().getCount()) {
			text += "<div>"+bundle.getString(VOTING_ENDED)+"</div>";
		} else {
			text += "<div>"+bundle.getString(VOTING_ON)+"</div>";
		}
		text += "<div>"+bundle.getString(SUBJECT)+": "+getOrigBody()+"</div>";
		text += "<div>"+bundle.getString(WORKFLOW_TYPE)+": "+ delivery.getDisplayName() +"</div>";
		
		text += "<div>"+bundle.getString(MEMBERS_COUNT)+": "+(delivery.getMemberCollection().getCount()-1)+"</div>";
		if(delivery.getMemberCollection().getCount()-1 != getResponseCollection().getCount()) {
			text += "<div>"+bundle.getString(REPLY_COUNT)+": "+getResponseCollection().getCount()+"</div>";
		}
				
		if (getResponseCollection().getCount()>0) {
			text = delivery.computeResultStatistics( this );
			text += "<br>";
		}
		
		for(WorkflowResponse response : getResponseCollection()) {
			text += "<div>"+response.getContactName()+": "+response.getValue()+"</div>";	
		}
		
		String passed = bundle.getString(NO);
		if( delivery.isPassed( this ) ) {
			passed = bundle.getString(YES);
		}
		text += "<br><div>"+bundle.getString(IS_PASSED)+": "+passed+"</div></body>";
		
		return text;
	}

	/**
	 * @return
	 */
	public boolean isVotingOn() {
		return getSubject().equals(bundle.getString(VOTING_ON));
	}

	/**
	 * @return
	 */
	public AbstractDelivery getModelForServer() {
		return DeliveryFactory.INSTANCE.getDeliveryForSphereByTypeForServer(getCurrentSphere(), getParentWorkflowType());
	}
}
