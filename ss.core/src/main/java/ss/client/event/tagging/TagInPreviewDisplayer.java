/*
 * Created on Nov 10, 2005
 */
package ss.client.event.tagging;

import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.event.tagging.obtainer.DataForTagObtainer;
import ss.client.localization.LocalizationLinks;
import ss.client.networking.protocol.getters.GetInfoOnRelatedKeywordsCommand;
import ss.client.ui.MessagesPane;
import ss.client.ui.PreviewHtmlTextCreator;
import ss.common.StringUtils;
import ss.common.ThreadUtils;
import ss.domainmodel.Statement;

public class TagInPreviewDisplayer {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TagInPreviewDisplayer.class);
	
	private static final String TOTAL_NUMBER_OF_TAGS = "ASSETTYPEACTIONS.TOTAL_NUMBER_OF_TAGS";
	
	private static final String TOTAL_NUMBER_WITH_THIS_TAG = "ASSETTYPEACTIONS.TOTAL_NUMBER_WITH_THIS_TAG";
	
	private final static String[] HTags = {"<H1>", "<H2>", "<H3>", "<H4>", "<H5>", "</H1>", "</H2>", "</H3>", "</H4>", "</H5>"}; 
	
	private final static ResourceBundle bundle = 
		ResourceBundle.getBundle(LocalizationLinks.CLIENT_EVENT_ASSETTYPEACTIONS);
	
	private final Document doc;
	
	private final MessagesPane mp;
	
	private final Hashtable session;
	
	private Hashtable<String, String> names;
	
	private Hashtable<String, String> tagsInSpheres;
	
	private InfoOnRelatedKeywordsData infoOnRelatedKeywordsData;

	private final String subject;
	 
	public TagInPreviewDisplayer( final MessagesPane mp, final Statement st, final Hashtable session ){
		this.doc = st.getBindedDocument();
		this.subject = st.getSubject();
		this.mp = mp;
		this.session = session;
	}

	public void processKeywordSelected() {
		
		showMockUp();
		
		showSelection();

	}
	
	private void showSelection() {
		Thread t = new Thread(){

			private MessagesPane mp = TagInPreviewDisplayer.this.mp;
			
			@Override
			public void run() {
				this.mp.setPreviewHtmlText( new PreviewHtmlTextCreator( this.mp ) );

				this.mp.getPreviewHtmlText().addText( infoRelatedTaggedDocuments( ) );
				this.mp.getPreviewHtmlText().addText( infoRelatedKeywords( ) );

				this.mp.showSmallBrowser(TagInPreviewDisplayer.this.session, true, null, this.mp
						.getPreviewHtmlText().getText(), null, null);
			}
			
		};
		ThreadUtils.start( t , "Data obtainer for displaying tag in preview" );
	}

	private void showMockUp() {
		this.mp.setPreviewHtmlText( new PreviewHtmlTextCreator( this.mp ) );

		this.mp.getPreviewHtmlText().addText( asHeader("Obtaining information...") );

		this.mp.showSmallBrowser(this.session, true, null, this.mp
				.getPreviewHtmlText().getText(), null, null);
	}

	private String infoRelatedTaggedDocuments() {
		String s = "";
		final DataForTagObtainer obtainer = new DataForTagObtainer( this.subject, this.mp.client );
		int countBookmarks = obtainer.getBoomarks().getCount();
		int countFiles = obtainer.getFiles().getCount();
		int countContacts = obtainer.getContacts().getCount();
		if (logger.isDebugEnabled()) {
			logger.debug("Data obtained: countBookmarks: " + countBookmarks + "countFiles: " + countFiles + 
					"countContacts: " + countContacts);
		}
		if ((countBookmarks <= 0) && (countFiles <= 0) && (countContacts <= 0)) {
			s += asHeader( "There are no tagged elements" );
		} else {
			s += asHeader( "<div style=\"cursor:pointer\" onclick=\"scroller_object.on_mouse_for_keyword_name_not_unique('"+ this.subject 
					+"')\"> Keyword: \"" + this.subject + "\" , Show tagged elements (" 
					+ printOnCount(countBookmarks,"bookmark") + ", " 
					+ printOnCount(countFiles,"file") + ", " 
					+ printOnCount(countContacts,"contact") + ") </div>");
		}
		s+= "<br>";
		return s;
	}
	
	private String printOnCount( final int count, final String type ){
		return ((count <= 0) ? "no " : "" + count + " ") + type + ( (count == 1) ? "" : "s");
	}

	private String infoRelatedKeywords(){
		String s = "";
		if (getRelatedKeywordsNames().isEmpty()) {
			s += asHeader( "There are no related keywords" );
		} else {
			s += "Related keywords: <br>";
			for (String uniqueId : getRelatedKeywordsNames().keySet()) {
				s += "<div style=\"cursor:pointer\" onclick = \"scroller_object.on_mouse_for_keyword_name_to_show_in_preview('"
					+ getRelatedKeywordsNames().get( uniqueId ) + "' , '" + getInfoForLocationsInSpheres().get( uniqueId ) +"')\">" 
					+ "<I><U>" + getRelatedKeywordsNames().get( uniqueId ) + "</I></U>" + 
					" ( related with current: " + getInfoOnRelated().getKeywordsCountForTag(uniqueId) + " ) </div>";
			}			
		}
		s+= "<br>";
		
		return s;
	}
	
	private InfoOnRelatedKeywordsData getInfoOnRelated(){
		if (this.infoOnRelatedKeywordsData == null) {
			final GetInfoOnRelatedKeywordsCommand command = new GetInfoOnRelatedKeywordsCommand();
			command.setKeywordsHash( getInfoForLocationsInSpheres() );
			this.infoOnRelatedKeywordsData =  command.execute(this.mp.client, InfoOnRelatedKeywordsData.class);
		}
		if (this.infoOnRelatedKeywordsData == null) {
			this.infoOnRelatedKeywordsData = new InfoOnRelatedKeywordsData();
		}
		return this.infoOnRelatedKeywordsData;
	}
	
	private static String asHeader( final String text ){
		return asHeader( text, 5 );
	}
	
	private static String asHeader( final String text, final int number ){
		return getTagHeader( number, true ) + text + getTagHeader( number, false );
	}
	
	private static String getTagHeader( int number, boolean opened ) {
		if ((number < 1) || (number > 5)) {
			number = 5;
		}
		return HTags[--number + (opened ? 0 : 5)];
	}
	
	private Hashtable<String, String> getRelatedKeywordsNames(){
		if ( this.names == null ) {
			constructInfoAboutRelatedTags();
		}
		return this.names;
	}
	
	private Hashtable<String, String> getInfoForLocationsInSpheres(){
		if ( this.tagsInSpheres == null ) {
			constructInfoAboutRelatedTags();
		}
		return this.tagsInSpheres;
	}

	private void constructInfoAboutRelatedTags(){
		this.names = new Hashtable<String, String>();
		this.tagsInSpheres = new Hashtable<String, String>();
		final List elements = this.doc.getRootElement().elements("keywords");
		if (elements == null) {
			return;
		}
		for (Object o : elements) {
			Element elem = (Element) o;
			String id = elem.attributeValue("unique_id");
			String name = elem.attributeValue("value");
			String sphereId = elem.attributeValue("current_location");
			if ( StringUtils.isNotBlank( name ) && StringUtils.isNotBlank( id ) && StringUtils.isNotBlank( sphereId ) ) {
				this.names.put( id , name );
				this.tagsInSpheres.put( id , sphereId );
			}
		}
	}
}
