package ss.server.networking.protocol.callbacks;


public class LargePacketEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3104225395969614961L;

	private final int packetId;
	
	private final String title;

	/**
	 * @param packetId
	 * @param title
	 */
	public LargePacketEvent(final int packetId, final String title) {
		super();
		this.packetId = packetId;
		this.title = title;
	}

	public int getPacketId() {
		return this.packetId;
	}

	public String getTitle() {
		return this.title;
	}


}
