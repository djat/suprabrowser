package ss.lab.dm3.persist.search;

import ss.lab.dm3.persist.DomainObjectCollector;
import ss.lab.dm3.persist.QueryHelper;
import ss.lab.dm3.persist.TypedQuery;
import ss.lab.dm3.testsupport.objects.UserAccount;

public class PagedSearchTestCase extends AbstractSearchTestCase {

	private static final String USER_QUERY = "contactName:(M* N*) ";

	public void testBasic() {
		// Search users
		TypedQuery<UserAccount> luceneSearch = QueryHelper.luceneSearch( UserAccount.class, USER_QUERY  );
		DomainObjectCollector<UserAccount> find = getDomain().find( luceneSearch );
		assertListSame( new Long[] { 6L, 14L, 15L, 16L, 19L,  20L }, find );
		
		luceneSearch.setLimitSize(2);
		DomainObjectCollector<UserAccount> find_2 = getDomain().find( luceneSearch );
		assertListSame( new Long[]{6L, 14L}, find_2 );
		
		luceneSearch.setLimitOffset( 1 );
		luceneSearch.setLimitSize( 2 );
		
		DomainObjectCollector<UserAccount> find1_2 = getDomain().find( luceneSearch );
		assertListSame( new Long[]{14L, 15L}, find1_2 );
	}
	
	public void testLimits() {
		TypedQuery<UserAccount> luceneSearch;
		DomainObjectCollector<UserAccount> find;
		
		luceneSearch = QueryHelper.luceneSearch( UserAccount.class, USER_QUERY  );
		luceneSearch.setLimitOffset( 10 );
		find = getDomain().find( luceneSearch );
		assertListSame( new Long[0], find );
		
		luceneSearch = QueryHelper.luceneSearch( UserAccount.class, USER_QUERY  );
		luceneSearch.setLimitSize( 20 );
		find = getDomain().find( luceneSearch );
		assertListSame( new Long[] { 6L, 14L, 15L, 16L, 19L,  20L }, find );
		
		luceneSearch = QueryHelper.luceneSearch( UserAccount.class, USER_QUERY  );
		luceneSearch.setLimitSize( 0 );
		find = getDomain().find( luceneSearch );
		assertListSame( new Long[] {}, find );
				
		luceneSearch = QueryHelper.luceneSearch( UserAccount.class, USER_QUERY  );
		luceneSearch.setLimitOffset( 5 );
		luceneSearch.setLimitSize( 20 );
		find = getDomain().find( luceneSearch );
		assertListSame( new Long[] { 20L }, find );
	}
	
	public void testTotals() {
		TypedQuery<UserAccount> luceneSearch;
		DomainObjectCollector<UserAccount> find;
		
		luceneSearch = QueryHelper.luceneSearch( UserAccount.class, USER_QUERY  );
		luceneSearch.setLimitOffset( 3 );
		luceneSearch.setLimitSize( 2 );
		find = getDomain().find( luceneSearch );
		assertListSame( new Long[] { 16L, 19L }, find );
		assertEquals( 6, find.getTotalCount() );
	}
}
