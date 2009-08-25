package br.com.caelum.vraptor.ioc.pico;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;

import org.reflections.Reflections;
import org.reflections.scanners.ClassAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Stereotype;

/**
 * Scanner implementation using the nice Reflections project (http://code.google.com/p/reflections) to do classpath
 * scanning.
 *
 * @author Fabio Kung
 */
@ApplicationScoped
public class ReflectionsScanner implements Scanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionsScanner.class);
	private final Reflections reflections;

    public ReflectionsScanner(ServletContext context) {
    	final URL webInfClasses;
		try {
			webInfClasses = new File(context.getRealPath("/WEB-INF/classes/")).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new VRaptorException(e);
		}

        this.reflections = new Reflections(new AbstractConfiguration() {
            {
            	setUrls(Arrays.asList(webInfClasses));
                setScanners(new ClassAnnotationsScanner(), new SubTypesScanner());
            }
        });
    }

    public Collection<Class<?>> getTypesWithAnnotation(Class<? extends Annotation> annotationType) {
        return reflections.getTypesAnnotatedWith(annotationType);
    }

    @SuppressWarnings({"unchecked"})
    public Collection<Class<?>> getTypesWithMetaAnnotation(Class<? extends Annotation> metaAnnotationType) {
        Set<Class<?>> stereotypeAnnotations = reflections.getTypesAnnotatedWith(Stereotype.class);

        if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Found the following sterotyped annotations: " + stereotypeAnnotations.toString());
		}

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

    public <T> Collection<Class<? extends T>> getSubtypesOf(Class<T> type) {
    	return reflections.getSubTypesOf(type);
    }
}
