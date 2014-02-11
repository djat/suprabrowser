package ss.lab.dm3.persist.transaction.concurrent;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ss.lab.dm3.connection.SystemConnectionProvider;
import ss.lab.dm3.persist.Domain;
import ss.lab.dm3.persist.DomainException;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.Transaction;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;

public class CreateTestAccounts {

	protected static final Log log = LogFactory.getLog(CreateTestAccounts.class);
	
	public static void main(String[] args) {
		
		SystemConnectionProvider connectionProvider = TestSystemConnectionProvider.INSTANCE;
		Domain domain = connectionProvider.get().getDomain();
		RunStatisticStamp start = new RunStatisticStamp();
		domain.lockOrThrow();
		try {
			List<Sphere> spheres = domain.find( QueryHelper.eq( Sphere.class ) ).toList();
			spheres.add( null );
			for( int n = 0; n < 10000; ++ n ) {
				try {
				Transaction tx = domain.beginTrasaction();
				UserAccount userAccount = domain.createObject( UserAccount.class, new Long( 20 + n ) );
				userAccount.setContactName( RandomHelper.randomName() );
				String login = RandomHelper.randomName();
				userAccount.setContactCardId( login );
				userAccount.setLogin( login );
				Sphere homeSphere = RandomHelper.randomItems( spheres, 1 ).get( 0 );
				userAccount.setHomeSphere( homeSphere );
				tx.commit();
				}
				catch( DomainException ex ) {
					log.warn( "Can't add object", ex );
					-- n;
				}
			}
		}
		finally {
			domain.unlock();
		}
		System.out.println( new RunStatisticStamp().minus( start ).toString() );
	}
}
