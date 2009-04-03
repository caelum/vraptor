package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations which are intended to mark VRaptor components should be annotated with this.
 *
 * @author Fabio Kung
 * @see br.com.caelum.vraptor.ioc.Component
 */
@Target(ElementType.ANNOTATION_TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Stereotype {
}
