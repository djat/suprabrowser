package ss.framework.install;

import ss.common.ArgumentNullPointerException;

public class Version implements Comparable {

	/**
	 * 
	 */
	static final int PART_COUNT = 4;


	private static final int BLANK_VALUE = 0;
	/**
	 * 
	 */
	private static final int MAJOR_INDEX = 0;

	/**
	 * 
	 */
	private static final int MINOR_INDEX = 1;

	/**
	 * 
	 */
	private static final int BUILD_INDEX = 2;

	/**
	 * 
	 */
	private static final int REVISION_INDEX = 3;

	private static final char PART_DELIMETER = '.';

	public final static String PART_DELIMETER_TO_SPLIT = "\\"+ PART_DELIMETER;
	
	private final int [] parts = new int[PART_COUNT];
	
	/**
	 * @param parts
	 */
	public Version(int ... parts) {
		super();
		blank();
		if ( parts == null ) {
			throw new ArgumentNullPointerException( "parts" );
		}
		if ( parts.length > PART_COUNT ) {
			throw new IllegalArgumentException( "Version should have " + PART_COUNT + " parts." );
		}
		for( int n = 0; n < parts.length; ++ n ) {
			set( n, parts[ n ] );
		}
	}

	/**
	 * 
	 */
	private void blank() {
		for( int n = 0; n < PART_COUNT; ++ n ) {
			set( n, BLANK_VALUE );
		}
	}

	/**
	 * @param i
	 * @return
	 */
	private int get(int i) {
		return this.parts[ i ];
	}
	
	/**
	 * @param i
	 */
	private void set(int i, int value ) {
		if ( value < 0 ) {
			throw new IllegalArgumentException( "Version part can be less that 0. Actual value is " + value );
		}
		this.parts[ i ] = value;
	}
	
	public int getMajor() {
		return get( MAJOR_INDEX );
	}

	public int getMinor() {
		return get( MINOR_INDEX );
	}
	
	public int getBuild() {
		return get( BUILD_INDEX );
	}

	public int getRevision() {
		return get( REVISION_INDEX );
	}

	public void setMajor(int major) {
		set( MAJOR_INDEX, major );
	}

	public void setMinor(int minor) {
		set( MINOR_INDEX, minor );
	}

	public void setBuild(int build) {
		set( BUILD_INDEX, build );
	}
	public void setRevision(int revision) {
		set( REVISION_INDEX, revision );
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final int significantCount = Math.max( getSignificantCount(), 1 );
		for( int n = 0; n < significantCount; ++ n ) {
			if ( sb.length() > 0 ) {
				sb.append( PART_DELIMETER );				 
			}
			sb.append( get( n ) );
		}
		return sb.toString();
	}

	/**
	 * @return
	 */
	private int getSignificantCount() {
		int count = 0;
		for( int n = 0; n < PART_COUNT;++n){
			if ( get( n ) > 0 ) {
				count = n + 1;
			}
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if ( this == o ) {
			return 0;
		}
		if ( o instanceof Version ) {
			final Version other = (Version) o;
			for( int n = 0; n < PART_COUNT; ++ n ) {
				final int thisPart = get( n );
				final int otherPart = other.get( n );
				if ( thisPart < otherPart ) {
					return -1;
				}
				else if ( thisPart > otherPart ) {
					return 1;
				}
			}
			return 0;
		}
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public final int[] getParts() {
		final int[] parts = new int[ PART_COUNT ];
		System.arraycopy( this.parts, 0, parts, 0, parts.length);
		return parts;
	}
	
}
