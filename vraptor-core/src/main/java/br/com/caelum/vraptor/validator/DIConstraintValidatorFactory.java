package br.com.caelum.vraptor.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.InstanceCreator;

/**
 * Create a custom {@link ConstraintValidatorFactory} to allow users to use constraints that uses components.
 *
 * @author Otávio Scherer Garcia
 * @since 3.5.2
 */
@ApplicationScoped
public class DIConstraintValidatorFactory
	implements ConstraintValidatorFactory {

	private static final Logger logger = LoggerFactory.getLogger(DIConstraintValidatorFactory.class);
	private final Container container;

	public DIConstraintValidatorFactory(Container container) {
	this.container = container;
	}

	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
	if (container.canProvide(key)) {
		logger.debug("we can provide instance for ConstraintValidator {}", key);
		return container.instanceFor(key);
	}

	return container.instanceFor(InstanceCreator.class).instanceFor(key);
	}

	public void releaseInstance(ConstraintValidator<?, ?> key) {
	// we don't need this
	}
}
