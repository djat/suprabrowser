package ss.common;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ThreadUtils.class);
	
	/**
	 * 
	 * @param runnable
	 * @param threadClass
	 */
	public static Thread startDemon( Runnable runnable, Class threadClass ) {
		return start(runnable, threadClass, true );
	}
	
	/**
	 * 
	 * @param runnable
	 * @param threadClass
	 */
	public static Thread startDemon( Runnable runnable, String threadGroupName) {
		return start(runnable, threadGroupName, true );
	}

	/**
	 * 
	 * @param runnable
	 * @param threadClass
	 */
	public static Thread startDemon( Runnable runnable) {
		return start(runnable, runnable.getClass() );
	}

	/**
	 * @param runnable
	 * @param class
	 */
	public static Thread start(Runnable runnable, Class threadClass ) {
		return start(runnable, threadClass, false );		
	}
	
	/**
	 * @param runnable
	 * @param class
	 */
	public static Thread start(Runnable runnable, String threadGroupName ) {
		return start(runnable, threadGroupName, false );		
	}
	

	private static Thread start(Runnable runnable, Class threadClass, boolean demon) {
		return start(runnable, threadClass.getSimpleName(), demon);
	}
	
	private static Thread start( Runnable runnable, String threadGroupName, boolean demon ) {
		if ( runnable == null ) {
			throw new ArgumentNullPointerException( "runnable" );
		}
		Thread thread = createThread(runnable, threadGroupName, demon);
		thread.start();
		return thread;
	}
	
	private static Thread createThread(Runnable runnable, String threadGroup, boolean demon) {
		Thread thread = new Thread( runnable );
		thread.setName( IdentityUtils.getNextRuntimeIdForThread(threadGroup) );
		thread.setDaemon( demon );
		return thread;
	}
	
	public static ThreadPoolExecutor createOneByOneDemonExecutor( final String name ) {
		return createOneByOneExecutor( name, true );
	}
	
	public static ThreadPoolExecutor createOneByOneExecutor( final String name, final boolean demon ) {
		return new ThreadPoolExecutor( 1, 1,
                10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>( 512 ),
                new ThreadFactory() {
					public Thread newThread(Runnable r) {
						logger.info( "Create thread "  + name + " is Demon " + demon );
						return createThread( r, name, demon );
					}
				}); 
	}
	
	/**
	 * @param runnable
	 */
	public static void start(Runnable runnable) {
		start(runnable, runnable.getClass() );
	}

	/**
	 * 
	 */
	public static void initializeDefaultExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler( new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				logger.error( "UncaughtException from " + t, e );
			}
		});		
	}
}
