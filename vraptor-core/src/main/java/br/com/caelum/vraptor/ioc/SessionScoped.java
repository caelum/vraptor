package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

/**
 * @author Fabio Kung
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface SessionScoped {
}
