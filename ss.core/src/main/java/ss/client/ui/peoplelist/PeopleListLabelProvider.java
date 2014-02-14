/**
 * 
 */
package ss.client.ui.peoplelist;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.PeopleTable;
import ss.client.ui.SDisplay;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class PeopleListLabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	private static final Color DARK_GREEN = new Color(Display.getDefault(), 0, 128, 19);
	
	private static final FontData DEFAULT_FONT_DATA = new FontData( "Microsoft Sans Serif", 8, SWT.NORMAL );
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(PeopleListLabelProvider.class);
	
	private final Font boldFont;
	
	private PeopleTable pt;
	
	public PeopleListLabelProvider(Font messagesPaneFont, PeopleTable pt) {
		FontData fontData = getBasicFontData(messagesPaneFont);
    	this.boldFont = new Font(SDisplay.display.get(), fontData.getName(), 8, SWT.NORMAL );
    	this.pt = pt;
	}

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}
	
	/**
	 * @param mp
	 * @return
	 */
	private FontData getBasicFontData(Font messagesPaneFont) {
		FontData[] fontDatas = messagesPaneFont != null ? messagesPaneFont.getFontData() : null;
		if ( fontDatas != null &&
			 fontDatas.length > 0 ) {
			return fontDatas[ 0 ];
		}
		else {
			return DEFAULT_FONT_DATA;
		}
	}


	public String getColumnText(Object o, int index) {
		SphereMember sm = (SphereMember)o;
		if(index==0) {
			return sm.getName();
		}
		return null;
	}


	public Color getBackground(Object arg0, int arg1) {
		return null;
	}


	public Color getForeground(Object o, int index) {
		SphereMember member = (SphereMember)o;
		final SphereMemberState memberState = member.getState();
		if(index==0) {
			Document doc = this.pt.getSelectedDocument();
			if (!memberState.isTyping() && doc!=null &&  
					this.pt.getOwner().getVotingEngine().hasVoted(member.getName(), doc) ) {
				return DARK_GREEN;
			}
			
			Color color = memberState.getDisplayColorSWT();
			return color;
		}
		return null;
	}


	public void addListener(ILabelProviderListener arg0) {
		
	}


	public void dispose() {
		
	}


	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
		
	}

	
	public Font getFont(Object o, int arg1) {
		return this.boldFont;
	}
}
