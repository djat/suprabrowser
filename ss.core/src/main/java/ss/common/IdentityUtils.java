/**
 * 
 */
package ss.common;

import java.util.Hashtable;
import java.util.Random;
import java.util.UUID;


/**
 *
 */
public class IdentityUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(IdentityUtils.class);
	
	/**
	 * 
	 */
	private static final String THREAD_PREFIX = "T*";

	/**
	 * 
	 */
	private static final String COUNTER_DELIMETER = "-";

	private final Random longIdGenerator = new Random();

	private final Hashtable<String,Integer> groupToCounter = new Hashtable<String,Integer>();
	
	private static final IdentityUtils INSTANCE = new IdentityUtils();
	
	private IdentityUtils() {
	}
	
	/**
	 * Returns next runtime id identity in group
	 * @param groupName
	 * @return
	 */
	public static String getNextRuntimeId( String groupName ) {
		return INSTANCE.getNextRuntimeIdImpl(groupName);
	}
	
	/**
	 * Returns next runtime id identity in group
	 * @param groupName
	 * @return
	 */
	public static String getNextRuntimeId( String groupName, String delimeter ) {
		return INSTANCE.getNextRuntimeIdImpl(groupName, delimeter);
	}

	/**
	 * Returns next runtime id identity in group
	 * @param groupName
	 * @return
	 */
	public static String getNextRuntimeId( Class classObj ) {
		return INSTANCE.getNextRuntimeIdImpl(classObj.getName());
	}
	
	/**
	 * Returns next runtime id identity in group
	 * @param groupName
	 * @return
	 */
	public static String getNextRuntimeIdForThread( Class classObj ) {
		return getNextRuntimeIdForThread( classObj.getName() );
	}
	
	/**
	 * Returns next runtime id identity in group
	 * @param groupName
	 * @return
	 */
	public static String getNextRuntimeIdForThread( String groupName ) {
		return INSTANCE.getNextRuntimeIdImpl( THREAD_PREFIX + groupName );
	}
	
	/**
	 * @param string
	 * @return
	 */
	private String getNextRuntimeIdImpl(String groupName) {
		return getNextRuntimeIdImpl(groupName, COUNTER_DELIMETER);
	}

	/**
	 * @param groupName
	 */
	private synchronized String getNextRuntimeIdImpl(String groupName, String delimeter ) {
		Integer counter = this.groupToCounter.get(groupName);
		if ( counter == null ) {
			counter = 0;
		}
		++ counter;
		this.groupToCounter.put(groupName, counter );
		return groupName + ( counter > 1 ? delimeter + counter : "" );
	}
	
	
	/**
	 * Generate UUID
	 * @return generated UUID
	 */
	public static UUID generateUuid() {
		// TODO: think about jug uuid generator
		return UUID.randomUUID();
	}

	/**
	 * @return
	 */
	public static long generateLongId() {
		for(;;) {
			final long id = Math.abs( INSTANCE.longIdGenerator.nextLong() );
			if ( id == 0 ) {
				logger.warn( "generateLongId cautch unsatisfied id: " + id );
			}
			else {
				return id;
			}
		} 
	}
	
}
