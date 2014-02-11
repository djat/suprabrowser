package ss.lab.dm3.snapshoots.objects;

import java.io.Serializable;

/**
 * @author Dmitry Goncharov
 *
 */
public class OnlineUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4735741404466763152L;
	
	private Long accountId;
	
	private int connectionsCount;
	
	/**
	 * @param accountId
	 * @param connectionsCount
	 */
	public OnlineUser(Long accountId, int connectionsCount) {
		super();
		this.accountId = accountId;
		this.connectionsCount = connectionsCount;
	}

	public Long getAccountId() {
		return this.accountId;
	}

	public int getConnectionsCount() {
		return this.connectionsCount;
	}
	
	
}
