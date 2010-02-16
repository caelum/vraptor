package br.com.caelum.vraptor.restfulie.serialization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
/**
 * Indicates that this method result should also be serializable
 *
 * @author Guilherme Silveira
 * @since 3.0.2
 */
public @ interface XStreamSerialize {

}
