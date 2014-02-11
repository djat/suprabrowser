package ss.lab.dm3.security2;

import java.io.Serializable;

/**
 * @author Dmitry Goncharov
 */
public class SecurityId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1892521282911061339L;
	
	private static final String QUALIFIER_DELIMETER_FOR_SPLIT = "\\.";
	private static final String QUALIFIER_DELIMETER = ".";

	private final String qualifier;

	/**
	 * Can be null
	 */
	private final Long decimalId;
	
	/**
	 * Cashed values 
	 */
	private volatile SecurityId parentId = null;
	private volatile boolean parentIdCreated = false;	
		
	public SecurityId(String qualifier) {
		this(qualifier, null);
	}
	/**
	 * @param qualifier
	 * @param decimalId
	 */
	public SecurityId(String qualifier, Long decimalId) {
		super();
		this.qualifier = qualifier;
		this.decimalId = decimalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.decimalId == null) ? 0 : this.decimalId.hashCode());
		result = prime * result
				+ ((this.qualifier == null) ? 0 : this.qualifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SecurityId other = (SecurityId) obj;
		if (this.decimalId == null) {
			if (other.decimalId != null)
				return false;
		} else if (!this.decimalId.equals(other.decimalId))
			return false;
		if (this.qualifier == null) {
			if (other.qualifier != null)
				return false;
		} else if (!this.qualifier.equals(other.qualifier))
			return false;
		return true;
	}

	
	/**
	 * @return
	 */
	public synchronized SecurityId getParent() {
		if ( !this.parentIdCreated ) {
			this.parentIdCreated = true;
			this.parentId = createParentId();
		}
		return this.parentId;
	}
	
	public String getQualifier() {
		return this.qualifier;
	}

	public Long getDecimalId() {
		return this.decimalId;
	}
	
	/**
	 * @return
	 */
	private SecurityId createParentId() {
		if ( this.decimalId != null ) {
			return new SecurityId( this.qualifier, null );
		}
		String[] qualifierParts = this.qualifier.split( QUALIFIER_DELIMETER_FOR_SPLIT );
		if ( qualifierParts.length > 1 ) {
			StringBuilder sb = new StringBuilder( this.qualifier.length() );
			for( int n = 0; n < qualifierParts.length - 1;  ++n ) {
				if ( sb.length() > 0 ) {
					sb.append( QUALIFIER_DELIMETER );
				}
				sb.append( qualifierParts[ n ] );
			}
			return new SecurityId( sb.toString(), null );
		}
		else {
			return null;
		}
	}
	
	
	
}
