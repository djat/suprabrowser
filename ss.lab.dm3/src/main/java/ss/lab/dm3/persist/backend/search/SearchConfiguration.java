package ss.lab.dm3.persist.backend.search;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author dmitry
 *
 */
public class SearchConfiguration {

	public static final String INDEX_FOLDER = "index";
	
	/**
	 * TODO should be some mechanism to guarantee searchLockProvider singleton for 
	 * system connection provider
	 */
	private SearchLockProvider searchLockProvider = new SearchLockProvider();
	
	private File baseDir = new File( System.getProperty("user.dir"), INDEX_FOLDER );
	
	public SearchConfiguration(File baseDir) {
		super();
		this.baseDir = baseDir;
	}
	
	public SearchConfiguration() {
		super();
	}

	public File getBaseDir() {
		return this.baseDir;
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public SearchLockProvider getLockProvider() {
		return this.searchLockProvider;
	}

	public SearchLockProvider getSearchLockProvider() {
		return searchLockProvider;
	}

	public void setSearchLockProvider(SearchLockProvider searchLockProvider) {
		this.searchLockProvider = searchLockProvider;
	}
	
	
	
	
}
