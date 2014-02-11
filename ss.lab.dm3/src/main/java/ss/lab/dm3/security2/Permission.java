package ss.lab.dm3.security2;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class Permission {

	public static final Permission ALL;
	public static final Permission NONE;
	public static final Permission CREATE;
	public static final Permission READ;
	public static final Permission UPDATE;
	public static final Permission DELETE;
	public static final Permission EXECUTE;
	public static final Permission CRUD;
	public static final Permission CRUDE;

	private static Permission[] PARSE_TABLE;
	
	static {
		ALL = new Permission(0xFFFFFFFF);
		PARSE_TABLE = new Permission[ 33 ];
		for( int n = 0; n < PARSE_TABLE.length; ++ n ) {
			PARSE_TABLE[ n ] = new Permission( n );
		}
		NONE = PARSE_TABLE[ 0 ];
		CREATE = PARSE_TABLE[ 1 ];
		READ = PARSE_TABLE[ 2 ];
		UPDATE = PARSE_TABLE[ 4 ];
		DELETE = PARSE_TABLE[ 8 ];
		EXECUTE = PARSE_TABLE[ 16 ];
		CRUD = PARSE_TABLE[ CREATE.or( READ.or( UPDATE.or( DELETE ) ) ).mask ];
		CRUDE = PARSE_TABLE[ CRUD.or( EXECUTE ).mask ];
	}

	private final int mask;
	
	/**
	 * @param mask
	 */
	public Permission(int mask) {
		super();
		this.mask = mask;
	}

	public static Permission parse( int mask ) {
		if ( mask >= 0  && mask < PARSE_TABLE.length ) {
			return PARSE_TABLE[ mask ];
		}
		else if ( mask == ALL.mask ) {
			return ALL;
		}
		else {
			return new Permission( mask	);
		}
	}
	
	/**
	 * @param permission
	 * @return
	 */
	public boolean contains(Permission permission) {
		return (this.mask & permission.mask) == permission.mask;
	}
	
	public boolean instresects(Permission permission) {
		return (this.mask & permission.mask) > 0;
	}

	public Permission or(Permission permission) {
		return parse( this.mask | permission.mask );
	}

	public Permission and(Permission permission) {
		return parse( this.mask & permission.mask );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.mask;
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
		final Permission other = (Permission) obj;
		if (this.mask != other.mask)
			return false;
		return true;
	}

	public int getMask() {
		return this.mask;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append( "mask", this.mask );
		return tsb.toString();
	}

	
}
