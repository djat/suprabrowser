package ss.lab.dm3.persist;

/**
 * @author Dmitry Goncharov
 */
public interface DomainIdGenerator {

	Long createId(DomainObject object);
	
}
