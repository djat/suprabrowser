package ss.framework.install;

import ss.framework.domainmodel2.StringConvertor;

public class VersionFactory {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VersionFactory.class);
	
	
	private VersionFactory() {
	}
	
	public static Version safeParse( final String strVersion) {
		if ( strVersion == null || strVersion.length() == 0 ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Version is null or empty returns blank");
			}
			return new Version();
		}
		final String [] qualifiedParts = splitVersionParst( strVersion );
		if ( qualifiedParts.length == 0 ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Version is null or empty returns blank");
			}
			return new Version();
		}
		else{
			if ( qualifiedParts.length > 1 ) {
				final String operationSystem = qualifiedParts[ 0 ];
				int[] simpleParts = safeParseSimpleVersion( qualifiedParts[ 1 ] );
				if (logger.isDebugEnabled()) {
					logger.debug("Version is Qualified. Os is " + operationSystem );
				}
				return new QualifiedVersion( operationSystem, simpleParts );
			}
			else {
				return new Version( safeParseSimpleVersion( qualifiedParts[ 0 ] ) ); 
			}	
		}
	}

	/**
	 * @param strVersion
	 * @return
	 */
	private static String[] splitVersionParst(String strVersion) {
		int delimeterPos = strVersion.lastIndexOf( QualifiedVersion.OS_TO_VERSION_DELIMETER );
		if ( delimeterPos >= 0 ) {
			return new String[] { strVersion.substring( 0, delimeterPos ), strVersion.substring( delimeterPos + 1 ) };
		}
		else {
			return new String[] { strVersion };
		}
	}

	/**
	 * @param simplePart
	 * @return
	 */
	private static int[] safeParseSimpleVersion(String simplePart) {
		if ( simplePart == null || simplePart.length() == 0 ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Simple version is null or empty, returns blank" );
			}
			return new int[ 0 ];
		}
		final String [] strParts = simplePart.split( Version.PART_DELIMETER_TO_SPLIT );
		final int count = Math.min( strParts.length, Version.PART_COUNT );
		if (logger.isDebugEnabled()) {
			logger.debug( "Simple version \"" + simplePart +"\" has " + count + " parts, truncate: " + (strParts.length - count) );
		}
		final int [] parts = new int[ count ];
		for( int n = 0; n < count; ++ n ) {
			final int intPart = StringConvertor.stringToInt( strParts[ n ], 0 );
			if (logger.isDebugEnabled()) {
				logger.debug( "Version part #" + n + " is " + intPart );
			}
			parts[ n ] = Math.max( intPart, 0 );
		}
		return parts;
	}

}
