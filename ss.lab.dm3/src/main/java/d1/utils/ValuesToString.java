package d1.utils;

public class ValuesToString {

	private StringBuilder sb = new StringBuilder();

	private boolean lastIsValue = false;
	
	public ValuesToString add(Object ... objs ) {
		addSpaceIfNeeded();
		for( Object obj : objs ) {
			this.sb.append( obj );
		}
		lastIsValue = true;
		return this;
	}

	private void addSpaceIfNeeded() {
		if ( lastIsValue ) {
			lastIsValue = false;
			this.sb.append( " " );
		}
	}

	public String toString() {
		return sb.toString();
	}
	
}
