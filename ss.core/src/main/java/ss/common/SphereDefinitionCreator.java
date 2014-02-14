/*
 * Created on Mar 28, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.common;

import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ss.util.SupraXMLConstants;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SphereDefinitionCreator {
    
    public static Document createDefinition(String displayName, String systemName) {

	Document createDoc = DocumentHelper.createDocument();

	Element root = createDoc.addElement("sphere").addAttribute(
		"display_name", displayName).addAttribute("system_name",
		systemName);

	//@SuppressWarnings("unused")
	Element newType = root.addElement("thread_types");

	Date current = new Date();
	String moment = DateUtils.dateToXmlEntityString(current);

	root.element("thread_types").addElement("terse").addAttribute(
		"enabled", "true").addAttribute("modify", "own");
	root.element("thread_types").addElement("message").addAttribute(
		"enabled", "true").addAttribute("modify", "own");
    root.element("thread_types").addElement(SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL).addAttribute(
            "enabled", "true").addAttribute("modify", "own");
	root.element("thread_types").addElement("bookmark").addAttribute(
		"enabled", "true").addAttribute("modify", "own");
	root.element("thread_types").addElement("rss").addAttribute("enabled",
		"true").addAttribute("modify", "own");
	root.element("thread_types").addElement("file").addAttribute("enabled",
		"true").addAttribute("modify", "own");
	root.element("thread_types").addElement("keywords").addAttribute(
		"enabled", "true").addAttribute("modify", "own");

	root.element("thread_types").addElement("contact").addAttribute(
		"enabled", "true").addAttribute("modify", "own");
	root.element("thread_types").addElement("sphere").addAttribute(
		"enabled", "true").addAttribute("modify", "own");

	root.addElement("expiration").addAttribute("value", "1 week");

	root.addElement("default_delivery").addAttribute("value", "normal");
	root.addElement("default_type").addAttribute("value", "terse");
	root.addElement("voting_model").addAttribute("type", "absolute")
		.addAttribute("desc", "Absolute without qualification");

	root.addElement("type").addAttribute("value", "sphere");
	root.addElement("thread_type").addAttribute("value", "sphere");

	root.element("voting_model").addElement("tally").addAttribute(
		"number", "0.0").addAttribute("value", "0.0");
	root.addElement("body").addElement("orig_body");

	root.addElement("moment").addAttribute("value", moment);
	root.addElement("status").addAttribute("value", "confirmed");

	return createDoc;

    }

	

}
