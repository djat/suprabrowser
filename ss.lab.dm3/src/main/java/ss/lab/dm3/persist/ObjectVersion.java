package ss.lab.dm3.persist;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class ObjectVersion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2785395494200920636L;

	private Long id;
	
	private String userName;
	
	private Date date;
	
	private int revision; 

	/**
	 * 
	 */
	public ObjectVersion() {
		super();
	}

	/**
	 * @param id
	 * @param userName
	 * @param date
	 * @param revision
	 */
	public ObjectVersion(Long id, String userName, Date date, int revision) {
		super();
		this.id = id;
		this.userName = userName;
		this.date = date;
		this.revision = revision;
	}


	public Long getId() {
		return this.id;
	}

	public String getUserName() {
		return this.userName;
	}

	public Date getDate() {
		return this.date;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getRevision() {
		return this.revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
			.append( "id", this.id )
			.append( "userName", this.userName )
			.append( "date", this.date )
			.append( "revision", this.revision )			
			.toString();
	}

	
	
}
