/**
 * 
 */
package ss.server.debug.commands;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import ss.common.StringUtils;
import ss.server.debug.IRemoteCommand;
import ss.server.debug.RemoteCommandContext;
import ss.server.functions.changecontact.ChangeContactAbstractFunction;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;

/**
 * @author zobo
 *
 */
public class ChangeContactNameEverywhere implements IRemoteCommand {
	
	class ChangeContactAbstractFunctionForLocalNeeds extends ChangeContactAbstractFunction {

		public ChangeContactAbstractFunctionForLocalNeeds(DialogsMainPeer peer) {
			super(peer);
		}
		
		void run( String login, String newContactName, String oldContactName, String newFirstName, String newLastName ) {
			replaceContactNamesInContacts(oldContactName, newFirstName, newLastName);
			if ( login != null ) {
				replaceInMembership(login, newContactName);
				replaceInSphereDefinitions(oldContactName, newContactName);
				replaceInVoutingAndGiver(oldContactName, newContactName);
				AddChangesToSupraSphereDoc(login , oldContactName, newContactName);
				updateVerifyAuth();
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ChangeContactNameEverywhere.class);

	public String evaluate(RemoteCommandContext context) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Command ChangeContactNameEverywhere started");
		}
		final StringWriter responce = new StringWriter();
		final PrintWriter writer = new PrintWriter(responce);
		perform( writer, context.getArgs() );
		writer.flush();
		final String toReturn = responce.toString();
		if (logger.isDebugEnabled()) {
			logger.debug("Command ChangeContactNameEverywhere, responce: " + toReturn);
		}
		return toReturn;
	}

	/**
	 * command [old name] [new name]
	 * @param writer
	 */
	private void perform( final PrintWriter writer, final String args) {
		if ( StringUtils.isBlank( args ) ) {
			writer.println("args is blank");
			return;
		}
		String oldContactName = null;
		String newContactName = null;
		try {
			final String data = args.trim();
			StringBuilder sb = null;
			for ( char ch : data.toCharArray() ) {
				if ( ch == '[' ) {
					sb = new StringBuilder();
				} else if ( ch == ']' ) {
					if ( oldContactName == null ) {
						oldContactName = sb.toString();
					} else if ( newContactName == null ) {
						newContactName = sb.toString();
					}
				} else {
					if ( sb != null ) {
						sb.append(ch);
					}
				}
			}
		} catch ( Exception ex ) {
			writer.println("could not parse messageId and sphereId");
			return;
		}
		if (StringUtils.isBlank(oldContactName)) {
			writer.println("oldContactName is blank");
			return;
		}
		if (StringUtils.isBlank(newContactName)) {
			writer.println("newContactName is blank");
			return;
		}
		writer.println("oldContactName from args: " + oldContactName);
		writer.println("newContactName from args: " + newContactName);
		
		performImpl( writer, oldContactName, newContactName);
	}

	/**
	 * @param writer
	 * @param oldContactName
	 * @param newContactName
	 */
	private void performImpl( final PrintWriter writer, final String oldContactName,
			final String newContactName ) {
		
		DialogsMainPeer peer = null;
		for ( DialogsMainPeer tp : DialogsMainPeerManager.INSTANCE.getHandlers() ) {
			if ( tp != null ) {
				peer = tp;
				break;
			}
		}
		if ( peer == null ) {
			writer.println( "no DialogsMainPeers to perform" );
			return;
		}
		
		String newFirstName = null;
		String newLastName = null;
		StringTokenizer st = null;
		try {
			st = new StringTokenizer( newContactName );
			newFirstName = st.nextToken();
			newLastName = st.nextToken();
			writer.println( "newFirstName" + newFirstName );
			writer.println( "newLastName" + newLastName );
		} catch (Exception ex) {
			writer.println( "Error in tokenizing first and last name for newContactName: " + newContactName + " : " + ex.getMessage() );
			return;
		}
		try {
			st = new StringTokenizer( oldContactName );
			writer.println( "oldFirstName" + st.nextToken() );
			writer.println( "oldLastName" + st.nextToken() );
		} catch (Exception ex) {
			writer.println( "Error in tokenizing first and last name for oldContactName: " + oldContactName + " : " + ex.getMessage() );
			return;
		}

		if ( StringUtils.isBlank(newFirstName) || StringUtils.isBlank(newLastName) ) {
			writer.println( "One part of new name is blank" );
			return;
		}
		
		String login = peer.getVerifyAuth().getLoginForContact(oldContactName);
		
		writer.println( (login!=null) ? ("login: " + login) : "login is null");
		
		if ( login == null ) {
			login = peer.getVerifyAuth().getLoginForContact(newContactName);
			writer.println( "For new contact name: " + ((login!=null) ? ("login: " + login) : "login is null"));
		}
		
		ChangeContactAbstractFunctionForLocalNeeds changer = new ChangeContactAbstractFunctionForLocalNeeds( peer  );
		changer.run(login, newContactName, oldContactName, newFirstName, newLastName);
	}


}
