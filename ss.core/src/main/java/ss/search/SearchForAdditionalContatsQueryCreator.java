/**
 * 
 */
package ss.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.dom4j.Document;

import ss.client.ui.spheremanagement.IManagedSphereVisitor;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereHierarchyBuilder;
import ss.common.ListUtils;
import ss.common.StringUtils;
import ss.domainmodel.SphereStatement;
import ss.server.db.XMLDB;
import ss.server.functions.setmark.common.SetReadOperations;

/**
 * @author zobo
 *
 */
public class SearchForAdditionalContatsQueryCreator {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchForAdditionalContatsQueryCreator.class);
	
	private List<String> spheres = null;
	
	private Query query = null;
	
	private final static String[] termsTypeToRemove = {"type:comment", "type:externalemail", "type:file", "type:keywords", "type:message", "type:terse"};
	
	private final static String[] termsTypeNeeded = {"bookmark", "contact"};
	
	//private final static String[] termsContentNeeded = {"subject:", "body:", "contact:", "content:", "comment:", "keywords:"};
	
	private final static String termSphereId = "sphere_id";
	
	//private final static String termGiver = "giver:";
	
	public SearchForAdditionalContatsQueryCreator( final List<String> baseSpheres, final Query originalQuery ){
		setUp( baseSpheres, originalQuery );
	}
	
	private void setUp( final List<String> baseSpheres, final Query originalQuery ){
		final Set<Term> terms = new TreeSet<Term>();
		originalQuery.extractTerms( terms );
		if ( (terms == null) || (terms.isEmpty()) ) {
			logger.error("Terms are void");
			return;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug(ListUtils.allValuesToString(terms));
			}
		}
		if ( !isNeededEnabled(terms) ) {
			if (logger.isDebugEnabled()) {
				logger.debug("Needed terms not enabled, returning");
			}
			return;
		}
		final List<String> newBaseSpheres = filterSpheres(baseSpheres, terms);
		if (logger.isDebugEnabled()) {
			logger.debug("newBaseSpheres : " + ListUtils.allValuesToString(newBaseSpheres));
		}
		this.query = constructAdditionalQuery( originalQuery, terms );
		if ( this.query != null ) {
			this.spheres = constructAdditionalSpheres( newBaseSpheres );
		}	
	}
	
	private boolean isNeededEnabled( final Set<Term> terms ){
		for( Term term : terms ) {
			if (term.field().equals("type")){
				for (String neededType : termsTypeNeeded) {
					if (term.text().equals(neededType)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private List<String> filterSpheres( final List<String> baseSpheres,
			final Set<Term> terms) {
		if ( (baseSpheres == null) || (baseSpheres.isEmpty())) {
			return new ArrayList<String>();
		}
		final List<String> spheresFromQuery = extractTermValues(terms, termSphereId);
		if (logger.isDebugEnabled()) {
			logger.debug("spheresFromQuery : " + ListUtils.allValuesToString(spheresFromQuery));
		}
		if ((spheresFromQuery!=null)&&(!spheresFromQuery.isEmpty())) {
			List<String> result = new ArrayList<String>();
			for (String s : baseSpheres) {
				if ((s != null) && (spheresFromQuery.contains(s.toLowerCase()))){
					result.add(s);
				}
			}
			return result;
		} else {
			return baseSpheres;
		}
	}
	
	private List<String> extractTermValues( final Set<Term> terms, final String extractTerm ){
		final List<String> values = new ArrayList<String>();
		for ( Term term : extractTerms(terms,extractTerm) ) {
			if ( StringUtils.isNotBlank(term.text()) ) {
				values.add( term.text() );
			}
		}
		return values;
	}
	
	private List<Term> extractTerms( final Set<Term> terms, final String extractTerm ){
		final List<Term> result = new ArrayList<Term>();
		for ( Term term : terms ) {
			if (term.field().equals(extractTerm)) {
				result.add( term );
			}
		}
		return result;
	}

	private Query constructAdditionalQuery( final Query originalQuery, final Set<Term> terms ) {
		if (logger.isDebugEnabled()) {
			logger.debug(originalQuery.toString());
		}
		String rawQuery = new String( originalQuery.toString() );
		for ( String termString : termsTypeToRemove ){
			rawQuery = removeWholeTerm(rawQuery, termString);
		}
		for ( Term term : extractTerms(terms,termSphereId) ) {
			rawQuery = removeWholeTerm(rawQuery, term.toString());
		}
		rawQuery = cleanUpQuery( rawQuery );
		Query q = getQuery( rawQuery );
		return q;
	}
	
	private String cleanUpQuery( final String rawQuery ) {
		if (StringUtils.isBlank(rawQuery)) {
			return rawQuery;
		}
		StringBuilder b = new StringBuilder();
		final char[] array = rawQuery.toCharArray();
		for ( int i = 0; i < array.length; i++ ) {
			if ( array[i] == '+' ) {
				if ((i+1) >= array.length) {
					break;
				}
				if (((i+1) < array.length) && (array[i+1] == ' ')) {
					continue;
				}
				if (((i+1) < array.length) && (array[i+1] == '(')) {
					int j = i+2;
					while ( (j < array.length) && (array[j] == ' ') ) {
						j++;
					}
					if ( j >= array.length ) {
						break;
					}
					if ( array[j] == ')' ) {
						i = j;
						continue;
					}
				}
			}
			b.append( array[i] );
		}
		return b.toString();
	}

	private Query getQuery( final String rawQuery ) {
		Analyzer analyzer = new StandardAnalyzer();
		Query query = null;
		try {
			query = new QueryParser("body", analyzer).parse(rawQuery);
		} catch (ParseException ex) {
			logger.error("error" + ex);
		}
		return query;
	}
	
	private String removeWholeTerm( final String rawQuery, final String term ){
		final int index = rawQuery.lastIndexOf( term );
		if ( index != -1 ) {
			final String first = rawQuery.substring( 0, index );
			final String second = rawQuery.substring( index );
			return first + second.replace(term, "");
		} else {
			return rawQuery;
		}
	}

	private List<String> constructAdditionalSpheres( final List<String> baseSpheres ) {
		if (logger.isDebugEnabled()) {
			logger.debug("baseSpheres: " + ListUtils.allValuesToString(baseSpheres));
		}
		final List<String> newspheres = new ArrayList<String>();
		if ( (baseSpheres==null) || (baseSpheres.isEmpty()) ) {
			return newspheres;
		}
		final SphereHierarchyBuilder builder = getBuilder();
		
		builder.getResult().traverse(new IManagedSphereVisitor(){
			
			private int counter = 0;

			public void beginNode(ManagedSphere sphere) {
				final String systemName = sphere.getId(); 
				if (baseSpheres.contains( systemName )) {
					this.counter++;
				} else {
					if ((this.counter > 0) && ( !sphere.getStatement().isEmailBox() )) {
						newspheres.add(systemName);
					}
				}
			}

			public void endNode(ManagedSphere sphere) {
				if (baseSpheres.contains( sphere.getId() )) {
					this.counter--;
				}
			}
			
		});
		if (logger.isDebugEnabled()) {
			logger.debug("newspheres size: " + newspheres.size());
			for ( String s : newspheres ) {
				logger.debug("next sphere: " + s);
			}
		}
		return newspheres;
	}

	private SphereHierarchyBuilder getBuilder(){
		final XMLDB xmldb = new XMLDB();
		final Vector<Document> docs = xmldb.getAllSpheres();
		final String rootId = xmldb.getSupraSphere().getSupraSphereName();
		final List<SphereStatement> spheres = new ArrayList<SphereStatement>();
		if (logger.isDebugEnabled()) {
			logger.debug("rootId : " + rootId);
			logger.debug("docs : " + ((docs == null) ? " null" : docs.size()));
		}
		if ( docs == null ) {
			logger.error("No spheres definitions");
		} else {
			for (Document doc : docs) {
				if (doc != null) {
					SphereStatement st = SphereStatement.wrap(doc);
					if (st.isSphere()) {
						spheres.add( st );
					}
				}
			}
		}
		
		final SphereHierarchyBuilder builder = new SphereHierarchyBuilder( new ISphereDefinitionProvider(){

			public void checkOutOfDate() {
			}

			public List<SphereStatement> getAllSpheres() {
				return spheres;
			}

			public String getRootId() {
				return rootId;
			}

			public boolean isSphereVisible(SphereStatement sphere) {
				if (sphere.isDeleted()) {
					return false;
				}
				return true;
			}

			public void outOfDate() {
			}
			
		});	
		return builder;
	}

	public List<String> getSpheres() {
		return this.spheres;
	}

	public Query getQuery() {
		return this.query;
	}
	
	public boolean isValid(){
		return ((this.query != null) && (this.spheres!=null) && (!this.spheres.isEmpty()));
	}
}
