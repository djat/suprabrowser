/**
 * 
 */
package ss.refactor.supraspheredoc.old.unused;

import org.dom4j.tree.AbstractDocument;

import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.framework.networking2.CommandHandleException;
import ss.refactor.supraspheredoc.old.Utils;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.protocol.getters.AbstractGetterCommandHandler;

/**
 *
 */
public class GetSelfSphereMemberHandler extends AbstractGetterCommandHandler<GetSelfSphereMemberCommand, AbstractDocument> {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger.getLogger(GetSelfSphereMemberHandler.class);
	
	
	public GetSelfSphereMemberHandler(DialogsMainPeer peer) {
		super(GetSelfSphereMemberCommand.class, peer);
	}

	@Override
	protected AbstractDocument evaluate(GetSelfSphereMemberCommand command) throws CommandHandleException {
		SupraSphereStatement supraSphere = Utils.getUtils( this.peer ).getSupraSphere();
		final String userLogin = this.peer.getUserLogin();
		SupraSphereMember member = supraSphere.getSupraMembers().findMemberByLogin( userLogin );
		if ( member == null ) {
			throw new CommandHandleException( "Can't find supra sphere member by " + userLogin  );
		}
		return (AbstractDocument) member.getDocumentCopy();
	}

}
