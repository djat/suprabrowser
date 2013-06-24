/**
 * 
 */
package ss.client.networking.protocol.actions;

import ss.smtp.defaultforwarding.ForcedForwardingInfo;

/**
 * @author zobo
 *
 */
public class PublishMessageAction extends AbstractAction {

	private static final String IS_PROCESS_FILES = "IsProcessFiles";
	private static final String FORCED_FORWARDING_INFO = "ForcedForwardingInfo";
	private static final long serialVersionUID = -4763240086946026301L;

	public void putForcedForwardingInfo( final ForcedForwardingInfo info ){
		putArg(FORCED_FORWARDING_INFO, info);
	}
	
	public ForcedForwardingInfo getForcedForwardingInfo(){
		return (ForcedForwardingInfo)getObjectArg(FORCED_FORWARDING_INFO);
	}
	
	public void setProcessFiles( final boolean value ){
		putArg(IS_PROCESS_FILES, value);
	}
	
	public boolean isProcessFiles(){
		return getBooleanArg(IS_PROCESS_FILES);
	}
}
