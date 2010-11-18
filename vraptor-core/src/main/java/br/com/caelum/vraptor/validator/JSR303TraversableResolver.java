package br.com.caelum.vraptor.validator;

import java.lang.annotation.ElementType;

import javax.validation.Path;
import javax.validation.TraversableResolver;

/**
 * A custom {@link TraversableResolver} to bootstrap Bean Validation avoiding classpath problems with Hibernate
 * Validator 4 and JPA2. See https://forum.hibernate.org/viewtopic.php?p=2422115#p2422115 for more details.
 * 
 * @author Otávio Scherer Garcia
 * @version $Revision$
 */
public class JSR303TraversableResolver
    implements TraversableResolver {

    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType,
            Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    public boolean isCascadable(Object object, Path.Node node, Class<?> clazz, Path path, ElementType elementType) {
        return true;
    }
}