package br.com.caelum.vraptor.vraptor2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.In;
import org.vraptor.annotations.Logic;
import org.vraptor.annotations.Out;
import org.vraptor.annotations.Parameter;

import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;

/**
 * The vraptor2 based resource registry analyses resources upon the registering
 * processes in order to find non-supported elements.
 * 
 * @author Guilherme Silveira
 */
public class VRaptor2ResourceRegistry extends DefaultResourceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(VRaptor2ResourceRegistry.class);

    public VRaptor2ResourceRegistry(MethodLookupBuilder lookupBuilder) {
        super(lookupBuilder);
    }

    public void register(List<Resource> results) {
        super.register(results);
        for (Resource resource : results) {
            Class<?> type = resource.getType();
            parse(type, type);
        }
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
        }
        parse(type.getSuperclass(), type);
    }

}
