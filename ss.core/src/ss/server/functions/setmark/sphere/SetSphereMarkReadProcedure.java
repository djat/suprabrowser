/**
 * 
 */
package ss.server.functions.setmark.sphere;

import ss.common.StringUtils;
import ss.server.functions.setmark.common.SetReadOperations;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class SetSphereMarkReadProcedure extends SetSphereMarkProcedure<SetSphereMarkReadData> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetSphereMarkReadProcedure.class);
	
	/**
	 * @param data
	 * @param peer
	 */
	public SetSphereMarkReadProcedure(final SetSphereMarkReadData data,
			final DialogsMainPeer peer) {
		super(data, peer);
	}

	@Override
	protected void proccesImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("!-- Processing SetSphereMarkReadProcedure started");
		}
		final String contactName = getPeer().getUserContactName();
		if (StringUtils.isBlank( contactName )) {
			logger.error( "ContactName is blank" );
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ContactName: " + contactName);
		}
		if ( getData().getSphereId() == null ){
			logger.error("SphereId is null");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("SphereId is : " + getData().getSphereId());
		}
		SetReadOperations.voteSphere( getData().getSphereId(), contactName, getPeer() );
		if (logger.isDebugEnabled()) {
			logger.debug("!-- Processing SetGlobalMarkReadProcedure finished");
		}
	}
}
