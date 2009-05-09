package br.com.caelum.vraptor.ioc;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Fabio Kung
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface ApplicationScoped {
}
