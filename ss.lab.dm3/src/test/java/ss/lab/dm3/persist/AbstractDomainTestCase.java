package ss.lab.dm3.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.classic.Session;

import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.persist.backend.DataManagerBackEnd;
import ss.lab.dm3.persist.backend.IDataManagerBackEnd;
import ss.lab.dm3.persist.backend.hibernate.SessionManager;
import ss.lab.dm3.testsupport.TestDataProvider;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;

public abstract class AbstractDomainTestCase extends TestCase  {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final boolean lockDomain;
	
	public AbstractDomainTestCase() {
		this( true );
	}
	/**
	 * @param lockDomain
	 */
	public AbstractDomainTestCase(boolean lockDomain) {
		super();
		this.lockDomain = lockDomain;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final SystemConnectionProvider systemConnectionProvider = getSystemConnectionProvider();
		String setUpBatchFileName = getSetUpBatchFileName();
		systemConnectionProvider.resetDomain();
		DataManagerBackEnd dataManagerBackEnd = (DataManagerBackEnd) systemConnectionProvider.getBackEndContextProvider().getSystemBackEndContext().getDataManagerBackEnd();
		if ( setUpBatchFileName != null ) {
			SessionManager sessionManager = dataManagerBackEnd.getSessionManager();
			final Session session = sessionManager.begin();
			TestDataProvider testDataProvider = new TestDataProvider( session, setUpBatchFileName );
			testDataProvider.loadTestData();
			this.log.info( "Default data loaded" );
			session.clear();
			sessionManager.commit();
		}
		if ( this.lockDomain ) {
			getDomain().lockOrThrow();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if ( this.lockDomain ) {
			getDomain().unlock();
		}
	}
	
	/**
	 * @return
	 */
	protected String getSetUpBatchFileName() {
		return TestDataProvider.DEFAULT_TEST_DATA_SQL;
	}

	/**
	 * @return
	 */
	protected SystemConnectionProvider getSystemConnectionProvider() {
		return TestSystemConnectionProvider.INSTANCE;
	}
		
	/**
	 * @return
	 */
	protected Domain getDomain() {
		Connection connection = getSystemConnectionProvider().get();
		return connection.getDomain();
	}

	/**
	 * @param message
	 * @param ex
	 */
	public static void fail(String message, Throwable cause) {
		fail(message + " caused by " + cause);
	}
	
	public void evictDomainObjects() {
		final Domain domain = getDomain();
		evictDomainObjects(domain);
	}
	
	public void evictDomainObjects(final Domain domain) {
		Transaction tx = domain.getTransaction();
		if ( tx != null ) {
			tx.rollback();
		}
		domain.getRepository().unloadAll();
	}
	
	/**
	 * @return the log
	 */
	public org.apache.commons.logging.Log getLog() {
		return this.log;
	}
		
	public static void assertListSame( DomainObject[] expectedSet, ChildrenDomainObjectList<?> items) {
		assertNotNull( items );
		final Class<? extends MappedObject> itemType = items.getController().getItemType();
		assertListSame(itemType, expectedSet, items.toSet() );
	}
	
	private static void assertListSame(final Class<? extends MappedObject> expectedType,
			DomainObject[] expectedSet,
			final Set<? extends DomainObject> itemsSet) {
		assertEquals( "Items size", expectedSet.length, itemsSet.size() );
		for( DomainObject expectedObject : expectedSet ) {
			assertTrue( "Check expected item clazz ", expectedType.isAssignableFrom(expectedObject.getClass()));
			assertTrue( "Check contains " + expectedObject.toShortString(), itemsSet.contains( expectedObject ) );
		}
	}
	
	public static <T extends DomainObject> void assertListSame(Long[] expectedIds,
			DomainObjectCollector<T> items ) {
		List<DomainObject> expectedSet = new ArrayList<DomainObject>();
		Class<T> entityClass = items.getQuery().getEntityClass();
		for (Long id : expectedIds ) {
			Domain currentDomain = DomainResolverHelper.getCurrentDomain();
			expectedSet.add( currentDomain.resolve( entityClass, id ) );
		}
		assertListSame( entityClass, expectedSet.toArray( new DomainObject[ expectedSet.size() ] ), items.toSet() );
	}
	
	public void luceneReindexBase() {
		SystemConnectionProvider systemConnectionProvider = getSystemConnectionProvider();
		BackEndContext systemBackEndContext = systemConnectionProvider.getBackEndContextProvider().getSystemBackEndContext();
		systemBackEndContext.getDataManagerBackEnd();
		final IDataManagerBackEnd dataManagerBackEnd = systemBackEndContext
		.getDataManagerBackEnd();
		dataManagerBackEnd.searchReindex();
	}
	
}
