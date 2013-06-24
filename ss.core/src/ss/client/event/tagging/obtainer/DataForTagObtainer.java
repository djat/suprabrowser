/**
 * 
 */
package ss.client.event.tagging.obtainer;

import ss.client.networking.DialogsMainCli;

/**
 * @author zobo
 *
 */
public class DataForTagObtainer {

	private final BookmarksForTagObtainer bookmarks;
	
	private final FileForKeywordObtainer files;
	
	private final ContactsForTagObtainer contacts;

	public DataForTagObtainer( final String tagName, final DialogsMainCli client ) {
		this.bookmarks = new BookmarksForTagObtainer( tagName, client );
		this.files = new FileForKeywordObtainer( tagName, client );
		this.contacts = new ContactsForTagObtainer( tagName, client );
	}
	
	public BookmarksForTagObtainer getBoomarks(){
		return this.bookmarks;
	}

	public FileForKeywordObtainer getFiles() {
		return this.files;
	}

	public ContactsForTagObtainer getContacts() {
		return this.contacts;
	}
}
