/**
 * 
 */
package ss.framework.networking2.io;

/**
 *
 */
public interface IPacketInformationProvider {

	boolean isShouldSendNotification();
	
	int getDesiredPacketId();
	
}
