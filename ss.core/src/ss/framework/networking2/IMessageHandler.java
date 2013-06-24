/**
 * 
 */
package ss.framework.networking2;


/**
 * 
 */
public interface IMessageHandler<M extends Message> {

	void handle( MessageHandlingContext<M> context ) throws CommandHandleException;
	
}
