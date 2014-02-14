/**
 * 
 */
package ss.server.functions.setmark;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author zobo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AssociatedProcedure {

	Class<? extends SetMarkProcedure<? extends SetMarkData>> value();
	
}
