/**
 * 
 */
package ss.client.ui;

import java.io.IOException;
import java.util.Calendar;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import ss.common.DateUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ExternalEmailStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.SystemMessageStatement;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.util.ImagesPaths;
import ss.util.SessionConstants;

/**
 * @author roman
 *
 */
public class TableLabelProvider implements ITableLabelProvider, IColorProvider, IFontProvider {
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TableLabelProvider.class);

	private static final FontData DEFAULT_FONT_DATA = new FontData( "Microsoft Sans Serif", 8, SWT.NORMAL );
	
	private final Color green;
    private final Color red;
    
    private final Font normalFont;
    private final Font boldFont;
   
    private MessagesPane mp;
    
    public static final String ACK = "ack";
    public static final String SYN = "syn";
    
    private WorkflowConfiguration configuration = null;
    
    
    public TableLabelProvider(MessagesPane mp) {
    	this.mp = mp;
    	this.green = new Color(mp.getDisplay(), 58,90,56);
    	this.red = new Color(mp.getDisplay(), 192,0,0);
    	FontData fontData = getBasicFontData(mp);
    	this.normalFont = new Font(mp.getDisplay(), fontData.getName(), fontData.getHeight(), SWT.NONE);
    	this.boldFont = new Font(mp.getDisplay(), fontData.getName(), fontData.getHeight(), SWT.BOLD );
    	initConfiguration();
    }

	/**
	 * @return
	 */
	private FontData getBasicFontData(MessagesPane messagesPane) {
		Font messagesPaneFont = messagesPane.getFont();
		FontData[] fontDatas = messagesPaneFont != null ? messagesPaneFont.getFontData() : null;
		if ( fontDatas != null &&
			 fontDatas.length > 0 ) {
			return fontDatas[ 0 ];
		}
		else {
			return DEFAULT_FONT_DATA;
		}
	}

	public Image getColumnImage(Object obj, int index) {

		String path = null;
		
		Statement statement = (Statement)obj;
		
		if(statement.isBookmark()) {
			path = ImagesPaths.BOOKMARK;
		} else if(statement.isTerse()) {
			path = ImagesPaths.TERSE;
		} else if(statement.isComment()) {
			path = ImagesPaths.COMMENT;
		} else if(statement.isRss()) {
			path = ImagesPaths.RSS;
		} else if(statement.isContact()) {
			path = ImagesPaths.CONTACT;
		} else if(statement.isSphere()) {
			path = ImagesPaths.SPHERE;
		} else if(statement.isMessage()) {
			path = ImagesPaths.MESSAGE;
		} else if(statement.isKeywords()) {
			path = ImagesPaths.KEYWORDS;
		} else if(statement.isFile()) {
			path = ImagesPaths.FILE;
		} else if(statement.isEmail()) {
			if(ExternalEmailStatement.wrap(statement.getBindedDocument()).isInput()) {
				path = ImagesPaths.EMAIL_IN_ICON;
			} else {
				path = ImagesPaths.EMAIL_OUT_ICON;
			}
		} else if(statement.isResult()) {
			path = ImagesPaths.RESULT_ICON;
		} else if(statement.isSystemStateMessage()) {
			path = ImagesPaths.ICON_SYSTEM_STATE_MESSAGE;
		} else if(statement.isSystemMessage()) {
			SystemMessageStatement systemMessage = SystemMessageStatement.wrap(statement.getBindedDocument());
			if(systemMessage.getSystemType().equals(SystemMessageStatement.SYSTEM_TYPE_ERROR)) {
				path = ImagesPaths.ICON_SYSTEM_ERROR;
			} else if(systemMessage.getSystemType().equals(SystemMessageStatement.SYSTEM_TYPE_INFO)) {
				path = ImagesPaths.ICON_SYSTEM_INFO;
			} else if(systemMessage.getSystemType().equals(SystemMessageStatement.SYSTEM_TYPE_WARNING)) {
				path = ImagesPaths.ICON_SYSTEM_WARNING;
			}
		}
		if(path != null && index==0) {
			try {
				return new Image(Display.getDefault(), getClass()
						.getResource(path).openStream());
			} catch (IOException ex) {
				return null;
			}
		}
		return null;
	
	}

	public String getColumnText(Object obj, int index) {
		Statement statement = (Statement) obj;
		switch (index) {
		case 0:
			return statement.getSubject();
		case 1:
			return statement.getGiver();
		case 2: {
			try {
				if(DateUtils.isMessageWrittenToday(statement, Calendar.getInstance().getTime())) {
					return statement.getDisplayMomentShort();
				} else {
					return statement.getDisplayMomentLong();
				}
			} catch (NullPointerException ex) {
				return "--:--:--";
			}
		}
		case 3: {
			return Boolean.toString(statement.getConfirmed());
		}
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

	
	public Color getBackground(Object arg0) {
		return null;
	}
	
	
	public Color getForeground(Object obj) {
		Statement statement = (Statement) obj;
		boolean shouldBeRed = this.mp.shouldBeRedInTable(statement, getConfiguration());
		if (shouldBeRed) {
			return this.red;
		}
		return this.green;
	}

	
	public Font getFont(Object arg0) {
		Statement st = (Statement)arg0;
		String contactName = (String)this.mp.client.session.get(SessionConstants.REAL_NAME);
		if((st.getGiver() != null && st.getGiver().equals(contactName)) ||
				this.mp.hasVoted(contactName, st.getBindedDocument())) {
			return this.normalFont;
		}
		return this.boldFont;
	}
	
	private void initConfiguration() {
		if (this.configuration == null){
			this.configuration = SsDomain.SPHERE_HELPER
				.getSpherePreferences(this.mp.getSystemName())
				.getWorkflowConfiguration();
		}
	}
	
	private WorkflowConfiguration getConfiguration(){
		if (this.configuration == null){
			this.configuration = SsDomain.SPHERE_HELPER
				.getSpherePreferences(this.mp.getSystemName())
				.getWorkflowConfiguration();
		}
		return this.configuration;
	}
}