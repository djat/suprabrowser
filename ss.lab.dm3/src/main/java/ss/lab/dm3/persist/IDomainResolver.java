package ss.lab.dm3.persist;

public interface IDomainResolver {
	
	Domain getCurrentDomainOrNull();
	
	Domain getCurrentDomain();
	
}
