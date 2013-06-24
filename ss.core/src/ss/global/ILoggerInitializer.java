/**
 * 
 */
package ss.global;

/**
 *
 */
public interface ILoggerInitializer {

	void initialize(LoggerConfiguration type);

	void initialize(String configurationName);

	void initializeByDefault();

}