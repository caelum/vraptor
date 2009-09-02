package br.com.caelum.vraptor.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A marked annotation to indicate that a certain exception class
 * should be used as a Validation Exception by VRaptor Validator.
 * 
 * Use this annotation to mark your business exceptions that
 * cause validation errors.
 * 
 * @author Sérgio Lopes
 * @see DefaultValidationException
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidationException {
	// TODO something to indicate that we return a message-key (for i18n)
}
