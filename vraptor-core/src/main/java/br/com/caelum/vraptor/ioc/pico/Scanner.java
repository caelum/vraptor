package br.com.caelum.vraptor.ioc.pico;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Implementations of this interface define several strategies for classpath scanning.
 *
 * @author Fabio Kung
 */
public interface Scanner {

    Collection<Class<?>> getTypesWithAnnotation(Class<? extends Annotation> annotationType);

    Collection<Class<?>> getTypesWithMetaAnnotation(Class<? extends Annotation> metaAnnotationType);

    <T> Collection<Class<? extends T>> getSubtypesOfWithAnnotation(Class<T> requiredType,
                                                                   Class<? extends Annotation> annotationType);

    <T> Collection<Class<? extends T>> getSubtypesOfWithMetaAnnotation(Class<T> requiredType,
                                                                       Class<? extends Annotation> annotationType);
}
