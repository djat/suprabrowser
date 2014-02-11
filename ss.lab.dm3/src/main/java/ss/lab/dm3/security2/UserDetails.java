package ss.lab.dm3.security2;

import java.io.Serializable;

/**
 * @author Dmitry Goncharov
 */
public class UserDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5914085318664743983L;

	private Long id;
	
	private String accountName;

	/**
	 * 
	 */
	public UserDetails() {
		super();
	}

	/**
	 * @param id
	 * @param accountName
	 */
	public UserDetails(Long id, String accountName) {
		super();
		this.id = id;
		this.accountName = accountName;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}	
	
}
