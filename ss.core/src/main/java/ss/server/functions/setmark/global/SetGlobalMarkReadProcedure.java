/**
 * 
 */
package ss.server.functions.setmark.global;

import ss.common.StringUtils;
import ss.server.functions.setmark.common.SetReadOperations;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SetGlobalMarkReadProcedure extends SetGlobalMarkProcedure<SetGlobalMarkReadData> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetGlobalMarkReadProcedure.class);
	
	/**
	 * @param data
	 * @param peer
	 */
	public SetGlobalMarkReadProcedure(SetGlobalMarkReadData data,
			DialogsMainPeer peer) {
		super(data, peer);
	}

	@Override
	protected synchronized void proccesImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("!-- Processing SetGlobalMarkReadProcedure started");
		}
		final String contactName = getPeer().getUserContactName();
		if (StringUtils.isBlank( contactName )) {
			logger.error( "ContactName is blank" );
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ContactName: " + contactName);
		}
		if ( getAllAvailableSpheres() == null ){
			logger.error("Available spheres is null");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Spheres size to vote: " + getAllAvailableSpheres().size());
		}
		for ( String sphereId : getAllAvailableSpheres() ) {
			SetReadOperations.voteSphere( sphereId, contactName, getPeer() );
		}
		if (logger.isDebugEnabled()) {
			logger.debug("!-- Processing SetGlobalMarkReadProcedure finished");
		}
	}
}
