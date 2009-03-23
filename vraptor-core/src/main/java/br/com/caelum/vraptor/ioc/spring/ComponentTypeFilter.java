package br.com.caelum.vraptor.ioc.spring;

import org.springframework.core.type.filter.TypeFilter;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.Resource;

/**
 * @author Fabio Kung
 */
public class ComponentTypeFilter implements TypeFilter {

    private final Collection<Class<? extends Annotation>> annotationTypes;

    public ComponentTypeFilter() {
        this.annotationTypes = new ArrayList<Class<? extends Annotation>>();
        this.annotationTypes.add(Component.class);
        this.annotationTypes.add(Resource.class);
    }

    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (metadata.hasAnnotation(annotationType.getName())) {
                return true;
            }
        }
        return metadata.hasMetaAnnotation(Stereotype.class.getName());
    }
}
