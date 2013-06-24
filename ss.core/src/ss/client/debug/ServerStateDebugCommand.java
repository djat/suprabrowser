/**
 * 
 */
package ss.client.debug;

import ss.common.domainmodel2.SsDomain;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObjectList;
import ss.framework.domainmodel2.RunntimeDomainObject;

/**
 *
 */
public class ServerStateDebugCommand extends AbstractDebugCommand {

	public ServerStateDebugCommand() {
		super( "server-state", "Shows server state information" );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		AbstractDomainSpace space = SsDomain.SPACE;
		DomainObjectList<RunntimeDomainObject> items = space.selectItems( CriteriaFactory.createEqual( RunntimeDomainObject.class, RunntimeDomainObject.IdDescriptor.class, -1L ) );
		if ( items.size() > 0 ) {
			getCommandOutput().appendln( items.getFirst().allFieldsToString() );
		}
		else {
			getCommandOutput().appendln( "No result found" );
		}
		
	}

}
