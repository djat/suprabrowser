package ss.framework.install.update;

import ss.framework.install.QualifiedVersion;
import ss.framework.networking2.Command;

public class UpdateHelloCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6580050108691138261L;
	
	
	private final String clientVersion;

	/**
	 * @param clientVersion
	 */
	public UpdateHelloCommand(final String clientVersion) {
		super();
		this.clientVersion = clientVersion;
	}

	/**
	 * @param applicationVersion
	 */
	public UpdateHelloCommand(QualifiedVersion applicationVersion) {
		this( applicationVersion.toString() );
	}

	public QualifiedVersion getClientVersion() {
		return QualifiedVersion.safeParse( this.clientVersion );
	}
	
}
