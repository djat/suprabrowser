/**
 * 
 */
package ss.client.ui.docking;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import ss.client.ui.tempComponents.SupraColors;

/**
 * @author zobo
 * 
 */
public class DockingTopTitle extends Composite {

	private int trimSize;

	private int style;

	public DockingTopTitle(Composite parent, int style) {
		super(parent, style = checkStyle(style));

		this.style = style;

		if ((style & SWT.SHADOW_ETCHED_IN) != 0
				|| (style & SWT.SHADOW_ETCHED_OUT) != 0)
			this.trimSize = 2;
		else
			this.trimSize = 1;

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				onPaint(event);
			}
		});
	}

	private static int checkStyle(int style) {
		int mask = SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
				| SWT.SHADOW_OUT;
		style &= mask;
		if (style == 0)
			style = SWT.SHADOW_ETCHED_IN;
		return style;
	}

	public Rectangle computeTrim(int x, int y, int width, int height) {
		checkWidget();
		Rectangle trim = super.computeTrim(x, y, width, height);
		trim.x -= this.trimSize;
		trim.y -= this.trimSize;
		trim.width += 2 * this.trimSize;
		trim.height += 2 * this.trimSize;
		return trim;
	}

	public Rectangle getClientArea() {
		checkWidget();
		Rectangle r = super.getClientArea();
		r.x += this.trimSize;
		r.y += this.trimSize;
		r.width -= 2 * this.trimSize;
		r.height -= 2 * this.trimSize;
		if (r.width < 0)
			r.width = 0;
		if (r.height < 0)
			r.height = 0;
		return r;
	}

	private void onPaint(PaintEvent event) {
		Rectangle r = super.getClientArea();
		if (r.width == 0 || r.height == 0)
			return;

		event.gc.setLineWidth(1);

		Color shadow = SupraColors.DOCKING_LABEL_NORMAL;
		Color highlight = SupraColors.DOCKING_LABEL_HIGHLITED;
		if (shadow == null || highlight == null)
			return;

		if ((this.style & SWT.SHADOW_IN) != 0) {
			drawBevelRect(event.gc, r.x, r.y, r.width - 1, r.height - 1,
					shadow, highlight);
		} else if ((this.style & SWT.SHADOW_OUT) != 0) {
			drawBevelRect(event.gc, r.x, r.y, r.width - 1, r.height - 1,
					highlight, shadow);
		} else if ((this.style & SWT.SHADOW_ETCHED_IN) != 0) {
			drawBevelRect(event.gc, r.x, r.y, r.width - 1, r.height - 1,
					shadow, highlight);
			drawBevelRect(event.gc, r.x + 1, r.y + 1, r.width - 3,
					r.height - 3, highlight, shadow);
		} else // if((style & SWT.SHADOW_ETCHED_OUT) != 0)
		{
			drawBevelRect(event.gc, r.x, r.y, r.width - 1, r.height - 1,
					highlight, shadow);
			drawBevelRect(event.gc, r.x + 1, r.y + 1, r.width - 3,
					r.height - 3, shadow, highlight);
		}
	}

	private static void drawBevelRect(GC gc, int x, int y, int w, int h,
			Color topleft, Color bottomright) {
		gc.setForeground(bottomright);
		gc.drawLine(x + w, y, x + w, y + h);
		gc.drawLine(x, y + h, x + w, y + h);

		gc.setForeground(topleft);
		gc.drawLine(x, y, x + w - 1, y);
		gc.drawLine(x, y, x, y + h - 1);
	}
}
