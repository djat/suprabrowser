package ss.common;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;

import ss.client.ui.SDisplay;
import ss.common.debug.DebugUtils;
import ss.framework.networking2.IHandlerExecutor;
import ss.framework.networking2.MessageHandlerRunnableAdaptor;

public class UiUtils {



	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UiUtils.class);

	public static final IHandlerExecutor UI_HANDLER_EXECUTOR = new UiHandlerExecutor();  
	
	private static boolean checkUnsafeUiCalls = false;
	
	/**
	 * @param runnable
	 */
	public static void swtInvoke(Runnable runnable) {
		if (runnable == null) {
			throw new ArgumentNullPointerException("runnable");
		}
		Display display = SDisplay.display.get();
		if (display.getThread() == Thread.currentThread() ) {
			runnable.run();
		} else {
			SDisplay.display.sync(runnable);			
		}		
	}

	/**
	 * @param name
	 * @return
	 */
	public static <T> T swtEvaluate(final Callable<T> evaluable) {
		if ( evaluable == null ) {
			throw new ArgumentNullPointerException( "evaluable" ); 
		}
		final AtomicReference<T> result = new AtomicReference<T>(); 
		SDisplay.display.sync( new Runnable() {
			public void run() {
				try {
					result.set( evaluable.call() );
				} catch (Exception ex) {
					logger.error( evaluable + " failed", ex );
				}
			}
			
		} );
		return result.get();
	}

	/**
	 * @param runnable
	 */
	public static void swtBeginInvoke(Runnable runnable) {
		if (runnable == null) {
			throw new ArgumentNullPointerException("runnable");
		}
		try {
			SDisplay.display.async(runnable);
		} catch (Throwable ex){
			logger.error("Error in trying async in Display", ex);
		}
	}

	/**
	 * 
	 */
	public static void checkUnsafeCallFromUi() {
		if ( isCheckUnsafeUiCalls() ) {
			if ( isCallFromUi() ) {
				final String message = "Unsafe call thread blocking operation from UI thread. Please refactor you code. Call stack " + DebugUtils.getCurrentStackTrace();
				logger.debug( message );
			}
		}
	}

	/**
	 * @return
	 */
	public static boolean isCallFromUi() {
		try {
			final Display display = SDisplay.display.get();
			return display != null && display.getThread() == Thread.currentThread();
		} catch (Throwable ex) {
			logger.error("Error occured with Display", ex);
			return false;
		}
	}

	/**
	 * @return the checkUnsafeUiCalls
	 */
	public static boolean isCheckUnsafeUiCalls() {
		return checkUnsafeUiCalls;
	}

	/**
	 * @param checkUnsafeUiCalls the checkUnsafeUiCalls to set
	 */
	public static void setCheckUnsafeUiCalls(boolean checkUnsafeUiCalls) {
		UiUtils.checkUnsafeUiCalls = checkUnsafeUiCalls;
	}


	/**
	 *
	 */
	private static class UiHandlerExecutor implements IHandlerExecutor {

		/* (non-Javadoc)
		 * @see ss.framework.networking2.IHandlerExecutor#beginExecute(ss.framework.networking2.MessageHandlerRunnableAdaptor)
		 */
		public void beginExecute(MessageHandlerRunnableAdaptor runnable) {
			swtBeginInvoke(runnable);
		}
	}


	/**
	 * @return
	 */
	public static Display getDisplay() {
		return SDisplay.display.get();
	}

}
