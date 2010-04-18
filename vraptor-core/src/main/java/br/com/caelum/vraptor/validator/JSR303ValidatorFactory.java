package br.com.caelum.vraptor.validator;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bring up JSR303 Bean Validation factory. This class builds the default validator factory once when application
 * starts.
 * 
 * @author Otávio Scherer Garcia
 * @since vraptor3.1.2
 */
public class JSR303ValidatorFactory {

    private static final Logger logger = LoggerFactory.getLogger(JSR303ValidatorFactory.class);

    private final Validator validator;
    private final MessageInterpolator interpolator;

    public JSR303ValidatorFactory() {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.interpolator = factory.getMessageInterpolator();

        logger.debug("Initializing JSR303 factory for bean validation");
    }

    public Validator getValidator() {
        return validator;
    }

    public MessageInterpolator getInterpolator() {
        return interpolator;
    }

}