package br.com.caelum.vraptor.validator;

import java.util.List;

/**
 * Implements a bean validator. This interface can be used with Bean Validator (JSR-303) and Hibernate Validator 3.
 * 
 * @author Otávio Scherer Garcia
 * @since vraptor3.1.2
 */
public interface BeanValidator {

    /**
     * Validate the bean and return a list of messages if has constraint violations. If the object is null, an empty
     * list will be returned.
     * 
     * @param object The object to be validated.
     * @return List of constraint violations.
     */
    List<Message> validate(Object object);

}
