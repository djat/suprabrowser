/**
 * 
 */
package ss.client.ui.viewers.comment;

import java.util.StringTokenizer;

import ss.domainmodel.CommentStatement;

/**
 * @author roman
 *
 */
public class HTMLCommentListBuilder {

	private String bodyContent;
	private static final String TAIL = "</body></html>";
	private static final String HEAD = "<html><head><style type=\"text/css\">" +
			".giver_out_back{font-family:times; font-size:12; font-weight:bold; color:blue}" +
			".giver_in_back{font-family:times; font-size:12; font-weight:bold; color:green}" +
			".moment_out_back{font-family:times; font-size:10; font-weight:bold; color:blue}" +
			".moment_in_back{font-family:times; font-size:10; font-weight:bold; color:green}" +
			".giver_out{font-family:times; font-size:12; font-weight:bold; color:blue}" +
			".giver_in{font-family:times; font-size:12; font-weight:bold; color:green}" +
			".moment_out{font-family:times; font-size:10; font-weight:bold; color:blue}" +
			".moment_in{font-family:times; font-size:10; font-weight:bold; color:green}" +
			".content{font-family:times; font-size:10; font-weight:lighter; color:black}" +
			".content_back{font-family:times; font-size:10; background-color:gainsboro; color:black}" +
			"</style></head><body>";
	private final String contactName;
	
	public HTMLCommentListBuilder(final String contactName) {
		this.bodyContent = "";
		this.contactName = contactName;
	}
	
	public void addComment(CommentStatement comment, String selectedId) {
		String giver = comment.getGiver();
		String moment = new StringTokenizer(comment.getMoment(), " ").nextToken();
		String content = comment.getComment();

		String giver_style = (this.contactName.equals(giver) ? "giver_out" : "giver_in");
		String moment_style = (this.contactName.equals(giver) ? "moment_out" : "moment_in");
		String content_style = "content";

		String div = "<div>";
		if(comment.getMessageId().equals(selectedId)) {
			div = "<div style=\"background-color:rgb(220,220,240);\">";
		}
		
		String messageBlock = div+"<span class=\""+giver_style+"\" align=\"left\">"+giver+"</span>" +
		"<span class=\""+moment_style+"\" align=\"right\">("+moment+"):</span><br>" +
		"<span class=\""+content_style+"\">"+content+"</div><br>";
		
		this.bodyContent = this.bodyContent+messageBlock;
	}
	
	public String getHtmlText() {
		return HEAD+this.bodyContent+TAIL;
	}
}
