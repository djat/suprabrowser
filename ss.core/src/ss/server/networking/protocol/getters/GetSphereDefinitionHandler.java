package ss.server.networking.protocol.getters;

import org.dom4j.Document;
import org.dom4j.tree.AbstractDocument;

import ss.client.networking.protocol.getters.GetSphereDefinitionCommand;
import ss.framework.networking2.CommandHandleException;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class GetSphereDefinitionHandler
		extends
		AbstractGetterCommandHandler<GetSphereDefinitionCommand, AbstractDocument> {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetSphereDefinitionHandler.class);

	/**
	 * @param peer
	 */
	public GetSphereDefinitionHandler(DialogsMainPeer peer) {
		super(GetSphereDefinitionCommand.class, peer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.networking2.RespondentCommandHandler#evaluate(ss.common.networking2.Command)
	 */
	@Override
	protected AbstractDocument evaluate(GetSphereDefinitionCommand command)
			throws CommandHandleException {
		String sphere_id = command.getStringArg( SC.SPHERE_ID );
		String supraSphere = command.getStringArg( SC.SUPRA_SPHERE );
		Document definition = this.peer.getXmldb().getSphereDefinition(
				supraSphere, sphere_id);
		if (definition == null) {
			if (command.getBooleanArg(SC.CREATE_SPHERE_DEFINITION_IF_NO_DEFINITION_FOUND)) {
				logger.info(String.format(
						"Create sphere definition with system name %s",
						sphere_id));
				definition = this.peer.getXmldb().getUtils()
						.createSphereDefinition(sphere_id, sphere_id);
			} else {
				logger.info(String.format(
						"Spherer definition for system name %s not found",
						sphere_id));
			}
		}
		return (AbstractDocument) definition;
	}

}
