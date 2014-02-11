package ss.lab.dm3.persist.context;


import java.util.Set;

import ss.lab.dm3.persist.Context;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.testsupport.objects.Sphere;

public class ContextWithLazyLoadingTestCase extends AbstractDomainTestCase {
	
	public void test() {
		
		final Domain domain = getDomain();
		Context myContext = new Context( domain );		
		domain.execute(myContext, new Runnable() {
			public void run() {
				// First clear domain from all objects
				domain.getRepository().unloadAll();		
				// Domain is empty, so test lazy loading
				resolve(domain);
				childrenItems(domain);
				find(domain);				
			}
		});
		
	}

	/**
	 * 
	 */
	protected void resolve( Domain domain ) {
		Sphere sphere = domain.resolve( Sphere.class, 1L );
		assertNotNull( sphere );
		// TODO additional check
	}

	/**
	 * 
	 */
	protected void childrenItems( Domain domain ) {
		Sphere sphere = domain.resolve( Sphere.class, 1L );
		assertNotNull( sphere );
		Set<Sphere> children = sphere.getChildrenSpheres().toSet();
		assertEquals( 2, children.size() );
		// TODO additional check
	}

	/**
	 * 
	 */
	protected void find( Domain domain ) {
		// TODO Auto-generated method stub		
	}
	

}
