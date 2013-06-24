package ss.framework.domainmodel2;

public class AbstractHelper {
	
	private final AbstractDomainSpace spaceOwner;

	/**
	 * @param spaceOwner
	 */
	public AbstractHelper(AbstractDomainSpace spaceOwner) {
		super();
		this.spaceOwner = spaceOwner;
	}

	/**
	 * @return the spaceOwner
	 */
	public final AbstractDomainSpace getSpaceOwner() {
		return this.spaceOwner;
	}
	
	
}
