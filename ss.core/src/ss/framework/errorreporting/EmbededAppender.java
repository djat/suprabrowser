/**
 * 
 */
package ss.framework.errorreporting;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import ss.common.ReflectionUtils;
import ss.common.ReflectionUtils.CannotCreateObjectException;
import ss.common.debug.DebugUtils;

/**
 * 
 */
public class EmbededAppender extends AppenderSkeleton {

	private boolean firstRunOfTransportCreation = true;

	private String transportClassName = null;

	private ILogEventTransport logEventTransport;

	private boolean enabled = false;

	private final Object appendMutex = new Object();

	private volatile int selfCalls = 0;

	/**
	 * @param logEventTransport
	 */
	public EmbededAppender() {
		this(null, false );
	}

	public EmbededAppender(String transportClassName, boolean enabled ) {
		super();
		this.transportClassName = transportClassName;
		this.enabled = enabled;
	}

	protected void append(LoggingEvent loggingEvent) {
		if ( isEnabled()
				&& (loggingEvent.getLevel() == Level.ERROR || loggingEvent
						.getLevel() == Level.FATAL)) {
			synchronized (this.appendMutex) {
				if (this.selfCalls > 0) {
					if (this.selfCalls == 1) {
						LogLog.error("Can't append " + loggingEvent.toString());
						return;
					} else {
						System.err
								.println("Detect possible self looping. Event "
										+ loggingEvent + ". Stack: "
										+ DebugUtils.getCurrentStackTrace());
					}
				} else {
					++this.selfCalls;
					try {
						ILogEventTransport transport = getLogEventTransport();
						if (transport != null) {
							transport.send(loggingEvent);
						}
					} finally {
						--this.selfCalls;
					}
				}
			}
		}
	}

	/**
	 * @return
	 */
	private ILogEventTransport getLogEventTransport() {
		if (this.logEventTransport == null) {
			this.logEventTransport = createTransport();
		}
		return this.logEventTransport;
	}

	/**
	 * @return
	 */
	private synchronized ILogEventTransport createTransport() {
		if (!this.firstRunOfTransportCreation) {
			return null;
		}
		this.firstRunOfTransportCreation = true;
		if (this.transportClassName == null) {
			LogLog.error("Transport name is null");
			return null;
		}
		try {
			return ReflectionUtils.create(this.transportClassName,
					ILogEventTransport.class);
		} catch (CannotCreateObjectException ex) {
			LogLog.error("Can't initialize transport", ex);
			return null;
		}
	}

	public boolean requiresLayout() {
		return false;
	}

	public synchronized void close() {
		if (this.logEventTransport != null) {
			this.logEventTransport.close();
			this.logEventTransport = null;
			this.firstRunOfTransportCreation = true;
		}
	}

	public synchronized String getTransportClassName() {
		return this.transportClassName;
	}

	public synchronized void setTransportClassName(String transportClassName) {
		this.transportClassName = transportClassName;
		this.logEventTransport = null;
		this.firstRunOfTransportCreation = true;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}