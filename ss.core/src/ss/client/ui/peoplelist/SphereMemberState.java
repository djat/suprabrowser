package ss.client.ui.peoplelist;

import java.awt.Color;

import org.eclipse.swt.widgets.Display;

public enum SphereMemberState {
	
	OFFLINE( Color.gray, false, false  ),
	ONLINE( Color.blue, true, false ),
	TYPING( Color.orange, true, true );

	private final Color displayColor;
	
	private final boolean online;
	
	private final boolean typing;

	/**
	 * @param displayColor
	 * @param online
	 * @param typing
	 */
	private SphereMemberState(final Color displayColor, final boolean online, final boolean typing) {
		this.displayColor = displayColor;
		this.online = online;
		this.typing = typing;
	}

	/**
	 * @return the displayColor
	 */
	public Color getDisplayColor() {
		return this.displayColor;
	}
	
	public org.eclipse.swt.graphics.Color getDisplayColorSWT() {
		return new org.eclipse.swt.graphics.Color(Display.getDefault(),
				this.displayColor.getRed(), this.displayColor.getGreen(),
				this.displayColor.getBlue());
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return this.online;
	}

	/**
	 * @return the typing
	 */
	public boolean isTyping() {
		return this.typing;
	}
	

	
	
}
