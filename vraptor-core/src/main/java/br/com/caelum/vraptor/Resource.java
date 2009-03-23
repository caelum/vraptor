package br.com.caelum.vraptor;

import br.com.caelum.vraptor.ioc.Stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Resources are entry points for requests; i.e, requests are handled by VRaptor Resources.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Stereotype
public @interface Resource {
}
