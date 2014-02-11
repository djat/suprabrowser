package ss.lab.dm3.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author Dmitry Goncharov
 * 
 * Said that this property take action in property injection.
 * So any changes should be carefull 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyInjection {
}
