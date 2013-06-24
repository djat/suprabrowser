package ss.domainmodel;


import java.util.Date;

import ss.client.ui.SupraSphereFrame;
import ss.common.DateUtils;
import ss.framework.entities.ISimpleEntityProperty;


public class UserActivity extends SupraSphereItem {
	
    
    /**
     * Threshold to show idle time  
     */
    private static final int IDLE_TIME_SHOW_THRESHOLD = 3 /* 60 */* 1000;
    
	/**
	 * 
	 */
	public static final String NEVER_DATE_TIME = "never";

	/**
	 * 
	 */
	private static final String ROOT_ELEMENT_NAME = "user_activity";

	/**
	 * 
	 */
	public static final String ITEM_TYPE = "user_activity";

	/**
	 * Create Presence that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static UserActivity wrap(org.dom4j.Document data) {
		return ss.framework.entities.xmlentities.XmlEntityObject.wrap(data, UserActivity.class);
	}

	/**
	 * Create Presence that wraps xml
	 */
	@SuppressWarnings("unchecked")
	public static UserActivity wrap(org.dom4j.Element data) {
		return ss.framework.entities.xmlentities.XmlEntityObject.wrap(data, UserActivity.class);
	}
	
	private final ISimpleEntityProperty userLogin = super
		.createAttributeProperty( "user_login/@value" );
	
	private final ISimpleEntityProperty lastLoginDate = super
		.createAttributeProperty("last_login_date/@value");
	
	private final ISimpleEntityProperty lastActivityDate = super
			.createAttributeProperty("last_activity_date/@value");

	
	
	private final ISimpleEntityProperty sphereId = super
			.createAttributeProperty("sphere_id/@value");


	
	public UserActivity() {
		super(ROOT_ELEMENT_NAME);
		super.setType( ITEM_TYPE );
		setLastLoginDate( NEVER_DATE_TIME );
	}
	
	public UserActivity( String sphereId, String userLogin ) {
		this();
		setSphereId(sphereId);
		setUserLogin(userLogin);		
	}
	
	/**
	 * Gets the userLogin
	 */
	public final String getUserLogin() {
		return this.userLogin.getValue();
	}

	/**
	 * Gets the userLogin
	 */
	public final void setUserLogin(String value) {
		this.userLogin.setNotNullValue( value );
	}
	
	/**
	 * Gets the lastLoginDate
	 */
	public final String getLastLoginDate() {
		return this.lastLoginDate.getValue();
	}

	/**
	 * Gets the lastLoginDate
	 */
	public final void setLastLoginDate(String value) {
		this.lastLoginDate.setValue(value);
		setLastActivityDate(value);
	}
	
	/**
	 * Gets the lastLoginDate
	 */
	public final void setLastLoginDate(long value) {
		this.lastLoginDate.setValue(Long.toString(value));
		setLastActivityDate(value);
	}


	/**
	 * Gets the sphere id
	 */
	public final String getSphereId() {
		return this.sphereId.getValue();
	}

	/**
	 * Gets the sphere id
	 */
	public final void setSphereId(String value) {
		this.sphereId.setNotNullValue(value);
	}
	
	/**
	 * Gets the lastActivityDate
	 */
	public final Date getLastActivityDateObject() {
		return hasLastActivityDate() ? DateUtils.canonicalStringToDate( getLastActivityDate() ) : null;		
	}

	/**
	 * @return
	 */
	public String getLastActivityDate() {
		return this.lastActivityDate.getValue();
	}

	/**
	 * Gets the lastActivityDate
	 */
	public final void setLastActivityDate(String value) {
		this.lastActivityDate.setValue(value);
	}
	
	public final void setLastActivityDate(long value) {
		this.lastActivityDate.setValue(Long.toString(value));
	}

	/**
	 * @return
	 */
	public final boolean hasLastLoginDate() {
		return getLastLoginDate() != null && !getLastLoginDate().equals(NEVER_DATE_TIME);
	}

	/**
	 * @return
	 */
	public final boolean hasLastActivityDate() {
		return getLastActivityDate() != null && !getLastActivityDate().equals(NEVER_DATE_TIME);
	}
	
	 
	public final boolean isIdle() {
		final long idleTime = getIdleTimeTicks(); 
		return idleTime > IDLE_TIME_SHOW_THRESHOLD;
	}

	/**
	 * @return
	 */
	public final String getIdleTime() {
		return DateUtils.timeSpanToPrettyString( getIdleTimeTicks() );
	} 
	
	private long getIdleTimeTicks() {
		return hasLastActivityDate() ? SupraSphereFrame.INSTANCE.getCurrentDateTime().getTime() - getLastActivityDateObject().getTime() : 0;
	}
}
