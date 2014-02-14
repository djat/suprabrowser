/**
 * 
 */
package ss.client.networking.protocol.actions;

/**
 * @author zobo
 *
 */
public class MoveSphereCommand extends AbstractAction {

	private static final long serialVersionUID = -804270524827515159L;

	private static final String SOURCE_ID = "source_id";
	
	private static final String TARGET_ID = "target_id";

	public void setSourceSphereId( final String source ){
		putArg(SOURCE_ID, source);
	}
	
	public String getSourceSphereId(){
		return getStringArg(SOURCE_ID);
	}
	
	public void setTargetSphereId( final String target ){
		putArg(TARGET_ID, target);
	}
	
	public String getTargetSphereId(){
		return getStringArg(TARGET_ID);
	}
}
