/*
 * Created on Apr 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.util;

/**
 * @author david
 * 
 */

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.ui.peoplelist.SphereMember;

public class VotingEngine {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(VotingEngine.class);

	private Document lastSelected = null;

	public void setLastSelected( final Document overrideDoc) {
		this.lastSelected = overrideDoc;
	}

	@SuppressWarnings("unchecked")
	public boolean hasVoted(final String contact_name1, Document dataDoc) {
		try {
			return votedContact( SphereMember.normalizeName(contact_name1), 
					(this.lastSelected != null) ? this.lastSelected : dataDoc);
		} catch (Exception e) {
			logger.error("Error in determing voting contact",e);
		}
		return false;
	}

	public void notifyEndUpdate() {
		this.lastSelected = null;
	}
	
	public static boolean votedContact( final String contactName, final Document doc ){
		if ( doc == null ) {
			return false;
		}
		final Element view = doc.getRootElement();
		if ( view == null ) {
			return false;
		}
		final Element voting_model = view.element("voting_model");
		if ( voting_model == null ) {
			return false;
		}
		final Element tally = voting_model.element("tally");
		if ( tally == null ) {
			return false;
		}
		final List<Element> elements = tally.elements();
		if ( elements == null ) {
			return false;
		}

		for ( Element one : elements ) {
			String voter = one.attributeValue("value");
			if ( (voter != null) && (voter.equals( contactName )) ) {
				return true;
			}
		}
		return false;
	}
}
