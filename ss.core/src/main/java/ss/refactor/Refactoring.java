package ss.refactor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Refactoring annotation. See package-info for more information.
 * 
 * @author Dmitry Goncharov
 */
@Target( { ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
		ElementType.PARAMETER, ElementType.CONSTRUCTOR,
		ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE,
		ElementType.PACKAGE })
@Retention(RetentionPolicy.SOURCE)
public @interface Refactoring {

	/**
	 * Level of refactoring message
	 */
	public enum Level {
		DEFAULT, POTENTIAL_BUG, BUG, PENDING_IMPLEMENTATION
	};

	/**
	 * Refactoring classification. This attribute is used for handle different
	 * refactoring is same time.
	 * 
	 * @return
	 */
	Class<? extends AbstractRefactorClass> classify();

	/**
	 * Message to developer. Default message means that marked code is involved
	 * in refactoring process.
	 * 
	 * @return
	 */
	String message() default "";

	/**
	 * Level of refactoring message
	 */
	Level level() default Level.DEFAULT;
}
