/**
 * 
 */
package ss.lab.dm3.persist.orm;

import ss.lab.dm3.orm.OrmManager;
import ss.lab.dm3.orm.OrmManagerProvider;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainResolverHelper;

/**
 *
 */
public class DomainOrmManagerProvider implements OrmManagerProvider {

	/* (non-Javadoc)
	 * @see ss.lab.dm3.orm.OrmManagerProvider#get()
	 */
	public OrmManager get() {
		Domain domain = DomainResolverHelper.getCurrentDomainOrNull();
		if ( domain != null ) {
			return domain.getOrmManager();
		}
		return null;
	}

	
}
