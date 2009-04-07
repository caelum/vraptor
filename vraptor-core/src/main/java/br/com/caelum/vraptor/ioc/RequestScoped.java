package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * @author Fabio Kung
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface RequestScoped {
}
