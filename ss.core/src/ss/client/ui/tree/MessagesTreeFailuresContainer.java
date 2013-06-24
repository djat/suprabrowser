/**
 * 
 */
package ss.client.ui.tree;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.common.StringUtils;
import ss.domainmodel.Statement;

/**
 * @author zobo
 * 
 */
public class MessagesTreeFailuresContainer {

	public class MessagesTreeFailure {
		private final Statement statement;

		private final boolean insertToSelectedOnly;

		private final boolean openTreeToMessageId;

		public MessagesTreeFailure(final Statement statement,
				final boolean insertToSelectedOnly,
				final boolean openTreeToMessageId) {
			super();
			this.statement = statement;
			this.insertToSelectedOnly = insertToSelectedOnly;
			this.openTreeToMessageId = openTreeToMessageId;
		}

		public boolean isInsertToSelectedOnly() {
			return this.insertToSelectedOnly;
		}

		public boolean isOpenTreeToMessageId() {
			return this.openTreeToMessageId;
		}

		public Statement getStatement() {
			return this.statement;
		}
	}

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesTreeFailuresContainer.class);

	private final Hashtable<String, List<MessagesTreeFailure>> failures;

	private final Object mutex = new Object();

	public MessagesTreeFailuresContainer() {
		this.failures = new Hashtable<String, List<MessagesTreeFailure>>();
	}

	public void put(final Statement statement,
			final boolean insertToSelectedOnly,
			final boolean openTreeToMessageId) {
		synchronized (this.mutex) {
			if ( statement == null ){
				logger.error("Statement is null");
				return;
			}
			final String responceId = statement.getResponseId();
			if (StringUtils.isBlank(responceId)){
				logger.error("ResponceId is null");
				return;
			}
			final MessagesTreeFailure failure = new MessagesTreeFailure(statement, insertToSelectedOnly, openTreeToMessageId);
			List<MessagesTreeFailure> existedFailures = this.failures.get(responceId);
			if ( existedFailures == null ){
				existedFailures = new ArrayList<MessagesTreeFailure>();
				existedFailures.add( failure );
				this.failures.put(responceId, existedFailures);
			} else {
				existedFailures.add( failure );
			}
		}
	}

	public List<MessagesTreeFailure> get(final String expectedMessageId) {
		synchronized (this.mutex) {
			if (StringUtils.isBlank(expectedMessageId)) {
				logger.error("Expected message Id is null");
				return null;
			}
			List<MessagesTreeFailure> failures = this.failures
					.get(expectedMessageId);
			if (failures == null) {
				return null;
			} else {
				this.failures.remove(expectedMessageId);
				return failures;
			}
		}
	}
}
