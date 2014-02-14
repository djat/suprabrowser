/**
 * 
 */
package ss.server.networking.protocol.getters;

import org.dom4j.Document;

import ss.client.networking.protocol.getters.GetUserPersonalEmailAddressCommand;
import ss.common.StringUtils;
import ss.domainmodel.ContactStatement;
import ss.framework.networking2.CommandHandleException;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;

/**
 * @author zobo
 *
 */
public class GetUserPersonalEmailAddressHandler extends
		AbstractGetterCommandHandler<GetUserPersonalEmailAddressCommand,String> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetUserPersonalEmailAddressHandler.class);
	
	/**
	 * @param peer
	 */
	public GetUserPersonalEmailAddressHandler(DialogsMainPeer peer) {
		super(GetUserPersonalEmailAddressCommand.class, peer);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.RespondentCommandHandler#evaluate(ss.framework.networking2.Command)
	 */
	@Override
	protected String evaluate(GetUserPersonalEmailAddressCommand command)
			throws CommandHandleException {
		return evaluateImpl( command.getUserLogin(), command.getSphereId(), this.peer );
	}
	
	public static String evaluateImpl( final String UserLogin, final String userSphereId, final DialogsMainPeer peer ){
			String login = filterLogin( UserLogin, peer );
			if ( StringUtils.isBlank(login) ) {
				logger.error("login is null");
				return null;
			}
			String sphereId = filterSphereId( userSphereId, login, peer );
			if ( StringUtils.isBlank(sphereId) ) {
				logger.error("sphereId is null");
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("sphereId: " + sphereId);
				logger.debug("login: " + login);
			}
			final String personalEmail = getPersonalEmail( sphereId, login, peer );
			if (logger.isDebugEnabled()) {
				logger.debug("Result personalEmail : " + personalEmail);
			}
			return personalEmail;
	}
	
	/**
	 * @param sphereId
	 * @param contactName
	 * @return
	 */
	private static String getPersonalEmail( final String sphereId, final String login, final DialogsMainPeer peer ) {
		final XMLDB xmldb = peer.getXmldb();
		final Document doc = xmldb.getContactDoc(sphereId, login);
		if ( doc == null ) {
			logger.warn( "No Document for user: " + login + " in sphere: " + sphereId );
			return null;
		}
		return ContactStatement.wrap( doc ).getEmailAddress();
	}

	private static String filterSphereId( final String outerSphereId, final String login, final DialogsMainPeer peer ){
		if ( StringUtils.isBlank(outerSphereId) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("outerSphereId is blank, private sphere will be used");
			}
			return peer.getVerifyAuth().getPrivateSphereId( login );
		} else {
			return outerSphereId;
		} 
	}
	
	private static String filterLogin( final String outerLogin, final DialogsMainPeer peer ){
		if ( StringUtils.isBlank(outerLogin) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("outerLogin is blank,current user will be used");
			}
			return peer.getUserLogin();
		} else {
			return outerLogin;
		} 
	}
}
