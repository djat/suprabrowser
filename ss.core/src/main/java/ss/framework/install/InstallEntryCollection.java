package ss.framework.install;

import ss.common.PathUtils;
import ss.framework.entities.xmlentities.IXmlEntityObjectFindCondition;
import ss.framework.entities.xmlentities.XmlListEntityObject;

public class InstallEntryCollection extends XmlListEntityObject<InstallEntry> {

	/**
	 * @param itemType
	 */
	public InstallEntryCollection() {
		super(InstallEntry.class, AbstractInstallEntry.ROOT_ELEMENT_NAME );
	}

	/**
	 * @param name
	 * @return
	 */
	public InstallEntry findEntry(final String name) {
		if ( name == null ) {
			return null;
		}
		return findFirst( new IXmlEntityObjectFindCondition<InstallEntry>() {
			public boolean macth(InstallEntry entityObject) {
				return name.equals( entityObject.getName() );
			}
		});
	}

	/**
	 * @param relativePath
	 */
	public InstallEntry findOrCreate(String relativePath) {
		final String [] parts = PathUtils.splitUnifiedPathParts(relativePath);
		InstallEntryCollection context = this;
		InstallEntry result = null;
		for( String name : parts ) {
			result = context.findEntry(name);
			if ( result == null ) {
				result = new InstallEntry( name );
				context.add( result );				
			}
			context = result.getChildren();
		}
		return result;
	}

	/**
	 * @param entry
	 */
	private void add(InstallEntry entry) {
		super.internalAdd(entry);
	}
	
}
