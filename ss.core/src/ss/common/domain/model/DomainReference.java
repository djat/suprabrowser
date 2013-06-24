/**
 * 
 */
package ss.common.domain.model;


/**
 * @author roman
 *
 */
public class DomainReference<T extends DomainObject> {
	
	public DomainReference( Class<T> domainObjectClazz ) {		
	}
	
	public T get() {
		return null;
	}
	
	public void set(T obj) {		
	}

	/**
	 * @param class1
	 * @return
	 */
	public static <T extends DomainObject> DomainReference<T> create(
			Class<T> domainObjectClazz) {
		return new DomainReference<T>( domainObjectClazz);
	}

}
