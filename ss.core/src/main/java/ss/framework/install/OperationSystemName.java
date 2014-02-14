package ss.framework.install;

import ss.common.EnumManager;


public final class OperationSystemName {
	
	public enum OsFamily {
		UNKNOWN,
		LINUX,
		WIN32,
		OSX,
		ANY;
		
		/**
		 * @param strValue
		 * @return
		 */
		public static OsFamily parse(String strValue) {
			if ( strValue == null ) {
				strValue = "";
			}
			final String strToMatch = strValue.toLowerCase();
			if ( strToMatch.startsWith( "linux" ) ) {
				return LINUX; 
			}
			else if ( strToMatch.startsWith( "win" ) ) {
				return WIN32;
			}
			else if ( strToMatch.equals("osx") ||
					  strToMatch.startsWith( "mac" ) ) {
				return OSX;
			}
			else if ( strToMatch.equals( "any" ) ) {
				return ANY;
			}
			else {
				return UNKNOWN;
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			 return EnumManager.SHARED_INSTANCE.getCanonicalStringValue( this );
		}
		
		
	}

	public static final OperationSystemName ANY = new OperationSystemName( OsFamily.ANY );
	
	private final String osName;
	
	/**
	 * @param osName
	 */
	public OperationSystemName(final String osName) {
		super();
		this.osName = osName != null ? osName : "";
	}

	public OperationSystemName(OsFamily osFamily) {
		this( osFamily.toString() );
	}

	public OsFamily getFamily() {
		return OsFamily.parse(this.osName);
	}

	public static OperationSystemName getFromSystem() { 
		String osNameStr = System.getProperty( "os.name" );
		return new OperationSystemName(osNameStr);
	}

	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.osName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof OperationSystemName ) {
			return this.getFamily().equals( ((OperationSystemName)obj).getFamily() );
		}
		return super.equals(obj);
	}
	
	
	
	
}
