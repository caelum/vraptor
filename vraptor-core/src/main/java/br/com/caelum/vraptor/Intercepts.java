
package br.com.caelum.vraptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.caelum.vraptor.ioc.Stereotype;

/**
 * Notifies vraptor to use this interceptor in the interceptor stack.<br>
 * Interceptors annotated are run in any order, therefore if the sequence is
 * important to your project, use an annotated InterceptorSequence.
 * 
 * @author Guilherme Silveira
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Stereotype
public @interface Intercepts {

}
