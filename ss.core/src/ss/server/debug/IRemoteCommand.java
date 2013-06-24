package ss.server.debug;

public interface IRemoteCommand {

	String evaluate(RemoteCommandContext context) throws Exception;

}
