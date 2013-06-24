/**
 * 
 */
package ss.framework.errorreporting;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
public class SessionInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3867006132094850120L;
		
	private final long id;
	
	private final Date creationDate;

	private final String sessionKey;

	private final String userName;

	private final String context;	

	/**
	 * @param id
	 * @param currentTimeMillis
	 * @param createSessionInfo
	 */
	public SessionInformation(long id, Date creationDate, ICreateSessionInformation createSessionInfo) {
		this.id = id;
		this.creationDate = creationDate;
		this.sessionKey = createSessionInfo.getSessionKey();
		this.userName = createSessionInfo.getUserName();
		this.context = createSessionInfo.getContext();
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return this.context;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * @return the sessionKey
	 */
	public String getSessionKey() {
		return this.sessionKey;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}
	
	
}
