package ss.lab.dm3.persist;

public class DomainResolver implements IDomainResolver {

	public Domain getCurrentDomain() {
		Domain domain = getCurrentDomainOrNull();
		if (domain == null) {
			throw new IllegalStateException(
				"Can't get domain for current thread");
		}
		return domain;
	}

	public Domain getCurrentDomainOrNull() {
		return DomainThreadsManager.INSTANCE.getCurrentDomain();
	}

}
