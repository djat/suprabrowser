package ss.common.formatting;

public class ListPartInformation {

	public final static int MAX_PART_SIZE = 3;
	
	private final int listSize;
	
	private final int desiredStart;

	/**
	 * @param listSize
	 */
	public ListPartInformation(final int listSize, final int desiredStart ) {
		super();
		this.listSize = listSize;
		this.desiredStart = desiredStart;
	}

	/**
	 * @return the part start
	 */
	public int getStart() {
		return isDesiredStartInListSizeRange() ? this.desiredStart : 0;
	}

	private boolean isDesiredStartInListSizeRange() {
		return this.desiredStart < this.listSize && this.desiredStart >= 0;
	}

	/**
	 * @return the part size
	 */
	public int getSize() {
		return isDesiredStartInListSizeRange() ? Math.min( this.listSize - getStart(), MAX_PART_SIZE ) : 0;
	}

	/**
	 * @return
	 */
	public int getEnd() {
		final int start = getStart();
		final int size = getSize();
		return size > 0 ? (start + size - 1) : start;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + getStart() + "~" + getEnd() + "]";
	}
	
}
