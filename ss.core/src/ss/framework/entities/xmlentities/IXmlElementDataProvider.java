package ss.framework.entities.xmlentities;

import org.dom4j.Element;

public interface IXmlElementDataProvider {

	/**
	 * Return true if correspondent element exits
	 */
	boolean isExist();
	
	/**
	 * Return correspondent element or null if element is not exist
	 */
	Element getElement();
	
	/**
	 * Returns correspondent element. Throws RuntimeException if element not found.
	 */
	Element requireElement() throws ElementNotFoundException;
	
	/**
	 * Return correspondent element. 
	 * If element is not exist then it will be created.
	 */
	Element getOrCreateElement();

	/**
	 * 
	 */
	void cleanup();
		
	void removeAllMatched();
	
}
