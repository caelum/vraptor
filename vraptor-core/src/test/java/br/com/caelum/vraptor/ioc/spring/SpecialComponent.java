package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.Stereotype;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Fabio Kung
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Stereotype
public @interface SpecialComponent {
}
