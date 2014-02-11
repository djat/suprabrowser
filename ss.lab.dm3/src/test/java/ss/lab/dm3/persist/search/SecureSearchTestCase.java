package ss.lab.dm3.persist.search;

import ss.lab.dm3.persist.DomainObjectCollector;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.testsupport.objects.Attachment;
import ss.lab.dm3.testsupport.objects.Sphere;
import ss.lab.dm3.testsupport.objects.UserAccount;


public class SecureSearchTestCase extends AbstractSearchTestCase {

	public void testPublic() {
		StringBuilder query = new StringBuilder( "name:(First)");
		TypedQuery<Attachment> luceneSearch = QueryHelper.luceneSearch( Attachment.class, query.toString() );
		luceneSearch.setSecure(true);
		assertListSame( new Long[] { 10L, 13L }, getDomain().find( luceneSearch ) );
	}
	
	public void testSecured() {
		// Search all user like "M%"
		// That are in sphere 1L
		TypedQuery<UserAccount> luceneSearch = QueryHelper.luceneSearch( UserAccount.class, "contactName:(M*) name:(First)" );
		luceneSearch.setSecure(true);
		luceneSearch.getSecureKeys().add( Sphere.class, 1L );
		// 
		DomainObjectCollector<UserAccount> findResult = getDomain().find( luceneSearch );
		assertListSame( new Long[] { 16L, 19L }, findResult );
	}
}
