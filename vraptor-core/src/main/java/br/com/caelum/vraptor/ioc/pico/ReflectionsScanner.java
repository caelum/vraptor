package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.Stereotype;
import org.reflections.Reflections;
import org.reflections.scanners.ClassAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.AbstractConfiguration;
import org.reflections.util.ClasspathHelper;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Scanner implementation using the nice Reflections project (http://code.google.com/p/reflections) to do classpath
 * scanning.
 *
 * @author Fabio Kung
 */
public class ReflectionsScanner implements Scanner {
    private final Reflections reflections;

    public ReflectionsScanner() {
        this.reflections = new Reflections(new AbstractConfiguration() {
            {
                setUrls(ClasspathHelper.getUrlsForPackagePrefix("WEB-INF/classes"));
                setScanners(new ClassAnnotationsScanner());
            }
        });
    }

    public Collection<Class<?>> getTypesWithAnnotation(Class<? extends Annotation> annotationType) {
        return reflections.getTypesAnnotatedWith(annotationType);
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Class<?>> getTypesWithMetaAnnotation(Class<? extends Annotation> metaAnnotationType) {
        Set<Class<?>> stereotypeAnnotations = reflections.getTypesAnnotatedWith(Stereotype.class);
        Set<Class<?>> componentTypes = new HashSet<Class<?>>();
        for (Class<?> stereotypeAnnotation : stereotypeAnnotations) {
            Set<Class<?>> annotatedComponents = reflections.getTypesAnnotatedWith(
                    (Class<? extends Annotation>) stereotypeAnnotation);
            componentTypes.addAll(annotatedComponents);
        }
        return componentTypes;
    }

    @SuppressWarnings({"unchecked"})
    public <T> Collection<Class<? extends T>> getSubtypesOfWithAnnotation(Class<T> requiredType,
                                                                          Class<? extends Annotation> annotationType) {
        Collection<Class<? extends T>> subtypes = new HashSet<Class<? extends T>>();
        Collection<Class<?>> annotatedTypes = getTypesWithAnnotation(annotationType);
        for (Class<?> annotatedType : annotatedTypes) {
            if (requiredType.isAssignableFrom(annotatedType)) {
                subtypes.add((Class<? extends T>) annotatedType);
            }
        }
        return subtypes;
    }

    @SuppressWarnings({"unchecked"})
    public <T> Collection<Class<? extends T>> getSubtypesOfWithMetaAnnotation(Class<T> requiredType, Class<? extends Annotation> annotationType) {
        Collection<Class<? extends T>> subtypes = new HashSet<Class<? extends T>>();
        Collection<Class<?>> annotatedTypes = getTypesWithMetaAnnotation(annotationType);
        for (Class<?> annotatedType : annotatedTypes) {
            if (requiredType.isAssignableFrom(annotatedType)) {
                subtypes.add((Class<? extends T>) annotatedType);
            }
        }
        return subtypes;
    }
}
