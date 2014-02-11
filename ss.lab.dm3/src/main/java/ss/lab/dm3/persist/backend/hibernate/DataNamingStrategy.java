package ss.lab.dm3.persist.backend.hibernate;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class DataNamingStrategy extends ImprovedNamingStrategy {

	/**
	 * 
	 */
	private static final String ID_COLUMN_NAME = "id";
	private static final String DATA_SUFFIX = "_data";
	private static final String UNDERSCORE = "_";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7888190276825785506L;

	/* (non-Javadoc)
	 * @see org.hibernate.cfg.ImprovedNamingStrategy#classToTableName(java.lang.String)
	 */
	@Override
	public String classToTableName(String className) {
		String tableName = super.classToTableName(className);
		if ( tableName.endsWith( DATA_SUFFIX ) ) {
			tableName = tableName.substring( 0, tableName.length() - DATA_SUFFIX.length() );
		}
		return tableName;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cfg.ImprovedNamingStrategy#foreignKeyColumnName(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String foreignKeyColumnName(String propertyName,
			String propertyEntityName, String propertyTableName,
			String referencedColumnName) {
		final String columnName = super.foreignKeyColumnName(propertyName, propertyEntityName,
					propertyTableName, referencedColumnName);
		if ( referencedColumnName.equals( ID_COLUMN_NAME ) ) {
			return columnName + UNDERSCORE + referencedColumnName;
		}
		else {
			return columnName;
		}
	}
	
	
}
