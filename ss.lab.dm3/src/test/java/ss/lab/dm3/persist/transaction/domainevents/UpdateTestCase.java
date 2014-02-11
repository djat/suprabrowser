package ss.lab.dm3.persist.transaction.domainevents;

import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.connection.ConnectionBuilder;
import ss.lab.dm3.persist.DomainChangeAdapter;
import ss.lab.dm3.persist.DomainObject;
import ss.lab.dm3.persist.LowLevelDomainTestCase;
import ss.lab.dm3.persist.changeset.ChangeSet;
import ss.lab.dm3.persist.changeset.CrudSet;
import ss.lab.dm3.persist.workers.DomainWorkerContext;
import ss.lab.dm3.persist.workers.TransactionWorker;
import ss.lab.dm3.testsupport.Checkpoint;
import ss.lab.dm3.testsupport.TestDataLoader;
import ss.lab.dm3.testsupport.objects.Sphere;

/**
 * @author Dmitry Goncharov
 * 
 */
public class UpdateTestCase extends LowLevelDomainTestCase {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(UpdateTestCase.class);
		
	private Checkpoint connection2Ready;
	//private Checkpoint dataChanged;
	private CallbackResultWaiter dataChanged;
	private Checkpoint finished;

	private Sphere connection2Sphere;
	private Sphere connection1Sphere;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Unlock domain. Test should be run from non domain thread
		getDomain().unlock();
		this.connection2Ready = new Checkpoint();
		this.dataChanged = new CallbackResultWaiter();
		this.finished = new Checkpoint();
	}

	@Override
	protected void tearDown() throws Exception {
		// See setUp() for details
		this.getSystemConnectionProvider().get().getDomain().lockOrThrow();
		super.tearDown();
		this.connection2Ready = null;
		this.dataChanged = null;
		this.finished = null;
	}

	public void test() {
		ConnectionBuilder connectionBuilder = getSystemConnectionProvider().createConnectionBuilder();
		final Connection connection2 = connectionBuilder
				.createConnectionProxy( "testuser" );
		// Connection 2
		// 1 loads data
		// 2 check that data loaded correctly
		// 3 notify that it's ready to get updates
		// 4 wait for connection 1
		// 5 when received dataChanged, then check data
		Thread c2Run = new Thread(new Runnable() {
			public void run() {
				createWorkersBuilder(connection2.getDomain()).add(
						TestDataLoader.class).add( "c2CheckBeforeChanges", "c2NotifyReady", "c2WaitForDataChangedAndCheck" ).run();
			}
		});
		c2Run.setName("C2");
		c2Run.start();
		// Connection 1
		// 1 loads data
		// 2 wait for connection 2 ready
		// 3 change data
		// 4 check changes
		// 5 notify connection 2 that it should check changes too
		Thread c1Run = new Thread(new Runnable() {
			public void run() {
				createWorkersBuilder().add(TestDataLoader.class).add( "c1WaitForConnection2Ready").add(
						new C1DataEditor()).add( "c1CheckChanges", "c1NotifyDataChanged" ).run();

			}
		});
		connection2.getDomain().addListener( new DomainChangeAdapter() {
			@Override
			public void domainChanged(CrudSet changeSet) {
				if (UpdateTestCase.log.isDebugEnabled()) {
					UpdateTestCase.log.debug("Domain changed, Updated size " + changeSet.getUpdated().size() );
					ChangeSet original = changeSet.getOriginalChangeSet();
					if ( original != null ) {
						UpdateTestCase.log.debug( original.toDebugString() );
					}
					for( DomainObject object : changeSet.getUpdated() ) {
						if ( object instanceof Sphere ) {
							UpdateTestCase.log.debug( "Sphere " + object + " DisplayName " + ((Sphere)object).getDisplayName() );
						}						
					}
					final Sphere locConnection1Sphere = UpdateTestCase.this.connection1Sphere;
					final Sphere locConnection2Sphere = UpdateTestCase.this.connection2Sphere;
					UpdateTestCase.log.debug("Connection1 sphere " + locConnection1Sphere + " DisplayName " + ( locConnection1Sphere != null ? locConnection1Sphere.getDisplayName() : null ));
					UpdateTestCase.log.debug("Connection2 sphere " + locConnection2Sphere + " DisplayName " + ( locConnection2Sphere != null ? locConnection2Sphere.getDisplayName() : null ));
				}		
			}
			
		}, Sphere.class );
		c1Run.setName("C1");
		c1Run.start();
		this.finished.waitToPass();
	}

	public class C1DataEditor extends TransactionWorker<DomainWorkerContext> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see ss.lab.dm3.persist.workers.TransactionWorker#runInTransaction(ss.lab.dm3.persist.workers.DomainWorkerContext)
		 */
		@Override
		protected void runInTransaction(DomainWorkerContext context) {
			if (getLog().isDebugEnabled()) {
				getLog().debug("C1: check edit results");
			}
			UpdateTestCase.this.connection1Sphere = context.getDomain().resolve(
					Sphere.class, 2L);
			assertNotNull(UpdateTestCase.this.connection1Sphere);
			assertEquals(new Long(2), UpdateTestCase.this.connection1Sphere
					.getId());
			assertEquals(UpdateTestCase.this.connection2Sphere.getId(),
					UpdateTestCase.this.connection1Sphere.getId());
			assertNotSame(UpdateTestCase.this.connection1Sphere,
					UpdateTestCase.this.connection2Sphere);
			UpdateTestCase.this.connection1Sphere.setDisplayName("c1ToC2Test");
		}

	}

	public void c2CheckBeforeChanges(DomainWorkerContext context) {
		if (log.isDebugEnabled()) {
			log.debug("C2: data loaded, check it.");
		}
		this.connection2Sphere = context.getDomain().resolve(Sphere.class, 2L);
		assertNotNull(this.connection2Sphere);
		assertEquals(new Long(2), this.connection2Sphere.getId());
		assertEquals("Sphere_Display#2", this.connection2Sphere
				.getDisplayName());
	}

	public void c2NotifyReady() {
		if (log.isDebugEnabled()) {
			log.debug("C2: ready");
		}
		this.connection2Ready.pass();
	}

	public void c2WaitForDataChangedAndCheck(DomainWorkerContext context) {
		if (log.isDebugEnabled()) {
			log.debug("C2: wait for notify from C1");
		}
		this.dataChanged.waitToResult();
		assertEquals("c1ToC2Test", this.connection2Sphere.getDisplayName());
		this.finished.pass();
	}

	public void c1WaitForConnection2Ready() {
		if (log.isDebugEnabled()) {
			log.debug("C1: begin wait for C2 ready");
		}
		this.connection2Ready.waitToPass();		
	}

	public void c1CheckChanges(DomainWorkerContext context) {
		if (log.isDebugEnabled()) {
			log.debug("C1: check edit results");
		}
		assertSame(this.connection1Sphere, context.getDomain().resolve(
				Sphere.class, 2L));
		assertEquals(new Long(2), this.connection1Sphere.getId());
		assertEquals("c1ToC2Test", this.connection1Sphere.getDisplayName());
	}

	public void c1NotifyDataChanged(DomainWorkerContext context) {
		if (log.isDebugEnabled()) {
			log.debug("C1: going to notify data changed, splee before do this");
		}
		try {
			Thread.sleep( 5000 );
		} catch (InterruptedException ex) {
		}
		if (log.isDebugEnabled()) {
			log.debug("C1: notify - data changed");
		}
		this.dataChanged.onSuccess(null);
	}
}
