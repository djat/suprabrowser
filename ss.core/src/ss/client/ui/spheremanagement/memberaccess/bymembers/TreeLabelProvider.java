/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess.bymembers;

import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereTreeLabelProvider;

/**
 * @author roman
 *
 */
public class TreeLabelProvider extends SphereTreeLabelProvider implements
		IFontProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TreeLabelProvider.class);
	
	private static final FontData DEFAULT_FONT_DATA = new FontData( "Microsoft Sans Serif", 8, SWT.NORMAL );
	
	private final Font normalFont;
    private final Font boldFont;
	
	private CheckedSpheresHieararchyComposite treeComp;
	
	public TreeLabelProvider(CheckedSpheresHieararchyComposite treeComposite) {
		this.treeComp = treeComposite;
		
		FontData fontData = getBasicFontData();
    	this.normalFont = new Font(Display.getDefault(), fontData.getName(), fontData.getHeight(), SWT.NONE);
    	this.boldFont = new Font(Display.getDefault(), fontData.getName(), fontData.getHeight(), SWT.BOLD );
	}
	
	public Font getFont(Object obj) {
		ManagedSphere sphere = (ManagedSphere)obj;
		if(this.treeComp.getLoginSphere()!=null && sphere.equals(this.treeComp.getLoginSphere())) {
			return this.boldFont;
		}
		return this.normalFont;
	}
	
	/**
	 * @param mp
	 * @return
	 */
	private FontData getBasicFontData() {
		Font compFont = this.treeComp.getFont();
		FontData[] fontDatas = compFont != null ? compFont.getFontData() : null;
		if ( fontDatas != null &&
			 fontDatas.length > 0 ) {
			return fontDatas[ 0 ];
		}
		else {
			return DEFAULT_FONT_DATA;
		}
	}

	
	@Override
	public String getText(Object obj) {
		String sphereName = super.getText(obj);
		ManagedSphere sphere = (ManagedSphere)obj;
		if(this.treeComp.getLoginSphere()!=null && sphere.equals(this.treeComp.getLoginSphere())) {
			sphereName += " (login sphere)";
		}
		return sphereName;
	}
	
	

}
