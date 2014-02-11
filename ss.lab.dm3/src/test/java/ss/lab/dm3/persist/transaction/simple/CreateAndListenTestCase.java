package ss.lab.dm3.persist.transaction.simple;

import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainChangeAdapter;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.AbstractDomainTestCase;
import ss.lab.dm3.persist.changeset.CrudSet;
import ss.lab.dm3.testsupport.objects.Sphere;

public class CreateAndListenTestCase extends AbstractDomainTestCase {

	private Sphere sphere4;
	
	private boolean checkCangeSetCalled;
	
	public void test() {
		final Domain domain = getDomain();
		domain.addListener( new DomainChangeAdapter() {
			@Override
			public void domainChanged(CrudSet changeSet) {
				try {
					checkChangeSet( changeSet );
				}
				finally {
					domain.removeListener( this );
				}
			}
			
		}, DomainObject.class );
		createSphere4( domain );
		checkListenerCalled( domain );
	}

	/**
	 * @param changeSet
	 */
	protected void checkChangeSet(CrudSet changeSet) {
		assertEquals( 1, changeSet.getCreated().size() );
		assertEquals( 0, changeSet.getUpdated().size() );
		assertEquals( 0, changeSet.getRetrieved().size() );
		assertEquals( 0, changeSet.getDeleted().size() );
		assertSame( this.sphere4, changeSet.getCreated().resolveOrNull( this.sphere4.getQualifiedId() ) );
		this.checkCangeSetCalled = true;
	}
	
	public void createSphere4(Domain domain) {
		domain.beginTrasaction();
		this.sphere4 = domain.createObject( Sphere.class );
		this.sphere4.setDisplayName( "4" );
		this.sphere4.setSystemName( String.valueOf( this.sphere4.getId() ) );
		domain.commitTrasaction();
	}
	
	public void checkListenerCalled(Domain domain) {
		assertTrue( this.checkCangeSetCalled );
	}
}
