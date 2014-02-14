/**
 * 
 */
package ss.domainmodel.configuration;

import java.util.ArrayList;
import java.util.List;

import ss.common.domainmodel2.SsDomain;

/**
 * @author zobo
 *
 */
public class DomainProvider {
	
	private final static List<String> DEFAULT_DOMAIN;
	
	static {
		DEFAULT_DOMAIN = new ArrayList<String>(1);
		DEFAULT_DOMAIN.add("localhost");
	}
	
	public final static List<String> getDomainsList(){
		return getDomainsImpl();
	}
	
	public final static String getDefaultDomain(){
		return getDomainsImpl().get(0);
	}
	
	public final static String[] getDomains(){
		final List<String> d = getDomainsImpl();
		final String[] dom = new String[d.size()];
		for (int i = 0; i < d.size(); i++){
			dom[i] = d.get(i);
		}
		return dom;
	}
	
	public final static boolean contains( final String domain ){
		if (domain == null) {
			throw new NullPointerException("Domain is null");
		}
		for (String currentdomain : getDomainsImpl()){
			if (currentdomain.equals(domain))
				return true;
		}
		return false;
	}
	
	private static List<String> getDomainsImpl(){
		final ConfigurationValue config = SsDomain.CONFIGURATION.getMainConfigurationValue();
		final EmailDomainsList emaildomains = config.getDomains();
		final List<String> d = new ArrayList<String>();
		for (EmailDomain domain : emaildomains){
			d.add(domain.getDomain());
		}
		return (d.isEmpty() ? DEFAULT_DOMAIN : d);
	}
}
