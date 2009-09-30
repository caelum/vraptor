
package br.com.caelum.vraptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notifies Vraptor that the annotated method supports the http GET
 * requisitions.
 * 
 * @author Guilherme Silveira
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
}
