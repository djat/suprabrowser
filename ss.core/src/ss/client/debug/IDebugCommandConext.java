package ss.client.debug;

import ss.client.ui.MessagesPane;

public interface IDebugCommandConext {

	void handleOutput( String commandOuput );

	ParsedDebugCommandLine getParsedDebugCommandLine();

	MessagesPane getMessagesPageOwner();

	DebugCommandCollection getAllCommands();
	
}
