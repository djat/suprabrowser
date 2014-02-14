package ss.domainmodel;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.dom4j.Document;

import ss.common.ArgumentNullPointerException;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.util.DateTimeParser;
import ss.util.SupraXMLConstants;

public class Statement extends SupraSphereItem {

	/**
	 * 
	 */
	public static final String SPHERE_TYPE = "sphere";

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Statement.class);
	
	private final ISimpleEntityProperty giver = super
			.createAttributeProperty( "giver/@value" );
	
	private final ISimpleEntityProperty giverUsername = super
			.createAttributeProperty( "giver/@username" );

	private final ISimpleEntityProperty subject = super
			.createAttributeProperty( "subject/@value" );
	
	private final ISimpleEntityProperty threadType = super
			.createAttributeProperty( "thread_type/@value" );

	private final ISimpleEntityProperty votingModelType = super
			.createAttributeProperty( "voting_model/@type" );

	private final ISimpleEntityProperty votingModelDesc = super
			.createAttributeProperty( "voting_model/@desc" );

	private final ISimpleEntityProperty confirmed = super
			.createAttributeProperty( "confirmed/@value" );
	
	private final ISimpleEntityProperty passed = super
	.createAttributeProperty( "passed/@value" );

	private final ISimpleEntityProperty lastUpdated = super
			.createAttributeProperty( "last_updated/@value" );

	private final ISimpleEntityProperty threadId = super
			.createAttributeProperty( "thread_id/@value" );

	private final ISimpleEntityProperty originalId = super
			.createAttributeProperty( "original_id/@value" );

	private final ISimpleEntityProperty messageId = super
			.createAttributeProperty( "message_id/@value" );

	private final ISimpleEntityProperty moment = super
			.createAttributeProperty( "moment/@value" );

	private final ISimpleEntityProperty lastUpdatedBy = super
			.createAttributeProperty( "last_updated_by/@value" );

	private final ISimpleEntityProperty currentSphere = super
			.createAttributeProperty( "current_sphere/@value" );

	private final ISimpleEntityProperty interestTotal = super
			.createAttributeProperty( "interest/@total" );

	private final ISimpleEntityProperty responseId = super
			.createAttributeProperty( "response_id/@value" );

	private final ISimpleEntityProperty address = super
			.createAttributeProperty( "address/@value" );
	
	private final ISimpleEntityProperty tallyNumber = super
			.createAttributeProperty( "voting_model/tally/@number" );
	
	private final ISimpleEntityProperty tallyValue = super
			.createAttributeProperty( "voting_model/tally/@value" );
	
	private final ISimpleEntityProperty tallyMember = super
			.createAttributeProperty( "voting_model/tally/member/@value" );
	
	private final ISimpleEntityProperty tallyMemberVoteMoment = super
			.createAttributeProperty( "voting_model/tally/member/@vote_moment" );
		
	private final SphereLocationCollection sphereLocations = super
			.bindListProperty( new SphereLocationCollection(), "locations" );
	
	private final ISimpleEntityProperty originalBody = super
    .createTextProperty( "body/orig_body" );
	
	private final ISimpleEntityProperty status = super
	.createAttributeProperty( "status/@value" );
	
	private final ISimpleEntityProperty unique_id = super
	.createAttributeProperty( "unique_id/@value" );
	
	private final ISimpleEntityProperty forwarded_by = super
	.createAttributeProperty( "forwarded_by/@value" );
	
	private final ISimpleEntityProperty body = super
    .createTextProperty( "body" );
	
	private final ISimpleEntityProperty version = super
	.createAttributeProperty( "body/version/@value" );
	
	private final ISimpleEntityProperty workflowType = super
	.createAttributeProperty( "workflow/@type" );
	
	private final ISimpleEntityProperty requiredPercent = super
	.createAttributeProperty( "workflow/@percent" );
	
	private final ISimpleEntityProperty resultId = super
	.createAttributeProperty( "workflow/@result_id" );
	
	private final VotedMembersCollection votedMembers = super
	.bindListProperty( new VotedMembersCollection(), "voting_model/tally" );

	

	public Statement() {
		super("email");
	}

	public Statement(String desiredRootElementName) {
		super(desiredRootElementName);
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static Statement wrap(org.dom4j.Document data) {
		return XmlEntityObject.wrap(data, Statement.class);
	}

	/**
	 * Create Statement that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static Statement wrap(org.dom4j.Element data) {
		return XmlEntityObject.wrap(data, Statement.class);
	}

	/**
	 * Gets the statement original id
	 */
	public final String getOriginalId() {
		return this.originalId.getValue();
	}

	/**
	 * Sets the statement original id
	 */
	public final void setOriginalId(String value) {
		this.originalId.setValue(value);
	}

	/**
	 * Gets the statement id
	 */
	public final String getMessageId() {
		return this.messageId.getValue();
	}

	/**
	 * Sets the statement id
	 */
	public final void setMessageId(String value) {
		this.messageId.setValue(value);
	}

	/**
	 * Gets statement is confirmed
	 */
	public final boolean getConfirmed() {
		return this.confirmed.getBooleanValue(true);
	}

	/**
	 * Sets statement is confirmed
	 */
	public final void setConfirmed(boolean value) {
		this.confirmed.setBooleanValue(value);
	}
	
	public void setBody(String value) {
		this.body.setValue(value);
	}
	
	public String getBody() {
		return this.body.getValue();
	}

	/**
	 * Gets the statement giver contact name
	 */
	public final String getGiver() {
		return this.giver.getValue();
	}

	/**
	 * Sets the statement giver contact name
	 */
	public final void setGiver(String value) {
		this.giver.setValue(value);
	}
	
	/**
	 * Gets the statement giver username
	 */
	public final String getGiverUsername() {
		return this.giverUsername.getValue();
	}

	/**
	 * Sets the statement giver username
	 */
	public final void setGiverUsername(String value) {
		this.giverUsername.setValue(value);
	}

	/**
	 * Gets the statement subject
	 */
	public final String getSubject() {
		return this.subject.getValue();
	}

	/**
	 * Sets the statement subject
	 */
	public final void setSubject(String value) {
		this.subject.setValue(value);
	}

	/**
	 * Gets the statement voting model type
	 * 
	 * @return
	 */
	public final String getVotingModelType() {
		return this.votingModelType.getValue();
	}

	/**
	 * Sets the statement voting model type
	 */
	public final void setVotingModelType(String value) {
		this.votingModelType.setValue(value);
	}

	/**
	 * Gets the statement voting model desc
	 * 
	 * @return
	 */
	public final String getVotingModelDesc() {
		return this.votingModelDesc.getValue();
	}

	/**
	 * Sets the statement voting model desc
	 */
	public final void setVotingModelDesc(String value) {
		this.votingModelDesc.setValue(value);
	}

	/**
	 * Gets the statement thread type
	 * 
	 * @return
	 */
	public final String getThreadType() {
		return this.threadType.getValue();
	}

	/**
	 * Sets the statement thread type
	 */
	public final void setThreadType(String value) {
		this.threadType.setValue(value);
	}

	/**
	 * Gets the statement moment
	 * 
	 * @return
	 */
	public final String getMoment() {
		return this.moment.getValue();
	}

	/**
	 * Sets the statement moment
	 */
	public final void setMoment(String value) {
		this.moment.setValue(value);
	}

	/**
	 * Gets the statement last updated
	 * 
	 * @return
	 */
	public final String getLastUpdated() {
		return this.lastUpdated.getValue();
	}

	/**
	 * Sets the statement last updated
	 */
	public final void setLastUpdated(String value) {
		this.lastUpdated.setValue(value);
	}

	/**
	 * Gets the statement thread id
	 * 
	 * @return
	 */
	public final String getThreadId() {
		return this.threadId.getValue();
	}

	/**
	 * Sets the statement thread id
	 */
	public final void setThreadId(String value) {
		this.threadId.setValue(value);
	}

	/**
	 * Gets the statement current sphere
	 * 
	 * @return
	 */
	public final String getCurrentSphere() {
		return this.currentSphere.getValue();
	}

	/**
	 * Sets the statement current sphere
	 */
	public final void setCurrentSphere(String value) {
		// This should remove abmigous current sphere
		this.currentSphere.removeAllMatched();
		//
		this.currentSphere.setValue(value);
	}

	/**
	 * Gets the statement last updated by
	 * 
	 * @return
	 */
	public final String getLastUpdatedBy() {
		return this.lastUpdatedBy.getValue();
	}

	/**
	 * Sets the statement last updated by
	 */
	public final void setLastUpdatedBy(String value) {
		this.lastUpdatedBy.setValue(value);
	}

	/**
	 * Gets the statement interest total
	 * 
	 * @return
	 */
	public final String getInterestTotal() {
		return this.interestTotal.getValue();
	}

	/**
	 * Sets the statement interest total
	 */
	public final void setInterestTotal(String value) {
		this.interestTotal.setValue(value);
	}

	/**
	 * Gets the message response id
	 * 
	 * @return
	 */
	public final String getResponseId() {
		return this.responseId.getValue();
	}

	/**
	 * Sets the message response id
	 */
	public final void setResponseId(String value) {
		this.responseId.setValue(value);
	}

	/**
	 * Gets the statment address
	 */
	public final String getAddress() {
		return this.address.getValue();
	}

	/**
	 * Gets the statment address
	 */
	public final void setAddress(String value) {
		this.address.setValue(value);
	}
	
	public final String getTallyNumber() {
		return this.tallyNumber.getValue();
	}
	
	public final void setTallyNumber(String value) {
		this.tallyNumber.setValue(value);
	}
	
	public final String getTallyValue() {
		return this.tallyValue.getValue();
	}
	
	public final void setTallyValue(String value) {
		this.tallyValue.setValue(value);
	}
	
	public final void setTallyMember(String value) {
		this.tallyMember.setValue(value);
	}
	
	public final void setTallyMemberVoteMoment(String value) {
		this.tallyMemberVoteMoment.setValue(value);
	}
	
	public void setVersion(String value) {
		this.version.setValue(value);
	}
	
	public String getVersion() {
		return this.version.getValue();
	}
	
	/**
	 * Returns true if message tree document has expected type
	 * 
	 * @param expectedType
	 * @return
	 */
	private boolean documentHasType(String expectedType) {
		String type = getType();
		return (type == expectedType)
				|| (type != null && expectedType != null && expectedType
						.equals(type));
	}

	/**
	 * Retruns true if statement is bookmark
	 */
	public final boolean isBookmark() {
		return this.documentHasType("bookmark");
	}

	/**
	 * Retruns true if statement is filesystem
	 */
	public final boolean isFileSystem() {
		return this.documentHasType("filesystem");
	}

	/**
	 * Retruns true if statement is systemfile
	 */
	public final boolean isSystemFile() {
		return this.documentHasType("systemfile");
	}

	/**
	 * Retruns true if statement is rss
	 */
	public final boolean isRss() {
		return this.documentHasType("rss");
	}

	/**
	 * Retruns true if statement is message
	 */
	public final boolean isMessage() {
		return this.documentHasType("message");
	}

	/**
	 * Retruns true if statement is reply
	 */
	public final boolean isReply() {
		return this.documentHasType("reply");
	}

	/**
	 * Retruns true if statement is comment
	 */
	public final boolean isComment() {
		return this.documentHasType("comment");
	}

	/**
	 * Returns true if statement is terse
	 */
	public final boolean isTerse() {
		return this.documentHasType("terse");
	}

	/**
	 * Returns true if statement is contact
	 */
	public final boolean isContact() {
		return this.documentHasType("contact");
	}

	/**
	 * Returns true if statement is contact
	 */
	public final boolean isSphere() {
		return this.documentHasType(SPHERE_TYPE);
	}

	/**
	 * Returns true if statement is audio
	 */
	public final boolean isAudio() {
		return this.documentHasType("audio");
	}

	/**
	 * Returns true if messages tree document is keywords
	 */
	public final boolean isKeywords() {
		return this.documentHasType("keywords");
	}

	/**
	 * Returns true if messages tree document is file
	 */
	public final boolean isFile() {
		return this.documentHasType("file");
	}

	/**
	 * Returns true if messages tree document is source
	 */
	public final boolean isSource() {
		return this.documentHasType("source");
	}

	/**
	 * Returns true if messages tree document is library
	 */
	public final boolean isLibrary() {
		return this.documentHasType("library");
	}

	/**
	 * Returns true if messages tree document is persona
	 */
	public final boolean isPersona() {
		return this.documentHasType("persona");
	}

	/**
	 * Returns true if messages tree document is tool
	 */
	public final boolean isTool() {
		return this.documentHasType("tool");
	}
	
	/**
	 * Returns true if messages tree document is suprasphere
	 */
	public final boolean isSupraSphere() {
		return this.documentHasType("suprasphere");
	}
    
    /**
     * Returns true if messages tree document is external email
     */
    public final boolean isEmail() {
        return this.documentHasType(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL);
    }
    
    public final boolean isResult() {
        return this.documentHasType(SupraXMLConstants.TYPE_VALUE_RESULT);
    }
    
    public final boolean isSystemStateMessage() {
        return this.documentHasType(SupraXMLConstants.TYPE_VALUE_SYSTEM_STATE_MESSAGE);
    }
    
    /**
     * Returns true if messages tree document is membership
     */
    public final boolean isMembership() {
        return this.documentHasType("membership");
    }
    
    

	/**
	 * Add statement to the sphere
	 * 
	 * @param childStatement
	 *			not null child statement
	 */
	public final void bindChild(Statement childStatement) {
		if (childStatement == null) {
			throw new ArgumentNullPointerException("childStatement");
		}
		childStatement.setResponseId(getMessageId());
		childStatement.setThreadId(getThreadId());
		childStatement.setCurrentSphere(getCurrentSphere());
	}
	
	
	/**
	 * Sets the parent statement. See bindChild for details
	 * 
	 * @param parentStatement
	 */
	public void setParent(Statement parentSphere) {
		if (parentSphere == null) {
			//TODO:D1 mabe something else?
			setResponseId( null );
			setThreadId( getMessageId() );
		}
		else {
			parentSphere.bindChild(this);
		}		
	}

	/**
	 * Returns true if statement confirmed property is defined
	 * 
	 * @return
	 */
	public boolean isConfirmedDefined() {
		return this.confirmed.isValueDefined();

	}
	
	public SphereLocationCollection getSphereLocations() {
		return this.sphereLocations;
	}
	
	public SphereLocation getSphereLocation(int index) {
		return this.getSphereLocations().get(index);
	}
	
	public void addSphereLocation(SphereLocation item) {
		this.getSphereLocations().add(item);
	}
	
	public void removeSphereLocation(int index) {
		this.getSphereLocations().remove(getSphereLocation(index));
	}
	
	public void removeSphereLocation(SphereLocation item)  {
		this.getSphereLocations().remove(item);
	}
	
	public int getLocationsCount() {
		return this.getSphereLocations().getCount();
	}
	
	public void setOrigBody(String value) {
		this.originalBody.setValue(value);
	}
	
	public String getOrigBody() {
		return this.originalBody.getValue();
	}
	
	public void setPassed(boolean value) {
		this.passed.setBooleanValue(value);
	}
	
	public boolean getPassed() {
		return this.passed.getBooleanValue(true);
	}
	
	public void setStatus(String value) {
		this.status.setValue(value);
	}
	
	public String getStatus() {
		return this.status.getValue();
	}
	
	public void setUniqueId(String value) {
		this.unique_id.setValue(value);
	}
	
	public String getUniqueId() {
		return this.unique_id.getValue();
	}
	
	public void setForwardedBy(String value) {
		this.forwarded_by.setValue(value);
	}
	
	public String getForwardedBy() {
		return this.forwarded_by.getValue();
	}
	
	public String getDeliveryType() {
		return this.workflowType.getValueOrDefault( DeliveryFactory.INSTANCE.getDefaultDeliveryDescriptor().getType());
	}
	
	public void setWorkflowType(String value) {
		this.workflowType.setValue(value);
	}
	
	public String getResultId() {
		return this.resultId.getValue();
	}
	
	public void setResultId(String value) {
		this.resultId.setValue(value);
	}
	
	public double getRequiredPercentDouble() {
		return Double.parseDouble(this.requiredPercent.getValue());
	}
	
	public void setRequiredPercent(String value) {
		this.requiredPercent.setValue(value);
	}
	
	public void setRequiredPercent(double value) {
		this.requiredPercent.setValue(new Double(value).toString());
	}

	/* (non-Javadoc)
	 * @see ss.domainmodel.SupraSphereItem#hasValidType()
	 */
	@Override
	public boolean hasValidType() {
		return super.hasValidType();
	}
	
	public boolean isSystemMessage() {
		return getType().equals(SupraXMLConstants.TYPE_VALUE_SYSTEM_MESSAGE);
	}

	/**
	 * @return
	 */
	public String getMessageTitle() {
		return getSubject()	+ ", by " + getGiver();
	}

	/**
	 * @return
	 */
	public String getDisplayMomentLong() {
		Date date = DateTimeParser.INSTANCE.parseToDate(getMoment());
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
		String formattedDate = format.format(date);
	
		return formattedDate;
	}

	
	/**
	 * @return
	 */
	public String getDisplayMomentShort() {
		Date date = DateTimeParser.INSTANCE.parseToDate(getMoment());
		DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
		String formattedDate = format.format(date);
		String[] splittedDate = formattedDate.split(" ");
		return splittedDate[3]+" "+splittedDate[4];
	}

	/**
	 * @return
	 */
	public String toShortString() {
		return  "Message: " + getType() + " #" + getMessageId() + " [" + getSubject() + " in " + getCurrentSphere() + "]";
	}

	/**
	 * @param id 
	 */
	public void setMessageId(long id) {
		setMessageId(String.valueOf( id ) );
	}

	/**
	 * @param date
	 */
	public void setMoment(Date date) {
		setMoment( ss.common.DateUtils.dateToXmlEntityString(date) );		
	}

	/**
	 * @param date
	 */
	public void setLastUpdated(Date date) {
		setLastUpdated( ss.common.DateUtils.dateToXmlEntityString(date) );
	}

	/**
	 * @param contactName
	 * @return
	 */
	public boolean hasVoted(String contactName) {
		return getVotedMembers().getByContactName(contactName)!=null;
	}
	
	public VotedMembersCollection getVotedMembers() {
		return this.votedMembers;
	}

	/**
	 * @return
	 */
	public boolean isCommentable() {
		return isBookmark() || isRss() || isComment() || isMessage() || isEmail();
	}
	
	/**
	 * @return
	 */
	public boolean isReloadable() {
		return isBookmark() || isRss() || isComment();
	}
	
	public boolean isSystemMessageToHide() {
		return isSystemMessage()
				&& !SystemMessageStatement.wrap(getBindedDocument())
						.getSystemType().equals(
								SystemMessageStatement.SYSTEM_TYPE_ERROR);
	}

	/**
	 * @param contact
	 * @return
	 */
	public boolean isVotedBy(final String contact) {
		for(VotedMember member : getVotedMembers()) {
			String name = member.getName();
			if( (name != null) && (name.equals(contact)) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 *  Casts given statement to the class represented by the <b>clazz</b>
	 * @param <E>
	 * @param statement
	 * @param clazz
	 * @return
	 */
	public static <E extends Statement> E cast(final Statement statement, Class<E> clazz) {
		if(statement==null || statement.getBindedDocument()==null) {
			throw new ArgumentNullPointerException("statement");
		}
		try {
			Method wrapMethod = clazz.getMethod("wrap", Document.class);
			if(wrapMethod!=null) {
				return clazz.cast(wrapMethod.invoke(null, statement.getBindedDocument()));
			} else {
				logger.error("No wrap method in "+clazz);
			}
		} catch (SecurityException ex) {
			logger.error("Cannot get access to wrap method from class "+clazz, ex);
		} catch (NoSuchMethodException ex) {
			logger.error("Wrap-method doesn't exist in class "+clazz, ex);
		} catch (IllegalArgumentException ex) {
			logger.error("Illegal argument in wrap-method", ex);
		} catch (IllegalAccessException ex) {
			logger.error("Illegal access to wrap-method from class "+clazz, ex);
		} catch (InvocationTargetException ex) {
			logger.error("Invocation exception caused during wrap document", ex);
		}
		return null;
	}
	
	public static void main(String[] args) {
		Statement statement = new BookmarkStatement();
		statement.setSubject("Test Statement");
		
		System.out.println("casted statement class:"+Statement.cast(statement, TerseStatement.class).getClass());
		System.out.println("casted statement class:"+Statement.cast(statement, FileStatement.class).getClass());
		System.out.println("casted statement class:"+Statement.cast(null, FileStatement.class).getClass());
	}

}