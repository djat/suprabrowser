package ss.framework.errorreporting;

import org.apache.log4j.spi.LoggingEvent;

public interface ILogEventTransport {

	void send( LoggingEvent loggingEvent );
	
	void close();
	
}