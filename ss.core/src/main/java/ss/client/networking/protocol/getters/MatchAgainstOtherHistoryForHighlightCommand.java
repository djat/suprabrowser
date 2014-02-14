/**
 * 
 */
package ss.client.networking.protocol.getters;

import ss.client.ui.tempComponents.researchcomponent.ResearchComponentDataContainer;

/**
 * @author zobo
 *
 */
public class MatchAgainstOtherHistoryForHighlightCommand extends AbstractGetterCommand {

	private static final String DATA = "data";
	
	private static final long serialVersionUID = 5696367753774497076L;
	
	public void setData( final ResearchComponentDataContainer data ){
		putArg(DATA, data);
	}
	
	public ResearchComponentDataContainer getData(){
		return ( ResearchComponentDataContainer ) getObjectArg( DATA );
	}
}
