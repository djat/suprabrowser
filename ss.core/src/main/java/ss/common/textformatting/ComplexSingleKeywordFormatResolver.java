/**
 * 
 */
package ss.common.textformatting;

/**
 * @author zobo
 *
 */
public class ComplexSingleKeywordFormatResolver extends ComplexMultiKeywordFormatResolver {

	public static final ComplexSingleKeywordFormatResolver INSTANCE = new ComplexSingleKeywordFormatResolver();

	@Override
	protected boolean isMultiLoad() {
		return false;
	}
}
