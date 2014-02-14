package ss.framework.install;

public class InvalidVersionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4854549254870015533L;

	/**
	 * @param version
	 */
	public InvalidVersionException(String version, Version parsedVersion ) {
		super( "Parsed version and version string are different. Version string " + version + ". Parsed version " + parsedVersion );
	}

}
