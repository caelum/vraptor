
package br.com.caelum.vraptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets a custom path to allow web requisitions to access this resource.<br>
 * To be used together with web methods annotations as Get, Post and so on.
 *
 * @author Guilherme Silveira
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

    String value();

    /**
     * Used to decide which path will be tested first.
     * Paths with priority 0 will be tested before paths with priority 1, and
     * so on.
     * @return
     */
    int priority() default Integer.MAX_VALUE - 1;

}
