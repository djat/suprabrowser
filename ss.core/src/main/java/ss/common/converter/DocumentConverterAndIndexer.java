package ss.common.converter;

import ss.common.ThreadUtils;

public final class DocumentConverterAndIndexer {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DocumentConverterAndIndexer.class);

	public static DocumentConverterAndIndexer INSTANCE = new DocumentConverterAndIndexer();

	private final ConvertingLine line;

	private final Runnable converter;

	private DocumentConverterAndIndexer() {
		this.line = new ConvertingLine();
		this.converter = new Thread() {
			@Override
			public void run() {
				while (true) {
					performConvert(getNextElement());
				}
			}
		};
		ThreadUtils.startDemon(this.converter,
				DocumentConverterAndIndexer.class);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void convert(ConvertingElement element){
		this.line.put(element);
	}

	protected void performConvert(ConvertingElement nextElement) {
		logger.warn("Process with this messageId, threadId: " + nextElement.getMessageId()
				+ " : " + nextElement.getThreadId());
		DocumentConvertAndIndex.process(nextElement);
	}

	protected ConvertingElement getNextElement() {
		return this.line.take();
	}
}