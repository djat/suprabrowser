package ss.lab.dm3.persist.init;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.testsupport.objects.Sphere;

public class WeakObjectsGcTestCase extends AbstractDomainTestCase {

	public void test() {
		Domain domain = getDomain();		
		domain.getRepository().unloadAll();
		//System.gc();
		//domain.getRepository().debugTo(this.log);
		System.gc();
		try {
			Thread.sleep( 2000 );
		}
		catch (InterruptedException ex) {
		}
		//System.gc();
		this.log.debug( domain.getRepository().resolveOrNull( Sphere.class, new Long(1) ) );
		System.gc();
		try {
			Thread.sleep( 2000 );
		}
		catch (InterruptedException ex) {
		}
		domain.getRepository().debugTo(this.log);
		//domain.getRepository().debugTo(this.log);
	}

	
}
