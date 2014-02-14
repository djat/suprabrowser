/**
 * 
 */
package ss.client.ui.tempComponents;

import java.util.TreeSet;

import org.dom4j.Element;

/**
 * 
 */
public class TabSorter {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TabSorter.class);
	
	private final TreeSet<TabOrderInformation> openedTabs = new TreeSet<TabOrderInformation>();
	
	private final TreeSet<String> openedSpheresTitles = new TreeSet<String>();
	
	private Element buildOrder;
	
	private int creationIndex = 0;
	
	/**
	 * @param itemCount
	 * @param buildOrder
	 * @param title
	 * @return
	 */
	public synchronized int queryPosition(int itemCount, Element buildOrder, String title) {
		this.buildOrder = buildOrder;
		final int desiredPosition = getDesiredPosition(buildOrder, title);
		return queryPosition(itemCount, title, desiredPosition);
	}
	
	/**
	 * @param itemCount
	 * @return
	 */
	public int queryPosition(int itemCount) {
		return queryPosition(itemCount, null, null);
	}

	/**
	 * @param itemCount
	 * @param title
	 * @param desiredPosition
	 * @return
	 */
	public synchronized int queryPosition(int itemCount, String title, int desiredPosition) {
		final TabOrderInformation newTab = new TabOrderInformation( title, desiredPosition, ++ this.creationIndex  );
		this.openedTabs.add( newTab );
		int index = 0;
		for( TabOrderInformation tab : this.openedTabs ) {
			if ( newTab == tab ) {
				break;
			}
			++ index;
		}
		final int pos = Math.min(itemCount, index );
		if (logger.isDebugEnabled()) {
			logger.debug( "Add tab "  + newTab + " to " + pos );
		}
		return pos;
	}

	private int getDesiredPosition(Element buildOrder, String title) {
		if ( buildOrder == null ) {
			return Integer.MAX_VALUE;
		}
		if ( title == null ){
			return Integer.MAX_VALUE;
		}
		if (this.openedSpheresTitles.contains(title)){
			return Integer.MAX_VALUE;
		} 
		this.openedSpheresTitles.add(title);
		for (Object e : buildOrder.elements()) {
			String position = ((Element) e).attributeValue("value");
			String displayName = ((Element) e).attributeValue("display_name");
			if (displayName!=null && displayName.equals(title)) {
				return Integer.parseInt(position);
			}
		}
		return Integer.MAX_VALUE;
	}

	/**
	 *
	 */
	public class TabOrderInformation implements Comparable<TabOrderInformation> {
		
		private final String title;
		
		private final int desiredPosition;
		
		private final int creationIndex;
		
		public TabOrderInformation( String title, int desiredPosition, int creationIndex ) {
			this.title = title;
			this.desiredPosition = desiredPosition;
			this.creationIndex = creationIndex;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(TabOrderInformation o) {
			int thisVal = this.desiredPosition;
			int anotherVal = o.desiredPosition;
			if ( thisVal <anotherVal  ) {
				return -1;
			}
			else if ( thisVal == anotherVal ) {
				return this.creationIndex < this.creationIndex ? -1 : 1;
			}
			else {
				return 1;
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.title + " [" + this.desiredPosition + "]";
		}
				
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isFirstOpeningFromTabOrder(String title) {
		if (title == null){
			return false;
		}
		if (this.buildOrder == null){
			if (this.openedSpheresTitles.isEmpty()){
				return true;
			} else {
				return false;
			}
		}
		boolean isFromOrder = false;
		for (Object e : this.buildOrder.elements()) {
			String displayName = ((Element) e).attributeValue("display_name");
			if (displayName!=null && displayName.equals(title)) {
				isFromOrder = true;
				break;
			}
		}
		int count = 0;
		if (isFromOrder) {
			for (TabOrderInformation openedTab : this.openedTabs){
				if (openedTab.title.equals(title)){
					count++;
				}
			}
		} 
		if (count == 1){
			return true;
		}
		return false;
	}
}
	