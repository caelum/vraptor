package br.com.caelum.vraptor.validator;

import javax.annotation.PostConstruct;
import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Factory for JSR303 ValidatorFactory
 *
 * @author Lucas Cavalcanti
 * @author Otávio Scherer Garcia
 * @since 3.1.3
 *
 */
@Component
@ApplicationScoped
public class ValidatorFactoryCreator implements ComponentFactory<ValidatorFactory> {

	private static final Logger logger = LoggerFactory.getLogger(ValidatorFactoryCreator.class);

	private ValidatorFactory factory;

	@PostConstruct
	public void buildFactory() {
		final Configuration<?> cfg = Validation.byDefaultProvider().configure();
        factory = cfg.traversableResolver(new JSR303TraversableResolver()).buildValidatorFactory();
        logger.debug("Initializing JSR303 factory for bean validation");
	}

	public ValidatorFactory getInstance() {
		if (factory == null) {
			buildFactory();
		}
		return factory;
	}

}
