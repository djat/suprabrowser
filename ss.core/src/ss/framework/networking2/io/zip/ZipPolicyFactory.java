/**
 * 
 */
package ss.framework.networking2.io.zip;

/**
 * @author dankosedin
 *
 */
public final class ZipPolicyFactory {
	
	public static ZipPolicyFactory INCTANCE = new ZipPolicyFactory();
	
	private ZipPolicyFactory()
	{
		
	}
	
	public final IZipPolicy getZipPolicy()
	{
		return new SimpleOptimizedZipPolicy();
	}

}
