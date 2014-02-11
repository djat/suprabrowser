package ss.lab.dm3.persist.transaction.concurrent;

import java.util.Date;

import com.sun.org.apache.xml.internal.serializer.ToSAXHandler;

public class RunStatisticStamp {

	private final long time;
	
	private final long freeMemory;
	
	private final long totalMemory;
	
	private final String notes;
	
	public RunStatisticStamp() {
		this( null );
	}
	
	public RunStatisticStamp(String notes) {
		this( System.currentTimeMillis(), Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory(), notes );
	}
	
	private RunStatisticStamp(long time, long freeMemory, long totalMemory,
			String notes) {
		super();
		this.time = time;
		this.freeMemory = freeMemory;
		this.totalMemory = totalMemory;
		this.notes = notes;
	}

	public long getTime() {
		return time;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public String getNotes() {
		return notes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( this.time );
		sb.append( " " );
		sb.append( freeMemory );
		sb.append( " " );
		sb.append( totalMemory );
		sb.append( " " );
		sb.append( notes );
		return sb.toString();
	}
	
	public RunStatisticStamp minus( RunStatisticStamp other ) {
		return new RunStatisticStamp( this.time - other.time, this.freeMemory 
				- other.freeMemory, this.totalMemory - other.totalMemory, null );
	}
}
