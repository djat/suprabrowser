package ss.framework.io.structure;

import java.util.ArrayList;
import java.util.List;

public final class FileNamePatternBuilder {

	/**
	 * 
	 */
	public static final String PATTERNS_DELIMETER = ";";

	public static final String ANY_NAME_PATTERN = "*.*";

	public static final IFileNamePattern NOTHING_MATCH = NothingMatchFileNamePattern.INSTANCE;

	public static IFileNamePattern create(String pattern) {
		final FileNamePatternBuilder builder = new FileNamePatternBuilder();
		builder.include(pattern);
		return builder.getResult();
	}

	private final List<IFileNamePattern> includes = new ArrayList<IFileNamePattern>();

	private final List<IFileNamePattern> excludes = new ArrayList<IFileNamePattern>();

	public void include(String pattern) {
		for (String siglePattern : pattern.split(PATTERNS_DELIMETER)) {
			includeSignle(siglePattern);
		}
	}

	private void includeSignle(String pattern) {
		if (pattern != null && pattern.length() > 0) {
			this.includes.add(new SingleFileNamePattern(pattern));
		}
	}

	public void exclude(String pattern) {
		for (String siglePattern : pattern.split(PATTERNS_DELIMETER)) {
			excludeSignle(siglePattern);
		}
	}

	private void excludeSignle(String pattern) {
		if (pattern != null && pattern.length() > 0) {
			this.excludes.add(new SingleFileNamePattern(pattern));
		}
	}

	public IFileNamePattern getResult() {
		if (this.includes.size() == 0) {
			return NothingMatchFileNamePattern.INSTANCE;
		} else {
			final IFileNamePattern include = toFileNamePattern(this.includes);
			
			if ( this.excludes.size() == 0 ) {
				return include;
			}
			else {
				final IFileNamePattern exclude = toFileNamePattern( this.excludes );
				return new ComplexFileNamePattern( include, exclude );
			}
		}
	}

	/**
	 * @param patterns
	 * @return
	 */
	private static IFileNamePattern toFileNamePattern(final List<IFileNamePattern> patterns) {
		if (patterns.size() == 1) {
			return patterns.get(0);
		} else {
			return new FileNamePatternList(patterns);
		}
	}
}
