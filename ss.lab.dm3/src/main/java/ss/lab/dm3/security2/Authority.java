package ss.lab.dm3.security2;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class Authority implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3281643628199602115L;
	
	private static HashMap<Long, Authority> ID_TO_AUTHORITY = new HashMap<Long, Authority>();
	
	public static final Authority SYSTEM = new Authority( 1L, "#System" );
	public static final Authority ADMINISTRATOR = new Authority( 2L, "#Administrator" );
	public static final Authority MODERATOR = new Authority( 3L, "#Moderator" );
	public static final Authority USER = new Authority( 4L, "#User" );
	public static final Authority OBSERVER = new Authority( 5L, "#Observer" );
	
	public static final Long FIRST_CUSTOM_AUTHORITY_ID = 100L;

	private final Long id;
	
	private final String name;

	/**
	 * @param id
	 * @param name
	 */
	public Authority(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
		if ( isBuiltin() ) {
			if ( ID_TO_AUTHORITY.containsKey( this.id ) ) {
				throw new IllegalStateException( "Can't builtin add athority " + this + " because it already exists" );
			}
			ID_TO_AUTHORITY.put( this.id, this );
		}
	}
	
	private boolean isBuiltin() {
		return this.id < FIRST_CUSTOM_AUTHORITY_ID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
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
		final Authority other = (Authority) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "id", this.id )
		.append( "name", this.name )
		.toString();
	}

	public String getDisplayName() {
		return this.name;
	}

	public Long getId() {
		return this.id;
	}

	/**
	 * @param authorityId
	 * @return
	 */
	public static Authority getBuitin(Long authorityId) {
		if ( authorityId >= FIRST_CUSTOM_AUTHORITY_ID ) {
			throw new IllegalArgumentException( "Authority is custom " + authorityId );
		}
		final Authority authority = ID_TO_AUTHORITY.get(authorityId);
		if ( authority == null ) {
			throw new IllegalArgumentException( "Can't find builtin with id " + authorityId );
		}
		return authority;
	}
	
}
