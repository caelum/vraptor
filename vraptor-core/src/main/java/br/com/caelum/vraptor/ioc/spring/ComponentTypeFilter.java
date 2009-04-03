package br.com.caelum.vraptor.ioc.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;

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
