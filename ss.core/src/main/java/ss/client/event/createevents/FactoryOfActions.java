/**
 * 
 */
package ss.client.event.createevents;

import java.util.Hashtable;

import ss.client.ui.tempComponents.DropDownItemAbstractAction;
import ss.util.SupraXMLConstants;

/**
 * @author zobo
 *
 */
public class FactoryOfActions {

    /**
     * 
     */
    private FactoryOfActions() {
        super();
    }

    public static DropDownItemAbstractAction getAction(String name, Hashtable session){
        
        if (name.equals(CreateBookmarkAction.BOOKMARK_TITLE))
            return new CreateBookmarkAction(session);
        
        if (name.equals(CreateContactAction.CONTACT_TITLE))
            return new CreateContactAction(session);
        
        if (name.equals(CreateFileAction.FILE_TITLE))
            return new CreateFileAction(session);
        
        if (name.equals(CreateFilesystemAction.FILESYSTEM_TITLE))
            return new CreateFilesystemAction(session);
        
        if (name.equals(CreateMessageAction.MESSAGE_TITLE))
            return new CreateMessageAction(session);
        
        if (name.equals(CreateResearchAction.RESEARCH_TITLE))
            return new CreateResearchAction(session);
        
        if (name.equals(CreateRssAction.RSS_TITLE))
            return new CreateRssAction(session);
        
        if (name.equals(CreateSphereAction.SPHERE_TITLE))
            return new CreateSphereAction(session);
        
        if (name.equals(CreateTerseAction.TERSE_TITLE))
            return new CreateTerseAction(session);
        
        if (name.equals(CreateKeywordsAction.KEYWORD_TITLE))
            return new CreateKeywordsAction();
        
        if (name.equalsIgnoreCase(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL))
            return new CreateEmailAction(session);
        
        return null;
    }
}
