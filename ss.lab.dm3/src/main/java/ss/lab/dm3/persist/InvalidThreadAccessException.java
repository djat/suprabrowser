package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public class InvalidThreadAccessException extends DomainException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5829922493551232435L;


	/**
	 * @param domain
	 */
	public InvalidThreadAccessException(Domain domain) {
		super( "Can't access to domain " + domain + " from non domain thread" );
	}
	
}
