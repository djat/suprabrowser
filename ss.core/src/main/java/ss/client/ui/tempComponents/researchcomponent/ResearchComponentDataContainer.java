/**
 * 
 */
package ss.client.ui.tempComponents.researchcomponent;

import java.io.Serializable;
import java.util.List;

/**
 * @author zobo
 * 
 */
public class ResearchComponentDataContainer implements Serializable {

	private static final long serialVersionUID = 6513817810123957278L;

	public static final int DEAFULT_SAME_KEYWORDS_COUNT = 3;

	private List<String> allowedUsersContactNames;

	private boolean lookInOwn;

	private boolean lookInOthers;

	private boolean newFromLastResearch;
	
	private boolean contactsAsKeywords;
	
	private boolean useRecent;
	
	private int numberRecentSpheres;
	
	private int numberRecentTags;

	private int sameKeywordsMaxCount = DEAFULT_SAME_KEYWORDS_COUNT;

	
	/// To Returning purposes. 
	private List<String> contacts;

	public ResearchComponentDataContainer() {
		
	}
	
	public ResearchComponentDataContainer(final boolean lookInOwn,
			final boolean lookInOthers, final boolean newFromLastResearch,
			final List<String> allowedUsersContactNames, final boolean useRecent, 
			final int numberRecentTags, final int numberRecentSpheres, final boolean contactsAsKeywords,
			final int sameKeywordsMaxCount) {
		super();
		this.lookInOwn = lookInOwn;
		this.lookInOthers = lookInOthers;
		this.newFromLastResearch = newFromLastResearch;
		this.allowedUsersContactNames = allowedUsersContactNames;
		this.useRecent = useRecent;
		this.numberRecentSpheres = numberRecentSpheres;
		this.numberRecentTags = numberRecentTags;
		this.contactsAsKeywords = contactsAsKeywords;
		this.sameKeywordsMaxCount = sameKeywordsMaxCount;
	}

	public boolean isLookInOthers() {
		return this.lookInOthers;
	}

	public boolean isLookInOwn() {
		return this.lookInOwn;
	}

	public void setLookInOthers(final boolean lookInOthers) {
		this.lookInOthers = lookInOthers;
	}

	public void setLookInOwn(final boolean lookInOwn) {
		this.lookInOwn = lookInOwn;
	}

	public boolean isNewFromLastResearch() {
		return this.newFromLastResearch;
	}

	public void setNewFromLastResearch(boolean newFromLastResearch) {
		this.newFromLastResearch = newFromLastResearch;
	}
	
	public List<String> getAllowedUsersContactNames() {
		return this.allowedUsersContactNames;
	}

	public void setAllowedUsersContactNames(List<String> allowedUsersContactNames) {
		this.allowedUsersContactNames = allowedUsersContactNames;
	}
	
	public boolean isContactsAsKeywords() {
		return this.contactsAsKeywords;
	}

	public void setContactsAsKeywords(boolean contactsAsKeywords) {
		this.contactsAsKeywords = contactsAsKeywords;
	}
	
	public int getNumberRecentSpheres() {
		return this.numberRecentSpheres;
	}

	public void setNumberRecentSpheres(int numberRecentSpheres) {
		this.numberRecentSpheres = numberRecentSpheres;
	}

	public int getNumberRecentTags() {
		return this.numberRecentTags;
	}

	public void setNumberRecentTags(int numberRecentTags) {
		this.numberRecentTags = numberRecentTags;
	}

	public boolean isUseRecent() {
		return this.useRecent;
	}

	public void setUseRecent(boolean useRecent) {
		this.useRecent = useRecent;
	}

	@Override
	protected Object clone() {
		ResearchComponentDataContainer newOne = new ResearchComponentDataContainer();
		newOne.setAllowedUsersContactNames(this.allowedUsersContactNames);
		newOne.setLookInOthers(this.lookInOthers);
		newOne.setLookInOwn(this.lookInOwn);
		newOne.setNewFromLastResearch(this.newFromLastResearch);
		newOne.setContactsAsKeywords(this.contactsAsKeywords);
		newOne.setUseRecent(this.useRecent);
		newOne.setNumberRecentSpheres(this.numberRecentSpheres);
		newOne.setNumberRecentTags(this.numberRecentTags);
		newOne.setSameKeywordsMaxCount(this.sameKeywordsMaxCount);
		return newOne;
	}

	@Override
	public String toString() {
		String s = "Data: ";
		s += "lookInOwn: " + this.lookInOwn;
		s += ", lookInOthers: " + this.lookInOthers;
		s += ", newFromLastResearch: " + this.newFromLastResearch;
		return s;
	}

	/**
	 * 
	 */
	public int getSameKeywordsMaxCount() {
		return this.sameKeywordsMaxCount;
	}

	public void setContactStrings( final List<String> contacts ) {
		this.contacts = contacts;		
	}

	public List<String> getContacts() {
		return this.contacts;
	}

	public void setSameKeywordsMaxCount( final int sameKeywordsMaxCount ) {
		this.sameKeywordsMaxCount = sameKeywordsMaxCount;
	}

	/**
	 * @param keyword
	 * @return
	 */
	public boolean containsContactIgnoreCase( final String keyword ) {
		if (this.contacts == null) {
			return false;
		}
		for (String c : this.contacts) {
			if (c.equalsIgnoreCase( keyword )) {
				return true;
			}
		}
		return false;
	}
}
