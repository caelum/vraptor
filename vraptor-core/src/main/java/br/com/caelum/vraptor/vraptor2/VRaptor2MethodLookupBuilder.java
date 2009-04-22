package br.com.caelum.vraptor.vraptor2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.In;
import org.vraptor.annotations.Logic;
import org.vraptor.annotations.Out;
import org.vraptor.annotations.Parameter;
import org.vraptor.plugin.hibernate.Validate;

import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * A vraptor 2 compatible method lookup builder.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class VRaptor2MethodLookupBuilder implements MethodLookupBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(VRaptor2MethodLookupBuilder.class);

    public ResourceAndMethodLookup lookupFor(Resource resource) {
        Class<?> type = resource.getType();
        if(Info.isOldComponent(resource)) {
            logger.warn("Old component found, remember to migrate to vraptor3: " + type.getName());
        }
        parse(type, type);
        return new VRaptor2MethodLookup(resource);
    }

    private void parse(Class<?> type, Class<?> originalType) {
        if (type.equals(Object.class)) {
            return;
        }
        for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class, Parameter.class }) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    logger.error("Field " + field.getName() + " from " + originalType.getName() + " is annotated with "
                            + annotation.getName() + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
        }
        for (Method method : type.getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class }) {
                if (method.isAnnotationPresent(annotation)) {
                    logger.error("Method " + method.getName() + " from " + originalType.getName()
                            + " is annotated with " + annotation.getName()
                            + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
            if (method.isAnnotationPresent(Logic.class)) {
                logger.warn("Method " + method.getName() + " from " + originalType.getName()
                        + " is annotated with @Logic. Although its supported, we suggest you to migrate to @Path.");
            }
            if (method.isAnnotationPresent(Validate.class)) {
                Validate validate = method.getAnnotation(Validate.class);
                if (validate.fields().length != 0) {
                    logger.error("Method " + method.getName() + " from " + originalType.getName()
                            + " is annotated with @Validate with fields. This is not supported.");
                }
            }
        }
        parse(type.getSuperclass(), type);
    }


}
