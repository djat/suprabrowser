package ss.suprabrowser.highlight;

import java.util.*;

/**
 * User: krishnm
 * Date: Jul 26, 2006
 * Time: 8:42:58 PM
 */

/**
 * Container for the keywords and the keywords highlight properties.
 */

public class HighlightItems
{
    private Map<String, HighlightProperties> highliteMap = new HashMap<String, HighlightProperties>();
    
    private List<String> order = new ArrayList<String>();

    /**
     * Adds the keyword and its properties to the map.
     * @param keyword
     * @param property
     */
    public void add(String keyword, HighlightProperties property)
    {
    	if (!this.order.contains( keyword )) {
    		this.order.add( keyword );
    	}
    	this.highliteMap.put(keyword, property);
    }

    /**
     * Returns a highlight properties for a given keyword from the map.
     * @param keyword
     * @return HighlightProperties
     */
    public HighlightProperties get(String keyword)
    {
        return this.highliteMap.get(keyword);
    }

    /**
     * Returns the list of all the keywords from the map.
     * @return String[] of keywords
     */
    public String[] getKeywords()
    {
        ArrayList<String> v = new ArrayList<String>();

        for (Iterator i = this.highliteMap.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry currentEntry = (Map.Entry)i.next();
            v.add((String)currentEntry.getKey());
        }

        return v.toArray(new String[0]);
    }

    /**
     * Returns all the information for the hight light properites of all the keywords in a specific format of
     * javascript variable style which is used to inject into the head element within js script tag.
     * @return String
     */
    public String getPropertyInfo()
    {
        StringBuffer buf = new StringBuffer();

        for (Iterator i = this.highliteMap.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry currentEntry = (Map.Entry)i.next();
            buf.append(((HighlightProperties)currentEntry.getValue()).getInfo());
        }

        return buf.toString();
    }

	public List<String> getOrder() {
		return this.order;
	}
}
