package ss.lab.dm3.orm.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ss.lab.dm3.orm.MappedObject;
import ss.lab.dm3.orm.query.TestItem.QueryMark;
import ss.lab.dm3.orm.mapper.Mapper;
import ss.lab.dm3.orm.mapper.MapperFactory;
import ss.lab.dm3.orm.query.Expression;
import ss.lab.dm3.orm.query.expressions.ExpressionList;
import ss.lab.dm3.orm.query.expressions.SimpleExpression;
import ss.lab.dm3.orm.query.index.IndexProvider;
import ss.lab.dm3.orm.query.matcher.ExpressionMatcher;
import ss.lab.dm3.orm.query.matcher.ExpressionMatcherFactory;
import ss.lab.dm3.orm.query.matcher.MatcherContext;
import junit.framework.TestCase;

/**
 * @author dmitry
 *
 * parentId != null && ownerId == 10
 * parentId == 5 && ownerId != null 
 * parentId != null && ownerId != null
 * parentId == null && ownerId == null
 * parentId != 4 && ownerId == 6
 *  
 */
public class QueryCaseTest extends TestCase {

	@SuppressWarnings("unchecked")
	private static <T extends MappedObject> Set<T> search( Class<T> beanClazz, Iterable<? extends MappedObject> items, Expression expression ) {
		final MapperFactory mapperFactory = new MapperFactory();
		final Mapper<MappedObject> mapper = mapperFactory.create( MappedObject.class, new Class<?>[] { beanClazz } );
		final ExpressionMatcherFactory matcherFactory = new ExpressionMatcherFactory( mapper.get( beanClazz ) );
		final ExpressionMatcher matcher = matcherFactory.create( expression );
		MatcherContext context = new MatcherContext( items );
		matcher.collect(context);
		Iterable collected = context.getCollected();
		Set<T> searchResult = new HashSet<T>();
		for( Object item : collected ) {
			searchResult.add( beanClazz.cast( item ) );
		}
		return searchResult;
	}
	
	public static void main(String[] args) {
		
		ExpressionList list2 = new ExpressionList( ExpressionList.Junction.AND );
		list2.add( new SimpleExpression( "parentId", SimpleExpression.Operator.EQ, 8L ) );
		list2.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.EQ, null ) );
		
		System.out.println( list2 );
		
	}
	
	public void test() {
		ExpressionList list1 = new ExpressionList( ExpressionList.Junction.AND );
		list1.add( new SimpleExpression( "parentId", SimpleExpression.Operator.EQ, null ) );
		list1.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.EQ, 10L ) );
		
		ExpressionList list2 = new ExpressionList( ExpressionList.Junction.AND );
		list2.add( new SimpleExpression( "parentId", SimpleExpression.Operator.EQ, 8L ) );
		list2.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.EQ, null ) );
		
		ExpressionList list3 = new ExpressionList( ExpressionList.Junction.AND );
		list3.add( new SimpleExpression( "parentId", SimpleExpression.Operator.EQ, null ) );
		list3.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.EQ, null ) );
		
		ExpressionList list4 = new ExpressionList( ExpressionList.Junction.AND );
		list4.add( new SimpleExpression( "parentId", SimpleExpression.Operator.NE, null ) );
		list4.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.NE, null ) );
		
		ExpressionList list5 = new ExpressionList( ExpressionList.Junction.AND );
		list5.add( new SimpleExpression( "enabled", SimpleExpression.Operator.EQ, true ) );
		list5.add( new SimpleExpression( "enabled", SimpleExpression.Operator.EQ, true ) );
		
		ExpressionList list6 = new ExpressionList( ExpressionList.Junction.OR );
		list6.add( new SimpleExpression( "enabled", SimpleExpression.Operator.EQ, false ) );
		list6.add( new SimpleExpression( "title", SimpleExpression.Operator.EQ, "field#4" ) );
		list6.add( new SimpleExpression( "parentId", SimpleExpression.Operator.EQ, 10L ) );
		list6.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.EQ, null ) );
		
		ExpressionList list7 = new ExpressionList( ExpressionList.Junction.AND );
		list7.add( new SimpleExpression( "parentId", SimpleExpression.Operator.EQ, null ) );
		list7.add( new SimpleExpression( "ownerId", SimpleExpression.Operator.EQ, null ) );
		list7.add( new SimpleExpression( "title", SimpleExpression.Operator.EQ, "field#4" ) );
		
		List<TestItem> examples = new ArrayList<TestItem>();
		examples.add( new TestItem( 1L, null, 10L, "field#1", true, QueryMark.Q1, QueryMark.Q5 ) );
		examples.add( new TestItem( 2L, null, 10L, "field#2", true, QueryMark.Q1, QueryMark.Q5 ) );
		examples.add( new TestItem( 3L, 5L, null, "field#3", false, QueryMark.Q6 ) );
		examples.add( new TestItem( 4L, null, 10L, "field#4", true, QueryMark.Q1, QueryMark.Q5, QueryMark.Q6 ) );
		examples.add( new TestItem( 5L, 10L, 10L, "field#5", false, QueryMark.Q4, QueryMark.Q6 ) );
		examples.add( new TestItem( 6L, null, 10L, "field#6", true, QueryMark.Q1, QueryMark.Q5 ) );
		examples.add( new TestItem( 7L, 10L, 10L, "field#7", false, QueryMark.Q4, QueryMark.Q6 ) );
		examples.add( new TestItem( 8L, 10L, null, "field#8", true, QueryMark.Q5, QueryMark.Q6 ) );
		examples.add( new TestItem( 9L, null, 10L, "field#9", false, QueryMark.Q1, QueryMark.Q6) );
		examples.add( new TestItem( 10L, 10L, null, "field#10", true, QueryMark.Q5, QueryMark.Q6 ) );
		examples.add( new TestItem( 11L, 8L, 6L, "field#11", true, QueryMark.Q4, QueryMark.Q5 ) );
		examples.add( new TestItem( 12L, 10L, null, "field#12", false, QueryMark.Q6 ) );
		examples.add( new TestItem( 13L, 8L, null, "field#13", true, QueryMark.Q2, QueryMark.Q5, QueryMark.Q6 ) );
		examples.add( new TestItem( 14L, 10L, 10L, "field#14", true , QueryMark.Q4, QueryMark.Q5, QueryMark.Q6) );
		examples.add( new TestItem( 15L, 8L, null, "field#15", false, QueryMark.Q2, QueryMark.Q6 ) );
		examples.add( new TestItem( 16L, null, 10L, "field#16", true, QueryMark.Q1, QueryMark.Q5 ) );
		examples.add( new TestItem( 17L, 10L, 10L, "field#17", false, QueryMark.Q4, QueryMark.Q6 ) );
		examples.add( new TestItem( 18L, null, 10L, "field#18", false, QueryMark.Q1, QueryMark.Q6 ) );
		examples.add( new TestItem( 19L, 15L, null, "field#19", false, QueryMark.Q6 ) );
		examples.add( new TestItem( 20L, null, null, "field#20", true, QueryMark.Q3, QueryMark.Q5, QueryMark.Q6 ) );
		
		IndexProvider indexedExamples = new IndexProvider();
		indexedExamples.addAll( examples );
		
		
		Set<TestItem> result1 = search( TestItem.class, indexedExamples, list1 );
		checkSearchResult(result1, 7, QueryMark.Q1 );
		
		Set<TestItem> result2 = search( TestItem.class, indexedExamples, list2 );
		checkSearchResult(result2, 2, QueryMark.Q2 );
		
		Set<TestItem> result3 = search( TestItem.class, indexedExamples, list3 );
		checkSearchResult(result3, 1, QueryMark.Q3 );
		
		Set<TestItem> result4 = search( TestItem.class, indexedExamples, list4 );
		checkSearchResult(result4, 5, QueryMark.Q4 );
		
		Set<TestItem> result5 = search( TestItem.class, indexedExamples, list5 );
		checkSearchResult(result5, 11, QueryMark.Q5 );
		
		Set<TestItem> result6 = search( TestItem.class, indexedExamples, list6 );
		checkSearchResult(result6, 15, QueryMark.Q6 );
		
		Set<TestItem> result7 = search( TestItem.class, indexedExamples, list7 );
		checkSearchResult(result7, 0, null );
	}

	/**
	 * @param result1
	 * @param i
	 * @param queryMark
	 */
	private void checkSearchResult(Set<TestItem> result1, int count, QueryMark queryMark) {
		assertEquals( "Check items count", count, result1.size());
		for (TestItem testItem : result1) {
			assertTrue( "Check item query mark " + queryMark, testItem.has(queryMark) );
		}
	}
	
	
}
