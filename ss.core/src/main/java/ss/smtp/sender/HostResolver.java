/**
 * 
 */
package ss.smtp.sender;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * @author zobo
 *
 */
public class HostResolver {
	
	private class MXHOSTRecord implements Comparable<MXHOSTRecord> {
		private int number;
		
		private final String mxHost;

		public MXHOSTRecord( String number, String mxHost ) {
			super();
			this.mxHost = mxHost;
			try {
				this.number = Integer.parseInt(number);
			} catch (Throwable ex) {
				logger.error("Not number");
				this.number = Integer.MAX_VALUE;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("MXHOSTRecord: " + this.number + ", " + this.mxHost);
			}
		}

		public String getMxHost() {
			return this.mxHost;
		}

		public int getNumber() {
			return this.number;
		}

		public int compareTo(MXHOSTRecord o) {
			if (this.number < o.number) {
				return -1;
			}
			return 1;
		}

		@Override
		public String toString() {
			return "" + this.number + " - " + this.mxHost;
		}
	}
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(HostResolver.class);
	
	public static final HostResolver resolver = new HostResolver();
	
	private final Hashtable<String, List<String>> domens;
	
	private HostResolver(){
		this.domens = new Hashtable<String, List<String>>();
	}
	
	public List<String> getHostAddress(String hostName){
		List<String> hostAddresses = get(hostName);
		if ((hostAddresses == null)||(hostAddresses.isEmpty())){
			try {
				hostAddresses = mxLookup(hostName);
				set(hostName, hostAddresses);
			} catch (Throwable ex) {
				logger.error(ex);
				List<String> toRet = new ArrayList<String>();
				toRet.add(hostName);
				return toRet;
			}
		}
		return hostAddresses;
	}

	private List<String> get(String hostName) {
		return this.domens.get(hostName);
	}
	
	private void set(String hostName, List<String> hostAddresses){
		this.domens.put(hostName, hostAddresses);
	}

	private List<String> mxLookup(String hostName) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = null;
		try {
			attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		} catch (NameNotFoundException nnfe) {
			List<String> toRet = new ArrayList<String>();
			toRet.add(hostName);
			return toRet;
		}
		Attribute attr = attrs.get("MX");

		if (attr != null) {
			TreeSet<MXHOSTRecord> records = new TreeSet<MXHOSTRecord>();
			final int size = attr.size();
			for (int i = 0; i < size; i++) {
				String first = (String) attr.get(i);
				if (first == null){
					continue;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Not parsed MX Record: " + first);
				}
				StringTokenizer st = new StringTokenizer(first, " ");
				String mxNumber = st.nextToken();
				String remainder = st.nextToken();
				String trimmed = remainder.substring(0, remainder.length() - 1);
				records.add(new MXHOSTRecord(mxNumber,trimmed));
			}
			List<String> toRet = new ArrayList<String>();
			for (MXHOSTRecord record : records){
				if (logger.isDebugEnabled()) {
					logger.debug(record.toString());
				}
				toRet.add(record.getMxHost());
			}
			return toRet;
		} else {

			return null;
		}
	}
}
