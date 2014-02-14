/**
 * 
 */
package ss.server.networking.protocol.getters;

import java.util.ArrayList;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetSphereTypesCommand;
import ss.common.StringUtils;
import ss.domainmodel.SphereStatement;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetSphereTypesCommandHandler extends AbstractGetterCommandHandler<GetSphereTypesCommand,ArrayList<String>> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetSphereTypesCommandHandler.class);
	
	public GetSphereTypesCommandHandler(DialogsMainPeer peer) {
		super(GetSphereTypesCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected ArrayList<String> evaluate(GetSphereTypesCommand command)
			throws CommandHandleException {
		if (logger.isDebugEnabled()) {
			logger.debug(" GetSphereTypesCommandHandler performed ");
		}
		final ArrayList<String> result = new ArrayList<String>();
		
		final Vector<Document> spheres = this.peer.getXmldb().getAllSpheres();
		if ( spheres == null ) {
			return result;
		}
		for ( Document doc : spheres ) {
			SphereStatement st = SphereStatement.wrap( doc );
			if ( st.isSphere() ) {
				String role = st.getRole();
				if ( StringUtils.isNotBlank(role) && !result.contains(role) ) {
					result.add( role );
				}
			}
		}
		return result;
	}

}
