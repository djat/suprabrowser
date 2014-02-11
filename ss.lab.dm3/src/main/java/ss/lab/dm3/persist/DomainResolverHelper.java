package ss.lab.dm3.persist;

public class DomainResolverHelper {
	
	private static IDomainResolver resolver = new DomainResolver();
	
	public static IDomainResolver getResolver() {
		return resolver;
	}

	public static void setResolver(IDomainResolver resolver) {
		DomainResolverHelper.resolver = resolver;
	}

	/**
	 * @return
	 */
	public static Domain getCurrentDomainOrNull() {
		return resolver.getCurrentDomainOrNull();
	}
	
	/**
	 * @return
	 */
	public static Domain getCurrentDomain() {
		return resolver.getCurrentDomain();
	}
}
