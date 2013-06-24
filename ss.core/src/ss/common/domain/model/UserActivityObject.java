/**
 * 
 */
package ss.common.domain.model;

import java.util.Date;

import ss.client.ui.SupraSphereFrame;
import ss.common.DateUtils;

/**
 * @author roman
 *
 */
public class UserActivityObject extends DomainObject {

	/**
     * Threshold to show idle time  
     */
    private static final int IDLE_TIME_SHOW_THRESHOLD = 3 /* 60 */* 1000;
    
	/**
	 * 
	 */
    public static final String ITEM_TYPE = "user_activity";
    
	public static final String NEVER_DATE_TIME = "never";
	
	private String userLogin;
	
	private Date lastLoginDate;
	
	private Date lastActivityDate;
	
	private String sphereId;
	

	public UserActivityObject() {
		setLastLoginDate(null);
	}
	/**
	 * @return the userLogin
	 */
	public String getUserLogin() {
		return this.userLogin;
	}

	/**
	 * @param userLogin the userLogin to set
	 */
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	/**
	 * @return the lastLoginDate
	 */
	public Date getLastLoginDate() {
		return this.lastLoginDate;
	}

	/**
	 * @param lastLoginDate the lastLoginDate to set
	 */
	public void setLastLoginDate(final Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	/**
	 * @return the lastActivityDate
	 */
	public Date getLastActivityDate() {
		return this.lastActivityDate;
	}

	/**
	 * @param lastActivityDate the lastActivityDate to set
	 */
	public void setLastActivityDate(final Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @param sphereId the sphereId to set
	 */
	public void setSphereId(final String sphereId) {
		this.sphereId = sphereId;
	}
	
	public String getType() {
		return ITEM_TYPE;
	}
	
	/**
	 * @return
	 */
	public final boolean hasLastLoginDate() {
		return getLastLoginDate() != null;
	}

	/**
	 * @return
	 */
	public final boolean hasLastActivityDate() {
		return getLastActivityDate() != null;
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
		return hasLastActivityDate() ? SupraSphereFrame.INSTANCE.getCurrentDateTime().getTime() - getLastActivityDate().getTime() : 0;
	}
}
